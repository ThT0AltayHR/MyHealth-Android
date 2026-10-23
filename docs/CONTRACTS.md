# MyHealth — Geliştirme Sözleşmesi (TÜM AJANLAR İÇİN ZORUNLU)

Bu belge, aynı projede paralel çalışan ajanların birbirini bozmaması ve ortaya **çökmeyen, derlenen,
premium kalitede** bir uygulama çıkması için kuralları sabitler. Önce bunu, sonra kendi görev tanımınızı
okuyun. Kullanıcının orijinal (sesle yazılmış) isteği: `docs/PLAN_TR.md` (kendi modülünüzle ilgili
kısımlarını okuyun).

## 0. Ürün

* **MyHealth**: Android sağlık / spor / yapay zekâ asistanı (paket `com.myhealth.app`), Google Play'de
  **1.500 TL'ye satılacak** premium uygulama. Test telefonu: Redmi, Android 16, HyperOS 3.
* Tasarım dili: **siyah zemin + turuncu/altın parlak cam (glass) yüzeyler**, yumuşak turuncu ışık
  halkaları, büyük başlıklar, yuvarlak (pill) düğmeler. Referans görseller (mutlaka bakın, kendi
  ekranınıza en yakın olanı inceleyin):
  `/tmp/claude-0/-home-claude/05338800-9f2b-5541-9a7d-f6c8ff00dd4d/scratchpad/ref/`
  * `ref1_home_ai_assistant_cards.png` — ana sayfa: selamlama, büyük başlık, sekme çipleri, cam kartlar
  * `ref2_onboarding_welcome_getstarted.png`, `ref3_onboarding_pages_ring_goslider.png` — ilk açılış sayfaları, halka + “Go” kaydırıcı
  * `ref4_getstarted_voice_profile_settings.png` — Get Started, sesli asistan, profil + ayar listeleri
  * `ref5_music_app_pill_nav_lists.png` — müzik uygulaması: listeler, oynat düğmeleri, yüzen alt gezinme
  * `ref6_3d_mock_home_cards.png`, `ref7..9` — arka plan ışık yayları, `ref10` — kalp ikonu
* **Emoji YASAK** (arayüz metni, ikon, çeviri, bildirim — hiçbir yerde). Tüm ikonlar SVG yol verisi:
  `Ic` (ui/kit/Ic.kt) + `LuxIcon`. Yeni ikon gerekirse **kendi paketinizde** 24x24 çizgi stilinde,
  `Ic` ile aynı dilde (stroke 1.8, yuvarlak uç) çizin.
* **6 dil**: Türkçe, İngilizce, Rusça, Arapça (SAĞDAN SOLA), Almanca, Korece.
* Giriş/oturum yok. Bulut hesabı yok. Reklam yok.
* **Çökme sıfır** hedefi: bölüm 4'ü harfiyen uygulayın.

## 1. Derleme ortamı (DEĞİŞTİRİLEMEZ)

Kotlin 2.0.21 (+ Compose compiler eklentisi), Compose BOM 2024.09.02 (compose-ui/foundation 1.7.x,
material3 1.3.0), AGP 8.9.1, compileSdk/targetSdk **36**, minSdk **26** (java.time kullanılabilir),
JVM 17. R8 kapalı. Derleme yalnızca GitHub Actions'ta olur; **burada Android SDK yoktur**.

Kullanılabilir bağımlılıklar (`app/build.gradle.kts`) — **YENİSİNİ EKLEMEYİN**:
`androidx.core:core-ktx 1.13.1`, `activity-compose 1.9.3`, `lifecycle-runtime-ktx 2.8.7`,
`lifecycle-viewmodel-compose 2.8.7`, `compose.ui`, `compose.foundation`, `compose.material3`,
`media3-exoplayer/ui/session 1.4.1`, `work-runtime-ktx 2.9.1`, `okhttp 4.12.0`,
`kotlinx-coroutines-android 1.8.1`, `play-services-location 21.3.0`.

**Yok (kullanmayın):** `androidx.compose.material.icons.*` (Icons.Default.* YOK), navigation-compose,
Coil/Glide/Picasso, Room, Retrofit, Gson/Moshi/kotlinx-serialization (JSON için `core/Json`),
`collectAsStateWithLifecycle` (lifecycle-runtime-compose yok → `collectAsState()` kullanın),
Guava'yı doğrudan import etmeyin, Hilt/Koin, Firebase, `androidx.compose.material3` **bileşenleri**
(Button, Card, TextField, Scaffold, Switch… kullanmayın; `ui/kit` bileşenlerini kullanın — M3 deneysel
API uyarı/hata riski taşır). `MaterialTheme` yalnızca tema içinde var; siz renk için `Lux`'u kullanın.

