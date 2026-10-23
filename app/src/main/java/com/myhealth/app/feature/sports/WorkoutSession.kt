package com.myhealth.app.feature.sports

import android.content.Context
import android.os.SystemClock
import com.myhealth.app.data.LatLng
import com.myhealth.app.data.Repo
import com.myhealth.app.data.SportCategory
import com.myhealth.app.data.SportsCatalog
import com.myhealth.app.data.WorkoutRecord
import com.myhealth.app.data.WorkoutState
import com.myhealth.app.util.CrashLog
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Canli antrenman oturumu (TASLAK: Sports modulu doldurur; state ve isActive imzalari SABITTIR). */
object WorkoutSession {
    private val _state = MutableStateFlow(WorkoutState())
    val state: StateFlow<WorkoutState> = _state.asStateFlow()

    fun isActive(): Boolean = _state.value.active

    // Tum degisiklikler ana thread'de (UI, servis konum geri cagirimi, Dispatchers.Main ticker) yapilir.
    private const val BACKUP_KEY = "sportlive_backup"
    private val _route = MutableStateFlow<List<LatLng>>(emptyList())
    val route: StateFlow<List<LatLng>> = _route.asStateFlow()
    private val _target = MutableStateFlow(0)

    /** Sure hedefi (sn); 0 = serbest. */
    val targetSec: StateFlow<Int> = _target.asStateFlow()

    private var appCtx: Context? = null
    private var scope: CoroutineScope? = null
    private var ticker: Job? = null
    private var accumMs = 0L
    private var runStartRt = 0L
    private var lastPoint: LatLng? = null
    private var lastPointMs = 0L
    private var lastAcceptRt = 0L
    private var met = 5.0
    private var weightKg = 70
    private var maxSpeed = 25.0
    private var backupSec = 0L
    private val window = ArrayList<Pair<Long, Double>>() // (elapsedRealtime ms, toplam mesafe m)

    private fun note(tag: String, e: Throwable) {
        val c = appCtx ?: return
        CrashLog.note(c, tag, e)
    }

    private fun configure(sportId: String) {
        val sp = SportsCatalog.byId(sportId)
        val m = sp?.met ?: 5.0
        met = if (m.isNaN() || m <= 0.0) 5.0 else m
        weightKg = try { Repo.profile.value.weightKg } catch (e: Exception) { 70 }
        maxSpeed = if (sp?.category == SportCategory.WINTER) 35.0 else 25.0
    }

    private fun resetTrack() {
        lastPoint = null
        lastPointMs = 0L
        lastAcceptRt = 0L
        window.clear()
    }

    private fun elapsedMs(): Long {
        val s = _state.value
        val add = if (s.active && !s.paused) (SystemClock.elapsedRealtime() - runStartRt).coerceAtLeast(0L) else 0L
        return accumMs + add
    }

    /** Yeni oturum baslatir; acik oturum varsa dokunmaz. Servisi cagiran (UI) ayrica baslatir. */
    fun start(ctx: Context, sportId: String, targetSec: Int = 0): Boolean {
        return try {
            if (_state.value.active) return false
            appCtx = ctx.applicationContext
            configure(sportId)
            accumMs = 0L
            runStartRt = SystemClock.elapsedRealtime()
            backupSec = 0L
            resetTrack()
            _route.value = emptyList()
            _target.value = targetSec.coerceIn(0, 6 * 3600)
            _state.value = WorkoutState(
                active = true, paused = false, sportId = sportId,
                startMs = System.currentTimeMillis(), position = _state.value.position
            )
            startTicker()
            true
        } catch (e: Exception) {
            note("workout_start", e)
            false
        }
    }

    fun setTarget(sec: Int) {
        _target.value = sec.coerceIn(0, 6 * 3600)
    }

