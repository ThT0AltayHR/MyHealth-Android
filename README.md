# MyHealth — Dark Luxury Health, Sports & AI (Android)

Kotlin + Jetpack Compose, paket: `com.myhealth.app`, minSdk 26, targetSdk 35.

## Özellikler
- Sıfır emoji: tüm ikonlar Canvas vektörleri (`components/CustomIcons.kt`)
- Siyah-altın tema `#070707` / `#FFAA00` / `#FF8C00`, glassmorphism
- Dynamic Island: çevrimiçi/çevrimdışı nabız + mikro hava durumu çentiği
- 3D tel kafes sporcu (`Character3DCanvas`): koşu, bisiklet, yoga, bekleme
- 54 spor, 8 kategori, canlı GPS rota, kalori (MET), nabız simülasyonu, küçültülebilir antrenman kapsülü
- Döngüsel video hava durumu (Open-Meteo, anahtar gerekmez) + 7 günlük tahmin
- Çift yapay zeka: Groq Llama-3 (isteğe bağlı anahtar) + otomatik devreye giren dahili sağlık motoru, sağlık dışı koruma
- Zihin & Nefes: 8s / 6s / 4s ritim, altın kalp halkaları
- Müzik çalar: cihaz taraması, karışık/tekrar, Bluetooth kayan yazı şeridi
- Ramazan modu: konuma göre imsak/iftar hesabı, sahur alarmı ve iftar zili

## Hava durumu videoları
Lisans nedeniyle videolar pakete dahil değildir. `app/src/main/res/raw/` içine şu adlarla ekleyin:
`bg_clear.mp4, bg_snow.mp4, bg_rain.mp4, bg_thunder.mp4, bg_fog.mp4, bg_sunset.mp4`.
Dosya yoksa uygulama altın-siyah gradyan arka plan kullanır, derleme bozulmaz.

## APK derleme (GitHub Actions)
1. Depoya yükleyin: `git push origin main`
2. `.github/workflows/build-apk.yml` JDK 17 kurar, imza anahtarı üretir, `gradle assembleRelease` çalıştırır.
3. Actions sekmesinden **MyHealth-Release-APK** dosyasını indirip telefona kurun.

Yerelde: Android Studio ile açın (Gradle 8.9+, JDK 17) ve `Build > Build APK` seçin.
