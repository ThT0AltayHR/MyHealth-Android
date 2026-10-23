package com.myhealth.app.data

import android.content.Context
import android.content.SharedPreferences
import com.myhealth.app.core.Fmt
import com.myhealth.app.core.Json
import com.myhealth.app.core.Lang
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Uygulamanin tek veri kaynagi. SharedPreferences uzerinde JSON olarak saklar, her veriyi
 * StateFlow olarak yayinlar. Her okuma/yazma hataya karsi korumalidir: bozuk veri varsayilana doner,
 * hicbir durumda cokme olmaz. Her thread'den cagrilabilir (servis, receiver, worker, arayuz).
 *
 * Ek, ozellige ozel veriler icin kv* fonksiyonlarini kullanin (anahtarinizi "ozellik.ad" biciminde verin).
 */
object Repo {
    private const val PREFS = "myhealth_store_v1"
    private const val MAX_WORKOUTS = 500
    private const val MAX_ROUTE_POINTS = 600
    private const val MAX_CHAT = 200
    private const val MAX_MEMORY = 80
    private const val MAX_DAYS = 400

    @Volatile private var prefs: SharedPreferences? = null
    private val lock = Any()

    private val _profile = MutableStateFlow(UserProfile())
    private val _settings = MutableStateFlow(AppSettings())
    private val _lang = MutableStateFlow(Lang.fromSystem(Locale.getDefault().language))
    private val _workouts = MutableStateFlow<List<WorkoutRecord>>(emptyList())
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    private val _alarms = MutableStateFlow<List<AlarmItem>>(emptyList())
    private val _meals = MutableStateFlow<List<MealEntry>>(emptyList())
    private val _chat = MutableStateFlow<List<ChatMessage>>(emptyList())
    private val _memory = MutableStateFlow<List<MemoryFact>>(emptyList())
    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    private val _weather = MutableStateFlow<WeatherNow?>(null)
    private val _forecast = MutableStateFlow<List<DayForecast>>(emptyList())
    private val _steps = MutableStateFlow<Map<String, Int>>(emptyMap())
    private val _water = MutableStateFlow<Map<String, Int>>(emptyMap())
    private val _location = MutableStateFlow<LatLng?>(null)

    val profile: StateFlow<UserProfile> = _profile.asStateFlow()
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    /** Gecerli uygulama dili (ayar bossa sistem dili, desteklenmiyorsa Ingilizce). */
    val lang: StateFlow<Lang> = _lang.asStateFlow()
    val workouts: StateFlow<List<WorkoutRecord>> = _workouts.asStateFlow()
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()
    val alarms: StateFlow<List<AlarmItem>> = _alarms.asStateFlow()
    val meals: StateFlow<List<MealEntry>> = _meals.asStateFlow()
    val chat: StateFlow<List<ChatMessage>> = _chat.asStateFlow()
    val memory: StateFlow<List<MemoryFact>> = _memory.asStateFlow()
    val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()
    val weather: StateFlow<WeatherNow?> = _weather.asStateFlow()
    val forecast: StateFlow<List<DayForecast>> = _forecast.asStateFlow()

    /** dateKey (yyyy-MM-dd) -> o gunku adim sayisi. */
    val steps: StateFlow<Map<String, Int>> = _steps.asStateFlow()

    /** dateKey -> o gun icilen bardak sayisi. */
    val water: StateFlow<Map<String, Int>> = _water.asStateFlow()

    /** Bilinen son konum (hava durumu, oruc vakitleri icin). */
    val lastLocation: StateFlow<LatLng?> = _location.asStateFlow()

    val isReady: Boolean get() = prefs != null

    // ------------------------------------------------------------------ baslatma
    fun init(context: Context) {
        if (prefs != null) return
        synchronized(lock) {
            if (prefs != null) return
            val p = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            prefs = p
            _profile.value = loadMap(p, "profile")?.let { safe { UserProfile.fromJson(it) } } ?: UserProfile()
            _settings.value = loadMap(p, "settings")?.let { safe { AppSettings.fromJson(it) } } ?: AppSettings()
            _lang.value = resolveLang(_settings.value.langCode)
            _workouts.value = loadList(p, "workouts") { WorkoutRecord.fromJson(it) }
            _notes.value = loadList(p, "notes") { Note.fromJson(it) }
            _alarms.value = loadList(p, "alarms") { AlarmItem.fromJson(it) }
            _meals.value = loadList(p, "meals") { MealEntry.fromJson(it) }
            _chat.value = loadList(p, "chat") { ChatMessage.fromJson(it) }
            _memory.value = loadList(p, "memory") { MemoryFact.fromJson(it) }
            _playlists.value = loadList(p, "playlists") { Playlist.fromJson(it) }
            _weather.value = loadMap(p, "weather")?.let { safe { WeatherNow.fromJson(it) } }
            _forecast.value = loadList(p, "forecast") { DayForecast.fromJson(it) }
            _steps.value = loadIntMap(p, "steps")
            _water.value = loadIntMap(p, "water")
            _location.value = loadMap(p, "location")?.let { m ->
                val la = (m["lat"] as? Number)?.toDouble()
                val ln = (m["lng"] as? Number)?.toDouble()
                if (la != null && ln != null) LatLng(la, ln) else null
            }
        }
    }

