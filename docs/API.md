# MyHealth API ozeti (otomatik uretildi, tools/mkapi.py) — ham dosyalari okumaya GEREK YOK
Paket adi = dizin yolu (com.myhealth.app.<yol>). Ornek: ui/kit/Buttons.kt -> `import com.myhealth.app.ui.kit.PrimaryButton`.
Imzalar kisaltilmistir (govde yok); varsayilan degerler gosterilir. Emin degilsen ILGILI dosyayi grep -n -A8 ile oku, tamamini okuma.

### ui/theme/Lux.kt  (package com.myhealth.app.ui.theme)
object Lux   // Marka renkleri: siyah zemin, turuncu/altin vurgu, yari saydam "cam" yuzeyler.
  vals: black, bg, surface, surface2, brownTop, brownMid, orange, orangeDeep, amber, gold, goldSoft, text, textDim, textFaint, glass, glassStrong, stroke, strokeSoft, green, red, blue, accentBrush: Brush, cardBrush: Brush, warmCardBrush: Brush, borderBrush: Brush
object Dim   // Sabit olculer.
  vals: pad, gap, radius, radiusSmall, islandBand, navClearance, miniPlayer
@Composable fun LuxTheme(lang: Lang, content: @Composable () -> Unit)   // Tum uygulamayi saran tema: dil, yazi yonu (RTL) ve Material renk semasi.
### ui/theme/Type.kt  (package com.myhealth.app.ui.theme)
val PoppinsFamily
val InterFamily
val InterDisplayFamily
enum class Sty(val size: Float, val weight: FontWeight, val heading: Boolean, val line: Float, val spacing: Float) { DISPLAY, H1, H2, H3, NUMBER, TITLE, BODY, BODY_STRONG, LABEL, BUTTON, SMALL, CAPTION }   // Yazi stilleri. Basliklar Poppins (Rusca'da Kiril destekli Inter Display), govde Inter.
fun familyFor(sty: Sty, lang: Lang): FontFamily
fun luxTextStyle(sty: Sty, lang: Lang, color: Color): TextStyle   // TextStyle gereken yerler (BasicTextField vb.) icin.
@Composable fun Txt( text: String, modifier: Modifier = Modifier, sty: Sty = Sty.BODY, color: Color = Lux.text, align: TextAlign? = null, maxLines: Int = Int.MAX_VALUE, overflow: TextOverflow = TextOverflow.Clip )   // Uygulamanin tek metin bileseni.
### ui/kit/AiDisclaimer.kt  (package com.myhealth.app.ui.kit)
@Composable fun AiDisclaimer(modifier: Modifier = Modifier, compact: Boolean = false)   // Yapay zeka metinlerinin ALTINA konan tibbi isaret (asa uzerinde iki yilan) ve soluk altin uyari.
### ui/kit/Buttons.kt  (package com.myhealth.app.ui.kit)
@Composable fun PrimaryButton( text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, icon: String? = null )   // Ana buton: siyah hap, ortada turuncu isik lekesi ve parlayan turuncu kenar
@Composable fun GhostButton( text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, icon: String? = null, tint: Color = Lux.textDim )   // Ikincil buton: koyu cam hap, soluk yazi (referanstaki "Sign In").
@Composable fun IconBtn( icon: String, onClick: () -> Unit, modifier: Modifier = Modifier, description: String = "", size: Dp = 44.dp, tint: Color = Lux.text, filled: Boolean = false, autoMirror: Boolean = false, background: Color = Lux.glass )   // Yuvarlak cam ikon dugmesi (geri, menu, bildirim).
@Composable fun Chip( text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier, icon: String? = null )   // Secim hapi: secili = beyaz dolgu / siyah yazi; degilse cam / soluk yazi.
@Composable fun TagPill(text: String, modifier: Modifier = Modifier)   // Turuncu cerceveli kucuk etiket ("Asistanin" gibi).
@Composable fun SmallTag(text: String, modifier: Modifier = Modifier)   // Liste satirlarindaki kucuk cam etiket ("45 dk", "Aksam").
@Composable fun RoundPlay( playing: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier, size: Dp = 48.dp )   // Turuncu yuvarlak oynat/duraklat dugmesi (referanstaki liste satiri dugmesi).
@Composable fun GoControl(label: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true)   // Ilk giris akisinin "Go" kapsulu: ustte dalgalanan oklar, altta yuvarlak dugme.
@Composable fun VGap(h: Dp)   // Dikey bosluk kisayolu.
@Composable fun HGap(w: Dp)   // Yatay bosluk kisayolu.
### ui/kit/Ic.kt  (package com.myhealth.app.ui.kit)
Ic ikon adlari: HOME, SPORTS, ASSISTANT, MUSIC, MORE, BACK, FORWARD, UP, DOWN, ARROW_LEFT, ARROW_RIGHT, ARROW_UP, ARROW_DOWN, CLOSE, PLUS, MINUS, CHECK, MENU, MORE_VERT, SEARCH, EDIT, TRASH, COPY, SEND, REFRESH, DOWNLOAD, MINIMIZE, EXPAND, POWER, SLIDERS, LIST, PLAY, PAUSE, STOP, NEXT, PREV, SHUFFLE, REPEAT, VOLUME, HEADPHONES, PLAYLIST, BLUETOOTH, MIC, FOLDER, HEART, PULSE, FLAME, DROP, STEPS, SCALE, HEIGHT, AGE, MALE, FEMALE, MEDICAL, TIMER, TROPHY, ROUTE, BOLT, TARGET, WAVES, MEAL, APPLE, NOTE, CHART, SUN, MOON, CLOUD, RAIN, STORM, SNOW, WIND, THERMOMETER, PIN, MAP, CLOCK, ALARM, CALENDAR, BELL, SETTINGS, USER, SHIELD, LOCK, INFO, WARNING, WIFI, WIFI_OFF, GLOBE, STAR, FLAG, EYE, EYE_OFF, BATTERY
### ui/kit/Inputs.kt  (package com.myhealth.app.ui.kit)
@Composable fun LuxTextField( value: String, onValueChange: (String) -> Unit, placeholder: String, modifier: Modifier = Modifier, singleLine: Boolean = true, keyboardType: KeyboardType = KeyboardType.Text, imeAction: ImeAction = ImeAction.Done, onDone: () -> Unit = {}, leadingIcon: String? = null, password: Boolean = false, minHeight: Int = 56, trailing: (@Composable () -> Unit)? = null )   // Cam gorunumlu metin alani.
@Composable fun LuxSwitch( checked: Boolean, onChange: (Boolean) -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true )   // Acma/kapama anahtari: acikken turuncu-amber, kapaliyken koyu.
@Composable fun SegmentedTabs( options: List<String>, selected: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier )   // Yan yana sekmeler (secili olan turuncu hap).
@Composable fun SettingsRow( icon: String, title: String, modifier: Modifier = Modifier, subtitle: String? = null, onClick: (() -> Unit)? = null, trailing: (@Composable () -> Unit)? = null )   // Ayar/profil listesi satiri: yuvarlak ikon, baslik (+ alt baslik), sagda deger/anahtar/ok.
@Composable fun LuxSlider( value: Float, onChange: (Float) -> Unit, valueRange: ClosedFloatingPointRange<Float>, modifier: Modifier = Modifier, steps: Int = 0, onChangeFinished: (() -> Unit)? = null )   // Turuncu izli kaydirici.
### ui/kit/Lifecycle.kt  (package com.myhealth.app.ui.kit)
fun Context.findActivity(): Activity?   // Context zincirinden Activity'yi bulur (yoksa null).
fun Context.findLifecycleOwner(): LifecycleOwner?   // Context zincirinden LifecycleOwner'i bulur (yoksa null).
@Composable fun OnResume(action: () -> Unit)   // Ekran her on plana geldiginde (ilk acilis dahil) action calisir. Sistem ayarlarindan donuste izin
### ui/kit/LuxIcon.kt  (package com.myhealth.app.ui.kit)
object IconCache   // SVG yol metnini Compose Path'e cevirir ve onbellekler (ayni ikon bir kez ayristirilir).
  fun get(d: String): Path
