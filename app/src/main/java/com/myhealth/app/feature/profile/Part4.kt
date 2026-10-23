package com.myhealth.app.feature.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.myhealth.app.core.Fmt
import com.myhealth.app.data.HealthLists
import com.myhealth.app.data.UserProfile
import com.myhealth.app.feature.assistant.AiInsights
import com.myhealth.app.feature.assistant.AssistantContext
import com.myhealth.app.feature.health.Exercises
import com.myhealth.app.feature.health.HealthLog
import com.myhealth.app.feature.health.SymptomTag
import com.myhealth.app.nav.LocalNav
import com.myhealth.app.nav.Route
import com.myhealth.app.ui.kit.AiDisclaimer
import com.myhealth.app.ui.kit.GhostButton
import com.myhealth.app.ui.kit.HGap
import com.myhealth.app.ui.kit.Ic
import com.myhealth.app.ui.kit.LuxIcon
import com.myhealth.app.ui.kit.MedIcons
import com.myhealth.app.ui.kit.ProgressRing
import com.myhealth.app.ui.kit.SmallTag
import com.myhealth.app.ui.kit.VGap
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt

// ---------------------------------------------------------------- belirti, egilim, aliskanlik ve yapay zeka panelleri (16-23 + AI)

/** Nefes seanslarinin ortalama siddeti (belirti) belirli bir pencerede. */
private fun breathAvg(fromAgo: Int, toAgo: Int): Double? =
    avgSeverity(entriesIn(fromAgo, toAgo).filter { it.exerciseId == Exercises.BREATHING })

/** 16. Son 7 gunde belirti sikligi (egzersiz sonu cevaplarindan). */
@Composable
internal fun PanelSymptoms() {
    ObserveHealthData()
    val counts = HealthLog.symptomCounts(7)
    val total = counts.values.sum()
    val top = counts.maxByOrNull { it.value }?.key
    PanelCard(Ic.PULSE, "health.panel.symptoms") {
        BigValue(total.toString(), t("health.unit.times"))
        if (top == null) {
            Txt(t("profile.none_sym"), sty = Sty.CAPTION, color = Lux.textFaint)
        } else {
            Txt(t("profile.symptom_top_fmt", t(top.nameKey)), sty = Sty.CAPTION, color = Lux.textFaint)
        }
    }
}

/** 17. Nefes rahatligi egilimi: son 7 gun ortalamasi ile onceki 7 gun. */
@Composable
internal fun PanelBreathTrend() {
    ObserveHealthData()
    val cur = breathAvg(7, 0)
    PanelCard(Ic.WAVES, "health.panel.breath_trend") {
        BigValue(if (cur == null) t("profile.none") else Fmt.oneDecimal(cur))
        TrendTag(trendOf(breathAvg(14, 7), cur))
    }
}

/** 18. Yorgunluk egilimi: son 7 gun ortalamasi ile onceki 7 gun. */
@Composable
internal fun PanelFatigueTrend() {
    ObserveHealthData()
    val cur = tagSeverity(SymptomTag.FATIGUE, 7, 0)
    PanelCard(Ic.BATTERY, "health.panel.fatigue_trend") {
        BigValue(if (cur == null) t("profile.none") else Fmt.oneDecimal(cur))
        TrendTag(trendOf(tagSeverity(SymptomTag.FATIGUE, 14, 7), cur))
    }
}

/** 19. Toparlanma puani (halka). */
@Composable
internal fun PanelRecovery() {
    ObserveHealthData()
    val score = recoveryScore()
    val frac = ratio(score.toDouble(), 100.0)
    PanelCard(Ic.SHIELD, "health.panel.recovery") {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ProgressRing(progress = frac, diameter = 72.dp, strokeWidth = 8.dp) {
                Txt(score.toString(), sty = Sty.SMALL, color = Lux.text)
            }
            HGap(14.dp)
            Txt(t("profile.recovery_hint"), Modifier.weight(1f), sty = Sty.CAPTION, color = Lux.textFaint)
        }
    }
}

