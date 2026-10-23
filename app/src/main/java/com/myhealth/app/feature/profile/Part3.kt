package com.myhealth.app.feature.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.myhealth.app.core.Fmt
import com.myhealth.app.data.Repo
import com.myhealth.app.data.UserProfile
import com.myhealth.app.feature.health.Exercises
import com.myhealth.app.feature.health.HealthLog
import com.myhealth.app.logic.Body
import com.myhealth.app.ui.kit.HGap
import com.myhealth.app.ui.kit.Ic
import com.myhealth.app.ui.kit.LuxIcon
import com.myhealth.app.ui.kit.MedIcons
import com.myhealth.app.ui.kit.ProgressRing
import com.myhealth.app.ui.kit.SportIcons
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt

// ---------------------------------------------------------------- olcum, hedef ve aktivite panelleri (1-15)

/** 1. Vucut kitle indeksi ve sinifi. */
@Composable
internal fun PanelBmi(p: UserProfile) {
    PanelCard(Ic.SCALE, "health.panel.bmi") {
        BigValue(Fmt.oneDecimal(p.bmi))
        Txt(t(bmiKey(Body.bmiClass(p.bmi))), sty = Sty.SMALL, color = Lux.textDim)
        LinearBar(ratio(p.bmi - 15.0, 25.0)) // olcek: BMI 15..40
    }
}

/** 2. Ideal kilo araligi (BMI 18.5-24.9) ve mevcut kilo. */
@Composable
internal fun PanelIdeal(p: UserProfile) {
    val range = Body.healthyWeightRange(p.heightCm)
    val kg = t("profile.u.kg")
    PanelCard(Ic.TARGET, "health.panel.ideal") {
        BigValue(t("profile.range_fmt", range.first, range.last), kg)
        Txt(t("profile.now_fmt", "${p.weightKg} $kg"), sty = Sty.CAPTION, color = Lux.textFaint)
        LinearBar(ratio(p.weightKg.toDouble(), range.last * 1.25))
    }
}

/** 3. Bazal metabolizma hizi (Mifflin-St Jeor). */
@Composable
internal fun PanelBmr(p: UserProfile) {
    val v = Body.bmr(p)
    PanelCard(Ic.FLAME, "health.panel.bmr") {
        BigValue(v.toInt().toString(), t("profile.u.kcal"))
        LinearBar(ratio(v, 3000.0)) // olcek: 3000 kcal
    }
}

/** 4. Gunluk toplam enerji ihtiyaci (haftalik antrenmana gore aktivite katsayisi). */
@Composable
internal fun PanelTdee(p: UserProfile) {
    val v = Body.tdee(p, activityFactor())
    PanelCard(Ic.BOLT, "health.panel.tdee") {
        BigValue(v.toInt().toString(), t("profile.u.kcal"))
        LinearBar(ratio(v, 4000.0)) // olcek: 4000 kcal
    }
}

/** 5. Bugunku su miktari ve hedef (halka). */
@Composable
internal fun PanelWater(p: UserProfile) {
    ObserveHealthData()
    val today = Repo.waterToday()
    val target = waterTargetMl(p)
    val frac = ratio(today.toDouble(), target.toDouble())
    PanelCard(Ic.DROP, "health.panel.water") {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ProgressRing(progress = frac, diameter = 72.dp, strokeWidth = 8.dp) {
                Txt("${(frac * 100f).toInt()}%", sty = Sty.SMALL, color = Lux.text)
            }
            HGap(14.dp)
            Column {
                BigValue(litersText(today), t("profile.u.l"))
                Txt(t("profile.of_fmt", litersText(today), litersText(target)), sty = Sty.CAPTION, color = Lux.textFaint)
            }
        }
    }
}

/** 6. Bugunku protein (ogunlerden) ve hedef. */
@Composable
internal fun PanelProtein(p: UserProfile) {
    ObserveHealthData()
    val got = Repo.mealsOn(Fmt.todayKey()).fold(0) { acc, m -> acc + m.proteinG }
    val target = proteinTargetG(p)
    PanelCard(Ic.APPLE, "health.panel.protein") {
        BigValue(got.toString(), t("profile.u.g"))
        LinearBar(ratio(got.toDouble(), target.toDouble()))
        Txt(t("profile.of_fmt", got.toString(), target.toString()), sty = Sty.CAPTION, color = Lux.textFaint)
    }
}