@Composable fun LuxIcon( path: String, modifier: Modifier = Modifier, tint: Color = Lux.text, size: Dp = 24.dp, stroke: Float = 1.8f, filled: Boolean = false, autoMirror: Boolean = false )   // Vektor ikon. path: Ic.* sabiti (24x24 izgara). filled=true ise ic de doldurulur.
### ui/kit/MedIcons.kt  (package com.myhealth.app.ui.kit)
object MedIcons   // Saglik/yapay zeka ikonlari (24x24 cizgi stili SVG yollari). Ikon ajani gercek cizimleri doldurur;
  vals: CADUCEUS: String, PILL: String, STETHOSCOPE: String, DOCUMENT: String, SHARE: String, ARCHIVE: String, LUNGS: String, SPARKLES: String
### ui/kit/Misc.kt  (package com.myhealth.app.ui.kit)
@Composable fun PulseDot(color: Color, modifier: Modifier = Modifier, dot: Dp = 9.dp)   // Isik halkasi olan nabiz atan nokta (cevrimici/cevrimdisi gostergesi vb.).
@Composable fun ProgressRing( progress: Float, modifier: Modifier = Modifier, diameter: Dp = 120.dp, strokeWidth: Dp = 10.dp, track: Color = Lux.glassStrong, brush: Brush = Lux.accentBrush, content: @Composable BoxScope.() -> Unit = {} )   // Yuzdelik halka: ortaya icerik konabilir. progress 0..1.
@Composable fun LuxDialog(onDismiss: () -> Unit, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit)   // Hizalanmis cam diyalog penceresi.
@Composable fun ConfirmDialog( title: String, message: String, confirmText: String, onConfirm: () -> Unit, onDismiss: () -> Unit, danger: Boolean = false )   // Onay diyalogu: danger=true ise onay dugmesi kirmizi vurgulu.
@Composable fun EmptyState(icon: String, title: String, modifier: Modifier = Modifier, subtitle: String? = null)   // Bos liste durumu.
@Composable fun MarqueeText( text: String, modifier: Modifier = Modifier, sty: Sty = Sty.BODY, color: Color = Lux.text, active: Boolean = true )   // Sigmayan uzun metni (cihaz adi gibi) sagdan sola ritimli kaydiran tek satir.
object Toasts   // Kisa gecici bildirim (Snackbar yerine). Herhangi bir yerden Toasts.show(...) cagrilir.
  vals: message: StateFlow<Pair<Long, String>?>
  fun show(text: String)