/** 20. Hidrasyon duzeni: son 7 gunun su grafigi ve hedefe ulasilan gun sayisi. */
@Composable
internal fun PanelHydration(p: UserProfile) {
    ObserveHealthData()
    val target = waterTargetMl(p)
    val series = waterSeries(7)
    val hit = series.count { it >= target }
    PanelCard(Ic.DROP, "health.panel.hydration") {
        BarChart(series.map { it.toFloat() }, dayKeys(7).map { dayLabel(it) })
        Txt(t("profile.hydration_fmt", hit.toString()), sty = Sty.CAPTION, color = Lux.textFaint)
    }
}

/** 21. Ogun kcal ortalamasi (kcal kaydi olan gunler) ve son 7 gun grafigi. */
@Composable
internal fun PanelMealKcal() {
    ObserveHealthData()
    val series = kcalSeries(7)
    val days = series.count { it > 0 }
    val avg = if (days == 0) 0 else series.fold(0) { acc, v -> acc + v } / days
    PanelCard(Ic.MEAL, "health.panel.meal_kcal") {
        BigValue(avg.toString(), t("profile.u.kcal"))
        BarChart(series.map { it.toFloat() }, dayKeys(7).map { dayLabel(it) })
    }
}

/** 22. Hastalik sayisi ve ilk bes tanesi (cip). */
@Composable
internal fun PanelConditions(p: UserProfile) {
    val list = p.conditions.toList()
    PanelCard(Ic.MEDICAL, "health.panel.conditions") {
        BigValue(list.size.toString())
        if (list.isEmpty()) {
            Txt(t("profile.none"), sty = Sty.CAPTION, color = Lux.textFaint)
        } else {
            list.take(5).forEach { id ->
                SmallTag(t(HealthLists.conditionKey(id)))
                VGap(4.dp)
            }
        }
    }
}

/** 23. Ilac ozeti (duzenli / ara sira) ve ilaclar sayfasina gecis. */
@Composable
internal fun PanelMedications() {
    val nav = LocalNav.current
    val (reg, occ) = medSets()
    PanelCard(MedIcons.PILL, "health.panel.medications") {
        BigValue((reg.size + occ.size).toString())
        Txt(t("profile.med_split_fmt", reg.size.toString(), occ.size.toString()), sty = Sty.CAPTION, color = Lux.textFaint)
        VGap(8.dp)
        GhostButton(text = t("profile.meds_open"), onClick = { nav.push(Route.Medications) })
    }
}

/**
 * DOGRULA: AiInsights icin AssistantContext nasil uretilir bilinmiyor (kurucu/fabrika API'si gorulmedi).
 * Null ise AI paneli bos durumu gosterir; uretici baglaninca burasi degistirilmelidir.
 */
internal fun assistantContextOrNull(): AssistantContext? = null

/** Olasi saglik sorunlari (yapay zeka): seviyeye gore renk; altinda tibbi uyari. */
@Composable
internal fun PanelAiFlags() {
    val flags = assistantContextOrNull()?.let { AiInsights.healthFlags(it) } ?: emptyList()
    PanelCard(MedIcons.SPARKLES, "profile.section_ai") {
        if (flags.isEmpty()) {
            Txt(t("profile.ai_none"), sty = Sty.CAPTION, color = Lux.textFaint)
        } else {
            flags.forEach { f ->
                val c = when (f.level) {
                    3 -> Lux.red
                    2 -> Lux.amber
                    else -> Lux.blue
                }
                Row(verticalAlignment = Alignment.Top) {
                    LuxIcon(if (f.level >= 2) Ic.WARNING else Ic.INFO, tint = c, size = 18.dp)
                    HGap(8.dp)
                    Column {
                        Txt(f.title, sty = Sty.BODY_STRONG, color = c)
                        Txt(f.detail, sty = Sty.SMALL, color = Lux.textDim)
                    }
                }
                VGap(8.dp)
            }
        }
        AiDisclaimer(compact = true)
    }
}
