package com.myhealth.app.feature.assistant

import android.content.Context
import com.myhealth.app.data.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/** Cevabin altinda gosterilen kaynak kutusu (site adi, baslik, adres, ozet, varsa resim). */
data class SourceCard(
    val title: String,
    val url: String,
    val site: String,
    val snippet: String,
    val imageUrl: String? = null
)

sealed class PipelineEvent {
    /** Gercek bir dusunme adimi (yerellestirilmis kisa etiket; arayuz soluk beyaz parlayan yazi olarak gosterir). */
    data class Step(val label: String) : PipelineEvent()

    /**
     * Son yanit. engine: "local" | "groq". notice: gecikme/anahtar hatasi bildirimi (arayuz ayri bir
     * balonda gosterir). learned: belleğe eklenecek olgu. Akis HER ZAMAN bir Final ile biter.
     */
    data class Final(
        val text: String,
        val engine: String,
        val sources: List<SourceCard> = emptyList(),
        val notice: String? = null,
        val learned: String? = null
    ) : PipelineEvent()
}

/**
 * YAPAY ZEKA BORU HATTI (SABIT SOZLESME; govdeyi Boru Hatti ajani doldurur): analiz -> (cevrimiciyse)
 * guvenilir kaynaklarda arama -> taslak -> denetim -> son yanit. Groq (kullanici anahtari) veya yerel
 * motor; Groq hatasinda otomatik yerel moda duser ve sonra kendiliginden Groq'a doner.
 * history: bu sohbetin onceki mesajlari; ac: baglam (ac.memory sohbet bellegini de icerir).
 */
object AiPipeline {
    fun run(ctx: Context, history: List<ChatMessage>, userText: String, ac: AssistantContext): Flow<PipelineEvent> =
        flowOf(PipelineEvent.Final(text = "...", engine = "local"))
}
