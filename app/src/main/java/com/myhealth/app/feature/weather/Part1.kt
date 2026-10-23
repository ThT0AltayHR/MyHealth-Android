package com.myhealth.app.feature.weather

import com.myhealth.app.core.Json
import com.myhealth.app.core.dbl
import com.myhealth.app.core.int
import com.myhealth.app.core.list
import com.myhealth.app.core.obj
import com.myhealth.app.core.str
import com.myhealth.app.core.strList
import com.myhealth.app.data.DayForecast
import com.myhealth.app.data.WeatherCondition
import com.myhealth.app.data.WeatherNow
import java.time.LocalDate
import java.util.Locale
import kotlin.math.roundToInt

/** Saatlik kayit: label "HH:mm", sicaklik, yagis olasiligi (%). */
data class WxHour(val label: String, val tempC: Int, val rainPct: Int)

/** Bir yenilemenin sonucu. rain3h: onumuzdeki 3 saatteki en yuksek yagis olasiligi. */
class WxData(val now: WeatherNow, val days: List<DayForecast>, val hours: List<WxHour>, val rain3h: Int)

/** Open-Meteo baglantisi ve yanit ayristirma (saf mantik, Android'e bagimli degil). */
object WxApi {
    private const val BASE = "https://api.open-meteo.com/v1/forecast"

    /** WMO hava kodu -> 6 durum. */
    fun condition(code: Int): WeatherCondition = when (code) {
        0 -> WeatherCondition.SUNNY
        1, 2, 3, 45, 48 -> WeatherCondition.CLOUDY
        51, 53, 55, 56, 57, 61, 80 -> WeatherCondition.LIGHT_RAIN
        63, 65, 66, 67, 81, 82 -> WeatherCondition.HEAVY_RAIN
        71, 73, 75, 77, 85, 86 -> WeatherCondition.SNOW
        95, 96, 99 -> WeatherCondition.STORM
        else -> WeatherCondition.CLOUDY
    }

    fun url(lat: Double, lng: Double): String {
        val la = String.format(Locale.US, "%.4f", lat)
        val lo = String.format(Locale.US, "%.4f", lng)
        return BASE + "?latitude=" + la + "&longitude=" + lo +
            "&current=temperature_2m,relative_humidity_2m,apparent_temperature,is_day,weather_code,wind_speed_10m" +
            "&hourly=temperature_2m,precipitation_probability" +
            "&daily=weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max" +
            "&timezone=auto&forecast_days=7&wind_speed_unit=kmh"
    }

    private fun Map<*, *>.num(key: String): Double? = dbl(key, Double.NaN).takeIf { it.isFinite() }

    private fun List<*>.nums(): List<Double?> = map { (it as? Number)?.toDouble()?.takeIf { d -> d.isFinite() } }

    /** Yanit bozuksa veya gunluk veri yoksa null. */
    fun parse(text: String, city: String, nowMs: Long): WxData? {
        val root = Json.parseOrNull(text) as? Map<*, *> ?: return null
        val cur = root.obj("current")
        val temp = cur.num("temperature_2m") ?: return null
        val nowIso = cur.str("time")
        val now = WeatherNow(
            city = city,
            tempC = temp.roundToInt(),
            feelsC = (cur.num("apparent_temperature") ?: temp).roundToInt(),
            humidity = (cur.num("relative_humidity_2m") ?: 0.0).roundToInt(),
            windKmh = (cur.num("wind_speed_10m") ?: 0.0).roundToInt(),
            condition = condition((cur.num("weather_code") ?: 0.0).toInt()),
            isDay = cur.num("is_day") != 0.0,
            updatedMs = nowMs
        )
        val hourly = root.obj("hourly")
        val hTimes = hourly.strList("time")
        val hTemp = hourly.list("temperature_2m").nums()
        val hRain = hourly.list("precipitation_probability").nums().map { (it ?: 0.0).roundToInt() }
        val start = hTimes.indexOfFirst { it > nowIso }.coerceAtLeast(0)
        val end = minOf(start + 12, hTimes.size)
        val hours = (start until end).map { i ->
            WxHour(
                hTimes[i].substringAfter('T', "").take(5),
                (hTemp.getOrNull(i) ?: 0.0).roundToInt(),
                hRain.getOrElse(i) { 0 }
            )
        }
        val rain3h = (start until minOf(start + 3, hTimes.size)).maxOfOrNull { hRain.getOrElse(it) { 0 } } ?: 0
        val daily = root.obj("daily")
        val dTimes = daily.strList("time")
        val dCode = daily.list("weather_code").nums()
        val dMax = daily.list("temperature_2m_max").nums()
        val dMin = daily.list("temperature_2m_min").nums()
        val dRain = daily.list("precipitation_probability_max").nums()
        val days = ArrayList<DayForecast>()
        for (i in 0 until minOf(dTimes.size, 7)) {
            val dow = runCatching { LocalDate.parse(dTimes[i]).dayOfWeek.value }.getOrNull() ?: continue
            val mx = dMax.getOrNull(i) ?: continue
            val mn = dMin.getOrNull(i) ?: continue
            val code = (dCode.getOrNull(i) ?: 0.0).toInt()
            val rain = (dRain.getOrNull(i) ?: 0.0).roundToInt()
            days.add(DayForecast(dTimes[i], dow, mn.roundToInt(), mx.roundToInt(), condition(code), rain))
        }
        if (days.isEmpty()) return null
        return WxData(now, days, hours, rain3h)
    }

    fun hoursToJson(h: List<WxHour>): List<Map<String, Any?>> =
        h.map { mapOf("h" to it.label, "t" to it.tempC, "p" to it.rainPct) }

    fun hoursFromJson(v: Any?): List<WxHour> =
        (v as? List<*>)?.mapNotNull { m -> (m as? Map<*, *>)?.let { WxHour(it.str("h"), it.int("t"), it.int("p")) } }
            ?: emptyList()
}
