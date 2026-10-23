package com.myhealth.app.feature.meds

import com.myhealth.app.data.Repo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Kullanicinin duzenli ve ara sira kullandigi ilaclar (MedDb kimlikleri). Repo.kv icinde saklanir. */
object MedStore {
    private val _regular = MutableStateFlow<Set<String>>(emptySet())
    private val _occasional = MutableStateFlow<Set<String>>(emptySet())

    val regular: StateFlow<Set<String>> = _regular.asStateFlow()
    val occasional: StateFlow<Set<String>> = _occasional.asStateFlow()

    init {
        try {
            _regular.value = readSet("meds_regular")
            _occasional.value = readSet("meds_occasional")
        } catch (e: Exception) {
            // bos baslar
        }
    }

    private fun readSet(key: String): Set<String> =
        (Repo.kvJson(key) as? List<*>).orEmpty().mapNotNull { it as? String }.toSet()

    /** Bir ilac iki kutuda birden olamaz: duzenli olan, ara sira listesinden cikarilir. */
    fun set(regular: Set<String>, occasional: Set<String>) {
        val occ = occasional - regular
        _regular.value = regular
        _occasional.value = occ
        try {
            Repo.kvPutJson("meds_regular", regular.toList())
            Repo.kvPutJson("meds_occasional", occ.toList())
        } catch (e: Exception) {
            // yoksay
        }
    }
}
