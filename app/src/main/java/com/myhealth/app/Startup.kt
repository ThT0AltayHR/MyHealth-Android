package com.myhealth.app

import android.content.Context
import com.myhealth.app.feature.alarm.AlarmScheduler
import com.myhealth.app.feature.fasting.FastingScheduler
import com.myhealth.app.feature.health.DailyTip
import com.myhealth.app.feature.meals.MealReminders
import com.myhealth.app.feature.steps.ActivityDetection
import com.myhealth.app.feature.steps.StepTracker
import com.myhealth.app.feature.weather.WeatherScheduler
import com.myhealth.app.util.CrashLog
import com.myhealth.app.util.Notif

/**
 * Zamanlamalari (alarm, oruc, ogun, hava) ayardan yeniden kurar. Her adim ayri korunur:
 * bir ozelligin hatasi digerlerini veya uygulamayi etkilemez.
 */
object Startup {
    /** Application.onCreate'ten (ana thread) cagrilir; hafif olmalidir. */
    fun onAppStart(ctx: Context) {
        Notif.ensureChannels(ctx)
        rescheduleAll(ctx)
    }

    /** Cihaz yeniden basladiginda, uygulama guncellendiginde veya saat/saat dilimi degistiginde. */
    fun onBoot(ctx: Context) {
        Notif.ensureChannels(ctx)
        safe(ctx, "steps_boot") { StepTracker.onBoot(ctx) }
        rescheduleAll(ctx)
    }

    /** MainActivity her one geldiginde. */
    fun onForeground(ctx: Context) {
        safe(ctx, "steps_refresh") { StepTracker.refresh(ctx) }
        safe(ctx, "fasting_daily") { FastingScheduler.rescheduleAll(ctx) }
        safe(ctx, "activity_sync") { ActivityDetection.sync(ctx) }
    }

    private fun rescheduleAll(ctx: Context) {
        safe(ctx, "weather_schedule") { WeatherScheduler.reschedule(ctx) }
        safe(ctx, "alarm_schedule") { AlarmScheduler.rescheduleAll(ctx) }
        safe(ctx, "fasting_schedule") { FastingScheduler.rescheduleAll(ctx) }
        safe(ctx, "meals_schedule") { MealReminders.rescheduleAll(ctx) }
        safe(ctx, "daily_tip") { DailyTip.ensure(ctx) }
        safe(ctx, "activity_sync") { ActivityDetection.sync(ctx) }
    }

    private inline fun safe(ctx: Context, tag: String, f: () -> Unit) {
        try {
            f()
        } catch (e: Throwable) {
            CrashLog.note(ctx, tag, e)
        }
    }
}
