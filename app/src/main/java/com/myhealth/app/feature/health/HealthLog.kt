package com.myhealth.app.feature.health

import com.myhealth.app.data.Repo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Egzersiz sonu saglik cevaplarinin kalici kaydi ("saglik belgesi"). PDF raporu, yapay zeka,
 * profil panelleri, bildirimler ve yedekleme bunu okur. Repo.kv icinde "health_log" anahtarinda saklanir.
 * Hicbir islev istisna firlatmaz.
 */
object HealthLog {
    private const val KEY = "health_log"
    private const val MAX = 500

    private val _entries = MutableStateFlow<List<HealthEntry>>(emptyList())

    /** Yeniden eskiye siralidir. */
    val entries: StateFlow<List<HealthEntry>> = _entries.asStateFlow()

    init {
        try {
            val raw = Repo.kvJson(KEY) as? List<*>
            _entries.value = raw.orEmpty()
                .mapNotNull { (it as? Map<*, *>)?.let { m -> HealthEntry.fromJson(m) } }
                .sortedByDescending { it.tsMs }
        } catch (e: Exception) {
            // bozuk veri: bos baslar
        }
    }

    fun add(e: HealthEntry) {
        val list = (listOf(e) + _entries.value.filter { it.id != e.id }).sortedByDescending { it.tsMs }.take(MAX)
        _entries.value = list
        persist(list)
    }

    fun delete(id: Long) {
        val list = _entries.value.filter { it.id != id }
        _entries.value = list
        persist(list)
    }

    fun clear() {
        _entries.value = emptyList()
        persist(emptyList())
    }

    fun forExercise(exerciseId: String): List<HealthEntry> = _entries.value.filter { it.exerciseId == exerciseId }

    fun recent(days: Int, nowMs: Long = System.currentTimeMillis()): List<HealthEntry> {
        val from = nowMs - days.coerceAtLeast(0) * 86_400_000L
        return _entries.value.filter { it.tsMs >= from }
    }

    /** severity >= 1 olan cevaplarin belirti turune gore sayisi. */
    fun symptomCounts(days: Int, nowMs: Long = System.currentTimeMillis()): Map<SymptomTag, Int> {
        val out = LinkedHashMap<SymptomTag, Int>()
        for (e in recent(days, nowMs)) {
            for (a in e.answers) {
                if (a.severity >= 1) out[a.tag] = (out[a.tag] ?: 0) + 1
            }
        }
        return out
    }

    private fun persist(list: List<HealthEntry>) {
        try {
            Repo.kvPutJson(KEY, list.map { it.toJson() })
        } catch (e: Exception) {
            // yoksay
        }
    }
}
