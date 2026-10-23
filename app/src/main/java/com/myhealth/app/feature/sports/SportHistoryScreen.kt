package com.myhealth.app.feature.sports

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.myhealth.app.data.Repo
import com.myhealth.app.data.WorkoutRecord
import com.myhealth.app.nav.LocalNav
import com.myhealth.app.ui.kit.Chip
import com.myhealth.app.ui.kit.ConfirmDialog
import com.myhealth.app.ui.kit.EmptyState
import com.myhealth.app.ui.kit.Ic
import com.myhealth.app.ui.kit.LocalBottomClearance
import com.myhealth.app.ui.kit.ScreenScaffold
import com.myhealth.app.ui.kit.SectionTitle
import com.myhealth.app.ui.kit.VGap
import com.myhealth.app.ui.theme.Dim
import com.myhealth.app.ui.t

/** Spor gecmisi sayfasi: gunlere gore gruplu kayitlar, spora gore suzme, toplamlar ve silme onayi. */
@Composable
fun SportHistoryScreen() {
    val nav = LocalNav.current
    val records by Repo.workouts.collectAsState()
    var filterId by remember { mutableStateOf<String?>(null) }
    var pending by remember { mutableStateOf<WorkoutRecord?>(null) }

    val ordered = records.sortedByDescending { it.startMs }
    val shown = filterId?.let { f -> ordered.filter { it.sportId == f } } ?: ordered
    val sportIds = ordered.map { it.sportId }.distinct()
    val grouped = shown.groupBy { localDateOfMs(it.startMs) }

    ScreenScaffold(title = t("sports.hist.title"), onBack = { nav.pop() }) {
        if (records.isEmpty()) {
            EmptyState(
                icon = Ic.CLOCK,
                title = t("sports.hist.empty"),
                subtitle = t("sports.hist.empty.sub"),
                modifier = Modifier.fillMaxWidth().padding(top = 48.dp)
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = Dim.pad),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Chip(text = t("sports.hist.all"), selected = filterId == null, onClick = { filterId = null })
                for (id in sportIds) {
                    Chip(text = sportDisplayName(id), selected = filterId == id, onClick = { filterId = id })
                }
            }
            VGap(12.dp)
            HistoryTotalsCard(records = shown, modifier = Modifier.padding(horizontal = Dim.pad))
            VGap(12.dp)
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(start = Dim.pad, end = Dim.pad, bottom = LocalBottomClearance.current + 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for ((day, list) in grouped) {
                    item(key = "d_" + day.toString()) { SectionTitle(text = fmtSportDate(day)) }
                    items(list, key = { it.id }) { rec ->
                        HistoryRecordRow(record = rec, onDelete = { pending = rec })
                    }
                }
            }
        }
    }

    pending?.let { rec ->
        ConfirmDialog(
            title = t("sports.hist.del.title"),
            message = t("sports.hist.del.msg", sportDisplayName(rec.sportId), fmtSportDate(localDateOfMs(rec.startMs))),
            confirmText = t("sports.hist.del.ok"),
            onConfirm = {
                Repo.deleteWorkout(rec.id)
                pending = null
            },
            onDismiss = { pending = null },
            danger = true
        )
    }
}
