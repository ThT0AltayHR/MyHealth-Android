package com.myhealth.app.logic

import java.time.LocalDate
import java.time.chrono.HijrahChronology
import java.time.temporal.ChronoField
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.tan

/** Hesaplama yontemi: acilar, sabit sureler ve bolgesel dakika duzeltmeleri. */
enum class PrayerMethod(
    val fajrAngle: Double,
    val ishaAngle: Double,
    val ishaMinutesAfterMaghrib: Int = 0,
    val maghribAngle: Double = 0.0,
    val adjSunrise: Int = 0,
    val adjDhuhr: Int = 0,
    val adjAsr: Int = 0,
    val adjMaghrib: Int = 0
) {
    MWL(18.0, 17.0),
    ISNA(15.0, 15.0),
    EGYPT(19.5, 17.5),
    MAKKAH(18.5, 0.0, ishaMinutesAfterMaghrib = 90),
    KARACHI(18.0, 18.0),
    TEHRAN(17.7, 14.0, maghribAngle = 4.5),
    DIYANET(18.0, 17.0, adjSunrise = -7, adjDhuhr = 5, adjAsr = 4, adjMaghrib = 7),
    SOUTHEAST_ASIA(20.0, 18.0, adjMaghrib = 1)
}

/** Dakika cinsinden (cihaz saat dilimine gore) gunluk vakitler. */
data class PrayerTimes(val fajr: Int, val sunrise: Int, val dhuhr: Int, val asr: Int, val maghrib: Int, val isha: Int) {
    /** Oruc: imsak = sabah vakti basi, iftar = aksam (gunes batimi) vakti. */
    val imsak: Int get() = fajr
    val iftar: Int get() = maghrib
}

object Prayer {
    /** Koordinata gore bolgesel yontem secimi (ag/geocoder gerektirmez). */
    fun methodFor(lat: Double, lng: Double): PrayerMethod = when {
        lat in 35.7..42.3 && lng in 25.6..44.9 -> PrayerMethod.DIYANET        // Turkiye
        lat in 15.5..32.3 && lng in 34.3..56.0 -> PrayerMethod.MAKKAH         // Arabistan yarimadasi
        lat in 22.0..31.8 && lng in 24.6..36.9 -> PrayerMethod.EGYPT          // Misir
        lat in 24.8..39.9 && lng in 44.0..63.4 -> PrayerMethod.TEHRAN         // Iran
        lat in 5.5..37.2 && lng in 60.8..92.5 -> PrayerMethod.KARACHI         // Pakistan, Hindistan, Bangladesh
        lat in -11.5..7.5 && lng in 94.5..141.5 -> PrayerMethod.SOUTHEAST_ASIA // Endonezya, Malezya, Singapur
        lat in 24.0..72.0 && lng in -170.0..-50.0 -> PrayerMethod.ISNA        // Kuzey Amerika
        else -> PrayerMethod.MWL
    }

    /**
     * Gunluk vakitler (gece yarisindan itibaren dakika). Degerler 1440'i asabilir (ornegin yatsi 25:10)
     * veya negatif olabilir; gosterirken Math.floorMod(x, 1440) kullanin, alarm kurarken oldugu gibi kullanin.
     *
     * Kutup gunu/gecesinde (gunes dogmaz/batmaz, ornegin Tromso haziran) "en yakin enlem" yontemi
     * uygulanir: hesap +-48.5 derece enlemle yapilir. Hicbir girdi icin istisna firlatmaz.
     */
    fun compute(date: LocalDate, lat: Double, lng: Double, tzOffsetHours: Double, method: PrayerMethod): PrayerTimes {
        val la = if (lat.isNaN()) 41.0 else lat.coerceIn(-90.0, 90.0)
        val lo = if (lng.isNaN()) 29.0 else lng.coerceIn(-180.0, 180.0)
        val tz = if (tzOffsetHours.isNaN()) 3.0 else tzOffsetHours.coerceIn(-14.0, 14.0)
        return raw(date, la, lo, tz, method)
            ?: raw(date, la.coerceIn(-48.5, 48.5), lo, tz, method)
            ?: PrayerTimes(5 * 60 + 30, 7 * 60, 12 * 60 + 30, 15 * 60 + 30, 18 * 60, 19 * 60 + 30)
    }