@Composable fun ToastHost(modifier: Modifier = Modifier)   // Kabuk tarafindan en ustte bir kez yerlestirilir.
### ui/kit/Pickers.kt  (package com.myhealth.app.ui.kit)
@Composable fun WheelPicker( items: List<String>, selectedIndex: Int, onSelectedChange: (Int) -> Unit, modifier: Modifier = Modifier, itemHeight: Dp = 52.dp, visibleCount: Int = 5 )   // Dikey liste secici (iOS tekerlegi gibi): ortadaki satir secili olur, birakinca en yakina oturur.
@Composable fun TimeWheelDialog( initialMinutes: Int, onDismiss: () -> Unit, onConfirm: (Int) -> Unit )   // Saat:dakika secim diyalogu (alarm, ogun saati). Dakika olarak 0..1439 dondurur.
### ui/kit/Scaffold.kt  (package com.myhealth.app.ui.kit)
val LocalBottomClearance
@Composable fun ScreenScaffold( modifier: Modifier = Modifier, title: String? = null, onBack: (() -> Unit)? = null, trailing: (@Composable () -> Unit)? = null, glow: Glow = Glow.WARM_TOP, reserveIsland: Boolean = true, clearBottomNav: Boolean = false, content: @Composable ColumnScope.() -> Unit )   // Standart ekran iskeleti: arka plan + ust serit (dinamik ada icin yer) + baslik + icerik.
@Composable fun ScreenHeader( title: String?, onBack: (() -> Unit)?, trailing: (@Composable () -> Unit)?, modifier: Modifier = Modifier )   // Ust baslik: solda geri dugmesi, ortada baslik, sagda istege bagli eylem.
@Composable fun SectionTitle( text: String, modifier: Modifier = Modifier, actionText: String? = null, onAction: (() -> Unit)? = null )   // Bolum basligi; actionText verilirse sagda soluk bir "Tumunu gor" baglantisi cikar.
@Composable fun Gap(h: Dp)   // Dikey boslugu Dp ile veren kisayol.
### ui/kit/SportIcons.kt  (package com.myhealth.app.ui.kit)
object SportIcons   // 57 spor ve 8 kategori icin SVG yol verileri (24x24 goruntu kutusu, cizgi stili; Ic ile ayni dil).
  fun sport(id: String): String
  fun category(c: SportCategory): String
