package com.myhealth.app.feature.profile

import androidx.compose.foundation.lazy.LazyListScope
import com.myhealth.app.data.UserProfile
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt

/** Profildeki 24 saglik paneli (her biri ayri LazyColumn ogesi): once yapay zeka uyarilari, sonra olcumler, antrenman, belirti ve egilimler, en sonda kisisel listeler. */
internal fun LazyListScope.ProfilePanelItems(p: UserProfile) {
    item(key = "sec_panels") {
        Txt(t("profile.section.panels"), sty = Sty.H3, color = Lux.text)
    }
    item(key = "panel_ai") { PanelAiFlags() }
    item(key = "panel_bmi") { PanelBmi(p) }
    item(key = "panel_ideal") { PanelIdeal(p) }
    item(key = "panel_bmr") { PanelBmr(p) }
    item(key = "panel_tdee") { PanelTdee(p) }
    item(key = "panel_water") { PanelWater(p) }
    item(key = "panel_hydration") { PanelHydration(p) }
    item(key = "panel_protein") { PanelProtein(p) }
    item(key = "panel_meal") { PanelMealKcal() }
    item(key = "panel_steps_today") { PanelStepsToday() }
    item(key = "panel_steps_week") { PanelStepsWeek() }
    item(key = "panel_workouts") { PanelWorkoutsWeek() }
    item(key = "panel_active") { PanelActiveMin() }
    item(key = "panel_kcal") { PanelKcalBurned() }
    item(key = "panel_distance") { PanelDistance() }
    item(key = "panel_fav_sport") { PanelFavSport() }
    item(key = "panel_streak") { PanelStreak() }
    item(key = "panel_breath") { PanelBreath() }
    item(key = "panel_breath_trend") { PanelBreathTrend() }
    item(key = "panel_symptoms") { PanelSymptoms() }
    item(key = "panel_fatigue") { PanelFatigueTrend() }
    item(key = "panel_recovery") { PanelRecovery() }
    item(key = "panel_conditions") { PanelConditions(p) }
    item(key = "panel_meds") { PanelMedications() }
}
