package com.myhealth.app.core

import kotlin.math.abs

/**
 * Kucuk, bagimsiz JSON okuyucu/yazici (Android'in org.json'una baglanmaz; JVM'de test edilebilir).
 * Nesne -> LinkedHashMap<String, Any?>, dizi -> ArrayList<Any?>, sayi -> Long veya Double.
 */
object Json {
    fun parse(text: String): Any? {
        val p = Parser(text)
        val v = p.readValue()
        p.skipWs()
        if (!p.atEnd()) throw IllegalArgumentException("JSON sonunda fazla karakter var")
        return v
    }

    fun parseOrNull(text: String?): Any? =
        if (text.isNullOrBlank()) null else try { parse(text) } catch (e: Exception) { null }

    fun stringify(value: Any?): String {
        val sb = StringBuilder()
        write(sb, value)
        return sb.toString()
    }

    private fun write(sb: StringBuilder, v: Any?) {
        when (v) {
            null -> sb.append("null")
            is Boolean -> sb.append(if (v) "true" else "false")
            is Int -> sb.append(v)
            is Long -> sb.append(v)
            is Float -> writeDouble(sb, v.toDouble())
            is Double -> writeDouble(sb, v)
            is Number -> sb.append(v.toString())
            is String -> writeString(sb, v)
            is Map<*, *> -> {
                sb.append('{')
                var first = true
                for ((k, x) in v) {
                    if (!first) sb.append(',')
                    first = false
                    writeString(sb, k.toString())
                    sb.append(':')
                    write(sb, x)
                }
                sb.append('}')
            }
            is Iterable<*> -> {
                sb.append('[')
                var first = true
                for (x in v) {
                    if (!first) sb.append(',')
                    first = false
                    write(sb, x)
                }
                sb.append(']')
            }
            else -> writeString(sb, v.toString())
        }
    }

    private fun writeDouble(sb: StringBuilder, d: Double) {
        if (d.isNaN() || d.isInfinite()) sb.append("null")
        else if (d == Math.rint(d) && abs(d) < 1e15) sb.append(d.toLong())
        else sb.append(d.toString())
    }

    private fun writeString(sb: StringBuilder, s: String) {
        sb.append('"')
        for (c in s) {
            when (c) {
                '"' -> sb.append("\\\"")
                '\\' -> sb.append("\\\\")
                '\n' -> sb.append("\\n")
                '\r' -> sb.append("\\r")
                '\t' -> sb.append("\\t")
                else -> if (c.code < 0x20) {
                    sb.append("\\u")
                    sb.append(c.code.toString(16).padStart(4, '0'))
                } else sb.append(c)
            }
        }
        sb.append('"')
    }

    private class Parser(private val s: String) {
        private var i = 0

        fun atEnd() = i >= s.length

        fun skipWs() {
            while (i < s.length && s[i].isWhitespace()) i++
        }

        fun readValue(): Any? {
            skipWs()
            if (i >= s.length) throw IllegalArgumentException("Beklenmeyen son")
            return when (val c = s[i]) {
                '{' -> readObject()
                '[' -> readArray()
                '"' -> readString()
                't' -> literal("true", true)
                'f' -> literal("false", false)
                'n' -> literal("null", null)
                else -> if (c == '-' || c.isDigit()) readNumber() else throw IllegalArgumentException("Beklenmeyen karakter: $c")
            }
        }

        private fun literal(word: String, value: Any?): Any? {
            if (!s.startsWith(word, i)) throw IllegalArgumentException("Gecersiz sabit")
            i += word.length
            return value
        }

        private fun readObject(): Map<String, Any?> {
            val m = LinkedHashMap<String, Any?>()
            i++ // {
            skipWs()
            if (i < s.length && s[i] == '}') { i++; return m }
            while (true) {
                skipWs()
                val k = readString()
                skipWs()
                if (i >= s.length || s[i] != ':') throw IllegalArgumentException("':' bekleniyordu")
                i++
                m[k] = readValue()
                skipWs()
                if (i >= s.length) throw IllegalArgumentException("Nesne kapanmadi")
                if (s[i] == ',') { i++; continue }
                if (s[i] == '}') { i++; return m }
                throw IllegalArgumentException("',' veya '}' bekleniyordu")
            }
        }

