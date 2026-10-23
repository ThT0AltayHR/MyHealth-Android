package com.myhealth.app.feature.profile

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.myhealth.app.core.Fmt
import com.myhealth.app.core.list
import com.myhealth.app.data.Repo
import com.myhealth.app.data.UserProfile
import com.myhealth.app.data.WorkoutRecord
import com.myhealth.app.feature.health.HealthEntry
import com.myhealth.app.feature.health.HealthLog
import com.myhealth.app.feature.health.SymptomTag
import com.myhealth.app.logic.BmiClass
import com.myhealth.app.logic.Body
import com.myhealth.app.ui.kit.GlassCard
import com.myhealth.app.ui.kit.HGap
import com.myhealth.app.ui.kit.Ic
import com.myhealth.app.ui.kit.LuxIcon
import com.myhealth.app.ui.kit.VGap
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt
import com.myhealth.app.util.CrashLog

// ---------------------------------------------------------------- hesap ve veri yardimcilari (Repo + HealthLog)

internal const val DAY_MS = 86_400_000L

/** Son n gunun tarih anahtarlari, en eski once. */
internal fun dayKeys(n: Int, nowMs: Long = System.currentTimeMillis()): List<String> {
    val count = n.coerceIn(0, 60)
    return (count - 1 downTo 0).map { Fmt.dateKey(nowMs - it * DAY_MS) }
}

/** Grafik etiketi: tarih anahtarinin gun kismi (rakam; dil bagimsiz). */
internal fun dayLabel(key: String): String = if (key.length >= 2) key.takeLast(2) else key

/** Son n gunun adim sayilari (en eski once). */
internal fun stepsSeries(n: Int): List<Int> {
    val map = Repo.steps.value
    return dayKeys(n).map { map[it] ?: 0 }
}

/** Son n gunun su miktarlari. DOGRULA: Repo.water birimi ml varsayildi (waterToday/addWater ile tutarli olmali). */
internal fun waterSeries(n: Int): List<Int> {
    val map = Repo.water.value
    return dayKeys(n).map { map[it] ?: 0 }
}

/** Son n gunun ogun kcal toplamlari. */
internal fun kcalSeries(n: Int): List<Int> =
    dayKeys(n).map { key -> Repo.mealsOn(key).fold(0) { acc, m -> acc + m.kcal } }

/** Son n gun icindeki antrenmanlar. */
internal fun workoutsSince(days: Int, nowMs: Long = System.currentTimeMillis()): List<WorkoutRecord> {
    val from = nowMs - days.coerceAtLeast(0) * DAY_MS
    return Repo.workouts.value.filter { it.startMs >= from }
}

/** Egzersiz sonu kayitlari: fromAgo gun once ile toAgo gun once arasi (toAgo = 0 -> simdi). */
internal fun entriesIn(fromAgo: Int, toAgo: Int, nowMs: Long = System.currentTimeMillis()): List<HealthEntry> {
    val lo = nowMs - fromAgo.coerceAtLeast(0) * DAY_MS
    val hi = nowMs - toAgo.coerceAtLeast(0) * DAY_MS
    return HealthLog.entries.value.filter { it.tsMs in lo..hi }
}

/** Ortalama siddet (0..3); kayit yoksa null. */
internal fun avgSeverity(list: List<HealthEntry>): Double? =
    if (list.isEmpty()) null else list.fold(0.0) { acc, e -> acc + e.severity } / list.size

/** Belirli bir belirtinin ortalama siddeti (0..3); kayit yoksa null. */
internal fun tagSeverity(tag: SymptomTag, fromAgo: Int, toAgo: Int): Double? {
    val values = ArrayList<Double>()
    for (e in entriesIn(fromAgo, toAgo)) {
        for (a in e.answers) if (a.tag == tag) values.add(a.severity.toDouble())
    }
    return if (values.isEmpty()) null else values.fold(0.0) { acc, v -> acc + v } / values.size
}

internal enum class Trend { UP, DOWN, FLAT, NONE }

