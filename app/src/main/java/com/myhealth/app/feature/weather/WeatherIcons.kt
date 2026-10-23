package com.myhealth.app.feature.weather

import com.myhealth.app.data.WeatherCondition
import com.myhealth.app.ui.kit.Ic

/**
 * Ozel hava durumu SVG ikonlari (24x24, cizgi stili). LuxIcon(WxIcons.path(c, isDay)) seklinde cizilir.
 * nameKey: ceviri anahtari "weather.cond.<id>" (StrWeather tablosunda 6 durumun hepsi bulunmalidir).
 * TASLAK: Ikon modulu gercek cizimleri doldurur; imzalar SABITTIR.
 */
object WxIcons {
    fun path(c: WeatherCondition, isDay: Boolean = true): String = when (c) {
        WeatherCondition.SUNNY -> if (isDay) Ic.SUN else Ic.MOON
        WeatherCondition.CLOUDY -> Ic.CLOUD
        WeatherCondition.LIGHT_RAIN -> Ic.RAIN
        WeatherCondition.HEAVY_RAIN -> Ic.RAIN
        WeatherCondition.STORM -> Ic.STORM
        WeatherCondition.SNOW -> Ic.SNOW
    }

    fun nameKey(c: WeatherCondition): String = "weather.cond." + c.id

    /** Nem, ruzgar, hissedilen sicaklik gibi ek ikonlar. */
    val humidity: String get() = Ic.DROP
    val wind: String get() = Ic.WIND
    val feelsLike: String get() = Ic.THERMOMETER
}
