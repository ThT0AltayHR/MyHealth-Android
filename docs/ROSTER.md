# MyHealth — Modül Görev Tanımları (ROSTER)

Okuma sırası: 1) `docs/CONTRACTS.md` (özellikle §4 ÇÖKMEZLİK ve §8 v2 EKLERİ), 2) bu dosyada **kendi bölümünüz**,
3) `docs/PLAN_TR.md` içinde kendi modülünüzle ilgili **kullanıcının kendi cümleleri** (sesle yazılmış; niyeti okuyun),
4) kendi taslak (stub) dosyalarınız ve `Repo.kt` / `Models.kt` / `SportsCatalog.kt` / `LiveState.kt`.

## Herkes için çalışma düzeni

1. **24 ajan aynı anda** çalışıyor ve makinede 2 CPU var: `kotlinc` ve Chromium sıraya girer (flock). Derlemeyi her dosyada
   değil, mantıksal bir parça bitince yapın (toplam ≤ ~10 derleme). Boşta beklemek yerine bir sonraki dosyayı yazın.
2. Dosyaları **parça parça ve hemen diske** yazın (oturum sınırına çarparsanız ilerleme kaybolmasın). Tek dosya ≤ ~1200
   satır; büyük tabloları/verileri birden çok dosyaya ve `private fun partN()` işlevlerine bölün (§8.5, JVM 64 KB sınırı).
3. **Önce çekirdek** (derlenen, çökmeyen, kullanıcının istediği ana akış), sonra cila. Yarım bırakılmış ekran, `TODO`,
   `PlaceholderScreen`, sahte/uydurma veri, İngilizce kopya çeviri YOK. Kalite çıtası: Google Play'de 1.500 TL'lik uygulama.
4. Başkasının dosyasına dokunmayın; ihtiyaç → final rapor (`PAYLAŞILAN DEĞİŞİKLİK` / `MANIFEST İSTEĞİ`).
5. İmzaları sabit stub'lara uyun; **yeni** public API eklemek serbest (yalnız kendi dosyalarınızda).
6. Android'e bağımlı olmayan mantığı ayrı saf dosyalara koyup `tools/ktrun.sh` ile **gerçekten çalıştırın** (test dosyalarınız
   `scratchpad/jvmtest/` altında, adı modülünüzle başlasın: `EngineTest.kt`, `MedTest.kt`…).
7. Final rapor ≤ 40 satır (CONTRACTS §7). Yapamadığınızı/emin olmadığınızı **dürüstçe** yazın.

Tablo dosyası sahipliği: her ajan yalnızca kendi `core/strings/Str*.kt` dosyasını doldurur (aşağıda belirtilir).

---

## S — Tip denetim koşum takımı (stub harness)  [scratchpad/tools/ — proje dosyalarına dokunmaz]

Amaç: Android SDK/Compose olmadan **tüm projeyi** `kotlinc` ile tip denetleyebilmek. Gerçek derleme yalnız kullanıcının
GitHub Actions'ında olacağı için bu, derleme hatalarını önceden yakalamanın tek yoludur.
* `tools/stubs/` altında kullanılan kütüphanelerin **gerçek imzalara sadık** taslakları: `android.*` (Context, Intent,
  PendingIntent, Service, BroadcastReceiver, AlarmManager, NotificationManager/Channel, LocationManager, MediaStore,
  ConnectivityManager, Bluetooth*, AudioManager, SensorManager, WebView, Geocoder, PdfDocument, BitmapFactory, ImageDecoder, Uri,
  SharedPreferences, Build, Looper/Handler, Log, Settings…), `androidx.core.*`, `androidx.activity.*`, `androidx.lifecycle.*`,
  `androidx.work.*`, `androidx.media3.*` (ExoPlayer, MediaSession(Service), PlayerView, DataSource, ResolvingDataSource…),
  `com.google.android.gms.location.*`, `okhttp3.*`, `kotlinx.coroutines.*` (+flow) ve **Compose** (`runtime`, `ui`,
  `foundation`: Modifier uzantıları, gesture, Canvas/DrawScope, Brush/Color/Path, animasyon API'leri, `AndroidView`,
  LazyColumn/LazyRow/Grid, text; `material3` yalnız tema içinde kullanılan kısım). Stub'lar **fazla serbest olmamalı**
  (gerçekte olmayan overload'a izin vermek yanlış güven verir) ve **fazla katı da olmamalı**.
* `com.myhealth.app.R` sınıfını `app/src/main/res` içeriğinden **otomatik üreten** betik (raw/drawable/font/mipmap/xml/values).
* `tools/typecheck.sh [dosya…]`: stub jar'ını bir kez derler (önbellek), sonra tüm `app/src/main/java` (veya verilen dosyalar +
  bağımlılıkları) tip denetler; hataları `dosya:satır: mesaj` biçiminde verir; `--missing` seçeneği çözülemeyen sembolleri
  sıklıklarıyla listeler (eksik stub mu gerçek hata mı ayırt etmek için). Compose derleyici eklentisi yok: `@Composable`
  yalnızca ek açıklama olarak tanımlanır (composable bağlam denetimi yapılamaz — raporunuza yazın).
* **İlk sürümü hızlı çıkarın** (en çok kullanılan API'ler) ve `tools/TYPECHECK_READY` dosyasını oluşturun; sonra eksik
  stub'larla genişletin. Diğer ajanlar `tools/stubs/` altına gerçek imzayla ekleme yapabilir (çakışmayı önlemek için dosya
  başına tek konu: `stubs/compose_foundation.kt` gibi; mevcut dosyaları silmeyin).
* Bittiğinde **tüm proje üzerinde** çalıştırın, çıkan hataları modül sahibine göre gruplayıp raporunuza yazın (düzeltmeyin).

---

## A — 3B sporcu  [assets/web3d/*, feature/sports/AthleteView.kt]  (tablo yok)

Kullanıcı: altın/siyah, **gerçek insana birebir benzeyen 3B insan**, her spora özgü hareket, bisikletli sporda bisiklet,
aktifken hareket / durunca bekleme duruşu. (three.js yok, hazır model yok: **kendi WebGL1 oluşturucumuzu** yazıyoruz.)
* `assets/web3d/index.html` + klasik `*.js` (ES2017, kütüphane/CDN YOK, her şey assets içinde). WebGL1 (GLSL ES 1.0),
  `alpha:true` şeffaf zemin, `antialias:true`, DPR ≤ 2, `webglcontextlost/restored` işlenir. Hedef: orta seviye telefonda 60 fps
  (≤ ~25k üçgen).
* İnsan: gerçekçi oranlar (≈ 7,5–8 baş boyu; omuz/bel/kalça oranları), yumuşak segmentler (gerilmiş elipsoit/kapsül ağları:
  baş+boyun, göğüs, karın, pelvis, üst kol, ön kol, el, uyluk, baldır, ayak), kas hatlarını imleyen hafif şişkinlikler; **parlak siyah
  “manken” malzeme + altın fresnel kenar ışığı**, altın halka/zemin yansıması, yumuşak blob gölge. FK iskeleti (omurga 3, boyun,
  omuz, dirsek, bilek, kalça, diz, ayak bileği). İnsana benzemiyorsa (kutu/robot gibi) **kabul edilmez**: ekran görüntüleriyle
  yineleyin.
* Hareketler: `data/SportsCatalog.kt` içindeki **tüm benzersiz `motion` kimlikleri** ve **tüm `prop` kimlikleri** (dosyadan
  çıkarın; ~34 hareket: run, walk, cycle, jumprope, rowmachine, stairs, jacks, dance, press, squat, swing, deadlift, pullup,
  pushup, plank, yoga, pilates, stretch, taichi … ve bike, treadmill, rope, rower, stairs, barbell, kettlebell, bar, mat,
  poles …). Her biri gerçekçi döngü animasyonu (faz farklı sinüslerle FK): koşuda karşıt kol-bacak salınımı + öne eğim +
  gövde dönüşü; bisiklette pedal döngüsü + **bisiklet modeli** (tekerlek dönüşü, zincir/pedal); ip atlamada ip yayı; halterde
  gerçek hareket kalıbı. Ayak yere basma kısıtı basit ama kayma yok (bantta/yerde).