/** Onceki ve simdiki ortalama arasindaki yon (esik 0.25). */
internal fun trendOf(prev: Double?, cur: Double?): Trend {
    if (prev == null || cur == null) return Trend.NONE
    val d = cur - prev
    return when {
        d > 0.25 -> Trend.UP
        d < -0.25 -> Trend.DOWN
        else -> Trend.FLAT
    }
}

internal fun trendKey(trend: Trend): String = "health.trend." + trend.name.lowercase()

/** Art arda aktif gun sayisi (antrenman veya saglik kaydi). Bugun bos ise dunden sayilir. */
internal fun streakDays(nowMs: Long = System.currentTimeMillis()): Int {
    val days = HashSet<String>()
    for (w in Repo.workouts.value) days.add(Fmt.dateKey(w.startMs))
    for (e in HealthLog.entries.value) days.add(Fmt.dateKey(e.tsMs))
    var count = 0
    for (i in 0 until 366) {
        if (Fmt.dateKey(nowMs - i * DAY_MS) in days) count++ else if (i > 0) break
    }
    return count
}

/** En cok yapilan sporun kimligi; yoksa null. */
internal fun favSport(): String? {
    val counts = HashMap<String, Int>()
    for (w in Repo.workouts.value) counts[w.sportId] = (counts[w.sportId] ?: 0) + 1
    return counts.maxByOrNull { it.value }?.key
}

/** Haftalik antrenman sayisina gore TDEE aktivite katsayisi. */
internal fun activityFactor(): Double = when (workoutsSince(7).size) {
    0 -> 1.2
    in 1..3 -> 1.4
    else -> 1.6
}

/** Toparlanma puani 0..100: son 7 gunun belirtileri, siddeti ve antrenman bonusu. */
internal fun recoveryScore(): Int {
    val symptoms = HealthLog.symptomCounts(7).values.sum()
    val severity = avgSeverity(entriesIn(7, 0)) ?: 0.0
    val workouts = minOf(workoutsSince(7).size, 4)
    val raw = 100.0 - symptoms * 5.0 - severity * 8.0 + workouts * 2.0
    return raw.coerceIn(0.0, 100.0).toInt()
}

/** Gunluk su hedefi (ml): Body.waterLiters litre doner. */
internal fun waterTargetMl(p: UserProfile): Int = (Body.waterLiters(p) * 1000.0).toInt().coerceAtLeast(500)

/** Protein hedefi (g): kilo x 1.2 (aktif kullanici icin pratik hedef). */
internal fun proteinTargetG(p: UserProfile): Int = (p.weightKg * 1.2).toInt().coerceAtLeast(1)

internal fun stepGoal(): Int = Repo.settings.value.stepGoal.coerceAtLeast(1)

/** Oran 0..1; sifir/negatif payda ve NaN korumali. */
internal fun ratio(part: Double, whole: Double): Float {
    if (whole <= 0.0 || part.isNaN()) return 0f
    return (part / whole).coerceIn(0.0, 1.0).toFloat()
}

internal fun litersText(ml: Int): String = Fmt.oneDecimal(ml / 1000.0)

internal fun bmiKey(c: BmiClass): String = when (c) {
    BmiClass.UNDER -> "health.bmi.under"
    BmiClass.NORMAL -> "health.bmi.normal"
    BmiClass.OVER -> "health.bmi.over"
    BmiClass.OBESE -> "health.bmi.obese"
}

/** Ilaclar (duzenli, ara sira) kimlikleri. DOGRULA: ilac verisinin Repo.kv anahtari ve bicimi (MedStore) bilinmiyor; "medications" varsayildi. */
internal fun medSets(): Pair<Set<String>, Set<String>> {
    val m = try { Repo.kvJson("medications") as? Map<*, *> } catch (e: Exception) { null }
    val reg = (m?.list("regular") ?: emptyList<Any?>()).mapNotNull { it as? String }.toSet()
    val occ = (m?.list("occasional") ?: emptyList<Any?>()).mapNotNull { it as? String }.toSet()
    return reg to occ
}

