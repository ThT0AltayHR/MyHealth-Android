package com.myhealth.app.core

import com.myhealth.app.core.strings.AllStrings

/**
 * Tek bir ceviri satiri. Alti dilin tamami zorunlu parametre oldugu icin
 * eksik bir ceviri DERLEME HATASI verir; boylece dil bosluklari olusamaz.
 * Yer tutucular {0}, {1} seklindedir ($ isareti kullanilmaz).
 */
class Row(
    val key: String,
    val tr: String,
    val en: String,
    val ru: String,
    val ar: String,
    val de: String,
    val ko: String
) {
    fun text(lang: Lang): String = when (lang) {
        Lang.TR -> tr
        Lang.EN -> en
        Lang.RU -> ru
        Lang.AR -> ar
        Lang.DE -> de
        Lang.KO -> ko
    }
}

fun row(key: String, tr: String, en: String, ru: String, ar: String, de: String, ko: String): Row =
    Row(key, tr, en, ru, ar, de, ko)

object I18n {
    private val tables: Map<Lang, Map<String, String>> by lazy {
        val out = HashMap<Lang, HashMap<String, String>>()
        for (l in Lang.entries) out[l] = HashMap()
        for (r in AllStrings.rows()) {
            for (l in Lang.entries) out.getValue(l)[r.key] = r.text(l)
        }
        out
    }

    fun get(lang: Lang, key: String): String =
        tables.getValue(lang)[key] ?: tables.getValue(Lang.EN)[key] ?: key

    fun fmt(lang: Lang, key: String, vararg args: Any?): String {
        var s = get(lang, key)
        args.forEachIndexed { i, a -> s = s.replace("{" + i + "}", a.toString()) }
        return s
    }

    fun has(key: String): Boolean = tables.getValue(Lang.EN).containsKey(key)

    fun allKeys(): Set<String> = tables.getValue(Lang.EN).keys
}