### ui/kit/Surfaces.kt  (package com.myhealth.app.ui.kit)
@Composable fun Modifier.pressable(enabled: Boolean = true, onClick: () -> Unit): Modifier   // Basinca hafifce kuculen tiklanabilir yuzey (dalga efekti yok, daha "premium" his).
enum class Glow { NONE, WARM_TOP, ARCS }
@Composable fun LuxBackdrop(modifier: Modifier = Modifier.fillMaxSize(), glow: Glow = Glow.WARM_TOP)   // Tum ekranlarin arka plani: saf siyah zemin, ustte sicak kahverengi/turuncu isik.
@Composable fun GlassCard( modifier: Modifier = Modifier, radius: Dp = Dim.radius, warm: Boolean = false, onClick: (() -> Unit)? = null, contentPadding: PaddingValues = PaddingValues(16.dp), content: @Composable ColumnScope.() -> Unit )   // Cam kart: yari saydam dolgu + ince altin kenar. onClick verilirse tiklanabilir.
@Composable fun Panel( modifier: Modifier = Modifier, radius: Dp = Dim.radius, content: @Composable ColumnScope.() -> Unit )   // Satir listeleri icin koyu panel (ayarlar, profil gibi gruplu listeler).
@Composable fun HairLine(modifier: Modifier = Modifier)   // Panel icindeki satirlari ayiran ince cizgi.
@Composable fun Section(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit)   // Ortak yatay dolgu ile bolum kutusu.
### ui/I18nCompose.kt  (package com.myhealth.app.ui)
val LocalLang
@Composable fun t(key: String): String   // Ceviri: t("anahtar"). Bulunamazsa Ingilizce, o da yoksa anahtarin kendisi doner (cokmez).
@Composable fun t(key: String, vararg args: Any?): String   // Yer tutuculu ceviri: t("greet.hey", name) -> {0} yerine name yazilir.
fun tr(key: String): String   // Composable disindan (bildirim, servis, geri cagirim) ceviri: gecerli uygulama dilini kullanir.
fun tr(key: String, vararg args: Any?): String
### data/HealthLists.kt  (package com.myhealth.app.data)
object HealthLists   // Hastalik ve alerji kimlikleri. Ceviri anahtarlari: health.cond.<id> ve health.allergy.<id>
  vals: conditions: List<String>, allergies: List<String>
  fun conditionKey(id: String): String
  fun allergyKey(id: String): String
### data/LiveState.kt  (package com.myhealth.app.data)
data class WorkoutState( val active: Boolean = false, val paused: Boolean = false, val sportId: String = "", val startMs: Long = 0L, val elapsedSec: Long = 0L, val distanceM: Double = 0.0, val kcal: Int = 0, val bpm: Int = 0, val speedKmh: Double = 0.0, val position: LatLng? = null )   // Canli antrenman anlik durumu. WorkoutSession (feature.sports) yayinlar;
### data/Models.kt  (package com.myhealth.app.data)
enum class Gender(val id: String) { MALE, FEMALE }   // ------------------------------------------------------------------ profil
data class UserProfile( val name: String = "", val gender: Gender = Gender.MALE, val age: Int = 30, val heightCm: Int = 175, val weightKg: Int = 70, val conditions: Set<String> = emptySet(), val allergies: Set<String> = emptySet(), val onboarded: Boolean = false )
  vals: bmi: Double
  fun toJson(): Map<String, Any?>
enum class AiMode(val id: String) { LOCAL, GROQ }   // ------------------------------------------------------------------ ayarlar
data class AppSettings( val langCode: String = "", val groqKey: String = "", val aiMode: AiMode = AiMode.LOCAL, val weatherNotif: Boolean = true, val weatherEveryMin: Int = 180, val activityNotif: Boolean = true, val mealNotif: Boolean = true, val breakfastMin: Int = 8 * 60 + 30, val lunchMin: Int = 13 * 60, val dinnerMin: Int = 19 * 60 + 30, val fastingAuto: Boolean = true, val fastingForce: Boolean = false, val sahurAlarm: Boolean = true, val sahurOffsetMin: Int = 45, val iftarAlarm: Boolean = true, val stepGoal: Int = 8000, val musicSource: String = "audius", /** Yapay zeka: egzersiz sonrasi 10-15 dk icinde destek bildirimi ve gunluk ipucu bildirimi. */ val aiFollowUp: Boolean = true, val aiDailyTip: Boolean = true, /** Aile hekimi: acikken asistan belirtiler surerse/agirlasirsa aile hekimine yonlendirmeyi vurgular. */ val doctorAlways: Boolean = true, val doctorName: String = "", val doctorPhone: String = "" )
  fun toJson(): Map<String, Any?>
enum class WeatherCondition(val id: String) { SUNNY, CLOUDY, LIGHT_RAIN, HEAVY_RAIN, STORM, SNOW }   // ------------------------------------------------------------------ hava durumu
data class WeatherNow( val city: String, val tempC: Int, val feelsC: Int, val humidity: Int, val windKmh: Int, val condition: WeatherCondition, val isDay: Boolean, val updatedMs: Long )
  fun toJson(): Map<String, Any?>
data class DayForecast( val dateIso: String, val dayOfWeek: Int, val minC: Int, val maxC: Int, val condition: WeatherCondition, val rainPct: Int )   // dayOfWeek: 1 = Pazartesi ... 7 = Pazar (java.time.DayOfWeek.value ile ayni).
  fun toJson(): Map<String, Any?>
data class LatLng(val lat: Double, val lng: Double)   // ------------------------------------------------------------------ antrenman
data class WorkoutRecord( val id: Long, val sportId: String, val startMs: Long, val durationSec: Int, val distanceM: Double, val calories: Int, val avgBpm: Int, val maxBpm: Int, val steps: Int = 0, val route: List<LatLng> = emptyList() )
  fun toJson(): Map<String, Any?>
