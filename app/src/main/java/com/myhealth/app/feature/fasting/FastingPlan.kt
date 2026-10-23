package com.myhealth.app.feature.fasting

import android.content.Context

/**
 * Bugunku oruc bilgisi. Dakikalar cihaz yerel saatine gore gece yarisindan itibarendir
 * (1440'i asabilir; gosterirken Fmt.hhmm kullanin).
 */
data class FastingInfo(
    val ramadan: Boolean,
    val fastingToday: Boolean,
    val ramadanDay: Int?,
    val imsakMin: Int,
    val iftarMin: Int,
    val locationBased: Boolean
)

/** TASLAK: Fasting modulu doldurur; imzalar SABITTIR. */
object FastingPlan {
    fun today(ctx: Context): FastingInfo =
        FastingInfo(ramadan = false, fastingToday = false, ramadanDay = null, imsakMin = 5 * 60, iftarMin = 19 * 60, locationBased = false)
}
