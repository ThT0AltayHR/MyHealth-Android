package com.myhealth.app.feature.sports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.myhealth.app.core.Fmt
import com.myhealth.app.data.WorkoutState
import com.myhealth.app.ui.kit.Chip
import com.myhealth.app.ui.kit.GlassCard
import com.myhealth.app.ui.kit.LuxDialog
import com.myhealth.app.ui.kit.PrimaryButton
import com.myhealth.app.ui.kit.ProgressRing
import com.myhealth.app.ui.kit.VGap
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt

/** Sure hedefi secimi: 0 = serbest. Onaylanan sure saniye olarak verilir. */
@Composable
internal fun SportLiveDurationDialog(current: Int, onDismiss: () -> Unit, onConfirm: (Int) -> Unit) {
    val options = listOf(0, 600, 1200, 1800, 2700, 3600, 5400)
    var picked by remember { mutableStateOf(current) }
    LuxDialog(onDismiss = onDismiss) {
        Txt(t("sportlive.target.title"), sty = Sty.H3, color = Lux.text)
        VGap(12.dp)
        options.forEach { sec ->
            Chip(
                text = if (sec == 0) t("sportlive.free") else t("sportlive.min", sec / 60),
                selected = picked == sec,
                onClick = { picked = sec },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            )
        }
        VGap(16.dp)
        PrimaryButton(
            text = t("sportlive.target.ok"),
            onClick = { onConfirm(picked) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/** Buyuk sayac, hedef halkasi ve olcum kutulari. showDist=false (GPS'siz spor): yalniz kcal. */
@Composable
internal fun SportLiveStats(st: WorkoutState, target: Int, showDist: Boolean) {
    val progress = if (target > 0) (st.elapsedSec.toFloat() / target.toFloat()).coerceIn(0f, 1f) else 0f
    val speed = if (st.speedKmh.isNaN()) 0.0 else st.speedKmh
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        ProgressRing(progress = progress, diameter = 220.dp, strokeWidth = 12.dp) {
            Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                Txt(Fmt.clock(st.elapsedSec), sty = Sty.NUMBER, color = Lux.text)
                Txt(
                    if (target > 0) t("sportlive.target.of", Fmt.clock(target.toLong())) else t("sportlive.free"),
                    sty = Sty.CAPTION,
                    color = Lux.textDim
                )
            }
        }
        VGap(16.dp)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SportLiveMetric(t("sportlive.lbl.kcal"), st.kcal.toString(), t("sportlive.unit.kcal"), Modifier.weight(1f))
            if (showDist) {
                SportLiveMetric(
                    t("sportlive.lbl.dist"), Fmt.oneDecimal(st.distanceM / 1000.0), t("sportlive.unit.km"),
                    Modifier.weight(1f)
                )
            }
        }
        if (showDist) {
            VGap(10.dp)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SportLiveMetric(t("sportlive.lbl.speed"), Fmt.oneDecimal(speed), t("sportlive.unit.kmh"), Modifier.weight(1f))
                SportLiveMetric(t("sportlive.lbl.pace"), WkGeo.pace(speed), t("sportlive.unit.pace"), Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun SportLiveMetric(label: String, value: String, unit: String, modifier: Modifier) {
    GlassCard(modifier = modifier) {
        Txt(label, sty = Sty.CAPTION, color = Lux.textDim)
        Txt(value, sty = Sty.H3, color = Lux.text)
        Txt(unit, sty = Sty.CAPTION, color = Lux.textFaint)
    }
}
