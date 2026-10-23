package com.myhealth.app.core

/** Desteklenen 6 dil. Arapca sagdan sola (RTL) calisir. */
enum class Lang(val code: String, val nativeName: String, val rtl: Boolean = false) {
    TR("tr", "Türkçe"),
    EN("en", "English"),
    RU("ru", "Русский"),
    AR("ar", "العربية", rtl = true),
    DE("de", "Deutsch"),
    KO("ko", "한국어");

    companion object {
        fun fromCode(code: String?): Lang = entries.firstOrNull { it.code == code } ?: EN

        /** Telefon dilinden en yakin desteklenen dili secer (bulunamazsa Ingilizce). */
        fun fromSystem(systemCode: String?): Lang {
            val c = (systemCode ?: "").lowercase().take(2)
            return entries.firstOrNull { it.code == c } ?: EN
        }
    }
}
