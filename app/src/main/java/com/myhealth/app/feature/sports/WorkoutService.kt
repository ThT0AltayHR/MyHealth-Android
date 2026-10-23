package com.myhealth.app.feature.sports

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.myhealth.app.core.Fmt
import com.myhealth.app.data.SportsCatalog
import com.myhealth.app.data.WorkoutState
import com.myhealth.app.ui.tr
import com.myhealth.app.util.CrashLog
import com.myhealth.app.util.Notif
import com.myhealth.app.util.Perms
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/** Antrenman suresince konumu ve bildirimi canli tutan on plan servisi (foregroundServiceType=location). TASLAK. */
class WorkoutService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    private var scope: CoroutineScope? = null
    private var job: Job? = null
    private var lm: LocationManager? = null
    private var listening = false
    private var lastNotifSec = -100L

    private val listener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            try {
                val t = if (location.time > 0L) location.time else System.currentTimeMillis()
                val acc = if (location.hasAccuracy()) location.accuracy else Float.NaN
                WorkoutSession.onLocation(location.latitude, location.longitude, acc, t)
            } catch (e: Exception) {
                CrashLog.note(applicationContext, "workout_svc_loc", e)
            }
        }

        // DOGRULA: API < 30'da bu uc yontem soyut; bos govdeyle birakmak AbstractMethodError'u onler
        @Deprecated("API 29'da kullanimdan kalkti")
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

        override fun onProviderEnabled(provider: String) {
            register(provider)
        }

        override fun onProviderDisabled(provider: String) {}
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            if (intent?.action == ACTION_STOP) {
                shutdown()
                return START_NOT_STICKY
            }
            // 5 sn kurali: ne olursa olsun ONCE on plana gec.
            val ok = enterForeground()
            if (!ok || !WorkoutSession.isActive() || !Perms.hasLocation(this)) {
                shutdown()
                return START_NOT_STICKY
            }
            startUpdates()
            startObserver()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            CrashLog.note(applicationContext, "workout_svc_cmd", e)
            shutdown()
        }
        return START_NOT_STICKY
    }

    private fun iconRes(): Int {
        val i = try { applicationInfo.icon } catch (e: Exception) { 0 }
        return if (i != 0) i else android.R.drawable.ic_menu_mylocation
    }

    private fun noteText(st: WorkoutState): String {
        val sp = SportsCatalog.byId(st.sportId)
        val name = if (sp != null) tr(sp.nameKey) else tr("sportlive.title")
        val km = Fmt.oneDecimal(st.distanceM / 1000.0) + " " + tr("sportlive.unit.km")
        val pause = if (st.paused) " | " + tr("sportlive.paused") else ""
        return name + " | " + Fmt.clock(st.elapsedSec) + " | " + km + pause
    }

    // DOGRULA: Notif bir Notification dondurmuyor; startForeground icin minimal bildirim burada kurulur
    // (kanal/icerik niyeti Notif'ten), sonraki guncellemeler Notif.post ile yapilir.
    private fun buildNotification(): Notification? {
        return try {
            Notif.ensureChannels(this)
            val st = WorkoutSession.state.value
            NotificationCompat.Builder(this, Notif.CH_WORKOUT)
                .setSmallIcon(iconRes())
                .setContentTitle(tr("sportlive.notif.title"))
                .setContentText(noteText(st))
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setContentIntent(Notif.contentIntent(this, "sport:" + st.sportId, 7))
                .build()
        } catch (e: Exception) {
            CrashLog.note(applicationContext, "workout_svc_notif", e)
            null
        }
    }

    private fun enterForeground(): Boolean {
        val n = buildNotification() ?: return false
        return try {
            if (Build.VERSION.SDK_INT >= 29) {
                startForeground(Notif.ID_WORKOUT, n, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
            } else {
                startForeground(Notif.ID_WORKOUT, n)
            }
            true
        } catch (e: Exception) {
            CrashLog.note(applicationContext, "workout_svc_fg", e)
            false
        }
    }

    private fun register(provider: String) {
        try {
            val m = lm ?: return
            if (!Perms.hasLocation(this) || !m.isProviderEnabled(provider)) return
            m.requestLocationUpdates(provider, 2000L, 0f, listener, Looper.getMainLooper())
            listening = true
        } catch (e: SecurityException) {
            CrashLog.note(applicationContext, "workout_svc_perm", e)
        } catch (e: Exception) {
            CrashLog.note(applicationContext, "workout_svc_reg", e)
        }
    }

    private fun startUpdates() {
        if (listening) return
        // Konum yalniz GPS'li sporlarda istenir; diger sporlarda servis yalniz sureci canli tutar.
        val gps = SportsCatalog.byId(WorkoutSession.state.value.sportId)?.gps == true
        if (!gps) return
        try {
            lm = getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            register(LocationManager.GPS_PROVIDER)
            register(LocationManager.NETWORK_PROVIDER)
        } catch (e: Exception) {
            CrashLog.note(applicationContext, "workout_svc_start", e)
        }
    }

    private fun stopUpdates() {
        try {
            lm?.removeUpdates(listener)
        } catch (e: Exception) {
            CrashLog.note(applicationContext, "workout_svc_rm", e)
        }
        listening = false
    }

    private fun startObserver() {
        if (job != null) return
        val sc = CoroutineScope(SupervisorJob() + Dispatchers.Main)
        scope = sc
        job = sc.launch {
            try {
                WorkoutSession.state.collect { st ->
                    if (!st.active) {
                        shutdown()
                    } else if (kotlin.math.abs(st.elapsedSec - lastNotifSec) >= 5L) {
                        lastNotifSec = st.elapsedSec
                        pushNotification(st)
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                CrashLog.note(applicationContext, "workout_svc_obs", e)
            }
        }
    }

    private fun pushNotification(st: WorkoutState) {
        try {
            if (!Notif.canPost(this)) return
            Notif.post(
                this, Notif.CH_WORKOUT, Notif.ID_WORKOUT, tr("sportlive.notif.title"), noteText(st),
                route = "sport:" + st.sportId, ongoing = true
            )
        } catch (e: Exception) {
            CrashLog.note(applicationContext, "workout_svc_post", e)
        }
    }

    private fun shutdown() {
        stopUpdates()
        try {
            job?.cancel()
            scope?.cancel()
        } catch (e: Exception) {
            CrashLog.note(applicationContext, "workout_svc_cancel", e)
        }
        job = null
        scope = null
        try {
            if (Build.VERSION.SDK_INT >= 24) {
                stopForeground(Service.STOP_FOREGROUND_REMOVE)
            } else {
                @Suppress("DEPRECATION")
                stopForeground(true)
            }
        } catch (e: Exception) {
            CrashLog.note(applicationContext, "workout_svc_stopfg", e)
        }
        try {
            stopSelf()
        } catch (e: Exception) {
            CrashLog.note(applicationContext, "workout_svc_stopself", e)
        }
    }

    override fun onDestroy() {
        try {
            stopUpdates()
            job?.cancel()
            scope?.cancel()
        } catch (e: Exception) {
            CrashLog.note(applicationContext, "workout_svc_destroy", e)
        }
        super.onDestroy()
    }

    companion object {
        private const val ACTION_START = "com.myhealth.app.workout.START"
        private const val ACTION_STOP = "com.myhealth.app.workout.STOP"

        /** Konum izni varsa hizmeti baslatir; yoksa false (uygulama yalniz sure/kcal tutar, calismaya devam eder). */
        fun start(ctx: Context): Boolean {
            return try {
                if (!Perms.hasLocation(ctx)) return false
                val i = Intent(ctx, WorkoutService::class.java).setAction(ACTION_START)
                ContextCompat.startForegroundService(ctx, i)
                true
            } catch (e: Exception) {
                CrashLog.note(ctx, "workout_svc_launch", e)
                false
            }
        }

        fun stop(ctx: Context) {
            try {
                ctx.stopService(Intent(ctx, WorkoutService::class.java).setAction(ACTION_STOP))
            } catch (e: Exception) {
                CrashLog.note(ctx, "workout_svc_halt", e)
            }
        }
    }
}
