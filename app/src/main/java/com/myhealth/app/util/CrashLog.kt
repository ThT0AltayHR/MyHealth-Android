package com.myhealth.app.util

import android.app.Application
import android.content.Context
import android.os.Build
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Hata kaydi. Cokmeler last_crash.txt dosyasina, yakalanmis (kritik olmayan) hatalar
 * errors.txt dosyasina yazilir. Ayarlar > Tanilama ekrani bunlari gosterir/paylasir;
 * boylece telefonda olusan bir hata derleme gunlugu olmadan bile bulunabilir.
 */
object CrashLog {
    private const val CRASH = "last_crash.txt"
    private const val ERRORS = "errors.txt"
    private const val MAX_ERRORS_BYTES = 48 * 1024

    fun install(app: Application) {
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, error ->
            try { writeCrash(app, thread, error) } catch (t: Throwable) { /* yoksay */ }
            previous?.uncaughtException(thread, error)
        }
    }

    private fun header(): String {
        val ts = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
        return "time=$ts sdk=${Build.VERSION.SDK_INT} device=${Build.MANUFACTURER} ${Build.MODEL}"
    }

    private fun trace(e: Throwable): String {
        val sw = StringWriter()
        e.printStackTrace(PrintWriter(sw))
        return sw.toString()
    }

    private fun writeCrash(ctx: Context, thread: Thread, e: Throwable) {
        File(ctx.filesDir, CRASH).writeText(header() + " thread=" + thread.name + "\n" + trace(e))
    }

    /** Yakalanmis hatayi kaydeder (uygulama calismaya devam eder). */
    fun note(ctx: Context, tag: String, e: Throwable) {
        try {
            val f = File(ctx.filesDir, ERRORS)
            val old = if (f.exists() && f.length() < MAX_ERRORS_BYTES) f.readText() else ""
            val line = header() + " tag=" + tag + "\n" + trace(e).lineSequence().take(14).joinToString("\n") + "\n\n"
            f.writeText(old + line)
        } catch (t: Throwable) {
            // kayit basarisiz olsa da uygulama etkilenmesin
        }
    }

    fun readCrash(ctx: Context): String? = readFile(ctx, CRASH)

    fun readErrors(ctx: Context): String? = readFile(ctx, ERRORS)

    private fun readFile(ctx: Context, name: String): String? = try {
        val f = File(ctx.filesDir, name)
        if (f.exists()) f.readText().ifBlank { null } else null
    } catch (e: Exception) {
        null
    }

    fun clear(ctx: Context) {
        try { File(ctx.filesDir, CRASH).delete() } catch (e: Exception) { /* yoksay */ }
        try { File(ctx.filesDir, ERRORS).delete() } catch (e: Exception) { /* yoksay */ }
    }
}
