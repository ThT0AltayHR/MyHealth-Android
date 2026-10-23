package com.myhealth.app.feature.health

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.myhealth.app.data.Repo
import com.myhealth.app.feature.assistant.AiInsights
import com.myhealth.app.feature.assistant.AssistantContexts
import com.myhealth.app.ui.tr
import com.myhealth.app.util.CrashLog
import com.myhealth.app.util.Notif
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CancellationException

private const val FOLLOW_UP_WORK = "health_followup"
private const val FOLLOW_UP_KEY_ID = "entry_id"
private const val FOLLOW_UP_DELAY_MIN = 12L
/** Bu sevirin ustundeki bir cevap (belirti) varsa takip bildirimi planlanir. */
private const val FOLLOW_UP_MIN_SEVERITY = 1

/** Egzersiz sonu takip: AppSettings.aiFollowUp aciksa ve cevaplarda belirti varsa 12 dk sonra isi kurar. Istisna firlatmaz. */
internal fun scheduleFollowUp(ctx: Context, entry: HealthEntry) {
    try {
        if (!Repo.settings.value.aiFollowUp) return
        if (entry.answers.none { it.severity >= FOLLOW_UP_MIN_SEVERITY }) return
        val input = Data.Builder().putLong(FOLLOW_UP_KEY_ID, entry.id).build()
        val request = OneTimeWorkRequestBuilder<HealthFollowUpWorker>()
            .setInitialDelay(FOLLOW_UP_DELAY_MIN, TimeUnit.MINUTES)
            .setInputData(input)
            .build()
        // DOGRULA: androidx.work bagimliligi projede var sayildi (brif: yeni bagimlilik yok).
        WorkManager.getInstance(ctx.applicationContext)
            .enqueueUniqueWork(FOLLOW_UP_WORK, ExistingWorkPolicy.REPLACE, request)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        CrashLog.note(ctx, "followup_schedule", e)
    }
}

/** Takip isci: kaydi HealthLog'dan id ile bulur, yapay zeka mesajini Notif ile gonderir. Hicbir hata disari cikmaz. */
internal class HealthFollowUpWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        try {
            val id = inputData.getLong(FOLLOW_UP_KEY_ID, -1L)
            val entry = HealthLog.entries.value.firstOrNull { it.id == id } ?: return Result.success()
            if (!Repo.settings.value.aiFollowUp) return Result.success()
            if (!Notif.canPost(applicationContext)) return Result.success()
            val ac = AssistantContexts.build(applicationContext)
            val text = AiInsights.followUpMessage(ac, entry).trim().take(160)
            if (text.isEmpty()) return Result.success()
            Notif.post(
                applicationContext,
                Notif.CH_AI,
                Notif.ID_FOLLOWUP,
                tr("quiz.notif.follow_title"),
                text,
                route = "assistant",
                actionTitle = tr("quiz.notif.start_chat"),
                actionRoute = "assistant"
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            CrashLog.note(applicationContext, "followup_worker", e)
        }
        return Result.success()
    }
}
