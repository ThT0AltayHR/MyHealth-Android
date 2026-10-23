package com.myhealth.app.feature.health

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.myhealth.app.ui.kit.Glow
import com.myhealth.app.ui.kit.LuxBackdrop
import com.myhealth.app.util.CrashLog
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Egzersiz bitis akisinin evreleri (yalniz bu dosyada kullanilir). */
private enum class FinishStep { CELEBRATE, QUESTIONS, SUMMARY }


/**
 * EGZERSIZ BITIS AKISI (SABIT SOZLESME; govdeyi Quiz ajani doldurur). Tum egzersizlerin (nefes ve spor)
 * sonunda tam ekran gosterilir: yesil daire + donen onay isareti + donen/parlayan halka -> isaret
 * yavasca kaybolur -> egzersize ozel 6-7 saglik sorusu (altta secenek dugmeleri) -> kisa ozet ve
 * yapay zeka yorumu. Biterken HealthEntry kaydedilir (HealthLog.add) ve gerekirse HealthFollowUp.schedule
 * cagrilir. onDone: akis bitince (veya atlaninca) bir kez cagrilir.
 * summaryLines: (etiket, deger) cifti ozet satirlari (orn. "Sure" to "12:30").
 * closingMessage: son ekranda baslik olarak gosterilen kapanis cumlesi (orn. nefes: "Mukemmel, aynen boyle devam et...").
 */
@Composable
fun ExerciseFinishFlow(
    exerciseId: String,
    durationSec: Int,
    summaryLines: List<Pair<String, String>>,
    onDone: () -> Unit,
    closingMessage: String? = null,
    modifier: Modifier = Modifier
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val done by rememberUpdatedState(onDone)
    val questions = remember(exerciseId) { runCatching { HealthQuiz.questionsFor(exerciseId) }.getOrNull().orEmpty() }
    val answers = remember { mutableStateMapOf<String, QuizAnswer>() }
    val finished = remember { AtomicBoolean(false) }
    var step by remember { mutableStateOf(FinishStep.CELEBRATE) }
    var qi by remember { mutableStateOf(0) }

    fun buildEntry(): HealthEntry {
        val list = answers.values.toList()
        val now = System.currentTimeMillis()
        return HealthEntry(
            id = now,
            tsMs = now,
            exerciseId = exerciseId,
            kind = if (exerciseId == Exercises.BREATHING) "breathing" else "sport",
            durationSec = durationSec.coerceAtLeast(0),
            answers = list,
            severity = (list.maxOfOrNull { it.severity } ?: 0).coerceIn(0, 3)
        )
    }

    fun finish() {
        if (!finished.compareAndSet(false, true)) return
        val entry = buildEntry()
        scope.launch {
            try {
                withContext(Dispatchers.IO) {
                    HealthLog.add(entry)
                    HealthFollowUp.schedule(ctx, entry)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                CrashLog.note(ctx, "finish_save", e)
            }
            done()
        }
    }

    fun nextOrSummary() {
        if (qi + 1 < questions.size) qi += 1 else step = FinishStep.SUMMARY
    }

    fun answer(index: Int) {
        val q = questions.getOrNull(qi) ?: return
        val opt = q.options.getOrNull(index)
        if (opt != null) answers[q.id] = QuizAnswer(q.id, index, opt.severity.coerceIn(0, 3), q.tag)
        nextOrSummary()
    }

    fun skip() {
        questions.getOrNull(qi)?.let { answers.remove(it.id) }
        nextOrSummary()
    }

    fun back() {
        if (qi > 0) qi -= 1
    }

    BackHandler(enabled = true) { finish() } // DOGRULA: activity-compose BackHandler projede hazir olmali

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        LuxBackdrop(glow = Glow.WARM_TOP)
        when (step) {
            FinishStep.CELEBRATE -> FinishCelebration(
                onFinished = { step = if (questions.isEmpty()) FinishStep.SUMMARY else FinishStep.QUESTIONS }
            )
            FinishStep.QUESTIONS -> {
                val q = questions.getOrNull(qi)
                if (q != null) {
                    FinishQuestionStep(
                        question = q,
                        index = qi,
                        total = questions.size,
                        onAnswer = { answer(it) },
                        onSkip = { skip() },
                        onBack = { back() },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    LaunchedEffect(Unit) { step = FinishStep.SUMMARY }
                }
            }
            FinishStep.SUMMARY -> FinishSummaryStep(
                entry = buildEntry(),
                summaryLines = summaryLines,
                closingMessage = closingMessage,
                onDone = { finish() },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/** Egzersiz sonu cevaplari agirsa 10-15 dk sonra yapay zeka sohbet bildirimi gonderir. */
object HealthFollowUp {
    fun schedule(ctx: Context, entry: HealthEntry) {
        scheduleFollowUp(ctx, entry)
    }
}