    private fun resolveLang(code: String): Lang =
        if (code.isBlank()) Lang.fromSystem(Locale.getDefault().language) else Lang.fromCode(code)

    // ------------------------------------------------------------------ profil ve ayarlar
    fun updateProfile(f: (UserProfile) -> UserProfile) {
        val v = _profile.updateAndGet(f)
        save("profile", v.toJson())
    }

    fun updateSettings(f: (AppSettings) -> AppSettings) {
        val v = _settings.updateAndGet(f)
        _lang.value = resolveLang(v.langCode)
        save("settings", v.toJson())
    }

    fun setLang(lang: Lang) = updateSettings { it.copy(langCode = lang.code) }

    // ------------------------------------------------------------------ antrenmanlar
    fun addWorkout(w: WorkoutRecord) {
        val trimmed = w.copy(route = downsample(w.route, MAX_ROUTE_POINTS))
        val v = _workouts.updateAndGet { (listOf(trimmed) + it.filter { x -> x.id != trimmed.id }).take(MAX_WORKOUTS) }
        save("workouts", v.map { it.toJson() })
    }

    fun deleteWorkout(id: Long) {
        val v = _workouts.updateAndGet { it.filter { x -> x.id != id } }
        save("workouts", v.map { it.toJson() })
    }

    // ------------------------------------------------------------------ notlar
    fun upsertNote(n: Note) {
        val v = _notes.updateAndGet { list ->
            val without = list.filter { it.id != n.id }
            (listOf(n) + without).sortedByDescending { it.updatedMs }
        }
        save("notes", v.map { it.toJson() })
    }

    fun deleteNote(id: Long) {
        val v = _notes.updateAndGet { it.filter { x -> x.id != id } }
        save("notes", v.map { it.toJson() })
    }

    // ------------------------------------------------------------------ alarmlar
    /** Kaydeder ve (id == 0 ise) yeni bir id atanmis halini dondurur. */
    fun upsertAlarm(a: AlarmItem): AlarmItem {
        var saved = a
        val v = _alarms.updateAndGet { list ->
            val id = if (a.id != 0) a.id else (list.maxOfOrNull { it.id } ?: 0) + 1
            saved = a.copy(id = id)
            (list.filter { it.id != id } + saved).sortedBy { it.minuteOfDay }
        }
        save("alarms", v.map { it.toJson() })
        return saved
    }

    fun deleteAlarm(id: Int) {
        val v = _alarms.updateAndGet { it.filter { x -> x.id != id } }
        save("alarms", v.map { it.toJson() })
    }

    // ------------------------------------------------------------------ ogunler
    fun addMeal(m: MealEntry) {
        val v = _meals.updateAndGet { (it.filter { x -> x.id != m.id } + m).sortedBy { x -> x.tsMs } }
        save("meals", v.map { it.toJson() })
    }

    fun deleteMeal(id: Long) {
        val v = _meals.updateAndGet { it.filter { x -> x.id != id } }
        save("meals", v.map { it.toJson() })
    }

    fun mealsOn(dateKey: String): List<MealEntry> = _meals.value.filter { it.dateKey == dateKey }

    // ------------------------------------------------------------------ yapay zeka sohbeti
    fun addChat(m: ChatMessage) {
        val v = _chat.updateAndGet { (it + m).takeLast(MAX_CHAT) }
        save("chat", v.map { it.toJson() })
    }

    fun clearChat() {
        _chat.value = emptyList()
        save("chat", emptyList<Any>())
    }

    fun addMemory(text: String) {
        val clean = text.trim().take(300)
        if (clean.isEmpty()) return
        val v = _memory.updateAndGet { list ->
            if (list.any { it.text.equals(clean, ignoreCase = true) }) list
            else (list + MemoryFact(clean, System.currentTimeMillis())).takeLast(MAX_MEMORY)
        }
        save("memory", v.map { it.toJson() })
    }

    fun clearMemory() {
        _memory.value = emptyList()
        save("memory", emptyList<Any>())
    }

    // ------------------------------------------------------------------ muzik listeleri
    fun upsertPlaylist(p: Playlist): Playlist {
        var saved = p
        val v = _playlists.updateAndGet { list ->
            val id = if (p.id != 0L) p.id else System.currentTimeMillis()
            saved = p.copy(id = id)
            list.filter { it.id != id } + saved
        }
        save("playlists", v.map { it.toJson() })
        return saved
    }

