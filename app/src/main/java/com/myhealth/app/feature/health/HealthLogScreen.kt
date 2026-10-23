package com.myhealth.app.feature.health

import androidx.compose.runtime.Composable
import com.myhealth.app.nav.LocalNav
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.myhealth.app.core.Fmt
import com.myhealth.app.feature.report.ReportExportCard
import com.myhealth.app.ui.kit.AiDisclaimer
import com.myhealth.app.ui.kit.Chip
import com.myhealth.app.ui.kit.ConfirmDialog
import com.myhealth.app.ui.kit.EmptyState
import com.myhealth.app.ui.kit.GhostButton
import com.myhealth.app.ui.kit.GlassCard
import com.myhealth.app.ui.kit.HGap
import com.myhealth.app.ui.kit.IconBtn
import com.myhealth.app.ui.kit.Ic
import com.myhealth.app.ui.kit.LocalBottomClearance
import com.myhealth.app.ui.kit.LuxIcon
import com.myhealth.app.ui.kit.MedIcons
import com.myhealth.app.ui.kit.ScreenScaffold
import com.myhealth.app.ui.kit.SmallTag
import com.myhealth.app.ui.kit.SportIcons
import com.myhealth.app.ui.kit.VGap
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt
import com.myhealth.app.util.CrashLog
import java.util.Calendar
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Ayarlardaki "saglik belgesi" (Route.HealthLog): gun gruplu egzersiz sonu cevaplari, belirti filtresi, tek ve toplu silme. Imza sabit. */
@Composable
fun HealthLogScreen() {
    val nav = LocalNav.current
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val entries by HealthLog.entries.collectAsState()
    var filter by remember { mutableStateOf<SymptomTag?>(null) }
    var pendingDelete by remember { mutableStateOf<Long?>(null) }
    var confirmClear by remember { mutableStateOf(false) }
    val shown = remember(entries, filter) {
        val f = filter
        if (f == null) entries else entries.filter { e -> e.answers.any { it.tag == f && it.severity >= 1 } }
    }
    val groups = remember(shown) { shown.groupBy { Fmt.dateKey(it.tsMs) }.toList() }
    val top = remember(entries) { HealthLog.symptomCounts(36500).entries.sortedByDescending { it.value }.take(3) }
    val tagsPresent = remember(entries) {
        entries.flatMap { e -> e.answers.filter { it.severity >= 1 }.map { it.tag } }.distinct()
    }

    fun runDb(tag: String, block: () -> Unit) {
        scope.launch {
            try {
                withContext(Dispatchers.IO) { block() }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                CrashLog.note(ctx, tag, e)
            }
        }
    }

    ScreenScaffold(title = t("quiz.log.title"), onBack = { nav.pop() }) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = LocalBottomClearance.current + 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(key = "disclaimer") { AiDisclaimer(modifier = Modifier.fillMaxWidth()) }
            item(key = "report") { ReportExportCard(modifier = Modifier.fillMaxWidth()) }
            if (entries.isEmpty()) {
                item(key = "empty") {
                    EmptyState(
                        icon = MedIcons.DOCUMENT,
                        title = t("quiz.log.empty_title"),
                        subtitle = t("quiz.log.empty_sub"),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                if (top.isNotEmpty()) item(key = "top") { TopSymptoms(top) }
                item(key = "filters") {
                    FilterRow(tagsPresent, filter) { filter = it }
                }
                item(key = "clear") {
                    GhostButton(text = t("quiz.log.clear_all"), onClick = { confirmClear = true }, tint = Lux.red)
                }
                groups.forEach { group ->
                    item(key = "day_" + group.first) {
                        val label = if (group.first == Fmt.todayKey()) t("quiz.log.today") else group.first
                        Txt(label, sty = Sty.LABEL, color = Lux.gold)
                    }
                    items(group.second, key = { it.id }) { entry ->
                        EntryCard(entry, onDelete = { pendingDelete = entry.id })
                    }
                }
            }
        }
    }

    pendingDelete?.let { id ->
        ConfirmDialog(
            title = t("quiz.log.del_title"),
            message = t("quiz.log.del_msg"),
            confirmText = t("quiz.log.del_yes"),
            onConfirm = {
                pendingDelete = null
                runDb("log_delete") { HealthLog.delete(id) }
            },
            onDismiss = { pendingDelete = null },
            danger = true
        )
    }
    if (confirmClear) {
        ConfirmDialog(
            title = t("quiz.log.clear_title"),
            message = t("quiz.log.clear_msg"),
            confirmText = t("quiz.log.del_yes"),
            onConfirm = {
                confirmClear = false
                runDb("log_clear") { HealthLog.clear() }
            },
            onDismiss = { confirmClear = false },
            danger = true
        )
    }
}