data class Note(val id: Long, val title: String, val body: String, val updatedMs: Long)   // ------------------------------------------------------------------ notlar, alarmlar
  fun toJson(): Map<String, Any?>
data class AlarmItem( val id: Int, val minuteOfDay: Int, val days: Set<Int>, val label: String, val enabled: Boolean, val vibrate: Boolean = true )   // days: 1 = Pazartesi ... 7 = Pazar; bos kume = tek seferlik.
  fun toJson(): Map<String, Any?>
enum class MealType(val id: String) { BREAKFAST, LUNCH, DINNER, SNACK }   // ------------------------------------------------------------------ ogun
data class MealEntry( val id: Long, val dateKey: String, val type: MealType, val foodId: String, val customName: String, val servings: Double, val kcal: Int, val proteinG: Int, val carbG: Int, val fatG: Int, val tsMs: Long )
  fun toJson(): Map<String, Any?>
data class ChatMessage(val id: Long, val fromUser: Boolean, val text: String, val engine: String, val tsMs: Long)   // ------------------------------------------------------------------ yapay zeka
  fun toJson(): Map<String, Any?>
data class MemoryFact(val text: String, val tsMs: Long)
  fun toJson(): Map<String, Any?>
enum class TrackSource(val id: String) { LOCAL, ONLINE }   // ------------------------------------------------------------------ muzik
data class Track( val id: String, val title: String, val artist: String, val album: String, val durationMs: Long, val uri: String, val artUri: String, val source: TrackSource )
  fun toJson(): Map<String, Any?>
data class Playlist(val id: Long, val name: String, val tracks: List<Track>)
  fun toJson(): Map<String, Any?>
### data/Repo.kt  (package com.myhealth.app.data)
object Repo   // Uygulamanin tek veri kaynagi. SharedPreferences uzerinde JSON olarak saklar, her veriyi
  vals: profile: StateFlow<UserProfile>, settings: StateFlow<AppSettings>, lang: StateFlow<Lang>, workouts: StateFlow<List<WorkoutRecord>>, notes: StateFlow<List<Note>>, alarms: StateFlow<List<AlarmItem>>, meals: StateFlow<List<MealEntry>>, chat: StateFlow<List<ChatMessage>>, memory: StateFlow<List<MemoryFact>>, playlists: StateFlow<List<Playlist>>, weather: StateFlow<WeatherNow?>, forecast: StateFlow<List<DayForecast>>, steps: StateFlow<Map<String, Int>>, water: StateFlow<Map<String, Int>>, lastLocation: StateFlow<LatLng?>
  val isReady: Boolean get()
  fun init(context: Context)   // ------------------------------------------------------------------ baslatma
  fun updateProfile(f: (UserProfile) -> UserProfile)   // ------------------------------------------------------------------ profil ve ayarlar
  fun updateSettings(f: (AppSettings) -> AppSettings)
  fun setLang(lang: Lang)
  fun addWorkout(w: WorkoutRecord)   // ------------------------------------------------------------------ antrenmanlar
  fun deleteWorkout(id: Long)
  fun upsertNote(n: Note)   // ------------------------------------------------------------------ notlar
  fun deleteNote(id: Long)
  fun upsertAlarm(a: AlarmItem): AlarmItem   // Kaydeder ve (id == 0 ise) yeni bir id atanmis halini dondurur.
  fun deleteAlarm(id: Int)
  fun addMeal(m: MealEntry)   // ------------------------------------------------------------------ ogunler
  fun deleteMeal(id: Long)
  fun mealsOn(dateKey: String): List<MealEntry>
  fun addChat(m: ChatMessage)   // ------------------------------------------------------------------ yapay zeka sohbeti
  fun clearChat()
  fun addMemory(text: String)
  fun clearMemory()
  fun upsertPlaylist(p: Playlist): Playlist   // ------------------------------------------------------------------ muzik listeleri
  fun deletePlaylist(id: Long)
  fun setWeather(now: WeatherNow, days: List<DayForecast>)   // ------------------------------------------------------------------ hava durumu ve konum
  fun setLocation(p: LatLng)
  fun stepsToday(): Int   // ------------------------------------------------------------------ adim ve su
  fun setSteps(dateKey: String, count: Int)
  fun waterToday(): Int
  fun addWater(delta: Int)
  fun kvGet(key: String): String?   // ------------------------------------------------------------------ ozellige ozel anahtar-deger
  fun kvPut(key: String, value: String)
  fun kvRemove(key: String)
  fun kvInt(key: String, def: Int = 0): Int
  fun kvLong(key: String, def: Long = 0L): Long
  fun kvBool(key: String, def: Boolean = false): Boolean
  fun kvPutInt(key: String, v: Int)
  fun kvPutLong(key: String, v: Long)
  fun kvPutBool(key: String, v: Boolean)
  fun kvJson(key: String): Any?   // JSON deger (Map/List/String/Number/Boolean) oku/yaz. Hata veya bos ise null.
  fun kvPutJson(key: String, value: Any?)
