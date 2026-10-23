package com.myhealth.app.feature.sports

import com.myhealth.app.data.LatLng
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

/** Saf (Android'siz) antrenman hesaplari: mesafe, konum filtresi, kcal, tempo, rota seyreltme. */
object WkGeo {
    private const val EARTH_R = 6371000.0

    fun valid(p: LatLng): Boolean =
        !p.lat.isNaN() && !p.lng.isNaN() && p.lat in -90.0..90.0 && p.lng in -180.0..180.0 &&
            !(p.lat == 0.0 && p.lng == 0.0)

    /** Haversine mesafesi (metre); gecersiz girdide 0. */
    fun haversine(a: LatLng, b: LatLng): Double {
        if (!valid(a) || !valid(b)) return 0.0
        val la1 = Math.toRadians(a.lat)
        val la2 = Math.toRadians(b.lat)
        val dLa = la2 - la1
        val dLo = Math.toRadians(b.lng - a.lng)
        val h = sin(dLa / 2) * sin(dLa / 2) + cos(la1) * cos(la2) * sin(dLo / 2) * sin(dLo / 2)
        val d = 2.0 * EARTH_R * asin(sqrt(h.coerceIn(0.0, 1.0)))
        return if (d.isNaN() || d.isInfinite()) 0.0 else d
    }

    /** Konum filtresi: kabulde eklenecek mesafe (m, ilk nokta 0.0), retteyse null. Dogruluk > 35 m atilir. */
    fun accept(prev: LatLng?, prevMs: Long, p: LatLng, ms: Long, accuracyM: Float, maxSpeedMps: Double): Double? {
        if (!valid(p)) return null
        if (!accuracyM.isNaN() && accuracyM > 35f) return null
        if (prev == null) return 0.0
        val d = haversine(prev, p)
        val acc = if (accuracyM.isNaN()) 0.0 else accuracyM.toDouble()
        if (d < max(3.0, acc * 0.5)) return null
        val dt = (ms - prevMs) / 1000.0
        if (dt > 0.0 && d / dt > maxSpeedMps) return null
        return d
    }

    /** MET x kilo x saat. */
    fun kcal(met: Double, weightKg: Int, elapsedSec: Long): Int {
        val v = met * weightKg.coerceIn(30, 250) * (elapsedSec.coerceAtLeast(0L) / 3600.0)
        return if (v.isNaN() || v.isInfinite()) 0 else v.roundToInt()
    }

    /** km basina "dd:ss" tempo; hiz cok dusukse "--:--". */
    fun pace(speedKmh: Double): String {
        if (speedKmh.isNaN() || speedKmh < 0.5) return "--:--"
        val s = (3600.0 / speedKmh).roundToInt().coerceIn(0, 5999)
        return (s / 60).toString().padStart(2, '0') + ":" + (s % 60).toString().padStart(2, '0')
    }

    /** Rotayi en cok maxN noktaya indirir (ilk ve son korunur). */
    fun thin(route: List<LatLng>, maxN: Int): List<LatLng> {
        if (maxN < 2 || route.size <= maxN) return route
        val out = ArrayList<LatLng>(maxN)
        val step = (route.size - 1).toDouble() / (maxN - 1)
        for (i in 0 until maxN) out.add(route[(i * step).roundToInt().coerceIn(0, route.size - 1)])
        return out
    }
}

/** Motivasyon mesajlari: sportlive.motiv.1..COUNT (17..COUNT kullanici adini {0} olarak icerir). */
object WkMotivation {
    const val COUNT = 24
    private const val NAME_FROM = 17

    /** Sonraki mesaj no (1..COUNT); oncekiyle ayni olmaz; ad yoksa adli mesajlar secilmez. */
    fun next(last: Int, hasName: Boolean): Int {
        val top = if (hasName) COUNT else NAME_FROM - 1
        var n = Random.nextInt(1, top + 1)
        if (n == last) n = if (n >= top) 1 else n + 1
        return n
    }

    fun key(n: Int): String = "sportlive.motiv." + n.coerceIn(1, COUNT)
}
