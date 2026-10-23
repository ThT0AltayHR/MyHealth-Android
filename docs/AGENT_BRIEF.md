# MyHealth — Ajan ortak talimatı (HERKES İÇİN, harfiyen uygula)

MyHealth: Android (Kotlin + Jetpack Compose, paket `com.myhealth.app`) premium sağlık / spor / yapay zekâ uygulaması; **Google Play'de 1.500 TL'ye satılacak**.
Proje kökü: `/home/claude/myhealth/myhealth-app` (kaynak kökü `app/src/main/java/com/myhealth/app`).
Scratchpad (araçlar, JVM testleri, referans görseller): `/tmp/claude-0/-home-claude/05338800-9f2b-5541-9a7d-f6c8ff00dd4d/scratchpad`.

## Okuma sırası

1. `docs/CONTRACTS.md` — özellikle **§4 ÇÖKMEZLİK KURALLARI** ve **§8 v2 EKLERİ** (tabloları, kuralları, STRICT doğrulayıcıyı).
2. `docs/ROSTER.md` — en üstteki “Herkes için çalışma düzeni” ve **yalnızca kendi “## <KOD> —” bölümün** (dosya sahipliğin, sabit imzalar, gereksinimler).
3. `docs/PLAN_TR.md` — modülünle ilgili **kullanıcının kendi cümleleri** (Türkçe, sesle yazılmış; niyetini oku; sonundaki “V2 GEREKSİNİMLERİ” dahil). Kullanıcının istediği, ROSTER'daki özetten daha önemlidir.
4. Kendi taslak (stub) dosyaların; ortak imzalar için `docs/API.md` (otomatik özet) — ham `ui/kit`, `ui/theme`, `Repo`, `Models` dosyalarını TAMAMEN OKUMA (maliyet).

## Bağlam ve beklenti

* Kullanıcı Türkçe konuşuyor, **çok sabırsız** ve “çökme sıfır, derlenen, premium kalite” istiyor. Yer tutucu/yarım ekran, `TODO`, sahte/uydurma veri, İngilizce kopya çeviri, emoji **YOK**.
* Derleme yalnızca kullanıcının GitHub Actions'ında olacak; **burada Android SDK/Compose/AndroidX yok**. Bu yüzden bildiğin en yaygın, sürüme en az duyarlı API'leri kullan; emin olmadığın
  her yere `// DOGRULA: neden` yaz ve raporuna ekle. Yeni bağımlılık eklenemez (CONTRACTS §1).
* ~24 ajan aynı anda çalışıyor: başkasının yarım dosyası bir an derlemeyi bozabilir (kısa süre sonra yeniden dene); makinede 2 CPU var, `kotlinc`/Chromium sıraya girer —
  derlemeyi mantıksal bir parça bitince yap (toplam ≤ ~10). Başkasının dosyasına dokunma (ihtiyaç → rapor).
* Android'e bağımlı olmayan mantığı **saf dosyalara** ayır ve `tools/ktrun.sh` ile gerçekten çalıştır (testlerin `scratchpad/jvmtest/` altında, adı modülünle başlasın). `tools/check_i18n.sh`
  (kendi anahtarların için `STRICT=1`), `tools/typecheck.sh` hazırsa (`tools/TYPECHECK_READY` var) kendi dosyaların için çalıştır.
* **Token ekonomisi** (ortak bir bütçeden harcıyoruz): büyük dosyaları baştan sona okuma (`grep` / satır aralığı), uzun çıktıları `head`/`tail` ile sınırla, aynı dosyayı tekrar okuma,
  görselleri yalnızca gerektiğinde gör, bol açıklama yazma — **kod her şeydir**.
* **Dosyaları parça parça ve hemen diske yaz** (oturum sınırına çarparsan ilerleme kaybolmasın). Tek dosya ≤ ~1200 satır; büyük tabloları `private fun partN()` ile böl (CONTRACTS §8.4-6).
* Önce **çekirdek** (derlenen, çökmeyen, kullanıcının istediği ana akış), sonra cila.

## Bitirince: CONTRACTS §7 biçiminde ≤ 40 satır DÜRÜST rapor

1) Yazılan/değiştirilen dosyalar. 2) Gerçekten çalıştırılan doğrulamalar ve sonuçları (komut + çıktı özeti). 3) `PAYLAŞILAN DEĞİŞİKLİK` / `MANIFEST İSTEĞİ` (varsa).
4) Emin olunmayan API'ler / bilinen eksikler. 5) Diğer modüllerden beklentiler.

---

## BÜTÇE PROTOKOLÜ — EN ÖNEMLİ KURAL (yukarıdaki her şeyin ÜSTÜNDE)

Kullanım limiti çok düşük; maliyet = TUR sayısı × bağlam boyutu. Önceki denemelerde ajanlar 25+ okuma çağrısıyla limiti bitirdi ve tek dosya yazamadı.
1. **TUR 1 (tek mesaj, paralel):** `docs/API.md` (ortak imzalar; ham ui/kit, ui/theme, Repo, Models dosyalarını OKUMA), `docs/CONTRACTS.md` satır 124–270 (§4–§8), `docs/ROSTER.md` satır 7–24 + yalnız kendi bölümün, komşu stub dosyaları (prompt'ta listeli, küçükler), kendi taslakların, `tail -n 140 docs/PLAN_TR.md`. Başka okuma turu AÇMA; bir imzadan emin değilsen `grep -n -A8 "fun Ad" <dosya>` kullan.
2. **TUR 2 hemen yazma:** tek mesajda 4–8 paralel `Write` (dosya ≤ ~700 satır). Yazdığını geri okuma, uzun açıklama yazma, kısa düşün.
3. Toplam hedef ≤ 5 tur: oku → yaz → yaz → (tek Bash: `bash /tmp/claude-0/-home-claude/05338800-9f2b-5541-9a7d-f6c8ff00dd4d/scratchpad/tools/check_i18n.sh 2>&1 | tail -25`) → rapor. Hata varsa tek paralel Edit turu. Compose kodunu derleyemezsin: bildiğin en güvenli API'leri kullan, emin olmadığına `// DOGRULA:` yaz.
4. Kod yoğun ve veri güdümlü olsun (listeden üretilen satırlar/paneller); 6 dilde gerçek çeviri; bütçe yetmezse kapsamı daralt ama teslim ettiğin her dosya derlenir/çökmez olsun. Yazılmamış şeye başka dosyadan referans verme.
5. Rapor ≤ 12 satır (yazılan dosyalar, doğrulama sonucu, eksikler, PAYLAŞILAN DEĞİŞİKLİK).