### data/SportsCatalog.kt  (package com.myhealth.app.data)
enum class SportCategory(val id: String) { CARDIO, STRENGTH, FLEXIBILITY, OUTDOOR, COMBAT, WATER, BALL, WINTER }
  val nameKey: String get()
data class Sport( val id: String, val category: SportCategory, val met: Double, val gps: Boolean, val motion: String, val prop: String = "none" )   // motion: 3B sahnedeki hareket kimligi (assets/web3d/athlete.js ile birebir ayni adlar).
  val nameKey: String get()
object SportsCatalog   // MET degerleri: Compendium of Physical Activities. 57 aktivite, 8 kategori.
  vals: all: List<Sport>
  fun byId(id: String): Sport?
  fun byCategory(c: SportCategory): List<Sport>
  vals: runId, walkId, cycleId
### core/Fmt.kt  (package com.myhealth.app.core)
object Fmt   // Bicimlendirme yardimcilari. Rakamlar her dilde Latin rakami olarak yazilir (Locale.US).
  fun pad2(n: Int): String
  fun clock(totalSeconds: Long): String   // 3725 -> "1:02:05", 125 -> "02:05"
  fun km(meters: Double): String
  fun oneDecimal(v: Double): String
  fun hhmm(minutesOfDay: Int): String
  fun parseHhmm(text: String): Int?
  fun dateKey(epochMs: Long, zone: ZoneId = ZoneId.systemDefault()): String
  fun todayKey(zone: ZoneId = ZoneId.systemDefault()): String
  fun hoursMinutes(totalMinutes: Int, hUnit: String, mUnit: String): String   // Dakika sayisini "1 sa 05 dk" benzeri kisa metne cevirir; birimler cagiran tarafindan verilir.
### core/I18n.kt  (package com.myhealth.app.core)
class Row( val key: String, val tr: String, val en: String, val ru: String, val ar: String, val de: String, val ko: String )   // Tek bir ceviri satiri. Alti dilin tamami zorunlu parametre oldugu icin
  fun text(lang: Lang): String
fun row(key: String, tr: String, en: String, ru: String, ar: String, de: String, ko: String): Row
object I18n
  fun get(lang: Lang, key: String): String
  fun fmt(lang: Lang, key: String, vararg args: Any?): String
  fun has(key: String): Boolean
  fun allKeys(): Set<String>
### core/Json.kt  (package com.myhealth.app.core)
object Json   // Kucuk, bagimsiz JSON okuyucu/yazici (Android'in org.json'una baglanmaz; JVM'de test edilebilir).
  fun parse(text: String): Any?
  fun parseOrNull(text: String?): Any?
  fun stringify(value: Any?): String
fun Map<*, *>.str(key: String, def: String = ""): String   // ---- Tipli erisim yardimcilari (eksik/yanlis tipli alanlar varsayilana duser) ----
fun Map<*, *>.int(key: String, def: Int = 0): Int
fun Map<*, *>.long(key: String, def: Long = 0L): Long
fun Map<*, *>.dbl(key: String, def: Double = 0.0): Double
fun Map<*, *>.bool(key: String, def: Boolean = false): Boolean
fun Map<*, *>.list(key: String): List<*>
fun Map<*, *>.obj(key: String): Map<*, *>
fun Map<*, *>.strList(key: String): List<String>
fun Map<*, *>.intList(key: String): List<Int>
### core/Lang.kt  (package com.myhealth.app.core)
enum class Lang(val code: String, val nativeName: String, val rtl: Boolean = false) { TR, EN, RU, AR, DE, KO }   // Desteklenen 6 dil. Arapca sagdan sola (RTL) calisir.
### logic/Body.kt  (package com.myhealth.app.logic)
enum class BmiClass { UNDER, NORMAL, OVER, OBESE }
object Body   // Vucut ve antrenman hesaplari (saf Kotlin, JVM'de test edilir).
  fun bmr(p: UserProfile): Double   // Mifflin-St Jeor bazal metabolizma hizi (kcal/gun).
  fun tdee(p: UserProfile, activityFactor: Double = 1.4): Double   // Gunluk toplam enerji ihtiyaci. activityFactor: 1.2 hareketsiz ... 1.725 cok aktif.
  fun bmiClass(bmi: Double): BmiClass
  fun healthyWeightRange(heightCm: Int): IntRange   // BMI 18.5 - 24.9 araligina denk gelen kilo araligi.
  fun waterLiters(p: UserProfile, workoutMinutes: Int = 0): Double   // Gunluk su ihtiyaci (litre): kilo basina 33 ml + antrenman dakikasi basina 12 ml.
  fun maxHr(age: Int): Int   // Tanaka formulu: 208 - 0.7 x yas.
  fun restHr(p: UserProfile): Int
  fun workoutKcal(sport: Sport, weightKg: Int, seconds: Long): Double   // kcal = MET x kilo x saat
  fun estimatedBpm(p: UserProfile, sport: Sport, elapsedSec: Long): Int   // Tahmini nabiz. Telefonda gercek nabiz sensoru yoktur; bu deger sporun MET yogunlugundan,
  fun stepLengthM(heightCm: Int): Double   // Adim uzunlugu (m) ~ boy x 0.415 (yuruyus); adimdan mesafe tahmini icin.