    fun pause() {
        val s = _state.value
        if (!s.active || s.paused) return
        accumMs = elapsedMs() // paused=true yapilmadan ONCE hesaplanmali
        _state.value = s.copy(paused = true, speedKmh = 0.0)
        saveBackup()
    }

    fun resume() {
        val s = _state.value
        if (!s.active || !s.paused) return
        runStartRt = SystemClock.elapsedRealtime()
        resetTrack()
        _state.value = s.copy(paused = false)
    }

    /** Bitirir; kaydedilecek WorkoutRecord'u dondurur (10 sn'den kisaysa null). Oturum her durumda kapanir. */
    fun finish(): WorkoutRecord? {
        val s = _state.value
        if (!s.active) return null
        val sec = elapsedMs() / 1000L
        val rec = if (sec >= 10L) {
            WorkoutRecord(
                id = System.currentTimeMillis(), sportId = s.sportId, startMs = s.startMs,
                durationSec = sec.coerceAtMost(Int.MAX_VALUE.toLong()).toInt(),
                distanceM = s.distanceM, calories = WkGeo.kcal(met, weightKg, sec),
                avgBpm = 0, maxBpm = 0, steps = 0, route = WkGeo.thin(_route.value, 800)
            )
        } else null
        reset()
        return rec
    }

    /** Kaydetmeden kapatir. */
    fun discard() = reset()

    private fun reset() {
        ticker?.cancel()
        ticker = null
        val pos = _state.value.position
        accumMs = 0L
        resetTrack()
        _route.value = emptyList()
        _target.value = 0
        _state.value = WorkoutState(position = pos)
        discardBackup()
    }

    private fun startTicker() {
        ticker?.cancel()
        val sc = scope ?: CoroutineScope(SupervisorJob() + Dispatchers.Main).also { scope = it }
        ticker = sc.launch {
            while (isActive) {
                delay(500L)
                try {
                    tick()
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    note("workout_tick", e)
                }
            }
        }
    }

    private fun tick() {
        val s = _state.value
        if (!s.active) return
        val sec = elapsedMs() / 1000L
        val now = SystemClock.elapsedRealtime()
        var speed = s.speedKmh
        if (s.paused || (lastAcceptRt != 0L && now - lastAcceptRt > 8000L)) speed = 0.0
        _state.value = s.copy(elapsedSec = sec, kcal = WkGeo.kcal(met, weightKg, sec), speedKmh = speed)
        if (sec - backupSec >= 15L || sec < backupSec) {
            backupSec = sec
            saveBackup()
        }
    }

    /** Servisten gelen konum. Duraklatmada yalniz anlik konum guncellenir. */
    fun onLocation(lat: Double, lng: Double, accuracyM: Float, timeMs: Long) {
        try {
            val s = _state.value
            if (!s.active) return
            val p = LatLng(lat, lng)
            if (!WkGeo.valid(p)) return
            val wall = System.currentTimeMillis()
            val t = if (timeMs > 0L) timeMs else wall
            if (wall - t > 60_000L) return // bayat (son bilinen) konum
            if (s.paused) {
                _state.value = s.copy(position = p)
                return
            }
            val add = WkGeo.accept(lastPoint, lastPointMs, p, t, accuracyM, maxSpeed)
            if (add == null) {
                if (s.position == null && (accuracyM.isNaN() || accuracyM <= 100f)) _state.value = s.copy(position = p)
                return
            }
            lastPoint = p
            lastPointMs = t
            val rt = SystemClock.elapsedRealtime()
            lastAcceptRt = rt
            val sum = s.distanceM + add
            val dist = if (sum.isNaN() || sum.isInfinite()) s.distanceM else sum
            window.add(rt to dist)
            while (window.size > 1 && rt - window[0].first > 15_000L) window.removeAt(0)
            var speed = s.speedKmh
            if (window.size >= 2) {
                val dtS = (rt - window[0].first) / 1000.0
                if (dtS >= 3.0) speed = ((dist - window[0].second) / dtS * 3.6).coerceIn(0.0, 150.0)
            }
            val r = _route.value
            _route.value = if (r.size >= 6000) WkGeo.thin(r, 3000) + p else r + p
            _state.value = s.copy(position = p, distanceM = dist, speedKmh = speed)
        } catch (e: Exception) {
            note("workout_onloc", e)
        }
    }

