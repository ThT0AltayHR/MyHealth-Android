package com.myhealth.app.feature.assistant

/**
 * Metin normallestirme (SABIT SOZLESME, saf Kotlin). Kullanici metni ve KbTopic.keywords AYNI islevle
 * normallestirilir: kucuk harf; Latin aksan ve Turkce/Almanca harf katlama (c g i o s u a e, ss);
 * Kiril yo -> e; Arapca harekeler/tatweel atilir, alef/ya/ta marbuta varyantlari birlestirilir ve bastaki
 * "al" takisi atilir; noktalama bosluga, apostrof hicbir seye cevrilir. Anahtar kelimeler bu islevden
 * gecmis halde yazilmalidir: norm(k) == k (JVM testi denetler).
 * Eslesme kurali: hasKeyword(norm(metin), anahtar) = anahtar, normallesmis metinde bir kelimenin BASINDA geciyor.
 */
object TextNorm {
    private const val AL = "ال"

    private fun fold(ch: Char): String? = when (ch) {
        'İ', 'I', 'ı', 'i', 'í', 'ì', 'î', 'ï', 'Í', 'Ì', 'Î', 'Ï' -> "i"
        'Ğ', 'ğ' -> "g"
        'Ç', 'ç' -> "c"
        'Ş', 'ş' -> "s"
        'Ö', 'ö', 'ó', 'ò', 'ô', 'õ', 'Ó', 'Ò', 'Ô', 'Õ' -> "o"
        'Ü', 'ü', 'ú', 'ù', 'û', 'Ú', 'Ù', 'Û' -> "u"
        'Ä', 'ä', 'á', 'à', 'â', 'ã', 'å', 'Á', 'À', 'Â', 'Ã', 'Å' -> "a"
        'é', 'è', 'ê', 'ë', 'É', 'È', 'Ê', 'Ë' -> "e"
        'ñ', 'Ñ' -> "n"
        'ß' -> "ss"
        'ё', 'Ё' -> "е"
        'أ', 'إ', 'آ', 'ٱ' -> "ا"
        'ى', 'ئ' -> "ي"
        'ة' -> "ه"
        'ؤ' -> "و"
        else -> null
    }

    fun norm(s: String): String {
        val sb = StringBuilder(s.length + 4)
        for (ch in s) {
            val code = ch.code
            if (code in 0x064B..0x065F || code == 0x0670 || code == 0x0640) continue   // harekeler, tatweel
            if (code in 0x0300..0x036F) continue                                         // birlesik aksan isaretleri
            if (ch == '\'' || ch == '’' || ch == 'ʼ') continue                // apostrof kelimeyi bolmez
            val f = fold(ch)
            if (f != null) { sb.append(f); continue }
            if (ch.isLetterOrDigit()) sb.append(ch.lowercaseChar()) else sb.append(' ')
        }
        val out = StringBuilder(sb.length)
        for (part in sb.toString().split(' ')) {
            if (part.isEmpty()) continue
            if (out.isNotEmpty()) out.append(' ')
            out.append(if (part.length > 4 && part.startsWith(AL)) part.substring(2) else part)
        }
        return out.toString()
    }

    /** Normallesmis kelime listesi. */
    fun tokens(s: String): List<String> = norm(s).split(' ').filter { it.isNotEmpty() }

    /** normText zaten norm() ile hazirlanmis olmalidir; kw bir kelimenin basinda geciyor mu. */
    fun hasKeyword(normText: String, kw: String): Boolean {
        if (kw.isEmpty()) return false
        return (" " + normText).contains(" " + kw)
    }
}
