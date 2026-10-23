package com.myhealth.app.feature.health

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.myhealth.app.core.Fmt
import com.myhealth.app.data.Repo
import com.myhealth.app.feature.assistant.AiInsights
import com.myhealth.app.feature.assistant.AssistantContexts
import com.myhealth.app.ui.tr
import com.myhealth.app.util.CrashLog
import com.myhealth.app.util.Notif
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CancellationException

/**
 * Gunluk yapay zeka ipucu bildirimi zamanlayicisi (SABIT SOZLESME; govdeyi Quiz ajani doldurur).
 * settings.aiDailyTip aciksa gunde bir kez (yerel saatle ~09:30) AiInsights.dailyTip(...) metniyle
 * Notif.post(CH_AI, ID_DAILY_TIP, ..., route = "assistant") gonderir (WorkManager; kesin alarm YOK).
 * Kapaliysa isi iptal eder. Startup.rescheduleAll ve ayarlar anahtari her degistiginde cagirir. Istisna firlatmaz.
 */
private const val DAILY_TIP_WORK = "daily_tip"
private const val DAILY_TIP_LAST_DAY = "dailytip_last_day"
private const val DAILY_TIP_HOUR = 9
private const val DAILY_TIP_MINUTE = 30
private const val DAILY_TIP_DAY_MS = 86_400_000L

object DailyTip {
    /** Ayar acikken ~09:30'da baslayan 24 saatlik is kurar; kapaliysa isi iptal eder. Istisna firlatmaz. */
    fun ensure(ctx: Context) {
        try {
            val wm = WorkManager.getInstance(ctx.applicationContext)
            if (!Repo.settings.value.aiDailyTip) {
                wm.cancelUniqueWork(DAILY_TIP_WORK)
                return
            }
            val request = PeriodicWorkRequestBuilder<DailyTipWorker>(24, TimeUnit.HOURS)
                .setInitialDelay(delayToDailyTipMs(System.currentTimeMillis()), TimeUnit.MILLISECONDS)
                .build()
            // DOGRULA: androidx.work bagimliligi projede var sayildi (brif: yeni bagimlilik yok).
            wm.enqueueUniquePeriodicWork(DAILY_TIP_WORK, ExistingPeriodicWorkPolicy.KEEP, request)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            CrashLog.note(ctx, "daily_tip_ensure", e)
        }
    }

    private fun delayToDailyTipMs(nowMs: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = nowMs
        cal.set(Calendar.HOUR_OF_DAY, DAILY_TIP_HOUR)
        cal.set(Calendar.MINUTE, DAILY_TIP_MINUTE)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        var delta = cal.timeInMillis - nowMs
        if (delta <= 0L) delta += DAILY_TIP_DAY_MS
        return delta
    }
}

/** Gunluk ipucu isci: ayni gun ikinci kez gondermez (kv 'dailytip_last_day'). Istisna firlatmaz. */
internal class DailyTipWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        try {
            if (!Repo.settings.value.aiDailyTip) return Result.success()
            val today = Fmt.todayKey()
            if (Repo.kvGet(DAILY_TIP_LAST_DAY) == today) return Result.success()
            if (!Notif.canPost(applicationContext)) return Result.success()
            val text = AiInsights.dailyTip(AssistantContexts.build(applicationContext)).trim().take(200)
            if (text.isEmpty()) return Result.success()
            Notif.post(
                applicationContext,
                Notif.CH_AI,
                Notif.ID_DAILY_TIP,
                tr("quiz.notif.tip_title"),
                text,
                route = "assistant"
            )
            Repo.kvPut(DAILY_TIP_LAST_DAY, today)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            CrashLog.note(applicationContext, "daily_tip_worker", e)
        }
        return Result.success()
    }
}