/** Ilaclari kaydeder. DOGRULA: medSets ile ayni anahtar ve bicim kullanilmali. */
internal fun saveMedSets(ctx: Context, regular: Set<String>, occasional: Set<String>) {
    try {
        Repo.kvPutJson("medications", mapOf("regular" to regular.toList(), "occasional" to occasional.toList()))
    } catch (e: Exception) {
        CrashLog.note(ctx, "med_save", e)
    }
}

/** Panellerin veri degisimini izlemesi icin (StateFlow -> collectAsState; deger kullanilmaz). */
@Composable
internal fun ObserveHealthData() {
    Repo.profile.collectAsState()
    Repo.settings.collectAsState()
    Repo.workouts.collectAsState()
    Repo.steps.collectAsState()
    Repo.water.collectAsState()
    Repo.meals.collectAsState()
    HealthLog.entries.collectAsState()
}

// ---------------------------------------------------------------- ortak panel bilesenleri

/** Panel kabugu: ust satirda ikon + baslik (t(titleKey)), altinda icerik. */
@Composable
internal fun PanelCard(icon: String, titleKey: String, content: @Composable ColumnScope.() -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            LuxIcon(icon, tint = Lux.orange, size = 20.dp)
            HGap(8.dp)
            Txt(t(titleKey), sty = Sty.LABEL, color = Lux.textDim, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        VGap(10.dp)
        content()
    }
}

/** Buyuk sayi + istege bagli birim. */
@Composable
internal fun BigValue(value: String, unit: String = "") {
    Row(verticalAlignment = Alignment.Bottom) {
        Txt(value, sty = Sty.NUMBER, color = Lux.text, maxLines = 1)
        if (unit.isNotEmpty()) {
            HGap(4.dp)
            Txt(unit, sty = Sty.SMALL, color = Lux.textDim, maxLines = 1)
        }
    }
}

/** Yatay ilerleme cubugu (0..1). */
@Composable
internal fun LinearBar(fraction: Float, modifier: Modifier = Modifier) {
    val f = if (fraction.isNaN()) 0f else fraction.coerceIn(0f, 1f)
    Box(
        modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Lux.glassStrong)
    ) {
        Box(Modifier.fillMaxWidth(f).fillMaxHeight().background(Lux.accentBrush))
    }
}

/** Dikey cubuk grafik; degerler herhangi bir birimde (en buyuge gore olceklenir). */
@Composable
internal fun BarChart(values: List<Float>, labels: List<String>, modifier: Modifier = Modifier) {
    val top = values.maxOrNull() ?: 0f
    val max = if (top > 0f) top else 1f
    Canvas(modifier.fillMaxWidth().height(72.dp)) {
        val n = values.size
        if (n == 0) return@Canvas
        val gap = 6.dp.toPx()
        val w = (size.width - gap * (n - 1)) / n
        values.forEachIndexed { i, v ->
            val h = size.height * (v / max).coerceIn(0f, 1f)
            drawRoundRect(
                brush = Lux.accentBrush,
                topLeft = Offset(i * (w + gap), size.height - h),
                size = Size(w.coerceAtLeast(1f), h),
                cornerRadius = CornerRadius(4.dp.toPx())
            )
        }
    }
    if (labels.isNotEmpty()) {
        Row(Modifier.fillMaxWidth()) {
            labels.forEach {
                Txt(it, Modifier.weight(1f), sty = Sty.CAPTION, color = Lux.textFaint, align = TextAlign.Center, maxLines = 1)
            }
        }
    }
}

/** Egilim etiketi: ok ikonu + health.trend.* metni. */
@Composable
internal fun TrendTag(trend: Trend) {
    val icon = when (trend) {
        Trend.UP -> Ic.ARROW_UP
        Trend.DOWN -> Ic.ARROW_DOWN
        else -> Ic.MINUS
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        LuxIcon(icon, tint = Lux.textDim, size = 14.dp)
        HGap(4.dp)
        Txt(t(trendKey(trend)), sty = Sty.CAPTION, color = Lux.textDim)
    }
}
