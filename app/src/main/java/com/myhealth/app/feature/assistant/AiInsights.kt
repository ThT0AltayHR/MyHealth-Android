package com.myhealth.app.feature.assistant

import com.myhealth.app.data.WorkoutRecord
import com.myhealth.app.feature.health.HealthEntry

/** level: 1 = bilgi, 2 = dikkat, 3 = doktora basvurun. Metinler yerellestirilmistir. */
data class HealthFlag(val title: String, val detail: String, val level: Int)

/**
 * UYGULAMANIN HER KOSESI ICIN YAPAY ZEKA YORUMLARI (SABIT SOZLESME; govdeyi Beyin ajani doldurur).
 * Hepsi cihaz ici ve anlik calisir (internet gerekmez), kullanicinin adini, profilini, ilaclarini,
 * saglik cevaplarini ve sporlarini bilir. Cikti uygulama dilindedir.
 */
object AiInsights {
    /** Ana sayfa: gunun kisisel onerisi. */
    fun dailyTip(ac: AssistantContext): String = ""

    /** Egzersiz bitisi: cevaplara ve sure/mesafeye gore yorum ve oneriler (entry null olabilir). */
    fun workoutFeedback(ac: AssistantContext, entry: HealthEntry?, record: WorkoutRecord?): String = ""

    /** Takip bildiriminden acilan sohbetin ilk yapay zeka mesaji (tarih/saat, belirti, bitki cayi/aile hekimi onerisi...). */
    fun followUpMessage(ac: AssistantContext, entry: HealthEntry): String = ""

    /** Istatistik ekrani: haftalik degerlendirme. */
    fun statsSummary(ac: AssistantContext): String = ""

    /** Ogun onerisi (mealTypeId: breakfast | lunch | dinner | snack). */
    fun mealSuggestion(ac: AssistantContext, mealTypeId: String): String = ""

    /** Profil paneli: cevaplardan/ilaclardan/olcumlerden cikan olasi saglik uyarilari. */
    fun healthFlags(ac: AssistantContext): List<HealthFlag> = emptyList()
}