    /** Sonuc sayisal olarak gecersizse (NaN) null doner. */
    private fun raw(date: LocalDate, lat: Double, lng: Double, tzOffsetHours: Double, method: PrayerMethod): PrayerTimes? {
        val jd = julian(date) - lng / (15.0 * 24.0)

        fun sunAt(t: Double) = sunPos(jd + t / 24.0)
        fun mid(t: Double) = fixHour(12.0 - sunAt(t).eqt)
        fun event(angle: Double, t: Double, before: Boolean): Double {
            val a = angleTime(angle, lat, sunAt(t).decl)
            return mid(t) + if (before) -a else a
        }

        var fajr = 5.0; var sunrise = 6.0; var dhuhr = 12.0; var asr = 13.0
        var sunset = 18.0; var maghrib = 18.0; var isha = 18.0
        repeat(2) {
            fajr = event(method.fajrAngle, fajr, true)
            sunrise = event(0.833, sunrise, true)
            dhuhr = mid(dhuhr)
            asr = mid(asr) + asrTime(1.0, lat, sunAt(asr).decl)
            sunset = event(0.833, sunset, false)
            maghrib = if (method.maghribAngle > 0.0) event(method.maghribAngle, maghrib, false) else sunset
            isha = if (method.ishaMinutesAfterMaghrib > 0) maghrib + method.ishaMinutesAfterMaghrib / 60.0
            else event(method.ishaAngle, isha, false)
        }

        val shift = tzOffsetHours - lng / 15.0
        fajr += shift; sunrise += shift; dhuhr += shift; asr += shift; sunset += shift; maghrib += shift; isha += shift

        // Yuksek enlem: gece suresinin acisal payi ile sinirla (gunes -18 dereceye inmeyen gunler).
        val night = timeDiff(sunset, sunrise)
        val fajrPortion = night * method.fajrAngle / 60.0
        if (fajr.isNaN() || timeDiff(fajr, sunrise) > fajrPortion) fajr = sunrise - fajrPortion
        if (method.ishaMinutesAfterMaghrib == 0) {
            val ishaPortion = night * method.ishaAngle / 60.0
            if (isha.isNaN() || timeDiff(sunset, isha) > ishaPortion) isha = sunset + ishaPortion
        }

        if (listOf(fajr, sunrise, dhuhr, asr, maghrib, isha).any { it.isNaN() || it.isInfinite() }) return null

        fun minutes(h: Double, adj: Int): Int = (h * 60.0).roundToInt() + adj
        return PrayerTimes(
            fajr = minutes(fajr, 0),
            sunrise = minutes(sunrise, method.adjSunrise),
            dhuhr = minutes(dhuhr, method.adjDhuhr),
            asr = minutes(asr, method.adjAsr),
            maghrib = minutes(maghrib, method.adjMaghrib),
            isha = minutes(isha, 0)
        )
    }

    // ------------------------------------------------------------ astronomi
    private class Sun(val decl: Double, val eqt: Double)

    private fun rad(d: Double) = Math.toRadians(d)
    private fun deg(r: Double) = Math.toDegrees(r)

    private fun fixAngle(a: Double): Double { var x = a % 360.0; if (x < 0) x += 360.0; return x }
    private fun fixHour(h: Double): Double { var x = h % 24.0; if (x < 0) x += 24.0; return x }
    private fun timeDiff(a: Double, b: Double) = fixHour(b - a)

    /** 0h UT'deki Jülyen gün. */
    private fun julian(date: LocalDate): Double {
        var y = date.year
        var m = date.monthValue
        val d = date.dayOfMonth
        if (m <= 2) { y -= 1; m += 12 }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + d + b - 1524.5
    }

    private fun sunPos(jd: Double): Sun {
        val d = jd - 2451545.0
        val g = fixAngle(357.529 + 0.98560028 * d)
        val q = fixAngle(280.459 + 0.98564736 * d)
        val l = fixAngle(q + 1.915 * sin(rad(g)) + 0.020 * sin(rad(2 * g)))
        val e = 23.439 - 0.00000036 * d
        val ra = deg(atan2(cos(rad(e)) * sin(rad(l)), cos(rad(l)))) / 15.0
        var eqt = q / 15.0 - fixHour(ra)
        if (eqt > 12.0) eqt -= 24.0
        if (eqt < -12.0) eqt += 24.0
        val decl = deg(asin(sin(rad(e)) * sin(rad(l))))
        return Sun(decl, eqt)
    }

    /** Gunes ufkun 'angle' derece altindayken ogle vaktine saat farki (|v| > 1 ise NaN). */
    private fun angleTime(angle: Double, lat: Double, decl: Double): Double {
        val v = (-sin(rad(angle)) - sin(rad(lat)) * sin(rad(decl))) / (cos(rad(lat)) * cos(rad(decl)))
        return deg(acos(v)) / 15.0
    }

    private fun asrTime(factor: Double, lat: Double, decl: Double): Double {
        val alt = atan(1.0 / (factor + tan(rad(abs(lat - decl)))))
        return angleTime(-deg(alt), lat, decl)
    }
}

/** Hicri takvim (Umm al-Qura) ile Ramazan tespiti. */
object Hijri {
    fun month(d: LocalDate): Int = HijrahChronology.INSTANCE.date(d).get(ChronoField.MONTH_OF_YEAR)

    fun day(d: LocalDate): Int = HijrahChronology.INSTANCE.date(d).get(ChronoField.DAY_OF_MONTH)

    fun isRamadan(d: LocalDate): Boolean = month(d) == 9

    /** Ramazan'in kacinci gunu (Ramazan degilse null). */
    fun ramadanDay(d: LocalDate): Int? = if (isRamadan(d)) day(d) else null

    /** 'from' tarihinden itibaren (dahil) bir sonraki Ramazan'in ilk gunu. */
    fun nextRamadanStart(from: LocalDate): LocalDate {
        var d = from
        repeat(420) {
            if (isRamadan(d) && day(d) == 1) return d
            d = d.plusDays(1)
        }
        return d
    }
}
