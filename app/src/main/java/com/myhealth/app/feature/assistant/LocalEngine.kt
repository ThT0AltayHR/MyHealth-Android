package com.myhealth.app.feature.assistant

import com.myhealth.app.data.ChatMessage

/**
 * Analiz adiminin sonucu (SABIT SOZLESME). Hem yerel hem Groq hatti ilk adimda LocalEngine.analyze kullanir.
 * intent: greeting | thanks | smalltalk | identity | help | name | memory | data | calc | food | med | kb | symptom |
 *         plan | motivation | offtopic | unknown
 * topicId: eslesen KnowledgeBase konusu (yoksa null). thinkLabel: SORUYA OZEL, yerellestirilmis dusunme etiketi
 * ("Sana en uygun bitki cayi seciliyor..."). searchQuery: internette aranacak kisa konu sorgusu (kisisel veri YOK;
 * arama gerekmiyorsa bos). urgent: kirmizi bayrak belirti. onTopic: false ise zorunlu "Uzgunum, sadece ..." mesaji verilir.
 */
data class Analysis(
    val intent: String,
    val topicId: String?,
    val thinkLabel: String,
    val searchQuery: String,
    val urgent: Boolean,
    val onTopic: Boolean
)

/**
 * YEREL MOTOR (SABIT SOZLESME; govdeyi D1a ajani doldurur). Saf Kotlin, Android importu YOK, istisna firlatmaz.
 * history: bu sohbetin onceki mesajlari (eskiden yeniye); bos olabilir.
 */
object LocalEngine {
    fun analyze(ac: AssistantContext, history: List<ChatMessage>, userText: String): Analysis =
        Analysis(intent = "unknown", topicId = null, thinkLabel = "", searchQuery = "", urgent = false, onTopic = true)

    fun reply(ac: AssistantContext, history: List<ChatMessage>, userText: String): LocalReply =
        LocalReply(text = "...")

    fun reply(ac: AssistantContext, userText: String): LocalReply = reply(ac, emptyList(), userText)
}