        private fun readArray(): List<Any?> {
            val a = ArrayList<Any?>()
            i++ // [
            skipWs()
            if (i < s.length && s[i] == ']') { i++; return a }
            while (true) {
                a.add(readValue())
                skipWs()
                if (i >= s.length) throw IllegalArgumentException("Dizi kapanmadi")
                if (s[i] == ',') { i++; continue }
                if (s[i] == ']') { i++; return a }
                throw IllegalArgumentException("',' veya ']' bekleniyordu")
            }
        }

        private fun readString(): String {
            if (i >= s.length || s[i] != '"') throw IllegalArgumentException("'\"' bekleniyordu")
            i++
            val sb = StringBuilder()
            while (true) {
                if (i >= s.length) throw IllegalArgumentException("Metin kapanmadi")
                val c = s[i++]
                when (c) {
                    '"' -> return sb.toString()
                    '\\' -> {
                        if (i >= s.length) throw IllegalArgumentException("Kacis dizisi eksik")
                        when (val e = s[i++]) {
                            '"' -> sb.append('"')
                            '\\' -> sb.append('\\')
                            '/' -> sb.append('/')
                            'b' -> sb.append('\b')
                            'f' -> sb.append('\u000C')
                            'n' -> sb.append('\n')
                            'r' -> sb.append('\r')
                            't' -> sb.append('\t')
                            'u' -> {
                                if (i + 4 > s.length) throw IllegalArgumentException("\\u eksik")
                                sb.append(s.substring(i, i + 4).toInt(16).toChar())
                                i += 4
                            }
                            else -> throw IllegalArgumentException("Gecersiz kacis: $e")
                        }
                    }
                    else -> sb.append(c)
                }
            }
        }

        private fun readNumber(): Any {
            val start = i
            if (s[i] == '-') i++
            while (i < s.length && s[i].isDigit()) i++
            var isDouble = false
            if (i < s.length && s[i] == '.') {
                isDouble = true; i++
                while (i < s.length && s[i].isDigit()) i++
            }
            if (i < s.length && (s[i] == 'e' || s[i] == 'E')) {
                isDouble = true; i++
                if (i < s.length && (s[i] == '+' || s[i] == '-')) i++
                while (i < s.length && s[i].isDigit()) i++
            }
            val t = s.substring(start, i)
            if (!isDouble) t.toLongOrNull()?.let { return it }
            return t.toDouble()
        }
    }
}

// ---- Tipli erisim yardimcilari (eksik/yanlis tipli alanlar varsayilana duser) ----
fun Map<*, *>.str(key: String, def: String = ""): String = (this[key] as? String) ?: def
fun Map<*, *>.int(key: String, def: Int = 0): Int = (this[key] as? Number)?.toInt() ?: def
fun Map<*, *>.long(key: String, def: Long = 0L): Long = (this[key] as? Number)?.toLong() ?: def
fun Map<*, *>.dbl(key: String, def: Double = 0.0): Double = (this[key] as? Number)?.toDouble() ?: def
fun Map<*, *>.bool(key: String, def: Boolean = false): Boolean = (this[key] as? Boolean) ?: def
fun Map<*, *>.list(key: String): List<*> = (this[key] as? List<*>) ?: emptyList<Any?>()
fun Map<*, *>.obj(key: String): Map<*, *> = (this[key] as? Map<*, *>) ?: emptyMap<Any?, Any?>()
fun Map<*, *>.strList(key: String): List<String> = list(key).filterIsInstance<String>()
fun Map<*, *>.intList(key: String): List<Int> = list(key).filterIsInstance<Number>().map { it.toInt() }
