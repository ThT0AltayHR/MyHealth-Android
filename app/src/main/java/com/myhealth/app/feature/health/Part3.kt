package com.myhealth.app.feature.health

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.myhealth.app.ui.kit.GhostButton
import com.myhealth.app.ui.kit.GlassCard
import com.myhealth.app.ui.kit.HGap
import com.myhealth.app.ui.kit.IconBtn
import com.myhealth.app.ui.kit.Ic
import com.myhealth.app.ui.kit.VGap
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt

/** Egzersiz bitis akisi 3. evre: tek soru; secenekler altta, buyuk cam dugmeler (SABIT imza). */
@Composable
internal fun FinishQuestionStep(
    question: QuizQuestion,
    index: Int,
    total: Int,
    onAnswer: (Int) -> Unit,
    onSkip: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconBtn(icon = Ic.BACK, onClick = onBack, description = t("quiz.q.back"), autoMirror = true)
            HGap(12.dp)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                for (i in 0 until total) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(if (i <= index) Lux.gold else Lux.glassStrong)) {}
                }
            }
        }
        Spacer(Modifier.weight(1f))
        Txt(t("quiz.q.progress", (index + 1).toString(), total.toString()), sty = Sty.CAPTION, color = Lux.textDim)
        VGap(12.dp)
        Txt(t(question.textKey), sty = Sty.H3, color = Lux.text, align = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.weight(1f))
        question.options.forEachIndexed { i, opt ->
            GlassCard(modifier = Modifier.fillMaxWidth(), onClick = { onAnswer(i) }) {
                Txt(t(opt.textKey), sty = Sty.BODY_STRONG, color = Lux.text, align = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
            VGap(10.dp)
        }
        VGap(4.dp)
        GhostButton(text = t("quiz.q.skip"), onClick = onSkip)
    }
}
