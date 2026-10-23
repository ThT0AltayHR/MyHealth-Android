package com.myhealth.app.feature.health

/**
 * Egzersize ozel saglik sorulari (SABIT SOZLESME; govdeyi Quiz ajani doldurur).
 * Her banka 6-7 soru icerir; her soru 3-5 secenek (severity 0..3). Ceviri anahtarlari:
 * soru: quiz.<banka>.<n>.q  secenek: quiz.<banka>.<n>.o<k>  (n: 1.., k: 1..)
 * Bankalar: breathing, walk_run, cycling, strength, flex, combat, water, ball, winter, outdoor, general.
 * Saf Kotlin (Android bagimliligi yok): JVM'de test edilir.
 */
object HealthQuiz {
    val banks: List<String> = listOf(
        "breathing", "walk_run", "cycling", "strength", "flex", "combat", "water", "ball", "winter", "outdoor", "general"
    )

    /** exerciseId ("breathing" veya spor kimligi) icin soru bankasi kimligi. */
    fun bankFor(exerciseId: String): String = "general"

    fun questionsForBank(bank: String): List<QuizQuestion> = emptyList()

    fun questionsFor(exerciseId: String): List<QuizQuestion> = questionsForBank(bankFor(exerciseId))
}
