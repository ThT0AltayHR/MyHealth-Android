package com.myhealth.app.logic

import com.myhealth.app.data.Gender
import com.myhealth.app.data.Sport
import com.myhealth.app.data.UserProfile
import kotlin.math.exp
import kotlin.math.roundToInt
import kotlin.math.sin

enum class BmiClass { UNDER, NORMAL, OVER, OBESE }

/** Vucut ve antrenman hesaplari (saf Kotlin, JVM'de test edilir). */
object Body {
    /** Mifflin-St Jeor bazal metabolizma hizi (kcal/gun). */
    fun bmr(p: UserProfile): Double {
        val base = 10.0 * p.weightKg + 6.25 * p.heightCm - 5.0 * p.age
        return if (p.gender == Gender.MALE) base + 5.0 else base - 161.0
    }

    /** Gunluk toplam enerji ihtiyaci. activityFactor: 1.2 hareketsiz ... 1.725 cok aktif. */
    fun tdee(p: UserProfile, activityFactor: Double = 1.4): Double = bmr(p) * activityFactor

    fun bmiClass(bmi: Double): BmiClass = when {
        bmi < 18.5 -> BmiClass.UNDER
        bmi < 25.0 -> BmiClass.NORMAL
        bmi < 30.0 -> BmiClass.OVER
        else -> BmiClass.OBESE
    }

    /** BMI 18.5 - 24.9 araligina denk gelen kilo araligi. */
    fun healthyWeightRange(heightCm: Int): IntRange {
        val m = heightCm / 100.0
        return (18.5 * m * m).roundToInt()..(24.9 * m * m).roundToInt()
    }

    /** Gunluk su ihtiyaci (litre): kilo basina 33 ml + antrenman dakikasi basina 12 ml. */
    fun waterLiters(p: UserProfile, workoutMinutes: Int = 0): Double =
        p.weightKg * 0.033 + workoutMinutes * 0.012

    /** Tanaka formulu: 208 - 0.7 x yas. */
    fun maxHr(age: Int): Int = (208 - 0.7 * age).roundToInt()

    fun restHr(p: UserProfile): Int = if (p.gender == Gender.MALE) 66 else 70

    /** kcal = MET x kilo x saat */
    fun workoutKcal(sport: Sport, weightKg: Int, seconds: Long): Double =
        sport.met * weightKg * (seconds / 3600.0)

    /**
     * Tahmini nabiz. Telefonda gercek nabiz sensoru yoktur; bu deger sporun MET yogunlugundan,
     * yasdan ve gecen sureden (isinma egrisi) hesaplanir. Arayuzde "tahmini" olarak etiketlenir.
     */
    fun estimatedBpm(p: UserProfile, sport: Sport, elapsedSec: Long): Int {
        val max = maxHr(p.age)
        val rest = restHr(p)
        val intensity = ((sport.met - 1.5) / 10.0).coerceIn(0.1, 0.95)
        val target = rest + (max - rest) * intensity * 0.92
        val ramp = 1.0 - exp(-elapsedSec / 90.0)
        val wobble = sin(elapsedSec / 7.0) * 2.5 + sin(elapsedSec / 3.1) * 1.2
        return (rest + (target - rest) * ramp + wobble).roundToInt().coerceIn(rest - 5, max)
    }

    /** Adim uzunlugu (m) ~ boy x 0.415 (yuruyus); adimdan mesafe tahmini icin. */
    fun stepLengthM(heightCm: Int): Double = heightCm * 0.415 / 100.0
}