Kotlin/Compose tuzakları (derleme hatası kaynağı olanlar):
* `Modifier.animateItemPlacement` / `animateItem`, `Modifier.nestedScroll` gibi deneysel API'lerden kaçının.
  `@OptIn` gerektiren API kullanıyorsanız dosyanın başına `@file:OptIn(...)` ekleyin.
* `LocalLifecycleOwner` import etmeyin; `ui/kit/Lifecycle.kt` içindeki `OnResume { }` ve
  `Context.findActivity()` kullanın.
* `String.format` için daima `Locale.US` verin (Fmt yardımcıları var).
* Birden çok overload'u olan Compose işlevlerinde **adlandırılmış parametre** kullanın.
* `when` ifadeleri `enum`/`sealed` üzerinde **kapsamlı** olmalı (veya `else` olmalı).
* Birim: `Dp`, `TextUnit` (sp) karıştırmayın; `Float` ile `Double` karıştırmayın (`toFloat()`).
* `Modifier.size(width=…)` geçersiz; `Modifier.size(width, height)` iki konumsal parametredir.
* Kaynaklar: `R.raw.weather_sunny` vb. ve `R.font.*`, `R.drawable.*` `com.myhealth.app.R` ile gelir.
* Dosya başına tek `package` satırı; paket adı klasörle birebir uyumlu olmalı.
* Kullanılmayan import hata değildir ama **olmayan** import hatadır — import'ları gerçek tanımlara göre yazın.

## 2. Mimari ve hazır yapı taşları (ÖNCE OKUYUN)

Kaynak kökü: `app/src/main/java/com/myhealth/app/`

| Dosya | Ne işe yarar |
|---|---|
| `ui/theme/Lux.kt` | `Lux` renk/fırça sabitleri, `Dim` ölçüleri, `LuxTheme` (RTL dahil) |
| `ui/theme/Type.kt` | `Txt(text, modifier, sty, color, align, maxLines, overflow)` + `Sty` (DISPLAY, H1, H2, H3, NUMBER, TITLE, BODY, BODY_STRONG, LABEL, BUTTON, SMALL, CAPTION). **Düz `Text` kullanmayın.** |
| `ui/kit/Surfaces.kt` | `GlassCard`, `Panel`, `HairLine`, `Section`, `LuxBackdrop`, `Modifier.pressable` |
| `ui/kit/Buttons.kt` | `PrimaryButton`, `GhostButton`, `IconBtn`, `Chip`, `TagPill`, `SmallTag`, `RoundPlay`, `GoControl`, `VGap`, `HGap` |
| `ui/kit/Scaffold.kt` | `ScreenScaffold(title, onBack, trailing, glow, reserveIsland, clearBottomNav, content)`, `SectionTitle`, `Gap`, `LocalBottomClearance` |
| `ui/kit/Inputs.kt` | `LuxTextField`, `LuxSwitch`, `SegmentedTabs`, `SettingsRow`, `LuxSlider` |
| `ui/kit/Pickers.kt` | `WheelPicker` (liste/çark seçici), `TimeWheelDialog` |
| `ui/kit/Misc.kt` | `PulseDot`, `ProgressRing`, `LuxDialog`, `ConfirmDialog`, `EmptyState`, `MarqueeText`, `Toasts.show`, `ToastHost` |
| `ui/kit/LuxIcon.kt`, `ui/kit/Ic.kt` | SVG ikon çizici ve 93 hazır ikon |
| `ui/kit/Lifecycle.kt` | `OnResume { }`, `Context.findActivity()`, `findLifecycleOwner()` |
| `ui/I18nCompose.kt` | `t("anahtar")`, `t("anahtar", arg0, arg1)` (composable); `tr(...)` (composable olmayan) |
| `data/Repo.kt` | Tek veri deposu (SharedPreferences JSON): StateFlow'lar (`profile, settings, lang, workouts, notes, alarms, meals, chat, memory, playlists, weather, forecast, steps, water, lastLocation`) ve yazma işlevleri; ayrıca `kv*` anahtar-değer deposu (**kendi modülünüzün verisi buraya**) |
| `data/Models.kt`, `data/LiveState.kt`, `data/SportsCatalog.kt` | Veri sınıfları, 57 sporluk katalog (`Sport.motion`, `Sport.prop`, `met`, `gps`) |
| `core/{I18n,Lang,Json,Fmt,SvgPath}.kt` | Çeviri, dil, JSON, biçimleyiciler, SVG ayrıştırıcı |
| `logic/Body.kt`, `logic/Prayer.kt` | BMR/TDEE/su/kalori hesapları; namaz/imsak/iftar ve Hicri takvim (doğrulanmış) |
| `util/Notif.kt` | Kanal kimlikleri, sabit bildirim kimlikleri, `Notif.post(...)`, `contentIntent(route)` |
| `util/Perms.kt` | İzin kontrolleri, sistem ayar ekranlarına gitme (HyperOS otomatik başlatma dahil) |
| `util/CrashLog.kt`, `util/Net.kt` | Hata günlüğü (`CrashLog.note(ctx, "etiket_alt_cizgi", e)`), `Net.http` (OkHttp), `Net.isOnline`, `Net.getText` |
| `nav/Nav.kt`, `nav/DeepLink.kt` | `LocalNav.current` → `nav.push(Route.X)`, `nav.pop()`, `nav.go(Tab.X)` |
| `ui/Shell.kt` | Kabuk: alt gezinme, mini oynatıcı, dinamik ada, spor baloncuğu, toast. **Dokunmayın.** |

