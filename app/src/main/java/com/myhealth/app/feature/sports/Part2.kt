package com.myhealth.app.feature.sports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.myhealth.app.data.WorkoutRecord
import com.myhealth.app.ui.kit.GlassCard
import com.myhealth.app.ui.kit.HGap
import com.myhealth.app.ui.kit.IconBtn
import com.myhealth.app.ui.kit.Ic
import com.myhealth.app.ui.kit.LuxIcon
import com.myhealth.app.ui.kit.SportIcons
import com.myhealth.app.ui.kit.VGap
import com.myhealth.app.ui.theme.Dim
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt
import com.myhealth.app.ui.t

/** Gecmis kaydi: ikon, spor adi, saat, sure, mesafe, kcal ve silme dugmesi (48dp dokunma alani). */
@Composable
internal fun HistoryRecordRow(record: WorkoutRecord, onDelete: () -> Unit) {
    val details = listOf(
        fmtSportClock(record.startMs),
        fmtSportDuration(record.durationSec),
        fmtSportDistance(record.distanceM),
        record.calories.toString() + " " + t("sports.kcal")
    ).joinToString(" · ")
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        radius = Dim.radiusSmall,
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            LuxIcon(path = SportIcons.sport(record.sportId), tint = Lux.gold, size = 24.dp)
            HGap(12.dp)
            Column(modifier = Modifier.weight(1f)) {
                Txt(
                    text = sportDisplayName(record.sportId),
                    sty = Sty.BODY_STRONG,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                VGap(2.dp)
                Txt(
                    text = details,
                    sty = Sty.CAPTION,
                    color = Lux.textDim,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconBtn(
                icon = Ic.TRASH,
                onClick = onDelete,
                description = t("sports.hist.delete"),
                size = 48.dp,
                tint = Lux.textDim
            )
        }
    }
}

/** Gecmis ozeti: kayit sayisi, toplam sure, toplam mesafe ve toplam kcal. */
@Composable
internal fun HistoryTotalsCard(records: List<WorkoutRecord>, modifier: Modifier = Modifier) {
    val totalSec = records.sumOf { it.durationSec }
    val totalM = records.sumOf { it.distanceM }
    val totalKcal = records.sumOf { it.calories }
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        warm = true,
        radius = Dim.radiusSmall,
        contentPadding = PaddingValues(14.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            TotalCell(value = records.size.toString(), label = t("sports.hist.count"))
            TotalCell(value = fmtSportDuration(totalSec), label = t("sports.hist.time"))
            TotalCell(value = fmtSportDistance(totalM), label = t("sports.hist.dist"))
            TotalCell(value = totalKcal.toString(), label = t("sports.kcal"))
        }
    }
}

@Composable
private fun TotalCell(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Txt(text = value, sty = Sty.H3, maxLines = 1, overflow = TextOverflow.Ellipsis, align = TextAlign.Center)
        Txt(
            text = label,
            sty = Sty.CAPTION,
            color = Lux.textDim,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            align = TextAlign.Center
        )
    }
}
