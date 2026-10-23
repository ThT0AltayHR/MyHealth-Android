package com.myhealth.app.core

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale

/** Bicimlendirme yardimcilari. Rakamlar her dilde Latin rakami olarak yazilir (Locale.US). */
object Fmt {
    fun pad2(n: Int): String = if (n in 0..9) "0$n" else n.toString()

    /** 3725 -> "1:02:05", 125 -> "02:05" */
    fun clock(totalSeconds: Long): String {
        val s = if (totalSeconds < 0) 0 else totalSeconds
        val h = s / 3600
        val m = (s % 3600) / 60
        val sec = s % 60
        return if (h > 0) "$h:${pad2(m.toInt())}:${pad2(sec.toInt())}" else "${pad2(m.toInt())}:${pad2(sec.toInt())}"
    }

    fun km(meters: Double): String = String.format(Locale.US, "%.2f", meters / 1000.0)

    fun oneDecimal(v: Double): String = String.format(Locale.US, "%.1f", v)

    fun hhmm(minutesOfDay: Int): String {
        val m = ((minutesOfDay % 1440) + 1440) % 1440
        return pad2(m / 60) + ":" + pad2(m % 60)
    }

    fun parseHhmm(text: String): Int? {
        val p = text.trim().split(":")
        if (p.size != 2) return null
        val h = p[0].toIntOrNull() ?: return null
        val m = p[1].toIntOrNull() ?: return null
        if (h !in 0..23 || m !in 0..59) return null
        return h * 60 + m
    }

    fun dateKey(epochMs: Long, zone: ZoneId = ZoneId.systemDefault()): String =
        Instant.ofEpochMilli(epochMs).atZone(zone).toLocalDate().toString()

    fun todayKey(zone: ZoneId = ZoneId.systemDefault()): String = LocalDate.now(zone).toString()

    /** Dakika sayisini "1 sa 05 dk" benzeri kisa metne cevirir; birimler cagiran tarafindan verilir. */
    fun hoursMinutes(totalMinutes: Int, hUnit: String, mUnit: String): String {
        val h = totalMinutes / 60
        val m = totalMinutes % 60
        return if (h > 0) "$h $hUnit ${pad2(m)} $mUnit" else "$m $mUnit"
    }
}