Ekran yazarken:
* Sekme ekranları (`HomeScreen`, `SportsScreen`, `AssistantScreen`, `MusicScreen`, `MoreScreen`):
  `ScreenScaffold(clearBottomNav = true, …)`; sayfa ekranları: `ScreenScaffold(title = …, onBack = { nav.pop() })`.
  Üst şerit dinamik ada için ayrılır (`reserveIsland=true` varsayılan) — bunu değiştirmeyin
  (tam ekran video/harita gibi istisnalar hariç; o zaman kendi üst boşluğunuzu verin).
* Kaydırma **ekranın kendi işidir**: `LazyColumn` **veya** `verticalScroll(rememberScrollState())`.
  **ASLA** `verticalScroll` içinde `LazyColumn/LazyVerticalGrid` koymayın ve aynı yönde iç içe kaydırılabilir
  bileşen kullanmayın (sonsuz yükseklik → çökme). Liste ekranı = `LazyColumn` + `contentPadding`.
* Alt gezinme/mini oynatıcı boşluğu: sekme ekranlarında `ScreenScaffold(clearBottomNav=true)` bunu sağlar;
  `LazyColumn` için `contentPadding = PaddingValues(bottom = LocalBottomClearance.current + 24.dp)`.
* Klavye: metin alanı olan ekranlarda `Modifier.imePadding()`.
* Dokunma hedefi ≥ 48dp, her ikon-düğmeye `semantics { contentDescription = t(...) }`.
* Sağdan sola (Arapça): `Modifier.padding(start/end)`, `Arrangement.Start/End`, `TextAlign.Start`
  kullanın (Left/Right DEĞİL). Yön gösteren ikonlarda `autoMirror` parametresi var.
* Sayılar için `Fmt`; saat için `Fmt.hhmm(dakika)`; tarih anahtarı `Fmt.dateKey/todayKey`.

## 3. Sahiplik, dosyalar ve imzalar

* Her ajan yalnızca **kendi dosyalarını** yazar/değiştirir (görev tanımında listelidir). Taslak (stub) dosyaların
  **imzaları SABİTTİR** (ad, parametre, dönüş tipi); gövdeyi doldurun. Yeni dosyaları kendi paket klasörünüzde açın.
* **Paylaşılan dosyalar** (`data/*`, `core/*.kt`, `core/strings/AllStrings.kt`, `core/strings/StrCommon.kt`,
  `ui/Shell.kt`, `ui/kit/*`, `ui/theme/*`, `util/*`, `nav/*`, `Startup.kt`, `MyHealthApp.kt`, `MainActivity.kt`,
  `AndroidManifest.xml`, `build.gradle.kts`): **değiştirmeyin**. Bir şeye ihtiyaç varsa:
  1. Önce kendi paketinizde çözün (`kv*` deposu, kendi yardımcı nesneniz).
  2. Gerçekten gerekliyse **yalnızca ekleme** yapın (mevcut satırı değiştirmeden), önce `grep` ile başkasının
     eklemediğini doğrulayın ve final raporunuzda “PAYLAŞILAN DEĞİŞİKLİK:” başlığıyla listeleyin.
  3. Manifest değişikliği gerekiyorsa değiştirmeyin; raporunuza “MANIFEST İSTEĞİ:” olarak yazın.
