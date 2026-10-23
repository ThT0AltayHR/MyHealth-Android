package com.myhealth.app.feature.health

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.myhealth.app.feature.assistant.AiInsights
import com.myhealth.app.feature.assistant.AssistantContexts
import com.myhealth.app.ui.kit.AiDisclaimer
import com.myhealth.app.ui.kit.GlassCard
import com.myhealth.app.ui.kit.PrimaryButton
import com.myhealth.app.ui.kit.SmallTag
import com.myhealth.app.ui.kit.VGap
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt
import com.myhealth.app.util.CrashLog
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Egzersiz bitis akisi 4. evre: ozet, bildirilen konular, yapay zeka yorumu ve "Bitir" (SABIT imza).
 * Yapay zeka yorumu entry alanlariyla anahtarlanir (buildEntry her cagrida yeni id uretir).
 */
@Composable
internal fun FinishSummaryStep(
    entry: HealthEntry,
    summaryLines: List<Pair<String, String>>,
    closingMessage: String?,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ctx = LocalContext.current
    val feedback by produceState<String?>(initialValue = null, entry.exerciseId, entry.answers) {
        value = withContext(Dispatchers.IO) {
            try {
                AiInsights.workoutFeedback(AssistantContexts.build(ctx), entry, null)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                CrashLog.note(ctx, "finish_feedback", e)
                null
            }
        }
    }
    val symptoms = entry.answers.filter { it.severity >= 1 }.map { it.tag }.distinct()
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Txt(t("quiz.sum.title"), sty = Sty.H2, color = Lux.text, align = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        if (closingMessage != null) {
            VGap(8.dp)
            Txt(closingMessage, sty = Sty.BODY, color = Lux.textDim, align = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
        VGap(16.dp)
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            summaryLines.forEach { line ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Txt(line.first, sty = Sty.CAPTION, color = Lux.textDim)
                    Txt(line.second, sty = Sty.BODY_STRONG, color = Lux.text)
                }
                VGap(6.dp)
            }
        }
        VGap(16.dp)
        Txt(
            t(if (symptoms.isEmpty()) "quiz.sum.sym_none" else "quiz.sum.sym_title"),
            sty = Sty.LABEL, color = Lux.textDim, modifier = Modifier.fillMaxWidth()
        )
        VGap(8.dp)
        symptoms.forEach { tag ->
            SmallTag(t(tag.nameKey))
            VGap(6.dp)
        }
        val text = feedback
        if (text != null && text.isNotBlank()) {
            VGap(16.dp)
            GlassCard(modifier = Modifier.fillMaxWidth(), warm = true) {
                Txt(t("quiz.sum.ai_title"), sty = Sty.LABEL, color = Lux.textDim)
                VGap(6.dp)
                Txt(text, sty = Sty.BODY, color = Lux.text)
            }
        }
        if (entry.severity >= 2) {
            VGap(12.dp)
            Txt(t("quiz.sum.follow_note"), sty = Sty.CAPTION, color = Lux.amber, align = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
        VGap(16.dp)
        AiDisclaimer(modifier = Modifier.fillMaxWidth(), compact = true)
        VGap(20.dp)
        PrimaryButton(text = t("quiz.finish.done"), onClick = onDone, modifier = Modifier.fillMaxWidth())
    }
}
