package com.myhealth.app.feature.weather

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ListenableWorker.Result
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.myhealth.app.data.Repo
import com.myhealth.app.ui.tr
import com.myhealth.app.util.CrashLog
import com.myhealth.app.util.Notif
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking

/** Hava durumu bildirimlerini ayardaki sikliga gore (15 dk - 3 sa) WorkManager ile zamanlar. Imza SABITTIR. */
object WeatherScheduler {
    private const val UNIQUE_NAME = "myhealth_weather_periodic"

    /** Bildirim aciksa periyodik isi kurar; kapaliysa isi iptal eder. Hata yutulur (cokmez). */
    fun reschedule(ctx: Context) {
        try {
            val wm = WorkManager.getInstance(ctx)
            val s = Repo.settings.value
            if (!s.weatherNotif) {
                wm.cancelUniqueWork(UNIQUE_NAME)
                return
            }
            // DOGRULA: androidx.work (WorkManager) bagimliligi projede olmali; yeni bagimlilik eklenmedi. Periyot alt siniri 15 dk.
            val mins = s.weatherEveryMin.coerceIn(15, 180).toLong()
            val req = PeriodicWorkRequest.Builder(WeatherWorker::class.java, mins, TimeUnit.MINUTES).build()
            // REPLACE: ayar degisince periyot bastan kurulur; ayni isim tek is olarak kalir.
            wm.enqueueUniquePeriodicWork(UNIQUE_NAME, ExistingPeriodicWorkPolicy.REPLACE, req)
        } catch (e: Exception) {
            CrashLog.note(ctx, "weather_schedule", e)
        }
    }

    /** Basarili veri cekiminden sonra bildirimi gunceller: hava ozeti; yakin saatlerde yagis ihtimali yuksekse yagmur uyarisi. */
    internal fun postWeather(ctx: Context) {
        try {
            if (!Repo.settings.value.weatherNotif) return
            if (!Notif.canPost(ctx)) return
            val w = Repo.weather.value ?: return
            val rain = Repo.kvInt("weather_rain3h", 0)
            val title = tr("weather.notif.title", w.city, w.tempC, tr(WxIcons.nameKey(w.condition)))
            val text = if (rain >= WX_RAIN_ALERT_PCT) {
                tr("weather.rain_alert", "$rain%")
            } else {
                tr("weather.notif.calm", w.feelsC, "${w.humidity}%")
            }
            Notif.ensureChannels(ctx)
            Notif.post(ctx, Notif.CH_WEATHER, Notif.ID_WEATHER, title, text, route = "weather")
        } catch (e: Exception) {
            CrashLog.note(ctx, "weather_notif", e)
        }
    }
}

/**
 * WorkManager isci: veriyi ceker, basariliysa bildirimi gunceller. Hata durumunda success doner (tekrar dongusu yok).
 * DOGRULA: Worker (work-runtime) kullanildi; CoroutineWorker ek artefakt (work-runtime-ktx) isteyebilir.
 */
class WeatherWorker(appContext: Context, params: WorkerParameters) : Worker(appContext, params) {
    override fun doWork(): Result {
        val ctx = applicationContext
        return try {
            val ok = runBlocking { WeatherRepo.refresh(ctx) }
            if (ok) WeatherScheduler.postWeather(ctx)
            Result.success()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            CrashLog.note(ctx, "weather_worker", e)
            Result.success()
        }
    }
}