* Veri saklama: yeni alanlar için `Repo.kvGet/kvPut/kvPutJson/kvInt/kvBool...` kullanın. Anahtarlar
  `modul_ad` biçiminde, **nokta içermeyen alt çizgili küçük harf** (örn. `weather_last_ok_ms`). Çeviri
  anahtarlarıyla karışmasın diye `kv` ve `CrashLog` etiketlerinde nokta kullanmayın.
* Bildirimler yalnızca `Notif.post/contentIntent` üzerinden. Kanal/kimlik sabitleri `Notif` içinde.
  Bildirime tıklayınca açılacak sayfa: `route` anahtarı (`DeepLink` içindeki anahtarlar).
* Zamanlayıcılar: alarm/oruç → `AlarmManager.setAlarmClock` (tam alarm izni gerekmez); diğer
  yinelemeler → `WorkManager` veya `setAndAllowWhileIdle` (kesin alarm izni **istemeyin**; `SCHEDULE_EXACT_ALARM`
  ve `USE_EXACT_ALARM` bildirilmedi).

## 4. ÇÖKMEZLİK KURALLARI (en önemli bölüm)

1. `!!` yok. `lateinit` yalnızca başlatma garantiliyse. Güvenli olmayan `as` yok (`as?`). `first()/last()/single()`
   yok (`firstOrNull`, `getOrNull`). `toInt()/toLong()` öncesi NaN/∞ kontrolü; bölmede sıfır kontrolü;
   `coerceIn(min, max)` için `min <= max` garanti; `substring` sınırları; `String.toIntOrNull()` kullanın.
2. Android çağrıları (`startActivity`, `startService`, `getSystemService`, `registerReceiver`, `LocationManager`,
   `MediaStore`, `PendingIntent`, `AlarmManager`, `WebView`, `ExoPlayer`, `Geocoder`, `Bluetooth*`…) **try/catch**
   içinde: `catch (e: Exception) { CrashLog.note(ctx, "etiket", e) }`. `SecurityException`,
   `IllegalStateException`, `ActivityNotFoundException`, `ForegroundServiceStartNotAllowedException` yaygındır.
3. Coroutine gövdelerinde `try { … } catch (e: CancellationException) { throw e } catch (e: Exception) { … }`.
   `Dispatchers.IO` ağ/disk, `Dispatchers.Main` UI. Ağ: `Net.http` (zaman aşımı hazır); JSON:
   `Json.parseOrNull` + `str/int/dbl/list/long/bool` yardımcıları (alan yoksa varsayılan döner).
4. İzin: kullanmadan önce `Perms.has…` ile denetleyin; istek `rememberLauncherForActivityResult(ActivityResultContracts.
   RequestMultiplePermissions())`; reddedilirse düzgün bir açıklama + ayarlara git düğmesi (`Perms.openAppDetails`).
5. Compose: yan etki yalnızca `LaunchedEffect/DisposableEffect/SideEffect` içinde; `remember` anahtarlarını doğru verin;
   `LazyColumn` öğelerine kararlı `key` verin; `StateFlow` → `collectAsState()`; kompozisyon içinde `.value` ile
   çizim durumu okumayın (yalnızca olay işleyicilerinde). Sonsuz animasyon için `rememberInfiniteTransition`.
   Çok ağır hesabı kompozisyonda yapmayın (`remember`/arka plan).
6. Foreground servis: `startForegroundService` sonrası **5 sn içinde** `ServiceCompat.startForeground(service, id,
   notification, type)` (Android 14+ tür zorunlu: location / mediaPlayback). Servisi yalnızca ön plandaki arayüzden
   veya izinli alarm yayınından başlatın; `try/catch` ile sarın; başlatılamazsa uygulama çalışmaya devam eder.
7. `BroadcastReceiver.onReceive` hızlı dönmeli; uzun iş için `goAsync()` + coroutine veya WorkManager. API 33+ dinamik
   `registerReceiver` için `ContextCompat.registerReceiver(ctx, r, filter, ContextCompat.RECEIVER_NOT_EXPORTED)`.
8. PendingIntent: bayrak her zaman verilir (`FLAG_IMMUTABLE`; sistemin içine ek koyduğu durumlarda — ör. Etkinlik
   Geçişi API — `FLAG_MUTABLE`). `getActivity/getBroadcast` istek kodları çakışmamalı.