    private fun saveBackup() {
        try {
            val s = _state.value
            if (!s.active) return
            val pts = WkGeo.thin(_route.value, 600).map { listOf(it.lat, it.lng) }
            // DOGRULA: kvPutJson Map/List/Number kabul eder (Repo.kt belgesine gore); ana thread'de apply() beklenir
            Repo.kvPutJson(
                BACKUP_KEY,
                mapOf(
                    "sport" to s.sportId, "start" to s.startMs, "acc" to elapsedMs(), "dist" to s.distanceM,
                    "target" to _target.value, "saved" to System.currentTimeMillis(), "route" to pts
                )
            )
        } catch (e: Exception) {
            note("workout_backup", e)
        }
    }

    /** Surec olumunden kalan yarim antrenman (24 saatten eskiyse silinir); aktif oturum varken null. */
    fun pendingBackup(): WkBackup? {
        return try {
            if (_state.value.active) return null
            val m = Repo.kvJson(BACKUP_KEY) as? Map<*, *> ?: return null
            val sport = m["sport"] as? String ?: return null
            val saved = (m["saved"] as? Number)?.toLong() ?: return null
            if (System.currentTimeMillis() - saved > 24L * 3600_000L) {
                Repo.kvRemove(BACKUP_KEY)
                return null
            }
            val pts = ArrayList<LatLng>()
            (m["route"] as? List<*>)?.forEach { e ->
                val l = e as? List<*>
                val la = (l?.getOrNull(0) as? Number)?.toDouble()
                val lo = (l?.getOrNull(1) as? Number)?.toDouble()
                if (la != null && lo != null) pts.add(LatLng(la, lo))
            }
            WkBackup(
                sportId = sport,
                startMs = (m["start"] as? Number)?.toLong() ?: saved,
                elapsedSec = ((m["acc"] as? Number)?.toLong() ?: 0L) / 1000L,
                distanceM = (m["dist"] as? Number)?.toDouble() ?: 0.0,
                targetSec = (m["target"] as? Number)?.toInt() ?: 0,
                route = pts
            )
        } catch (e: Exception) {
            note("workout_pending", e)
            null
        }
    }

    /** Yedegi DURAKLATILMIS olarak geri yukler (kullanici "Devam et"e basar). */
    fun restore(ctx: Context): Boolean {
        return try {
            val b = pendingBackup() ?: return false
            appCtx = ctx.applicationContext
            configure(b.sportId)
            accumMs = b.elapsedSec * 1000L
            runStartRt = SystemClock.elapsedRealtime()
            backupSec = b.elapsedSec
            resetTrack()
            _route.value = b.route
            _target.value = b.targetSec.coerceIn(0, 6 * 3600)
            _state.value = WorkoutState(
                active = true, paused = true, sportId = b.sportId, startMs = b.startMs,
                elapsedSec = b.elapsedSec, distanceM = b.distanceM,
                kcal = WkGeo.kcal(met, weightKg, b.elapsedSec), position = b.route.lastOrNull()
            )
            startTicker()
            true
        } catch (e: Exception) {
            note("workout_restore", e)
            false
        }
    }

    fun discardBackup() {
        try {
            Repo.kvRemove(BACKUP_KEY)
        } catch (e: Exception) {
            note("workout_discard", e)
        }
    }
}

/** Yarim kalan antrenman ozeti (WorkoutSession.pendingBackup). */
data class WkBackup(
    val sportId: String,
    val startMs: Long,
    val elapsedSec: Long,
    val distanceM: Double,
    val targetSec: Int,
    val route: List<LatLng>
)
