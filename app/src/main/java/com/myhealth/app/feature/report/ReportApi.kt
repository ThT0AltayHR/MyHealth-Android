package com.myhealth.app.feature.report

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import java.io.File

/*
 * RAPOR / YEDEK / PAYLASIM ARAYUZLERI (SABIT SOZLESME; govdeyi Rapor ajani doldurur).
 * Hicbir islev istisna firlatmaz; basarisizlikta null/false doner.
 */

/** Doktora gosterilebilir PDF raporu (altin tema, ustte uygulama logosu, her spora ozel sayfa). */
object ReportBuilder {
    /** cache/share/ altinda PDF uretir (arka plan thread'inde cagirin). Basarisizsa null. */
    fun build(ctx: Context): File? = null
}

object ShareHelper {
    /** FileProvider ile paylas menusunu acar (WhatsApp, Instagram, e-posta... tum uygulamalar). */
    fun sharePdf(ctx: Context, file: File) {}

    fun shareFile(ctx: Context, file: File, mime: String) {}

    /** Tarayicida acar (http/https disi adresleri reddeder). */
    fun openUrl(ctx: Context, url: String) {}
}

/** Tum veriyi (JSON) tek ZIP dosyasinda yedekler / geri yukler. */
object BackupManager {
    /** cache/share/myhealth-backup-<tarih>.zip uretir (arka plan thread'inde cagirin). */
    fun exportZip(ctx: Context): File? = null

    /** Kullanicinin sectigi ZIP'ten geri yukler; basari = true. Mevcut veri uzerine yazilir. */
    fun importZip(ctx: Context, uri: Uri): Boolean = false
}

/** Hazir kart: "Doktor raporu (PDF)" - olustur ve paylas (ilerleme/hata durumlariyla). Istatistik ve profilde kullanilir. */
@Composable
fun ReportExportCard(modifier: Modifier = Modifier) {
    // TASLAK
}

/** Hazir kart: yedekle (ZIP paylas) ve yedekten geri yukle. Ayarlarda kullanilir. */
@Composable
fun BackupCard(modifier: Modifier = Modifier) {
    // TASLAK
}