9. Hiçbir ekran ağ, konum, izin veya veri **yokken** boş/çökük kalmaz: her zaman anlaşılır durum metni (`EmptyState`).
10. Uzun liste/çizim/hesap ana thread'i kilitlememeli (ANR yok). Dosya/ağ asla ana thread'de değil.
11. Bellek sızıntısı: dinleyici/observer/receiver/WebView/ExoPlayer `DisposableEffect` veya servis `onDestroy` ile bırakılır.
12. Sabit metin yok: **kullanıcıya görünen her metin çeviri tablosundan** (`t("…")`). Marka adları ve sayılar hariç.

## 5. Çeviri protokolü

* Her modül **kendi tablosunu** doldurur: `core/strings/Str<Modül>.kt` içindeki `listOf(row("anahtar", tr, en, ru, ar, de, ko), …)`.
  Altı dilin **tamamı zorunlu parametre**; eksik dil derleme hatasıdır. Yer tutucu `{0}`, `{1}` (**`$` kullanmayın**).
  Her dilde aynı yer tutucular bulunmalı. İngilizce kopya yapıştırma: Arapça/Rusça/Korece metinler kendi yazı
  sisteminde olmalı (test denetler). Almanca/Türkçe de gerçekten çevrilmiş olmalı.
* Anahtar biçimi `önek.alt.alt` (küçük harf/rakam/alt çizgi). Önekler: `home.*` `more.*` `mind.*` `sports.*` `sport.<id>`
  `cat.<id>` `map.*` `weather.*` (`weather.cond.<id>` 6 hava durumu için zorunlu) `assistant.*` `kb.*` `music.*`
  `notes.*` `alarm.*` `fasting.*` `meals.*` `food.<id>` `foodcat.<id>` `stats.*` `settings.*` `perms.*` `ob.*` `health.*`
  `notif.*`. Hazır ortak anahtarlar (`nav.*`, `common.*`, `unit.*`, `day.*`, `net.*`, `island.hint`, `greet.*`)
  `StrCommon.kt` içindedir — **yeniden tanımlamayın**, kullanın (`grep -h 'row("' core/strings/StrCommon.kt`).
* Zorunlu birebir Türkçe metinler (diğer dillere anlamca doğru çevirin):
  * Konu dışı: “Üzgünüm, sadece uygulama içerisindeki sağlık, beslenme, diyet ve spor konuları ile ilgileniyorum. Başka bir sağlık sorunuz olursa buradayım.”
  * Harita: “harita bilgisi alınamadı lütfen konumunuzu etkinleştirdiğinizden veya internet bağlantınızın açık olduğundan emin olun”
  * Ada ipucu (hazır): `island.hint` = “hava durumu bilgilerini görüntülemek için tıklayınız”
  * Nefes sonu: “Mükemmel, aynen böyle devam et, yarın seni yine bekliyorum”; kalp: “nefes al” / “nefes ver”
  * Groq gecikme: “üzgünüm şu anda yanıtlarım gecikti lokal modele geçiyorum yanıtlar tazelendiğinde tekrardan otomatik olarak geçeceğim”
* Doğrulama (her çalışmadan sonra): `bash /tmp/claude-0/-home-claude/05338800-9f2b-5541-9a7d-f6c8ff00dd4d/scratchpad/tools/check_i18n.sh`
  (yinelenen/eksik anahtar, yer tutucu, emoji, kodda geçen ama tabloda olmayan anahtar). **0 hata** olmalı.
  Başkasının tablosundaki geçici eksiklik sizi ilgilendirmez; yalnızca kendi anahtarlarınızı düzeltin.

## 6. Doğrulama araçları (scratchpad = `/tmp/claude-0/-home-claude/05338800-9f2b-5541-9a7d-f6c8ff00dd4d/scratchpad`)

* `tools/ktrun.sh <AnaSınıf> dosyalar…` — saf Kotlin (Android bağımlılığı olmayan) mantığı derleyip JVM'de çalıştırır.
  Mantık kodunuzu (hesap, ayrıştırma, motor) **Android'e bağımlı olmayan dosyalara** ayırın ve burada test edin
  (`jvmtest/` altındaki örneklere bakın: PrayerTest.kt, SvgTest.kt, DataTest.kt).
* `tools/ktc.sh dosyalar…` — yalnızca tip denetimi. Derlemeler **sıraya girer** (flock; 2 CPU var, sabırlı olun,
  paralel kotlinc başlatmayın, `-Xmx` büyütmeyin).
