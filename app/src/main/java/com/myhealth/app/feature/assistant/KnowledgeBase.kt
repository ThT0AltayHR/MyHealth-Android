package com.myhealth.app.feature.assistant

import com.myhealth.app.feature.assistant.kb.KbFitness
import com.myhealth.app.feature.assistant.kb.KbHealth
import com.myhealth.app.feature.assistant.kb.KbNutrition

/**
 * YEREL BILGI TABANI ARAYUZU (SABIT SOZLESME). Bilgi ajani topics listesini ve StrKnowledge
 * tablosundaki yanitlari yazar; Beyin ajani (LocalEngine) bunlari esler ve yanitlar.
 * keywords: TUM dillerde (TR/EN/RU/AR/DE/KO) kucuk harfli, aksansiz kelime kokleri; onek eslesmesi yapilir.
 * answerKey: kb.<id>.a (yer tutucu {0} = kullanici adi), thinkKey: kb.<id>.t ("Sana en uygun bitki cayi
 * araniyor..." gibi dusunme etiketi). Konular birbirinden ayri kimliklidir.
 */
data class KbTopic(
    val id: String,
    val keywords: List<String>,
    val answerKey: String,
    val thinkKey: String,
    /** HealthLists kimlikleri: kullanicinin hastalik/alerjisiyle ilgili konular onceliklendirilir. */
    val relatedConditions: Set<String> = emptySet(),
    val priority: Int = 0
)

object KnowledgeBase {
    /** Uc bilgi ajaninin konulari; kimlikler (nut_, fit_, hlt_) birbirinden ayridir. */
    val topics: List<KbTopic> by lazy { KbNutrition.topics + KbFitness.topics + KbHealth.topics }
}