    fun deletePlaylist(id: Long) {
        val v = _playlists.updateAndGet { it.filter { x -> x.id != id } }
        save("playlists", v.map { it.toJson() })
    }

    // ------------------------------------------------------------------ hava durumu ve konum
    fun setWeather(now: WeatherNow, days: List<DayForecast>) {
        _weather.value = now
        _forecast.value = days
        save("weather", now.toJson())
        save("forecast", days.map { it.toJson() })
    }

    fun setLocation(p: LatLng) {
        if (p.lat.isNaN() || p.lng.isNaN()) return
        _location.value = p
        save("location", mapOf("lat" to p.lat, "lng" to p.lng))
    }

    // ------------------------------------------------------------------ adim ve su
    fun stepsToday(): Int = _steps.value[Fmt.todayKey()] ?: 0

    fun setSteps(dateKey: String, count: Int) {
        val v = _steps.updateAndGet { trimDays(it + (dateKey to count.coerceAtLeast(0))) }
        save("steps", v)
    }

    fun waterToday(): Int = _water.value[Fmt.todayKey()] ?: 0

    fun addWater(delta: Int) {
        val key = Fmt.todayKey()
        val v = _water.updateAndGet { trimDays(it + (key to ((it[key] ?: 0) + delta).coerceIn(0, 40))) }
        save("water", v)
    }

    // ------------------------------------------------------------------ ozellige ozel anahtar-deger
    fun kvGet(key: String): String? = try { prefs?.getString("kv.$key", null) } catch (e: Exception) { null }

    fun kvPut(key: String, value: String) {
        try { prefs?.edit()?.putString("kv.$key", value)?.apply() } catch (e: Exception) { /* yoksay */ }
    }

    fun kvRemove(key: String) {
        try { prefs?.edit()?.remove("kv.$key")?.apply() } catch (e: Exception) { /* yoksay */ }
    }

    fun kvInt(key: String, def: Int = 0): Int = kvGet(key)?.toIntOrNull() ?: def
    fun kvLong(key: String, def: Long = 0L): Long = kvGet(key)?.toLongOrNull() ?: def
    fun kvBool(key: String, def: Boolean = false): Boolean = kvGet(key)?.let { it == "1" } ?: def
    fun kvPutInt(key: String, v: Int) = kvPut(key, v.toString())
    fun kvPutLong(key: String, v: Long) = kvPut(key, v.toString())
    fun kvPutBool(key: String, v: Boolean) = kvPut(key, if (v) "1" else "0")

    /** JSON deger (Map/List/String/Number/Boolean) oku/yaz. Hata veya bos ise null. */
    fun kvJson(key: String): Any? = Json.parseOrNull(kvGet(key))
    fun kvPutJson(key: String, value: Any?) = kvPut(key, Json.stringify(value))

    // ------------------------------------------------------------------ yardimcilar
    private inline fun <T> MutableStateFlow<T>.updateAndGet(f: (T) -> T): T {
        var next: T
        while (true) {
            val cur = value
            next = f(cur)
            if (compareAndSet(cur, next)) return next
        }
    }

    private fun save(key: String, value: Any?) {
        try {
            prefs?.edit()?.putString(key, Json.stringify(value))?.apply()
        } catch (e: Exception) { /* diske yazilamazsa bellekteki deger gecerli kalir */ }
    }

    private inline fun <T> safe(f: () -> T): T? = try { f() } catch (e: Exception) { null }

    private fun loadMap(p: SharedPreferences, key: String): Map<*, *>? =
        try { Json.parseOrNull(p.getString(key, null)) as? Map<*, *> } catch (e: Exception) { null }

    private fun <T> loadList(p: SharedPreferences, key: String, build: (Map<*, *>) -> T): List<T> =
        try {
            (Json.parseOrNull(p.getString(key, null)) as? List<*>)
                ?.mapNotNull { (it as? Map<*, *>)?.let { m -> safe { build(m) } } }
                ?: emptyList()
        } catch (e: Exception) { emptyList() }

    private fun loadIntMap(p: SharedPreferences, key: String): Map<String, Int> =
        try {
            (Json.parseOrNull(p.getString(key, null)) as? Map<*, *>)
                ?.mapNotNull { (k, v) -> if (k is String && v is Number) k to v.toInt() else null }
                ?.toMap() ?: emptyMap()
        } catch (e: Exception) { emptyMap() }

    private fun trimDays(m: Map<String, Int>): Map<String, Int> =
        if (m.size <= MAX_DAYS) m else m.toSortedMap().entries.toList().takeLast(MAX_DAYS).associate { it.key to it.value }

    private fun downsample(route: List<LatLng>, max: Int): List<LatLng> {
        if (route.size <= max) return route
        val out = ArrayList<LatLng>(max)
        val step = (route.size - 1).toDouble() / (max - 1)
        for (i in 0 until max) out.add(route[(i * step).toInt().coerceIn(0, route.size - 1)])
        return out
    }
}