### logic/Prayer.kt  (package com.myhealth.app.logic)
enum class PrayerMethod( val fajrAngle: Double, val ishaAngle: Double, val ishaMinutesAfterMaghrib: Int = 0, val maghribAngle: Double = 0.0, val adjSunrise: Int = 0, val adjDhuhr: Int = 0, val adjAsr: Int = 0, val adjMaghrib: Int = 0 ) { MWL, ISNA, EGYPT, MAKKAH, KARACHI, TEHRAN, DIYANET, SOUTHEAST_ASIA }   // Hesaplama yontemi: acilar, sabit sureler ve bolgesel dakika duzeltmeleri.
data class PrayerTimes(val fajr: Int, val sunrise: Int, val dhuhr: Int, val asr: Int, val maghrib: Int, val isha: Int)   // Dakika cinsinden (cihaz saat dilimine gore) gunluk vakitler.
  val imsak: Int get()
  val iftar: Int get()
object Prayer
  fun methodFor(lat: Double, lng: Double): PrayerMethod   // Koordinata gore bolgesel yontem secimi (ag/geocoder gerektirmez).
  fun compute(date: LocalDate, lat: Double, lng: Double, tzOffsetHours: Double, method: PrayerMethod): PrayerTimes   // Gunluk vakitler (gece yarisindan itibaren dakika). Degerler 1440'i asabilir (ornegin yatsi 25:10)
object Hijri   // Hicri takvim (Umm al-Qura) ile Ramazan tespiti.
  fun month(d: LocalDate): Int
  fun day(d: LocalDate): Int
  fun isRamadan(d: LocalDate): Boolean
  fun ramadanDay(d: LocalDate): Int?   // Ramazan'in kacinci gunu (Ramazan degilse null).
  fun nextRamadanStart(from: LocalDate): LocalDate   // 'from' tarihinden itibaren (dahil) bir sonraki Ramazan'in ilk gunu.
### util/CrashLog.kt  (package com.myhealth.app.util)
object CrashLog   // Hata kaydi. Cokmeler last_crash.txt dosyasina, yakalanmis (kritik olmayan) hatalar
  fun install(app: Application)
  fun note(ctx: Context, tag: String, e: Throwable)   // Yakalanmis hatayi kaydeder (uygulama calismaya devam eder).
  fun readCrash(ctx: Context): String?
  fun readErrors(ctx: Context): String?
  fun clear(ctx: Context)
