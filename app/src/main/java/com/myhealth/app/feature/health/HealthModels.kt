package com.myhealth.app.feature.health

import com.myhealth.app.core.int
import com.myhealth.app.core.list
import com.myhealth.app.core.long
import com.myhealth.app.core.str

/** Egzersiz sonu sorularinin olcmeye calistigi belirti turu. Ceviri anahtari: quiz.tag.<id>. */
enum class SymptomTag(val id: String) {
    BREATHLESSNESS("breathless"), DIZZINESS("dizzy"), CHEST_PAIN("chest"), PALPITATIONS("palpitation"),
    JOINT_PAIN("joint"), MUSCLE_PAIN("muscle"), BACK_PAIN("back"), HEADACHE("headache"), NAUSEA("nausea"),
    FATIGUE("fatigue"), COLD_SWEAT("sweat"), CRAMP("cramp"), COUGH("cough"), WELLBEING("wellbeing"), OTHER("other");

    val nameKey: String get() = "quiz.tag.$id"

    companion object {
        fun from(id: String?): SymptomTag = entries.firstOrNull { it.id == id } ?: OTHER
    }
}

/** Bir soruya verilen yanit. severity: 0 (sorun yok) .. 3 (belirgin sorun). */
data class QuizAnswer(val questionId: String, val optionIndex: Int, val severity: Int, val tag: SymptomTag) {
    fun toJson(): Map<String, Any?> = mapOf("q" to questionId, "o" to optionIndex, "s" to severity, "t" to tag.id)

    companion object {
        fun fromJson(m: Map<*, *>) =
            QuizAnswer(m.str("q"), m.int("o"), m.int("s").coerceIn(0, 3), SymptomTag.from(m.str("t")))
    }
}

/** Bir egzersiz bitiminde toplanan saglik kaydi. exerciseId: "breathing" veya SportsCatalog kimligi. */
data class HealthEntry(
    val id: Long,
    val tsMs: Long,
    val exerciseId: String,
    val kind: String,
    val durationSec: Int,
    val answers: List<QuizAnswer>,
    val severity: Int
) {
    fun toJson(): Map<String, Any?> = mapOf(
        "id" to id, "tsMs" to tsMs, "exerciseId" to exerciseId, "kind" to kind, "durationSec" to durationSec,
        "answers" to answers.map { it.toJson() }, "severity" to severity
    )

    companion object {
        fun fromJson(m: Map<*, *>) = HealthEntry(
            m.long("id"), m.long("tsMs"), m.str("exerciseId"), m.str("kind"), m.int("durationSec"),
            m.list("answers").mapNotNull { (it as? Map<*, *>)?.let { x -> QuizAnswer.fromJson(x) } },
            m.int("severity").coerceIn(0, 3)
        )
    }
}

data class QuizOption(val textKey: String, val severity: Int)

data class QuizQuestion(val id: String, val tag: SymptomTag, val textKey: String, val options: List<QuizOption>)

/** Egzersiz kimlikleri: "breathing" veya SportsCatalog kimligi. */
object Exercises {
    const val BREATHING = "breathing"

    /** Egzersizin yerellestirilmis adinin ceviri anahtari (nefes: mind.breath.title, spor: sport.<id>). */
    fun nameKey(exerciseId: String): String = if (exerciseId == BREATHING) "mind.breath.title" else "sport.$exerciseId"
}