/** 7. Bugunku adim ve gunluk hedef (halka). */
@Composable
internal fun PanelStepsToday() {
    ObserveHealthData()
    val steps = Repo.stepsToday()
    val goal = stepGoal()
    val frac = ratio(steps.toDouble(), goal.toDouble())
    PanelCard(Ic.STEPS, "health.panel.steps_today") {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ProgressRing(progress = frac, diameter = 72.dp, strokeWidth = 8.dp) {
                Txt("${(frac * 100f).toInt()}%", sty = Sty.SMALL, color = Lux.text)
            }
            HGap(14.dp)
            Column {
                BigValue(steps.toString(), t("health.unit.steps"))
                Txt(t("profile.of_fmt", steps.toString(), goal.toString()), sty = Sty.CAPTION, color = Lux.textFaint)
            }
        }
    }
}

/** 8. Son 7 gunun adim grafigi. */
@Composable
internal fun PanelStepsWeek() {
    ObserveHealthData()
    val values = stepsSeries(7)
    PanelCard(Ic.CHART, "health.panel.steps_week") {
        BigValue(values.fold(0) { acc, v -> acc + v }.toString(), t("health.unit.steps"))
        BarChart(values.map { it.toFloat() }, dayKeys(7).map { dayLabel(it) })
    }
}

/** 9. Haftalik antrenman sayisi. */
@Composable
internal fun PanelWorkoutsWeek() {
    ObserveHealthData()
    val n = workoutsSince(7).size
    PanelCard(Ic.SPORTS, "health.panel.workouts_week") {
        BigValue(n.toString(), t("health.unit.times"))
        LinearBar(ratio(n.toDouble(), 5.0)) // olcek: haftada 5 antrenman
    }
}

/** 10. Son 7 gunde aktif dakika (WHO onerisi: 150 dk). */
@Composable
internal fun PanelActiveMin() {
    ObserveHealthData()
    val min = workoutsSince(7).fold(0) { acc, w -> acc + w.durationSec } / 60
    PanelCard(Ic.TIMER, "health.panel.active_min") {
        BigValue(min.toString(), t("health.unit.min"))
        LinearBar(ratio(min.toDouble(), 150.0))
        Txt(t("profile.of_fmt", min.toString(), "150"), sty = Sty.CAPTION, color = Lux.textFaint)
    }
}

/** 11. Son 7 gunde yakilan kcal (antrenman kayitlarindan). */
@Composable
internal fun PanelKcalBurned() {
    ObserveHealthData()
    val kcal = workoutsSince(7).fold(0) { acc, w -> acc + w.calories }
    PanelCard(Ic.FLAME, "health.panel.kcal_burned") {
        BigValue(kcal.toString(), t("profile.u.kcal"))
        LinearBar(ratio(kcal.toDouble(), 2000.0)) // olcek: 2000 kcal
    }
}

/** 12. Son 7 gunde toplam mesafe. */
@Composable
internal fun PanelDistance() {
    ObserveHealthData()
    val meters = workoutsSince(7).fold(0.0) { acc, w -> acc + w.distanceM }
    PanelCard(Ic.ROUTE, "health.panel.distance") {
        BigValue(Fmt.km(meters))
        LinearBar(ratio(meters, 10_000.0)) // olcek: 10 km
    }
}

/** 13. En cok yapilan spor (ikon + ad). */
@Composable
internal fun PanelFavSport() {
    ObserveHealthData()
    val id = favSport()
    PanelCard(Ic.TROPHY, "health.panel.fav_sport") {
        if (id == null) {
            Txt(t("profile.none"), sty = Sty.BODY, color = Lux.textDim)
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                LuxIcon(SportIcons.sport(id), tint = Lux.gold, size = 28.dp)
                HGap(10.dp)
                Txt(t(Exercises.nameKey(id)), sty = Sty.BODY_STRONG, color = Lux.text)
            }
        }
    }
}

/** 14. Art arda aktif gun serisi. */
@Composable
internal fun PanelStreak() {
    ObserveHealthData()
    val days = streakDays()
    PanelCard(Ic.CALENDAR, "health.panel.streak") {
        BigValue(days.toString(), t("health.unit.days"))
        Txt(t("profile.streak_hint"), sty = Sty.CAPTION, color = Lux.textFaint)
    }
}

/** 15. Nefes seanslari: toplam ve son 7 gun. */
@Composable
internal fun PanelBreath() {
    ObserveHealthData()
    val total = HealthLog.entries.value.count { it.exerciseId == Exercises.BREATHING }
    val week = entriesIn(7, 0).count { it.exerciseId == Exercises.BREATHING }
    PanelCard(MedIcons.LUNGS, "health.panel.breath") {
        BigValue(total.toString(), t("health.unit.times"))
        Txt(t("profile.week_fmt", week.toString()), sty = Sty.CAPTION, color = Lux.textFaint)
    }
}