* `tools/shot.sh <html|url> <png> [genişlik] [yükseklik]` — Chromium (WebGL/SwiftShader dahil) ekran görüntüsü;
  oluşan PNG'yi `Read` ile görüntüleyin. HTML denemeleri için kullanın (3B sporcu, ikon sayfaları).
  `NODE_PATH=$(npm root -g):/opt/npm-tools/node_modules` ile `node` + `playwright` da kullanılabilir.
* `tools/typecheck.sh` — (S ajanı hazırlıyor) Android/Compose taslak (stub) kütüphaneleriyle **tüm projeyi** tip
  denetler. Hazırsa çalışmanızın sonunda kendi dosyalarınız için çalıştırın; kendi kullandığınız bir API stub'da yoksa
  `tools/stubs/` altına **gerçek imzasıyla** ekleyin (başka dosyaları bozmayın).
* Ağ: kabuktan yalnızca paket kayıtları/GitHub açıktır. Maven, Google, npm/pip yok; internetten dosya indirmeyi
  denemeyin. Bağımlılık eklemek mümkün değil — sıfırdan yazın.
* Gerçek Android derlemesi burada yapılamaz. Bu yüzden **API'leri ezberden kullanırken en yaygın, en az sürüme
  duyarlı çağrıları seçin**; emin olmadığınız API yerine daha basit/eski ve kesin bildiğiniz API'yi kullanın.
  Şüpheli olan her yere kısa bir `// DOGRULA: neden` yorumu bırakıp final raporunuza yazın.

## 7. Final rapor biçimi (kısa tutun)

1. Yazılan/değiştirilen dosyalar (yol listesi). 2. Gerçekten çalıştırılan doğrulamalar ve sonuçları.
3. `PAYLAŞILAN DEĞİŞİKLİK` / `MANIFEST İSTEĞİ` varsa. 4. Emin olunmayan API'ler / bilinen eksikler (dürüst olun).
5. Diğer modüllerden beklentiler. Uzun anlatımdan kaçının; kod her şeydir.

---

## 8. v2 EKLERİ — sağlık günlüğü, her köşede yapay zekâ, ilaç, rapor (TÜM AJANLAR OKUSUN)

Kullanıcının ikinci istek paketi (`docs/PLAN_TR.md` sonundaki “V2 GEREKSİNİMLERİ”). Modül görev tanımları: **`docs/ROSTER.md`**.

### 8.1 Ürünün omurgası

    Egzersiz biter (nefes + 57 spor)
      -> ExerciseFinishFlow: yeşil daire + dönen tik + parlayan halka -> egzersize özel 6-7 sağlık sorusu -> özet + yapay zekâ yorumu
      -> HealthLog.add(HealthEntry)    (“belge”: Ayarlar -> Sağlık günlüğü, Route.HealthLog)
      -> HealthFollowUp.schedule: şiddet >= 2 ise 10-15 dk sonra yapay zekâ bildirimi (Notif.CH_AI, “Sohbeti başlat” eylemi)
             -> AssistantSeed.put(AiInsights.followUpMessage(...)); dokununca asistan yeni sohbet açar, ilk mesajı yapay zekâ yazar
      -> ReportBuilder.build: altın temalı PDF, her spor ayrı sayfa; ReportExportCard (İstatistikler, Profil, Sağlık günlüğü, Ayarlar) -> ShareHelper
      -> BackupManager.exportZip: sohbetler, PDF raporlar, sağlık günlüğü, yapay zekâ belleği, ilaçlar, profil fotoğrafı ... tek ZIP
    Uygulamanın HER köşesi yapay zekâya bağlıdır: AssistantContexts.build(ctx) + AiInsights.* (aşağıdaki tablo).

### 8.2 Ortak yapı taşları (dosyalar mevcut; imzalar SABİT; gövdeyi “Sahip” doldurur)