### util/Net.kt  (package com.myhealth.app.util)
object Net   // Ag yardimcilari. Hicbiri istisna firlatmaz.
  fun isOnline(ctx: Context): Boolean   // Cihazin internete cikabilen bir agi var mi (Wi-Fi, mobil, ethernet).
  vals: http: OkHttpClient by lazy {
  fun getText(url: String, headers: Map<String, String> = emptyMap()): String?   // Basit GET: govde metni veya hata durumunda null. Arka plan thread'inde cagirin.
### util/Notif.kt  (package com.myhealth.app.util)
object Notif   // Bildirim kanallari ve ortak gonderim. Tum ozellikler bildirimleri buradan yollar,
  vals: CH_WEATHER, CH_ACTIVITY, CH_MEAL, CH_ALARM, CH_FASTING, CH_WORKOUT, CH_MUSIC, CH_GENERAL, CH_AI, ID_WEATHER, ID_ACTIVITY, ID_MEAL_BASE, ID_ALARM_BASE, ID_FASTING_SAHUR, ID_FASTING_IFTAR, ID_WORKOUT, ID_MUSIC, ID_FOLLOWUP, ID_DAILY_TIP, EXTRA_ROUTE
  fun ensureChannels(ctx: Context)
  fun canPost(ctx: Context): Boolean   // Bildirim gonderilebilir mi (Android 13+ izni ve kullanicinin genel anahtari).
  fun contentIntent(ctx: Context, route: String? = null, requestCode: Int = 0): PendingIntent   // Bildirime dokunuldugunda MainActivity'yi acar; route verilirse ilgili sayfaya gider.
  fun post( ctx: Context, channel: String, id: Int, title: String, text: String, route: String? = null, ongoing: Boolean = false, /** Bildirime ikinci bir eylem dugmesi ekler (orn. "Sohbeti baslat"); actionRoute yoksa route kullanilir. */ actionTitle: String? = null, actionRoute: String? = null )   // Standart metin bildirimi. route: "weather", "meals", "alarms", "fasting", "sport:run", "settings" ...
  fun cancel(ctx: Context, id: Int)
### util/Perms.kt  (package com.myhealth.app.util)
object Perms   // Izin kontrolleri ve sistem ayar ekranlarina gitme yardimcilari (HyperOS/MIUI dahil).
  vals: locationPermissions: Array<String>
  fun hasLocation(ctx: Context): Boolean
  fun hasPreciseLocation(ctx: Context): Boolean
  fun hasNotifications(ctx: Context): Boolean
  vals: notificationPermissions: Array<String>, audioPermission: String
  fun hasAudio(ctx: Context): Boolean
  vals: bluetoothPermissions: Array<String>
  fun hasBluetooth(ctx: Context): Boolean
  vals: activityPermissions: Array<String>
  fun hasActivityRecognition(ctx: Context): Boolean
  fun canFullScreenIntent(ctx: Context): Boolean   // Alarm calarken tam ekran bildirim gosterebilir mi (Android 14+ kullanici onayi gerekebilir).
  fun isXiaomiFamily(): Boolean   // ------------------------------------------------------------------ cihaz
  fun appDetailsIntent(ctx: Context): Intent   // ------------------------------------------------------------------ ayar ekranlari
  fun notificationSettingsIntent(ctx: Context): Intent
  fun batterySettingsIntent(): Intent
  fun fullScreenIntentSettings(ctx: Context): Intent?
  fun autostartIntents(ctx: Context): List<Intent>   // Otomatik baslatma ekranlari (HyperOS/MIUI ve diger ureticiler); sirayla denenir.
  fun startFirst(ctx: Context, intents: List<Intent>): Boolean   // Verilen niyetlerden ilk acilabileni baslatir. Hicbiri acilmazsa uygulama ayrintilarina gider.
  fun openAppDetails(ctx: Context): Boolean
  fun openAutostart(ctx: Context): Boolean
  fun openBattery(ctx: Context): Boolean
  fun openNotificationSettings(ctx: Context): Boolean
### nav/DeepLink.kt  (package com.myhealth.app.nav)
object DeepLink   // Bildirimden gelen yonlendirmeler. Notif.contentIntent(route = "weather") gibi anahtarlar
  vals: pending
  fun open(nav: Nav, key: String)
  fun routeFor(key: String): Route?
### nav/Nav.kt  (package com.myhealth.app.nav)
enum class Tab { HOME, SPORTS, ASSISTANT, MUSIC, MORE }   // Alt gezinmedeki bes sekme.
sealed class Route   // Sekmelerin ustune acilan tam ekran sayfalar.
  object Weather : Route()
  object Breathing : Route()
  object Notes : Route()
  data class NoteEdit(val id: Long) : Route() // 0 = yeni not
  object Alarms : Route()
  data class AlarmEdit(val id: Int) : Route() // 0 = yeni alarm
  object Meals : Route()
  object Fasting : Route()
  object Stats : Route()
  object Settings : Route()
  object Permissions : Route()
  object Profile : Route()
  data class SportLive(val sportId: String) : Route()
  object SportHistory : Route()
  object NowPlaying : Route()
  object HealthLog : Route() // egzersiz sonu saglik cevaplari belgesi (Ayarlar'dan acilir)
  object Medications : Route() // ilaclarim (duzenli / ara sira)
class Nav   // Basit durum tabanli gezinme. Sekme degistirmek sayfa yigitini temizler.
  var tab by mutableStateOf(Tab.HOME)
  vals: stack
  val top: Route? get()
  fun go(t: Tab)
  fun push(r: Route)
  fun pop(): Boolean   // Sayfayi kapatir; yigin bossa false doner.
  fun canGoBack(): Boolean
  fun back()   // Sistem geri tusu davranisi.
  fun isOpen(predicate: (Route) -> Boolean): Boolean   // Belirli bir sayfa acik mi (spor kucultme baloncugu gibi durumlar icin).
val LocalNav