* JS API: `window.Athlete.configure({motion, prop})`, `.setActive(bool)`, `.setTempo(0.5..2.0)`, `.freeze()` (döngüyü durdur,
  GPU'yu bırak). Köprü: başlatma/bağlam hatasında `AndroidBridge.onUnavailable()`.
* `AthleteView.kt` (imza sabit): `AndroidView` + `WebView` (JS açık, `file:///android_asset/web3d/index.html`, saydam arka plan,
  donanım katmanı), yalnız `onUnavailable` için `@JavascriptInterface`; parametre değişince `evaluateJavascript` (sayfa
  yüklenmeden gelen çağrıları kuyruğa al); `DisposableEffect`'te WebView'u düzgün yok et (parent'tan çıkar, `loadUrl("about:blank")`,
  `destroy()`); her çağrı try/catch; WebView oluşturulamazsa `onUnavailable()`.
* Doğrulama: `tools/shot.sh` ile (Chromium+SwiftShader) en az 8 hareketin görüntüsü (koşu, bisiklet, halter, yoga, boks,
  ip atlama, yüzme/kürek, ski vb.) → `Read` ile inceleyin, iyileştirin. Rapora görüntü yollarını ekleyin.

## I — İkonlar  [ui/kit/SportIcons.kt, ui/kit/MedIcons.kt, feature/weather/WeatherIcons.kt]  (tablo yok)

* `SportIcons.sport(id)`: **57 spor için tek tek tanınır** 24×24 çizgi (stroke 1.8, yuvarlak uç) SVG yolu; `SportIcons.category(c)`: 8
  kategori. `WxIcons.path(c, isDay)`: 6 hava durumu × gündüz/gece (güneş, bulut, hafif yağmur, sağanak, fırtına, kar; gece ayı), + nem,
  rüzgâr, hissedilen. `MedIcons`: **CADUCEUS (asa üzerinde iki yılan, kanatlı — Sağlık Bakanlığı tarzı tıbbi işaret; küçük boyutta
  da net okunmalı)**, PILL, STETHOSCOPE, DOCUMENT, SHARE, ARCHIVE, LUNGS, SPARKLES + ek ikonlar (HEART_PULSE, DOCTOR, CHAT_AI, …)
  — imzalardaki mevcut özellikleri koruyun, yenilerini ekleyin.
* Yollar `core/SvgPath` ayrıştırıcısıyla çalışmalı (desteklenen komutlar için `core/SvgPath.kt`'yi okuyun; `jvmtest/SvgTest.kt` ile
  doğrulayın). Emoji/harf YOK. `tools/iconsheet.py` ile görsel sayfa üretip `Read` ile inceleyin, yinelemeyle düzeltin.
* Aynı dil ve ağırlık: mevcut `Ic` ikonlarıyla yan yana durunca tutarlı olmalı.

## B — Sporlar  [feature/sports/* (AthleteView.kt hariç)]  tablo: StrSports

Kullanıcı: 57 spor, **her sporun kendi ekranı**: başlat/durdur/bitir, mesafe, koyu+altın harita (çevrimiçi), çevrimdışı mesajı,
3B insan (altın/siyah) spora özgü hareketle, küçültünce yuvarlak baloncuk, son çekilen hava durumu kartı, süre seçimi, motivasyon
mesajları (“güzel çok iyi gidiyorsun”).
* `SportsScreen` (sekme): 8 kategori çipi + arama + 57 sporluk premium cam kart ızgarası/listesi (`SportIcons`), son yapılan,
  “Geçmiş” (`Route.SportHistory`); sporu seç → `nav.push(Route.SportLive(id))`.
* `SportLiveScreen(sportId)`: spor adı/kategori; **süre hedefi** (serbest / 10-20-30-45-60-90 dk, ilerleme halkası); büyük sayaç;
  mesafe (GPS'li sporlar), kcal (`logic/Body` MET×kilo×süre), hız/tempo; GPS'li sporlarda `RouteMap(route, current)` (koyu+altın;
  izin/internet yoksa harita kendi `map.unavailable` mesajını gösterir), diğerlerinde ve “3B” sekmesinde `AthleteView`
  (`onUnavailable` → ikon illüstrasyonu); Başlat/Duraklat/Devam/Bitir (bitirmede onay); **küçült** → yuvarlak baloncuk;
  son çekilen **hava kartı** (`Repo.weather`, çevrimdışıyken de “son güncelleme” ile); aralıklı **motivasyon mesajları** (≥ 24
  çeşit, 6 dil, kullanıcı adıyla). **Sahte nabız YOK** (`WorkoutState.bpm` sensör yoksa 0 kalır, arayüzde gösterilmez).
* **Bitiş**: `WorkoutRecord` kaydı (`Repo`), sonra tam ekran `ExerciseFinishFlow(exerciseId = sportId, durationSec, summaryLines,
  onDone)` (Q'nun bileşeni; imza sabit) → `onDone` ile sayfa kapanır. Kayıt, akış gösterilmeden ÖNCE yazılır (çökme/çıkış kaybı yok).
* `WorkoutSession` (tekil): durum `StateFlow<WorkoutState>`, rota noktaları, konum filtresi (doğruluk > 35 m at, min. yer değiştirme,
  hız sınırı), haversine mesafe, duraklatma, süreci düzenli `kv`'ye yedekleme (süreç ölümünden sonra “devam et?” sorusu);
  `WorkoutService` (FGS, tür `location`; 5 sn içinde `startForeground`; izin yoksa hizmeti başlatmayın, yalnız süre/kcal tutun).
  Konum `android.location.LocationManager` (GMS yok): GPS+NETWORK, izinler denetlenir.
* `SportBubbleHost` (Shell'in üst katmanı): canlı antrenman varken ve canlı ekran kapalıyken sürüklenebilir yuvarlak baloncuk
  (ikon + süre), dokununca `Route.SportLive`. `SportHistoryScreen`: tarihe göre gruplu kayıtlar, kayıt ayrıntısı (etkileşimli
  `RouteMap`), spora göre süzme, silme, toplamlar.
* Yapay zekâ: canlı veri `AssistantContext.live` üzerinden zaten asistana gider; bitişte yorum Q'nun akışında
  (`AiInsights.workoutFeedback`).
* StrSports: 57 `sport.<id>` + 8 `cat.<id>` + arayüz + motivasyon (tablo > 250 satır → `partN()`).

## M — Harita  [feature/map/*]  tablo: StrMap

`RouteMap` (imza sabit) — **kendi yazdığımız Compose karo haritası**: slippy-map matematiği saf dosyada (`MapMath.kt`: enlem/boylam↔karo px,
zoom sığdırma; `ktrun` ile test), OSM karoları (`Repo.kvGet("map_tile_url")` ile değiştirilebilir, varsayılan
`https://tile.openstreetmap.org/{z}/{x}/{y}.png`; geçerli `User-Agent`, ≤ 2 eşzamanlı indirme, ek yük yok), diskte
(`cacheDir/tiles`, ≤ 80 MB LRU) + bellekte (`LruCache`) önbellek, **koyu + altın görünüm** (ColorMatrix: ters çevir + altın ton;
yollar altın, zemin siyah), rota çizgisi (altın parıltılı), anlık konum işareti (nabız atan turuncu nokta + doğruluk halkası),
`follow`, `interactive` (sürükle/sıkıştır `detectTransformGestures`, çift dokunma, +/− düğmeleri, konuma dön), `onStatus`.
İnternet/konum/karo yoksa harita yerine **`t("map.unavailable")`** (TR birebir: “harita bilgisi alınamadı lütfen konumunuzu
etkinleştirdiğinizden veya internet bağlantınızın açık olduğundan emin olun”) cam kartta; daha önce indirilen karolar çevrimdışıyken
gösterilir. Atıf “© OpenStreetMap contributors” haritada küçük. Ağ/çizim ana thread dışında; bellek sızıntısı yok.

## C — Hava durumu  [feature/weather/* (WeatherIcons.kt hariç)]  tablo: StrWeather

* `WeatherRepo`: Open-Meteo (anahtarsız) güncel + 7 gün + saatlik yağış; WMO kodu → 6 durum (`WeatherCondition`); şehir adı: Geocoder
  (API 33+ asenkron dinleyici, eski sürümde IO) → BigDataCloud ters geocode → son çare “Konum”; **manuel şehir arama**
  (Open-Meteo geocoding), seçilen şehir `kv`'de; izin/konum yoksa onu kullanır. Son başarılı veri `Repo.weather/forecast` içinde;
  çevrimdışıyken “son güncelleme” ile gösterilir.
* `WeatherScheduler.reschedule(ctx)`: WorkManager periyodik (ayar `weatherEveryMin`, en az 15 dk; kapalıysa iptal) → veriyi çek,
  bildirim (`Notif.CH_WEATHER`, `ID_WEATHER`, route `weather`): “İstanbul 18°, parçalı bulutlu”; yakın saatlerde yağış olasılığı yüksekse
  **yağmur uyarısı**. Metinler `tr()` ile.
* `WeatherScreen`: **6 döngülü video** (`R.raw.weather_*`) ExoPlayer `PlayerView` (ZOOM, sessiz, `REPEAT_MODE_ONE`, kontrolsüz),
  **hafif karartma** katmanı altında; hafif yağmurda arayüz “yağmur camın arkasında” hissi (buzlu cam kartlar); şehir adı **en altta büyük**,
  büyük sıcaklık, durum adı, hissedilen/nem/rüzgâr (`WxIcons`), saatlik şerit, **haftalık tablo**, yağmur uyarı paneli, şehir seçici,
  yenile; durum değişince videolar çapraz geçiş; oynatıcı `DisposableEffect`/yaşam döngüsüyle bırakılır; video açılamazsa degrade zemin.
* `DynamicIslandHost`: üstte pill; kapalıyken nabız atan nokta (**yeşil=çevrimiçi / kırmızı=çevrimdışı**; `ConnectivityManager`
  NetworkCallback `DisposableEffect`'te kaydolup çıkarılır) + `island.hint` ipucu (“hava durumu bilgilerini görüntülemek için
  tıklayınız”, birkaç sn sonra kompakt hava özetine döner); **dokununca** hava kartına genişler (yay animasyonu), **tekrar dokununca**
  küçülür; ayrıntı → `Route.Weather`. Çevrimdışıyken son veriyi kullanır.

## D1a — Yerel yapay zekâ motoru  [feature/assistant/{LocalEngine,TopicGuard}.kt, feature/assistant/engine/*]  tablo: StrInsights (ins.*)

Kullanıcı: “merhaba deyince merhaba bugün sana nasıl yardımcı olabilirim… basit bir model olmasın, uygulama 300 MB olsa da olur, devasa”.
**DÜRÜSTLÜK**: cihaz içinde gerçek nöral LLM mümkün değil (bağımlılık/ağırlık yok); bu, çok büyük **kural + bilgi tabanlı çok dilli motor**
olacak; gerçek LLM Groq ile gelir. Saf Kotlin (Android importu YOK; JVM'de test edilir).
* Sabit API (stub'a bakın): `LocalEngine.analyze(ac, history, userText): Analysis`, `LocalEngine.reply(ac, history, userText): LocalReply`
  (+ 2 parametreli kolaylık). `Analysis(intent, topicId, thinkLabel, searchQuery, urgent, onTopic)`: `thinkLabel` **soruya özel, yerelleştirilmiş**
  düşünme etiketi (“Sana en uygun bitki çayı seçiliyor...”, “Baş ağrına iyi gelen meyveler araştırılıyor...”); `searchQuery` internette
  aranacak kısa konu sorgusu (kişisel veri YOK; arama gerekmiyorsa boş).
* Niyetler: selam/hal-hatır/teşekkür/kimlik/yardım, ad (“adın ne”, kullanıcı adıyla hitap), “şunu hatırla …” (`learned`), uygulama verileri
  (bugünkü adım/kcal/su, son antrenmanlar, haftalık özet, hava, **egzersiz sonu sağlık cevapları**: “nefesim son günlerde nasıldı?”,
  **canlı antrenman**: “şu an kaç dakikadır koşuyorum?”), hesaplayıcılar (`Calc.kt`: BMI+sınıf, Mifflin-St Jeor BMR, TDEE, su ihtiyacı, protein,
  ideal kilo, Tanaka maks. nabız + bölgeler, tempo/hız, MET kalori, makro dağılımı, 1RM), yemek (`FoodDb.search`: “pilav kaç kalori”,
  karşılaştırma), **ilaç** (`MedDb.search`: marka/etken → amaç `med.<id>`, **doz VERİLMEZ**, `MedDb.warnings` ile kişiye özel uyarı,
  aile hekimi/eczacı yönlendirmesi), **bilgi tabanı** (`KnowledgeBase.topics`: `TextNorm` ile anahtar kelime puanlama, kullanıcının
  hastalık/alerjisiyle ilişkili konular öncelikli; cevapta alerji/hastalık çatışması varsa açık uyarı), belirti sınıflandırma (**kırmızı
  bayrak** → acil: 112/en yakın sağlık kuruluşu + aile hekimi), plan istekleri (haftalık antrenman/öğün planı: profil+`FoodDb`+hedef),
  motivasyon, sohbet. Çok turlu bağlam: `history`'den son konu/niyet (“peki ya akşam?”).
* `settings.doctorAlways` → yanıtlarda belirtiler sürerse/ağırlaşırsa aile hekimine yönlendirme **vurgusu** (`ac.doctorName/doctorPhone`
  varsa adıyla). Asla teşhis koyma; “ben doktor değilim, bilgi veriyorum” tonu; bol çeşitli, doğal, sıcak Türkçe/İngilizce/Rusça/Arapça/
  Almanca/Korece (şablon tekrarını azaltmak için her niyet için ≥ 4 çeşit cümle kalıbı).
* `TopicGuard.isOnTopic(text)`: sağlık/beslenme/diyet/spor + uygulama özellikleri + sohbet kalıpları serbest; politika, finans/kripto, kodlama,
  magazin, genel kültür vb. **konu dışı** → zorunlu birebir mesaj (PLAN/CONTRACTS §5, `LocalReply(onTopic=false)`). 6 dilde ≥ 80 test cümlesi.
* Dönen metinler `I18n.fmt(ac.lang, "ins.…")` ile (StrInsights); **emoji yok**; uzun tablo `partN()`.
* Testler: `jvmtest/EngineTest.kt` (en az: 6 dilde selam, hesaplayıcılar, konu dışı, kırmızı bayrak, ilaç uyarısı, KB eşleşme, bellek).
  Gerçekten çalıştırıp sonuçları raporlayın.

## D1c — Uygulama geneli yapay zekâ yorumları  [feature/assistant/AiInsights.kt, feature/assistant/insights/*]  tablo: StrAiInsights (aii.*)

`AiInsights` (imza sabit): `dailyTip`, `workoutFeedback`, `followUpMessage`, `statsSummary`, `mealSuggestion`, `healthFlags` (+ `HealthFlag`).
Hepsi anlık, çevrimdışı çalışır, **ad + profil + hastalık/alerji + ilaç + egzersiz sonu cevapları + spor geçmişi + hava + oruç** bilgisini kullanır.
Saf Kotlin (Android importu yok), çıktı `ac.lang` dilinde, emoji yok.
* `dailyTip`: gün/saat/hava/profil/sağlık bayraklarına göre ≥ 40 farklı kişisel öneri (aynı gün aynı metin; ertesi gün farklı).
* `workoutFeedback(ac, entry, record)`: süre/mesafe/kcal + cevaplara göre yorum; belirti varsa dinlenme/hidrasyon/bitki çayı gibi **güvenli** öneriler,
  şiddet ≥ 2 ise “10-15 dk sonra sana yazacağım / aile hekimine danış” notu; kayıt yoksa (null) genel.
* `followUpMessage(ac, entry)`: **kullanıcının tarif ettiği ilk mesaj**: “{ad}, {tarih/saat}'te {egzersiz} sırasında {belirti} yaşadığını gördüm. İstersen biraz
  sohbet edebiliriz; sana bir bitki çayı önerebilirim, duruma göre seni aile hekimine yönlendirebilirim” (belirtiye/şiddete/geçmişe göre uyarlanır;
  önceki günlerle karşılaştırma: “dün de daralmıştı / bugün biraz daha rahat”).
* `statsSummary`: haftalık değerlendirme (adım, kcal, antrenman, belirti eğilimi, su). `mealSuggestion(ac, mealTypeId)`: hedef kcal, alerji/hastalık,
  oruç (Ramazan) ve `FoodDb`'ye göre somut yemek önerileri. `healthFlags`: **≥ 20 kural türü** (tekrarlayan nefes darlığı, baş dönmesi, göğüs ağrısı=seviye 3,
  eklem ağrısı, çarpıntı, aşırı yorgunluk, BMI sınıfı, düşük su/adım, hastalık+yoğun egzersiz uyumsuzluğu, `MedDb.warnings`, gebelik uyarıları…).
* Aralarında paylaşılan yardımcılar `insights/*` altında. Test: `jvmtest/InsightsTest.kt`.

## K1 / K2 / K3 — Bilgi tabanı içeriği  [feature/assistant/kb/Kb<Nutrition|Fitness|Health>*.kt]  tablolar: StrKbNutrition / StrKbFitness / StrKbHealth

Üç ajan **ayrı konu alanlarını** yazar; kimlikler `nut_` (K1), `fit_` (K2), `hlt_` (K3) ile başlar. Her `KbTopic`:
* `keywords`: **≥ 8**, TR/EN/RU/AR/DE/KO için **dil başına ≥ 2** kök (tek kelime kökleri ağırlıklı; çok kelimeli ifadeler de olabilir); hepsi
  `TextNorm.norm(k) == k` olmalı (küçük harf, aksansız Latin, Kiril е, Arapça harekesiz/“ال”siz) — JVM testi denetler. Eşleşme: kelime BAŞINDA
  önek (`TextNorm.hasKeyword`); çok genel kökler (ör. “ag”) YASAK.
* `answerKey = kb.<id>.a`: 6 dilde **400–1100 karakter**, doğal ve sıcak, pratik: ne olduğu → somut öneriler (3–6 cümle) → ne zaman doktora gidilmeli
  (konuya özgü kırmızı bayraklar). `{0}` = kullanıcı adı (en fazla bir kez, tüm dillerde aynı yer tutucu). **Doz YOK**, teşhis YOK, kesin tedavi/“iyileştirir” iddiası YOK;
  gebelik/alerji/ilaç etkileşimi uyarıları gereken yerde (ör. meyan kökü + tansiyon, sarı kantaron + ilaçlar, papatya + Asteraceae alerjisi). Kaynaklar WHO/NHS/Mayo/
  Diyanet İşleri değil **genel kabul görmüş** bilgi düzeyi. Emoji/sembol süsü YOK.
* `thinkKey = kb.<id>.t`: ≤ 60 karakter, soruya özel “düşünme” cümlesi, sonu “...” (ör. “Sana uygun bitki çayı seçenekleri değerlendiriliyor...”).
* `relatedConditions` (HealthLists kimlikleri) ve `priority` doldurun.
* Dosyaları ~12 konuluk parçalara bölün (`KbNutritionA.kt`…); `object KbNutrition { val topics = partA() + partB() … }`; tablo da `private fun partN()` ile
  bölünür (§8.5). Çeviri kalitesi: Arapça/Rusça/Korece/Almanca **gerçekten çevrilmiş** (kopya yok).
* **K1 (≥ 55) beslenme**: su/hidrasyon, protein/karbonhidrat/yağ/lif/şeker/tuz/kafein, kahvaltı/atıştırmalık/öğün zamanlaması, aralıklı oruç, **Ramazan
  beslenmesi (sahur/iftar)**, antrenman öncesi/sonrası, kilo verme/alma, kalori açığı, diyetler (Akdeniz, keto, vegan, vejetaryen, düşük karbonhidrat, DASH, glütensiz,
  laktozsuz), vitamin/mineral (D, B12, C, demir, magnezyum, çinko, omega-3, kalsiyum, potasyum), takviyeler (kreatin, protein tozu), **bitki çayları (baş ağrısı,
  mide, uyku, soğuk algınlığı, stres, bulantı, sindirim, şişkinlik, bağışıklık, öksürük, adet krampı — ayrı konular)**, baş ağrısına/soğuk algınlığına/enerjiye iyi
  gelen meyve ve besinler, demir eksikliği, tansiyon/kolesterol/diyabet dostu beslenme, bağırsak sağlığı/probiyotik, reflü/kabızlık/şişkinlik beslenmesi, çocuk/yaşlı/gebelik
  beslenmesi, etiket okuma, porsiyon, bilinçli/duygusal yeme, antienflamatuvar/bağışıklık/cilt/kemik/göz/beyin besinleri, yağlı karaciğer/gut/böbrek notları.
* **K2 (≥ 50) spor ve zihin**: ısınma/soğuma/esneme, yeni başlayanlar planı, koşu/5K/maraton temelleri, interval/HIIT, bisiklet/yüzme teknik ve güvenlik, yürüyüş/10 bin adım,
  kuvvet temelleri/aşamalı yüklenme/vücut ağırlığı/core, duruş/bel/diz/omuz/boyun, masa başı, yoga/pilates/tai chi/denge, nabız bölgeleri/RPE, toparlanma/dinlenme günü/DOMS/aşırı
  antrenman, sakatlık önleme/RICE, **uyku hijyeni**, şekerleme, stres yönetimi, **nefes teknikleri (kutu, 4-7-8, diyafram)**, meditasyon, kaygı için öz yardım, motivasyon/alışkanlık/hedef/plato,
  evde antrenman, sıcak/soğukta egzersiz, egzersiz sırasında su, Ramazan'da egzersiz, astım/diyabet/hipertansiyon/gebelik/yaşlı/çocukla egzersiz, kas krampı, yan ağrısı, koşarken nefes,
  **egzersiz sonrası nefes darlığı / baş dönmesi / çarpıntı / baş ağrısı: normal mi uyarı mı**, ayakkabı/ekipman, açık hava güvenliği.
* **K3 (≥ 55) sağlık**: baş ağrısı, migren, ateş, soğuk algınlığı, grip, öksürük, boğaz ağrısı, alerjik nezle, sinüzit, mide ağrısı, bulantı/kusma, ishal, kabızlık, gaz/şişkinlik, reflü, gastrit, IBS,
  idrar yolu belirtileri, bel/boyun/diz/eklem/kas ağrısı, burkulma/gerilme, kırık belirtileri, tendinit, baş dönmesi, bayılma, çarpıntı, **göğüs ağrısı (kırmızı bayrak)**, yüksek/düşük tansiyon, kolesterol,
  diyabet temelleri/hipo-hiperglisemi, tiroid, anemi, D vitamini eksikliği, astım/nefes darlığı, alerjiler, cilt (egzama/akne/güneş yanığı), böcek ısırığı, yanık/kesik/burun kanaması/boğulma ilk yardım,
  sıcak çarpması/hipotermi/dehidrasyon, temel ilk yardım ve **112 ne zaman aranır**, göz yorgunluğu, kulak ağrısı, diş/ağız sağlığı, uykusuzluk, kaygı/depresyon farkındalığı, adet krampı,
  gebelikte temel dikkatler, çocuklarda ateş/öksürük (çocuk doktoru), yaşlıda düşme önleme, **ilaç güvenliği** (karıştırma, antibiyotik kullanımı, parasetamol/ibuprofen farkı DOZSUZ, saklama, alkol,
  gebelik), aile hekimine hazırlık (ne anlatmalı), **uygulamadaki PDF raporu doktorla nasıl kullanırım**, seyahat sağlığı/aşı temelleri.

## D2a — Asistan arayüzü  [feature/assistant/{AssistantScreen,ChatStore,…UI}.kt]  tablo: StrAssistant

* **Çoklu sohbet**: `ChatStore` (`Repo.kv`: `chats_index` + `chat_<id>`): kimlik, başlık (ilk mesajdan otomatik), oluşturma/güncelleme, **sabitleme**, mesajlar, **sohbete özel bellek**
  (o sohbette öğrenilen olgular; `ac.memory` yalnız o sohbetinki; “açıldığı andan itibaren”). Eski `Repo.chat/Repo.memory` verisi ilk açılışta ilk sohbete **taşınır**. **Geçmiş sohbetler** listesi
  (sabitlenenler üstte), sabitle/çöz, **sil (onaylı)**, yeni sohbet. Yedekleme ZIP'i `chats_index`/`chat_*` anahtarlarını R ajanı `Repo` kv taramasıyla alır — biçimi sade JSON tutun.
* Mesaj baloncukları (kullanıcı: altın degrade, asistan: cam); **her asistan cevabının altında `AiDisclaimer()`** (tam metin); kaynak kutuları (`SourceCard`: site adı, başlık, kısaltılmış URL, küçük görsel/harf
  simgesi; dokununca `ShareHelper.openUrl`) cevabın en altında “Kaynaklar” başlığıyla; uzun basınca kopyala.
* **Düşünme arayüzü**: cevap gelene kadar asistan tarafında `PipelineEvent.Step.label` metni **“...” yerine**, **soluk beyaz yazı + üzerinden sürekli geçen parlaklık dalgası** (shimmer; `rememberInfiniteTransition`
  + `TextStyle(brush=…)`/`drawWithContent` ile; ~1,4 sn döngü), adım değişince çapraz geçiş; durdur düğmesi (iptal). Düşünme etiketleri soruya özeldir (D2b/D1a üretir).
* Mod anahtarı (Yerel | Groq) sohbet başlığında (`AppSettings.aiMode`); Groq seçili ve anahtar yoksa nazik uyarı + ayarlara gitme. Groq gecikme/anahtar hatası bildirimi (`Final.notice`) ayrı vurgulu balonda.
* **Tohum**: ekran açılışında ve `OnResume`'da `AssistantSeed.take()` → yeni sohbet + ilk mesajı **asistan yazar** (takip bildiriminden gelen); sohbet başlığı `assistant.followup_title`.
* Çalıştırma: `AssistantContexts.build(ctx)` (arka planda) + `AiPipeline.run(ctx, history, text, ac)`; akış ekran dışına çıkılınca da bitsin diye kendi `CoroutineScope(SupervisorJob()+Dispatchers.Main)` taşıyan bir
  `AssistantRuntime` nesnesi (sohbet kalıcı kaydedilir, geri dönünce görünür). Boş sohbette karşılama (“Merhaba {ad}”) + dönen **öneri çipleri** (≥ 12: “Bugün ne yemeliyim?”, “Başım ağrıyor”, “Nefes egzersizi öner”…).
* Klavye (`imePadding`), gönder düğmesi, boş/hata durumları, RTL doğru; liste `LazyColumn` + kararlı anahtarlar; ağır işi ana thread'de yapma.

## D2b — Yapay zekâ boru hattı  [feature/assistant/{AiPipeline,GroqClient,WebSearch}.kt, feature/assistant/pipeline/*]  tablo: StrPipeline (pipe.*)

Kullanıcı: “soruyu alacak, ölçüp biçip tartacak, sonra cevap verecek; **düşünme adımlarını gerçekten yap**, ilk taslak direkt teslim edilmesin; internet varsa soruyu hazırlayıp internette arayacak, en olumlu
sonucu bulup altta **kaynak kutusu** olarak gösterecek; yerel modda internet yoksa “üzgünüm web'de arama yapamıyorum fakat şu ana kadar bildiklerimden sana şunu önerebilirim” diyecek”.
`AiPipeline.run(ctx, history, userText, ac): Flow<PipelineEvent>` (imza sabit; akış **her zaman** `Final` ile biter, istisna fırlatmaz, iptal edilebilir):
1. **Analiz**: `LocalEngine.analyze` → `Step(thinkLabel)`; konu dışıysa ağ kullanmadan `Final(onTopic=false mesajı, "local")`. Kırmızı bayrak (`urgent`) → yanıt başına acil uyarı.
2. **Arama** (çevrimiçi ve `searchQuery` doluysa): `WebSearch.search(ctx, query, lang): List<SourceCard>` — Wikipedia (uygulama dilinde + İngilizce yedek) ve DuckDuckGo Instant Answer; alaka süzgeci; en iyi ≤ 3 sonuç; özet + küçük görsel
   (Wikipedia `pageimages`); toplam ≤ 8 sn; hata → boş. Sorguda **kişisel veri YOK**. `Step(pipe.step.search)`.
3. **Taslak**: Groq (anahtar+mod varsa; çevrimdışı/gecikmede yerel `LocalEngine.reply`). Sistem istemi: kimlik (MyHealth asistanı), kapsam (yalnız sağlık/beslenme/diyet/spor; konu dışı → **birebir zorunlu cümle**), teşhis/doz yasağı,
   kullanıcının adı, profil + hastalık/alerji/ilaç + son egzersiz cevapları + bellek (**kimlik/e-posta/koordinat/API anahtarı ASLA gönderilmez**; bağlam ≤ ~1500 token), aile hekimi vurgusu (`doctorAlways`), kısa-sıcak-yapılandırılmış üslup,
   emoji yok, cevap dili = uygulama dili, arama özetleri “referans” olarak. `Step(pipe.step.draft)`.
4. **Denetim**: ikinci çağrı: “tıbbi güvenlik editörü” — taslağı konu/teşhis/doz/kullanıcı hastalık-alerji-ilaç çelişkisi/soruyla uyum/dil açısından kontrol edip **düzeltilmiş nihai cevabı** döndürür; çağrı başarısızsa taslak + yerel güvenlik son işleme.
   `Step(pipe.step.review)`. Yerel yolda da analiz→taslak→denetim (yerel son işleme: çelişki/güvenlik/aile hekimi satırı) uygulanır.
* **Yerel mod + çevrimdışı + bilgi niteliğinde soru** (kb/semptom/yemek/ilaç): cevabın başına `pipe.offline_search` (“üzgünüm web'de arama yapamıyorum fakat şu ana kadar bildiklerimden sana şunu önerebilirim:”). Selam/veri cevaplarına eklenmez.
* **Groq hata sınıflandırması** (saf dosya, JVM testli): 401/403 → anahtar hatası bildirimi; 429/5xx/zaman aşımı/ağ hatası → **birebir gecikme mesajı** (TR: “üzgünüm şu anda yanıtlarım gecikti lokal modele geçiyorum yanıtlar tazelendiğinde tekrardan otomatik olarak geçeceğim”, diğer 5 dile doğru çeviri) +
  soğuma süresi (`kv`, ör. 90 sn; `Retry-After` varsa o); bildirim yalnız geçişte bir kez; soğuma bitince bir sonraki mesaj **otomatik Groq'a döner**. `model_decommissioned` (400) → sıradaki model. Modeller: `llama-3.3-70b-versatile`, sonra `llama-3.1-8b-instant`
  (liste tek yerde, kolay güncellenir). `https://api.groq.com/openai/v1/chat/completions`, OkHttp (`Net.http`), `Authorization: Bearer`.
* Saf dosyalar (`GroqProtocol.kt`: istek JSON'u kur, yanıt/hata ayrıştır; `PipelineRules.kt`) için `jvmtest/PipelineTest.kt`. Ağ çağrıları Android'de ama mantık saf.

## E1 — Müzik motoru  [feature/music/{MusicApi,PlayerHolder,MusicService,…}.kt]  tablo: StrMusicEngine (music.err.*)

`MusicApi.kt` imzaları sabit. Gövdeler: tek paylaşılan `ExoPlayer` (`PlayerHolder`; `AudioAttributes(CONTENT_TYPE_MUSIC, USAGE_MEDIA)`, `setHandleAudioBecomingNoisy(true)`, `WAKE_MODE_NETWORK`), `MusicService` (`MediaSessionService`: bildirim,
kilit ekranı ve Bluetooth kontrolleri; `Notif.CH_MUSIC` kanalı, `ic_stat_heart`; `MediaController`/Guava kullanma), arayüzden `startService` try/catch (Media3 ön plana alır). `MusicLibrary`: `MediaStore.Audio` taraması (IS_MUSIC / süre > 30 sn), `ContentObserver` ile
**canlı yeniden tarama** (`startWatching/stopWatching`), izin yoksa `permissionMissing`. `BluetoothMonitor`: `AudioDeviceCallback` (A2DP/BLE çıkış cihazı + `productName`) + ACL yayınları + adaptör durumu; izin gerektirenler `BLUETOOTH_CONNECT` kontrolüyle; durumlar UNSUPPORTED/NO_PERMISSION/OFF/DISCONNECTED/CONNECTED.
`OnlineMusic.search`: **Audius** (varsayılan, yasal; `https://api.audius.co` ana bilgisayar listesi → `/v1/tracks/search?query=…&app_name=MyHealth`; akış `/v1/tracks/{id}/stream`) ve **“YouTube tarzı” deneysel kaynak** (Piped uyumlu örnek listesi: `search?q=…&filter=music_songs` + `streams/{id}` en iyi ses akışı; örnekler kararsızdır → hata
yönetimi, birden çok örneği sırayla dene; `Repo.settings.musicSource` = `audius|youtube`). Çevrimiçi parçalar kuyruğa `o:<kaynak>:<id>` ile girer, **gerçek akış URL'si çalma anında** `ResolvingDataSource` ile çözülür (süresi dolan URL saklanmaz). Hata kodları (`offline, unplayable, no_permission, unavailable,
empty_query, failed`) → `music.err.<kod>` metinleri. Bellek/Player sızıntısı yok; servis ölse de uygulama çökmez. Saf mantık (sıralama, shuffle, repeat, kuyruk) ayrı dosyada test edilir.

## E2 — Müzik arayüzü  [feature/music/{MusicScreen,NowPlayingScreen,MiniPlayerHost}.kt + yeni UI dosyaları]  tablo: StrMusic

`ref5_music_app_pill_nav_lists.png` tarzı. `MusicScreen` (sekme): Kütüphane / Çalma listeleri / Çevrimiçi (SegmentedTabs), arama (yerel süz; çevrimiçi gönderince ara), “Karıştır” düğmesi, parça satırları (kapak/degrade yer tutucu, ad, sanatçı, süre, çalıyor göstergesi),
**çalma listeleri** (`Repo.playlists` CRUD, listeye ekle sayfası, uzun basma menüsü: sıradaki/kuyruğa ekle/kaldır), izin kartı (`permissionMissing`: istek + ayarlar), **yuvarlak Bluetooth paneli**: bağlı değil → **kırmızı yanıp söner**, bağlı → **yeşil**, cihaz adı kayan yazı (`MarqueeText`),
dokununca izin iste/Bluetooth ayarlarını aç; `OnResume { rescan }` + elle yeniden tara; çevrimiçi sekmede kaynak seçici ve hata durumları (`music.err.*`). `MiniPlayerHost`: çalarken alt gezinmenin üstünde mini kontrol (kapak, kayan başlık, oynat/duraklat, sonraki), dokununca `Route.NowPlaying`;
**nefes ve spor ekranlarında gösterilmez** (Shell `allowMini` bunu sağlar). `NowPlayingScreen`: büyük kapak, arama çubuğu (`LuxSlider`), önceki/oynat/sonraki, karıştır/tekrar, kuyruk. Konum (`positionMs`) kompozisyonda değil `LaunchedEffect` ile güncellenir.

## F — Alarm + Oruç  [feature/alarm/*, feature/fasting/*]  tablolar: StrAlarm, StrFasting

**Klasik alarm**: `AlarmScheduler` (`AlarmManager.setAlarmClock(AlarmClockInfo(...))`; tekrar günleri için sonraki tetik hesabı; tek seferlik alarm çalınca kapanır), `AlarmReceiver` → `AlarmRingService` (`startForegroundService` try/catch; FGS `mediaPlayback`; varsayılan alarm sesi `RingtoneManager.TYPE_ALARM`
döngüde + titreşim; tam ekran bildirim → `AlarmRingActivity`), `AlarmActionReceiver` (ertele 5/10 dk, kapat), `BootReceiver` (yeniden kur), `AlarmRingActivity` (kilit ekranında, ekranı açar, büyük saat, kaydırarak kapat `GoControl`, ertele), servis başlatılamazsa **yedek**: tam ekran niyetli bildirim.
`AlarmsScreen` (büyük saatler, anahtarlar, tekrar etiketleri `day.*`, ekle), `AlarmEditScreen(id)` (saat çarkı, tekrar günleri, etiket, titreşim, sil). **İzin ekranı/kartı**: bildirim izni, tam ekran niyet (Android 14 `canUseFullScreenIntent`), pil/otomatik başlatma (HyperOS yardımcıları `Perms`), açıklamalı.
**Oruç**: `FastingPlan.today(ctx)` (`FastingInfo`; Ramazan tespiti Hicri takvimle `logic/Prayer`; **dünya çapında konuma dayalı** imsak/iftar: `Repo.lastLocation` yoksa kayıtlı şehir/manuel; kutup yedeği), `FastingScheduler.rescheduleAll` (Ramazan'da sahur = imsak − `sahurOffsetMin`, iftar = akşam vakti için `setAlarmClock` alarmları; ayarlar: `fastingAuto`, `fastingForce` (Ramazan dışı elle oruç), `sahurAlarm`, `iftarAlarm`
— **ayarlardan kapatılabilir**; çalınca alarm sesi/bildirim `CH_FASTING`), `FastingScreen` (bugünün imsak/iftar saatleri, geri sayım, Ramazan günü, konum kaynağı + manuel şehir, anahtarlar). Saf hesap kodu `logic/Prayer.kt` doğrulanmıştır — yeniden yazmayın, kullanın.

## ML — Öğünler  [feature/meals/*]  tablolar: StrMeals, StrFoods

`FoodDb` (imza sabit, **saf dosya**): **≥ 300 gıda**, 18 kategori, Türk mutfağı ağırlıklı + uluslararası; porsiyon başına makrolar gerçekçi (kcal ≈ 4P+4C+9F; doğrulayıcı uyarır); `search` aksan/ büyük-küçük harf duyarsız (`TextNorm`), marka/yaygın ad. `StrFoods`: `food.<id>` (6 dil) + `foodcat.<id>`
(`partN()` ile bölün). `MealsScreen`: kahvaltı/öğle/akşam (+ara öğün; **Ramazan'da sahur/iftar**), günlük toplam kcal/makro halkası (`logic/Body` hedefi), yemek ekleme (arama, kategori çipleri, porsiyon adımlayıcı/gram, özel giriş), silme, **su takibi** (bardak), gün gezintisi, haftalık mini grafik,
her öğün için **`AiInsights.mealSuggestion` kartı + `AiDisclaimer`**. `MealReminders.rescheduleAll(ctx)` (3 günlük hatırlatma, ayardaki saatler; **kesin alarm izni yok**: `setAndAllowWhileIdle`/WorkManager; Ramazan'da öğle atlanır/sahur-iftar'a uyar; `Notif.CH_MEAL`, route `meals`), `MealReminderReceiver` hızlı döner.

## ST — İstatistik + adım + notlar  [feature/stats/*, feature/steps/*, feature/notes/*]  tablolar: StrStats, StrNotes

* `StepTracker` (TYPE_STEP_COUNTER taban değeri `kv`, gün sıfırlama, yeniden başlatma `onBoot`, `refresh(ctx)` tek seferlik okuma + zaman aşımı; `Repo.steps`; izin yoksa nazik durum), `ActivityDetection.sync` + `ActivityTransitionReceiver`: Play Services **Activity Transition** (WALKING/RUNNING/ON_BICYCLE ENTER;
  PendingIntent `FLAG_MUTABLE`) → bildirim (`CH_ACTIVITY`, `ID_ACTIVITY`): “Yürüyüşe başladığını fark ettim, kaydetmek ister misin? Son dakikalarda yaklaşık 750 m yürüdün” (**mesafe**: adım farkı × boy temelli adım uzunluğu; bisiklette adım yok → mesafesiz metin); dokununca `sport:walk|run|cycling`; en az 30 dk aralık, `activityNotif` ayarına saygı;
  Play Services/geçişler yoksa **adım temposu yedeği** (uygulama ön planda veya 15 dk'lık WorkManager). `sync` ayara/izne göre kaydolur/çıkar (hepsi try/catch).
* `StatsScreen`: Bugün/Hafta/Ay; adım halkası+hedef, yakılan vs alınan kcal, su, antrenman sayısı/süre/mesafe, spor dağılımı, haftalık sütun grafik (kendi Canvas çizimi), seri; **nabız bölümü**: sensör yok → **elle ölçüm girişi** (`WheelPicker`), eğilim çizgisi, dinlenme nabzı sınıfı (sahte sensör verisi YOK);
  **sağlık günlüğü özeti** (`HealthLog.symptomCounts`, son 30 gün), `AiInsights.statsSummary` kartı + `AiDisclaimer`, belirgin **`ReportExportCard`** (“Doktor raporu (PDF)”), `Route.HealthLog` bağlantısı.
* `NotesScreen` (liste, arama), `NoteEditScreen(noteId)` (başlık/gövde, otomatik kayıt, silme; 0 = yeni).

## O1 — İlk açılış + Profil  [feature/onboarding/*, feature/profile/*]  tablolar: StrOnboarding, StrHealth

* `OnboardingFlow` (imza sabit): sıra → **Dil** (6 dil, anında geçiş) → Hoş geldin + **Get Started** (giriş/hesap YOK; `ref2/3/4` tarzı halka + `GoControl`) → Cinsiyet → Ad → Yaş → **Boy listesi 1–400 cm** → **Kilo listesi 1–600 kg** (`WheelPicker`) → Hastalıklar (`HealthLists.conditions`, arama, “Yok”) → Alerjiler →
  **İlaçlar (`MedicationEditor`: “sıklıkla” / “ara sıra” ayrı kutular)** → Aile hekimi (isteğe bağlı ad/telefon; `Repo.setSettings`) → **izin tanıtım ekranları** (konum, bildirim, Bluetooth/yakındaki cihazlar, hareket algılama, müzik dosyaları, arka plan/otomatik başlatma; her biri açıklamalı “İzin ver”/“Şimdi değil”; kendi bileşenleriniz) →
  **Groq anahtarı (isteğe bağlı, “Atla”)** + nasıl ücretsiz alınır → bitir. Hepsi sonradan **düzenlenebilir**. Uygulama ikonlu açılış ekranı (splash) YOK.
* `ProfileScreen`: üstte `Avatar` (dokun → `ActivityResultContracts.PickVisualMedia` → `ProfilePhoto.saveFrom` IO'da), ad/yaş/BMI; kişisel veriler düzenleme sayfaları; **≥ 20 sağlık paneli** (her biri farklı istatistik, mini grafik/gösterge): BMI+sınıf, ideal kilo aralığı, BMR, TDEE, su hedefi/bugün, protein hedefi, bugünkü adım/hedef, 7 gün adım,
  haftalık antrenman, aktif dakika, yakılan kcal, mesafe, favori spor, seri, nefes seansları, **belirti sıklığı (egzersiz sonu cevaplarından)**, **nefes rahatlığı eğilimi**, yorgunluk eğilimi, toparlanma puanı, hidrasyon düzeni, öğün/kcal ortalaması, hastalık/alerji çipleri (düzenle), ilaç özeti (`Route.Medications`),
  aile hekimi kartı (ara: ACTION_DIAL), **“Olası sağlık sorunları” yapay zekâ paneli** (`AiInsights.healthFlags`, seviyeye göre renk + `AiDisclaimer`), `ReportExportCard`.
* `ProfilePhoto` (stub imzalı): EXIF'e duyarlı çözme (API 28+ `ImageDecoder`, değilse `BitmapFactory`), ≤ 640 px, JPEG 88, `filesDir/profile/avatar.jpg`, `version` artar. `Avatar` fotoğrafı IO'da yükleyip `version`'a göre önbelleğe alır (bellek sızıntısı/OOM yok).
* StrHealth: `health.*` (ölçüler, paneller) + `health.cond.<id>` (40) + `health.allergy.<id>` (28).

## O2 — Ayarlar + İzinler  [feature/settings/*, feature/permissions/*]  tablolar: StrSettings, StrPerms

* `SettingsScreen`: **Aile hekimi** kartı en üstte (ad/telefon → `Repo.setSettings`; ara düğmesi; **`doctorAlways`** anahtarı: “Belirtilerim sürerse asistan her zaman aile hekimime yönlendirsin”; son 14 günde şiddetli belirti varsa vurgulu “aile hekiminle görüşmeni öneririm” kartı) ·
  **Sağlık günlüğü (belge)** → `Route.HealthLog` · **İlaçlarım** → `Route.Medications` · **Doktor raporu** (`ReportExportCard`) · **Yapay zekâ**: mod (Yerel/Groq), Groq anahtarı (maskeli, doğrulama düğmesi), `aiFollowUp`, `aiDailyTip` (değişince `DailyTip.ensure`) ·
  **Bildirimler**: hava (anahtar + sıklık 15 dk–3 sa seçici), hareket algılama, öğünler (+saatler), oruç (`fastingAuto`, `fastingForce`, sahur alarmı + dk ofset, iftar alarmı), yapay zekâ bildirimleri · **Adım hedefi** · **Müzik kaynağı** (Audius / YouTube tarzı deneysel + ToS uyarısı) · **Yedek** (`BackupCard`) ·
  **İzinler** (`Route.Permissions`) · **Hakkında** (sürüm, gizlilik özeti, lisanslar: Poppins/Inter OFL, OSM atıf, Open-Meteo, Audius, Wikipedia), **Teşhis kaydı** (CrashLog paylaş/temizle), **Verilerimi sil** (onaylı; `Repo` temizleme API'sini kullanın). Dil değişimi anında.
* `PermissionsScreen`: tüm izinler için durum çipi + açıklama + “İzin ver” + ayarlara git: konum, bildirim (13+), tam ekran bildirim (14+), Bluetooth/yakındaki cihazlar (12+), hareket algılama, müzik/ses dosyaları, pil/arka plan ve HyperOS otomatik başlatma (`Perms` yardımcıları; `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` bildirilmedi →
  pil ayar listesi ekranına gönderin). `OnResume`'da durumları yenileyin.

## H — Ana sayfa + Daha fazla + Nefes  [feature/home/*, feature/more/*, feature/mind/*]  tablolar: StrHome, StrMind

* `HomeScreen` (`ref1_home_ai_assistant_cards.png`): zamana göre selamlama (“Günaydın, {ad}”), `Avatar` (→ `Route.Profile`), **yapay zekâ günün önerisi kartı** (`AiInsights.dailyTip` + `AiDisclaimer`, dokununca asistan), hızlı kartlar: spora devam/başla, nefes, son hava durumu, adım halkası, su (+1 bardak),
  sıradaki alarm, **Ramazan'daysa iftar/imsak geri sayımı**, bugünün kcal'i; `hero_arcs/bg_arcs` görselleri; gerçek `Repo` verisi, boş durumlar zarif.
* `MoreScreen`: Hava durumu, Nefes, Notlar, Alarmlar, Öğünler, Oruç, İstatistikler, **İlaçlarım**, **Sağlık günlüğü**, Ayarlar, İzinler, Profil, Hakkında; premium liste satırları + ikonlar.
* `BreathingScreen` (`Route.Breathing`): **3 hız** (yavaş/orta/hızlı), süre seçimi (1/3/5/10 dk), verilen **kalp görseli** (`R.drawable.heart_core`, `ring_heart`, `rings_only`) nefes alırken büyür/verirken küçülür + “nefes al” / “nefes ver” yazısı, süre sayacı, hafif titreşim (isteğe bağlı), durdur; bitince
  **`ExerciseFinishFlow(exerciseId = "breathing", durationSec, summaryLines, closingMessage = t("mind.breath.done"))`** (kapanış: “Mükemmel, aynen böyle devam et, yarın seni yine bekliyorum”); 30 sn'den kısa erken çıkış akışsız kapanır. `Exercises.nameKey("breathing")` = `mind.breath.title` (**bu anahtarı siz tanımlayın**).
  Mini oynatıcı/ada çakışması Shell'de yönetilir.

## Q — Sağlık soruları, bitiş akışı, günlük, takip  [feature/health/*]  tablo: StrQuiz (quiz.*)

* `HealthQuiz` (11 banka × **6–7 soru** × 3–5 seçenek; her soruda “sorun yok” seçeneği; `severity` 0..3; her soru bir `SymptomTag`): banka eşleme `bankFor(exerciseId)` (breathing → `breathing`; sporlar `SportsCatalog.category`/spor kimliğine göre; bilinmeyen → `general`). Sorular **egzersize özel** ve doğal (kullanıcının örneği: nefes
  egzersizi — “yoruldun mu / nefesin daraldı mı?” → “Evet, nefesim daraldı” / “Hayır, rahatça nefes alıp verdim, tamamen takip edebildim” / “Orta: ne çok daraldı ne çok rahat” ; “baş dönmesi oldu mu?”…). Spor örnekleri: koşu/yürüyüş (nefes, yan ağrısı, diz/bilek, göğüs sıkışması, baş dönmesi, çarpıntı, kramp), bisiklet (diz, el uyuşması, bel/boyun, nefes…),
  kuvvet (eklem, bel, keskin vs kas ağrısı, baş dönmesi, baş ağrısı, bulantı), esneklik, dövüş (baş darbesi/baş dönmesi, bilek, kaburga), su sporları (nefes kontrolü, kulak/sinüs, kramp, üşüme), top sporları, kış sporları, açık hava (sıcaklık belirtileri, susuzluk).
  Çeviriler `quiz.<banka>.<n>.q` / `.o<k>` (11×7×~5×6 ≈ 2300 metin → banka başına `partN()`; 6 dil gerçek çeviri).
* `ExerciseFinishFlow` (imza sabit, tam ekran): **evre 1** (~2,4 sn) yeşil daire yaylı büyür, içinde beyaz tik çizilerek belirir, sonra **tik kendi etrafında döner**, çevresindeki **halka döner ve parlar** (süpürme degradesi + nabız atan ışıma); **evre 2** tik **yavaşça kaybolur**; **evre 3** sorular: üstte ilerleme noktaları,
  büyük soru metni, **seçenekler altta** büyük cam düğmeler, seçince kayarak sonraki; “Atla”; **evre 4** özet (`summaryLines`, `closingMessage` başlık) + `AiInsights.workoutFeedback` yorumu + `AiDisclaimer`, şiddet ≥ 2 ise “10-15 dk sonra sana yazacağım” notu, “Bitir” → `onDone` (bir kez). Çıkışta/geri tuşunda
  o ana kadarki cevaplarla kayıt tamamlanır. Kayıt: `HealthLog.add(HealthEntry)` + `HealthFollowUp.schedule`. Sadece Compose çizimi (Canvas/animasyon API'leri), ağır hesap yok, RTL doğru, ekran okuyucu metinleri.
* `HealthFollowUp.schedule(ctx, entry)`: `settings.aiFollowUp` açıksa ve şiddet ≥ 2 (veya son 3 günde aynı belirti ≥ 2 kez hafif) → WorkManager `OneTimeWorkRequest` **10–15 dk** gecikme; worker: `AssistantContexts.build` → `AiInsights.followUpMessage` → `AssistantSeed.put` → `Notif.post(CH_AI, ID_FOLLOWUP, başlık, kısa metin, route="assistant",
  actionTitle=tr("quiz.notif.start_chat"))`. `DailyTip.ensure(ctx)`: günlük ~09:30 periyodik (`aiDailyTip` kapalıysa iptal) → `AiInsights.dailyTip` bildirimi (`CH_AI`, `ID_DAILY_TIP`). Worker'lar try/catch, `Result.success()`.
* `HealthLogScreen` (“belge”): güne göre gruplu kayıtlar (egzersiz adı/ikon, saat, süre, şiddet çipi, soru→cevap listesi), tek tek silme, “Tümünü sil” (onaylı), en sık belirtiler özeti, üstte `ReportExportCard`, boş durum. Egzersiz adı: `Exercises.nameKey(id)`.

## R — Rapor, yedek, paylaşım  [feature/report/*]  tablo: StrReport (report.*, backup.*)

* **PDF raporu** (`ReportBuilder.build`, `android.graphics.pdf.PdfDocument`, A4 595×842 pt): **altın tema**, **en üstte uygulama logosu** (`R.drawable.heart_core` ve/veya ikon + “MyHealth” yazısı), tarih; sayfalar: (1) kapak/özet (kişi, yaş, boy, kilo, BMI, toplam egzersiz, toplam kayıt, belirti sıklığı tablosu), (2) sağlık profili (hastalıklar, alerjiler, **ilaçlar: sıklıkla/ara sıra**, aile hekimi),
  (3) yapay zekâ değerlendirmesi (`AiInsights.statsSummary/healthFlags` + `ai.disclaimer` metni), **(4…N) yapılan HER spor/egzersiz için ayrı sayfa** (nefes dahil; sayıya göre sıralı; başlık+ikon, seans sayısı/toplam süre/mesafe/kcal, seans tablosu: tarih, süre, **verilen cevaplar** renkli şiddet, **günden güne seyir** mini çizgi grafik “bugün daraldı / yarın daraldı / bir hafta sonra açılmaya başladı”),
  (son) gün gün zaman çizelgesi + “doktorunuza gösterin” notu. Sayfa numarası, uzun tablolarda otomatik sayfa taşırma, **RTL (Arapça) doğru** (`StaticLayout`, `TextDirectionHeuristics`), yazı tipleri `ResourcesCompat.getFont(R.font.*)` (eksik glifler sistem yedeğine düşer). **Canlı**: her çağrıda güncel veriden üretilir.
  **Doğrulama stratejisi**: yerleşim mantığını **saf Kotlin**'e koyun (`ReportLayout.kt` → çizim komutları listesi: metin/dikdörtgen/çizgi/yol/görsel), `PdfDocument` yalnız bu komutları Canvas'a uygulayan ince katman (`ReportPdfWriter.kt`); JVM'de aynı komutları **SVG/HTML'e** çevirip `tools/shot.sh` ile görüntüleyin ve tasarımı gözle doğrulayın (gerçek PdfDocument burada çalışmaz — raporunuza yazın).
* `ShareHelper.sharePdf/shareFile` (FileProvider `ctx.packageName + ".fileprovider"`, `ACTION_SEND` + `createChooser` + okuma izni; WhatsApp/Instagram/e-posta vb. tüm hedefler), `openUrl` (yalnız http/https, try/catch), PDF'i açma.
* **`BackupManager`**: `exportZip` → `cache/share/MyHealth_yedek_<tarih>.zip`: `manifest.json` (uygulama, şema=2, tarih), `data.json` (profil, ayarlar **Groq anahtarı HARİÇ**, antrenmanlar, notlar, alarmlar, öğünler, su/adım, çalma listeleri), `chats/*.json` + sohbet belleği, `memory.json`, `health_log.json`, `meds.json`, `kv.json` (kalan tüm `kv`),
  `reports/MyHealth_rapor_<tarih>.pdf` (o anda üretilmiş), `profile/avatar.jpg` (`ProfilePhoto.file`). `importZip` → önce tümünü ayrıştır/doğrula, sonra uygula (atomik), `ProfilePhoto.saveBytes`, StateFlow'ları tazele; hata → `false`, mevcut veri bozulmaz. **`Repo`'ya yalnız ekleme** yapabilirsiniz (yalnız siz): `kvKeys()`, `kvSnapshot()`, `kvRestore(map)`, `exportAll()`, `importAll(map)`
  vb.; “PAYLAŞILAN DEĞİŞİKLİK” olarak raporlayın. Saf kısımlar (ZIP düzeni, JSON birleştirme) JVM testli.
* `ReportExportCard` (altın cam kart: son rapor zamanı, “Oluştur ve paylaş” → ilerleme → Paylaş/Aç/Hata) ve `BackupCard` (yedekle/ZIP paylaş, yedekten geri yükle `ActivityResultContracts.OpenDocument`, onay + sonuç) bileşenleri; her ikisi `Dispatchers.IO` + hata yakalama.

## MD — İlaçlar  [feature/meds/*]  tablo: StrMeds (med.*, medcat.*, meds.*)

* `MedDb.all`: **≥ 120 halk arasında çok bilinen ilaç (Türkiye pazarı ağırlıklı)**, gerçek marka+etken madde+kategori: ağrı/ateş (Parol, Minoset, Tylol…), NSAİİ (Majezik, Apranax, Nurofen, Voltaren, Arveles…), antibiyotik (Augmentin, **Amoklavin**, Largopen, Cipro, Zinnat, Klacid…), mide (Nexium, **Lansor/Lanzor**, Pantpas, Gaviscon, Rennie…), antispazmodik (**Buscopan („Puskoban“ yazımıyla de aranır)**, Spasmomen…),
  **Andolor**, soğuk algınlığı (Sudafed, Tantum Verde, Strepsils, Theraflu, Gripin…), alerji (Zyrtec, Aerius, Bilaxten…), solunum (Ventolin, Pulmicort, Symbicort), kalp-damar (Coraspin, Beloc, Concor, Norvasc, Lipitor, Crestor…), diyabet (Glucophage…), tiroit (Euthyrox…), vitaminler (D vitamini damlaları, B12, demir, Supradyn…), uyku/sakinleştirici (yalnız reçeteli olduğu belirtilerek bilgi), topikal (Bepanthol, Fucidin, Voltaren jel…).
  `Med`'e **sona varsayılanlı `aliases: List<String> = emptyList()`** ekleyebilirsiniz (yanlış/ağız yazımları: “puskoban”, “amoklavin”, “lanzor”…). `med.<id>`: tek cümle **genel amaç** (doz YOK, “doktorunuza/eczacınıza danışın” tonu) 6 dilde.
* `search(query, limit)`: `TextNorm` ile aksan duyarsız; marka/etken/takma ad/kategori adı; yakın yazım toleransı (Levenshtein ≤ 1–2, kısa sorgularda önek). `warnings(p, medIds, lang)`: hastalık/alerji/gebelik + ilaç etkileşimleri (NSAİİ + gastrit/reflü/böbrek/hipertansiyon/astım/gebelik; penisilin alerjisi + penisilin grubu; parasetamol + yağlı karaciğer/alkol; sedatif antihistaminik + araç kullanımı;
  dekonjestan + hipertansiyon; aynı etken maddeli ürünleri birlikte kullanma (çift parasetamol/NSAİİ); aspirin + kanama/gastrit; metformin + böbrek …), metinler `meds.warn.<kod>` ile yerelleştirilmiş, seviye 1–3. **`custom:<ad>` ile başlayan kimlikler serbest metin ilaçtır**: `byId` null döner, arayüz adı gösterir, `warnings` yok sayar.
* `MedicationEditor(regular, occasional, onChange, modifier)`: iki ayrı kutu (“Sıklıkla kullandıklarım” / “Ara sıra kullandıklarım”), arama (`MedDb.search`) + kategori çipleri, ilaca dokun → bilgi sayfası (amaç + bu kullanıcıya özel uyarılar + hangi kutuya ekle), seçilenler silinebilir çipler, bulunamazsa **özel ad ekle**; ilk açılışta ve profil/ayarlarda aynı bileşen.
  `MedicationsScreen()` (`Route.Medications`): `MedStore`'a bağlı editör + “Dikkat edilmesi gerekenler” (uyarılar) + `AiDisclaimer` + “doktor/eczacıya danışın” bandı. Saf dosyalar JVM testli (`jvmtest/MedTest.kt`: arama, uyarı kuralları, yinelenen kimlik yok).