| Yapı | Dosya | Sahip |
|---|---|---|
| `AiDisclaimer(modifier, compact)` — caduceus SVG + soluk altın uyarı | `ui/kit/AiDisclaimer.kt` | entegratör (hazır) |
| `AssistantContexts.build(ctx)`, `AssistantSeed.put/take`, `AssistantContext`, `LocalReply` | `feature/assistant/` | entegratör (hazır; yalnız EKLEME) |
| `LocalEngine.analyze/reply`, `Analysis`, `TopicGuard.isOnTopic` | `feature/assistant/` | D1a |
| `AiInsights.*`, `HealthFlag` | `feature/assistant/AiInsights.kt` | D1c |
| `KnowledgeBase.topics`, `KbTopic`, `kb/Kb{Nutrition,Fitness,Health}` | `feature/assistant/` | K1/K2/K3 |
| `TextNorm` (saf) | `feature/assistant/TextNorm.kt` | entegratör (hazır; yalnız EKLEME) |
| `AiPipeline.run`, `PipelineEvent`, `SourceCard`, `WebSearch`, `GroqClient` | `feature/assistant/` | D2b |
| `AssistantScreen`, `ChatStore` (çoklu sohbet, sohbete özel bellek) | `feature/assistant/` | D2a |
| `HealthLog`, `HealthEntry`, `QuizAnswer`, `SymptomTag`, `Exercises`, `HealthQuiz`, `ExerciseFinishFlow`, `HealthFollowUp`, `DailyTip`, `HealthLogScreen` | `feature/health/` | Q |
| `MedDb`, `Med`, `MedStore`, `MedicationEditor`, `MedicationsScreen` | `feature/meds/` | MD |
| `ReportBuilder`, `ShareHelper`, `BackupManager`, `ReportExportCard`, `BackupCard` | `feature/report/ReportApi.kt` (+yeni) | R |
| `Avatar`, `ProfilePhoto`, `ProfileScreen` | `feature/profile/` | O1 |
| `SportIcons`, `WxIcons`, `MedIcons` (CADUCEUS...) | `ui/kit/`, `feature/weather/` | I |
| `RouteMap` | `feature/map/` | M |
| `MusicController`, `MusicLibrary`, `BluetoothMonitor`, `OnlineMusic` | `feature/music/MusicApi.kt` | E1 |
| `FoodDb` | `feature/meals/FoodDb.kt` | ML |
| `Notif.post(..., actionTitle, actionRoute)`, `CH_AI`, `ID_FOLLOWUP=6001`, `ID_DAILY_TIP=6002` | `util/Notif.kt` | entegratör (hazır) |
| `Route.HealthLog`, `Route.Medications`; DeepLink `healthlog`, `medications` | `nav/` | entegratör (hazır) |
| `AppSettings.aiFollowUp/aiDailyTip/doctorAlways/doctorName/doctorPhone` | `data/Models.kt` | entegratör (hazır) |
| `AthleteView` | `feature/sports/AthleteView.kt` | A |

### 8.3 Her köşe yapay zekâya bağlı — kim neyi kullanır

Ana sayfa: `AiInsights.dailyTip` · Egzersiz sonu: `AiInsights.workoutFeedback` + `HealthLog` · Sporlar: canlı veri `AssistantContext.live` + motivasyon · İstatistik: `AiInsights.statsSummary`, `ReportExportCard` ·
Öğünler: `AiInsights.mealSuggestion` · Profil: `AiInsights.healthFlags` (20+ panel) · Asistan: `AiPipeline` (Groq/yerel, web araması, kaynak kutuları) · Bildirimler: `HealthFollowUp`, `DailyTip` ·
Ayarlar: aile hekimi, yapay zekâ anahtarları, sağlık günlüğü, ilaçlar, ZIP yedek · Rapor: `ReportBuilder` (her spor ayrı sayfa).

### 8.4 Zorunlu kurallar

1. **Her yapay zekâ metninin ALTINDA `AiDisclaimer()`** gösterilir (asistan cevapları, günlük ipucu, egzersiz sonu yorumu, istatistik/profil/öğün yapay zekâ kartları): kartın/balonun içinde en altta. Bildirim metinleri hariç.
2. **Tıbbi güvenlik**: teşhis yok, **doz yok**, kesin tedavi/“iyileştirir” iddiası yok; ilaç bilgisi genel amaç düzeyinde. Kırmızı bayrak (göğüs ağrısı, şiddetli nefes darlığı, bayılma, felç/konuşma bozukluğu, şiddetli kanama, bilinç değişikliği…) → acil yönlendirme
   (112 / en yakın sağlık kuruluşu) ve aile hekimi. `settings.doctorAlways` açıkken belirtiler sürerse aile hekimine yönlendirme **vurgulanır**; `doctorName/doctorPhone` varsa adıyla anılır.
