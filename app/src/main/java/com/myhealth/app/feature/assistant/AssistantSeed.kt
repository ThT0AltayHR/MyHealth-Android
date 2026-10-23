package com.myhealth.app.feature.assistant

import com.myhealth.app.data.Repo

/**
 * Bildirimden acilan sohbetin ILK yapay zeka mesaji. Egzersiz sonu takip bildirimi (HealthFollowUp)
 * metni buraya yazar; AssistantScreen acilista take() ile alir, yeni bir sohbet baslatip mesaji
 * asistan mesaji olarak ekler. Tek seferliktir.
 */
object AssistantSeed {
    private const val KEY = "assistant_seed"

    fun put(text: String, entryId: Long) {
        try {
            Repo.kvPutJson(KEY, mapOf("text" to text, "entryId" to entryId, "tsMs" to System.currentTimeMillis()))
        } catch (e: Exception) {
            // yoksay
        }
    }

    /** (metin, kayitId) doner ve siler; yoksa null. */
    fun take(): Pair<String, Long>? {
        return try {
            val m = Repo.kvJson(KEY) as? Map<*, *> ?: return null
            Repo.kvRemove(KEY)
            val text = (m["text"] as? String).orEmpty()
            if (text.isBlank()) null else Pair(text, (m["entryId"] as? Number)?.toLong() ?: 0L)
        } catch (e: Exception) {
            null
        }
    }
}
