package com.myhealth.app.feature.assistant

import android.content.Context
import com.myhealth.app.core.Fmt
import com.myhealth.app.core.I18n
import com.myhealth.app.data.Repo
import com.myhealth.app.data.SportsCatalog
import com.myhealth.app.feature.fasting.FastingPlan
import com.myhealth.app.feature.health.HealthLog
import com.myhealth.app.feature.meds.MedStore
import com.myhealth.app.feature.sports.WorkoutSession
import com.myhealth.app.util.Net

/** Uygulamanin her yerinde yapay zeka baglamini tek noktadan olusturur. Hicbir zaman istisna firlatmaz. */
object AssistantContexts {
    fun build(ctx: Context): AssistantContext {
        val lang = Repo.lang.value
        val live = WorkoutSession.state.value
        val sport = if (live.active) SportsCatalog.byId(live.sportId) else null
        val settings = Repo.settings.value
        val online = try { Net.isOnline(ctx) } catch (e: Exception) { false }
        val fasting = try { FastingPlan.today(ctx) } catch (e: Exception) { null }
        return AssistantContext(
            lang = lang,
            profile = Repo.profile.value,
            nowMs = System.currentTimeMillis(),
            recentWorkouts = Repo.workouts.value.sortedByDescending { it.startMs }.take(10),
            live = live,
            liveSportName = if (sport != null) I18n.get(lang, sport.nameKey) else "",
            weather = Repo.weather.value,
            memory = Repo.memory.value,
            todayKcal = Repo.mealsOn(Fmt.todayKey()).sumOf { it.kcal },
            todaySteps = Repo.stepsToday(),
            stepGoal = settings.stepGoal,
            waterGlasses = Repo.waterToday(),
            healthEntries = HealthLog.recent(60),
            medsRegular = MedStore.regular.value.toList(),
            medsOccasional = MedStore.occasional.value.toList(),
            doctorName = settings.doctorName,
            doctorPhone = settings.doctorPhone,
            online = online,
            ramadan = fasting?.ramadan ?: false,
            fastingToday = fasting?.fastingToday ?: false,
            imsakMin = fasting?.imsakMin ?: 0,
            iftarMin = fasting?.iftarMin ?: 0
        )
    }
}