@Composable
private fun TopSymptoms(top: List<Map.Entry<SymptomTag, Int>>) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Txt(t("quiz.log.top_title"), sty = Sty.LABEL, color = Lux.textDim)
        VGap(8.dp)
        top.forEach { e ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Txt(t(e.key.nameKey), sty = Sty.BODY, color = Lux.text)
                SmallTag(e.value.toString())
            }
            VGap(6.dp)
        }
    }
}

@Composable
private fun FilterRow(tags: List<SymptomTag>, selected: SymptomTag?, onSelect: (SymptomTag?) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Chip(text = t("quiz.log.filter_all"), selected = selected == null, onClick = { onSelect(null) })
        tags.forEach { tag ->
            Chip(
                text = t(tag.nameKey),
                selected = selected == tag,
                onClick = { onSelect(if (selected == tag) null else tag) }
            )
        }
    }
}

@Composable
private fun EntryCard(entry: HealthEntry, onDelete: () -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            LuxIcon(path = exerciseIcon(entry.exerciseId), tint = Lux.gold, size = 26.dp)
            HGap(12.dp)
            Column(modifier = Modifier.weight(1f)) {
                Txt(
                    t(Exercises.nameKey(entry.exerciseId)),
                    sty = Sty.BODY_STRONG,
                    color = Lux.text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Txt(Fmt.hhmm(minuteOfDay(entry.tsMs)), sty = Sty.CAPTION, color = Lux.textDim)
            }
            IconBtn(
                icon = Ic.TRASH,
                onClick = onDelete,
                description = t("quiz.log.delete"),
                size = 48.dp,
                tint = Lux.textDim
            )
        }
        VGap(10.dp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SmallTag(t("quiz.log.dur_fmt", Fmt.clock(entry.durationSec.toLong())))
            SmallTag(t("quiz.log.sev_fmt", entry.severity.toString()))
        }
        VGap(10.dp)
        entry.answers.forEach { a -> AnswerLine(entry.exerciseId, a) }
    }
}

@Composable
private fun AnswerLine(exerciseId: String, a: QuizAnswer) {
    val questions = remember(exerciseId) { runCatching { HealthQuiz.questionsFor(exerciseId) }.getOrNull().orEmpty() }
    val q = questions.firstOrNull { it.id == a.questionId }
    val opt = q?.options?.getOrNull(a.optionIndex)
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Txt(if (q != null) t(q.textKey) else t(a.tag.nameKey), sty = Sty.SMALL, color = Lux.textDim)
        Txt(
            if (opt != null) t(opt.textKey) else t("quiz.log.sev_fmt", a.severity.toString()),
            sty = Sty.BODY,
            color = Lux.text
        )
    }
}

private fun exerciseIcon(exerciseId: String): String =
    if (exerciseId == Exercises.BREATHING) MedIcons.LUNGS else SportIcons.sport(exerciseId).ifEmpty { Ic.SPORTS }

private fun minuteOfDay(ms: Long): Int {
    val c = Calendar.getInstance()
    c.timeInMillis = ms
    return c.get(Calendar.HOUR_OF_DAY) * 60 + c.get(Calendar.MINUTE)
}
