package com.myhealth.app.feature.sports

import com.myhealth.app.core.Fmt
import com.myhealth.app.data.Sport
import com.myhealth.app.data.SportsCatalog
import com.myhealth.app.data.WorkoutRecord
import com.myhealth.app.ui.tr
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/** Spor adi (sport.<id>); katalogda olmayan kimlikte kimligin kendisi. */
internal fun sportDisplayName(id: String): String {
    val sport: Sport = SportsCatalog.byId(id) ?: return id
    return tr(sport.nameKey)
}

/** Son yapilanlar: en cok yapilan 4 spor ve kayit sayisi (katalogda olmayan kimlikler atilir). */
internal fun recentSportCounts(records: List<WorkoutRecord>): List<Pair<Sport, Int>> {
    if (records.isEmpty()) return emptyList()
    val counts = records.groupingBy { it.sportId }.eachCount()
    return counts.entries
        .sortedByDescending { it.value }
        .mapNotNull { e -> SportsCatalog.byId(e.key)?.let { sport -> Pair(sport, e.value) } }
        .take(4)
}

/** Sure: saniye -> "1:02:05" veya "02:05" (Fmt.clock). */
internal fun fmtSportDuration(sec: Int): String = Fmt.clock(sec.coerceAtLeast(0).toLong())

/** Mesafe: sonlu ve pozitifse km metni, degilse "-". */
internal fun fmtSportDistance(meters: Double): String =
    if (meters.isFinite() && meters > 0.0) Fmt.km(meters) else "-"

/** Epoch milisaniyesinden yerel tarih. */
internal fun localDateOfMs(ms: Long): LocalDate =
    Instant.ofEpochMilli(ms).atZone(ZoneId.systemDefault()).toLocalDate()

/** Tarih: GG.AA.YYYY (rakamlar Latin). */
internal fun fmtSportDate(d: LocalDate): String =
    Fmt.pad2(d.dayOfMonth) + "." + Fmt.pad2(d.monthValue) + "." + d.year.toString()

/** Gun icinde saat: SS:DD. */
internal fun fmtSportClock(ms: Long): String {
    val z = Instant.ofEpochMilli(ms).atZone(ZoneId.systemDefault())
    return Fmt.hhmm(z.hour * 60 + z.minute)
}