3. **Veri gizliliği**: yapay zekâya giden her şey `AssistantContexts.build(ctx)` üzerinden. Groq'a: ad + yaş/cinsiyet/boy/kilo + hastalık/alerji/ilaç adları + egzersiz cevap özeti. **ASLA**: e-posta, konum koordinatı, API anahtarı, cihaz kimliği. Web aramasına kişisel veri koymayın.
4. **Emoji yok** (bilgi tabanı/yapay zekâ metinlerinde de); simge gereken yerde SVG ikon.
5. **Egzersiz kimlikleri**: `Exercises.BREATHING = "breathing"` veya `SportsCatalog` kimliği; ad anahtarı `Exercises.nameKey(id)` (`mind.breath.title` / `sport.<id>`; ilkini H tanımlar).
6. **Büyük tablolar — “Method too large” tuzağı**: bir `object`'in TÜM özellik başlatıcıları tek `<clinit>` metoduna girer, JVM sınırı 64 KB. **Tek `listOf(...)` ≤ 250 satır**; büyük çeviri/veri listelerini `private fun partN(): List<Row> = listOf(...)` işlevlerine bölün
   ve `val rows: List<Row> = part1() + part2() + …` yapın. Aynı kural `MedDb.all`, `FoodDb.all`, KB konuları, soru bankaları için geçerlidir.
7. **WorkManager**: `Worker`/`CoroutineWorker`; `doWork` try/catch, `Result.success()`; benzersiz iş adı; periyodik: `enqueueUniquePeriodicWork(ad, ExistingPeriodicWorkPolicy.UPDATE, …)`; bir kerelik gecikmeli: `enqueueUniqueWork(ad, ExistingWorkPolicy.REPLACE, …)`. Kesin alarm izni yok.
8. **Çeviri anahtarı sanılan sabitler**: nokta içeren tek parça dize (`"abc.def"`), öneki tabloda varsa anahtar sayılır; dosya uzantılıları (`.pdf .zip .json .png …`) test tarafından atlanır. Başka noktalı sabit gerekiyorsa `"a" + "." + "b"` biçiminde yazın.
9. **Metin normallestirme/eşleştirme**: tek yol `TextNorm` (kullanıcı metni, anahtar kelime, arama). Anahtar kelimeler `norm(k) == k` olmalı.
10. **Yeni çeviri önekleri** (tabloyu yalnız sahibi yazar): `ai.*` entegratör · `ins.*` D1a · `aii.*` D1c · `kb.nut_* | kb.fit_* | kb.hlt_*` K1/K2/K3 · `pipe.*` D2b · `assistant.*` D2a · `quiz.*` (+`quiz.notif.*`) Q · `med.<id>`, `medcat.*`, `meds.*` MD ·
    `report.*`, `backup.*` R · `music.err.*` E1 · `music.*` E2 · `mind.*` H (nefes başlığı `mind.breath.title`, kapanış `mind.breath.done`).
11. **Gerçek ölçüm olmayan veri uydurulmaz**: nabız sensörü yok → sahte bpm gösterilmez; nabız yalnız kullanıcının elle girdiği ölçümdür.
12. **İlaçlar**: kimlikler `MedDb` kimliği veya `custom:<serbest ad>`; doz önerilmez; her ilaç metni “doktorunuza/eczacınıza danışın” tonundadır.

### 8.5 Doğrulayıcı (STRICT=1 ile entegrasyonda zorunlu olacak)

`check_i18n.sh` ayrıca denetler: her `SymptomTag` adı; 11 soru bankası × 6–7 soru × 3–5 seçenek (“sorun yok” seçeneği şart, şiddet 0..3, metinler tabloda); her spor için banka; `MedDb.all ≥ 120` ve kimlik/metin tutarlılığı; `KnowledgeBase.topics ≥ 150`, kimlik öneki
(`nut_|fit_|hlt_`), ≥ 8 anahtar kelime ve `TextNorm.norm(k) == k`; `HealthLists` ad anahtarları; `Exercises.nameKey("breathing")`. Kendi alanınızı bu doğrulayıcıyla `STRICT=1` çalıştırarak sınayın (başkalarının eksikleri sizi ilgilendirmez).

## 9. Paylaşılan ekler (entegratör tarafından YAPILDI; tekrar eklemeyin)

`AppSettings` yeni alanlar · `Route.HealthLog/Medications` + DeepLink · `Shell.kt` yeni sayfa dalları · `Notif.CH_AI`, `ID_FOLLOWUP`, `ID_DAILY_TIP`, `post(actionTitle, actionRoute)` · `Startup` → `DailyTip.ensure` · Manifest: FileProvider `${applicationId}.fileprovider`
+ `res/xml/file_paths.xml` (cache `share/`, files `exports/`) + PDF görünürlük sorgusu · `StrAi` (`ai.disclaimer`, `ai.disclaimer.short`, `ai.badge`, `ai.sources`) · `StrSystem` `notif.ch.ai`.
