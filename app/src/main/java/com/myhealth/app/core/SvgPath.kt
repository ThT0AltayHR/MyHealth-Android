package com.myhealth.app.core

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

/** Cizim hedefi. Arayuzde Compose Path'e, testlerde sayaca baglanir (saf Kotlin). */
interface PathSink {
    fun moveTo(x: Float, y: Float)
    fun lineTo(x: Float, y: Float)
    fun quadTo(x1: Float, y1: Float, x2: Float, y2: Float)
    fun cubicTo(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float)
    fun close()
}

/**
 * SVG path "d" metni ayristirici. Desteklenen komutlar: M L H V C S Q T A Z (mutlak ve goreli).
 * Yay (A) komutu kubik Bezier'e cevrilir. Hatali girdide istisna firlatmaz; false doner
 * (o ana kadar uretilen cizim hedefte kalir).
 */
object SvgPath {
    fun parse(d: String, sink: PathSink): Boolean {
        val s = Scan(d)
        var cx = 0f; var cy = 0f          // gecerli nokta
        var sx = 0f; var sy = 0f          // alt yolun baslangici
        var lcx = 0f; var lcy = 0f        // son kontrol noktasi (S/T yansitmasi icin)
        var prev = ' '                    // onceki komutun buyuk harfi
        var cmd = ' '
        try {
            while (true) {
                s.skipSep()
                if (s.atEnd()) break
                val ch = s.peek()
                if (ch.isLetter()) {
                    cmd = ch
                    s.next()
                    if (cmd == 'Z' || cmd == 'z') {
                        sink.close(); cx = sx; cy = sy; prev = 'Z'
                        continue
                    }
                } else if (cmd == ' ' || cmd == 'Z' || cmd == 'z') {
                    return false
                }
                val rel = cmd.isLowerCase()
                val ox = if (rel) cx else 0f
                val oy = if (rel) cy else 0f
                when (cmd.uppercaseChar()) {
                    'M' -> {
                        val x = ox + s.num(); val y = oy + s.num()
                        sink.moveTo(x, y); cx = x; cy = y; sx = x; sy = y
                        prev = 'M'
                        cmd = if (rel) 'l' else 'L'   // ardisik ciftler cizgidir
                    }
                    'L' -> {
                        val x = ox + s.num(); val y = oy + s.num()
                        sink.lineTo(x, y); cx = x; cy = y; prev = 'L'
                    }
                    'H' -> {
                        val x = (if (rel) cx else 0f) + s.num()
                        sink.lineTo(x, cy); cx = x; prev = 'H'
                    }
                    'V' -> {
                        val y = (if (rel) cy else 0f) + s.num()
                        sink.lineTo(cx, y); cy = y; prev = 'V'
                    }
                    'C' -> {
                        val x1 = ox + s.num(); val y1 = oy + s.num()
                        val x2 = ox + s.num(); val y2 = oy + s.num()
                        val x = ox + s.num(); val y = oy + s.num()
                        sink.cubicTo(x1, y1, x2, y2, x, y)
                        lcx = x2; lcy = y2; cx = x; cy = y; prev = 'C'
                    }
                    'S' -> {
                        val x2 = ox + s.num(); val y2 = oy + s.num()
                        val x = ox + s.num(); val y = oy + s.num()
                        val x1 = if (prev == 'C' || prev == 'S') 2 * cx - lcx else cx
                        val y1 = if (prev == 'C' || prev == 'S') 2 * cy - lcy else cy
                        sink.cubicTo(x1, y1, x2, y2, x, y)
                        lcx = x2; lcy = y2; cx = x; cy = y; prev = 'S'
                    }
                    'Q' -> {
                        val x1 = ox + s.num(); val y1 = oy + s.num()
                        val x = ox + s.num(); val y = oy + s.num()
                        sink.quadTo(x1, y1, x, y)
                        lcx = x1; lcy = y1; cx = x; cy = y; prev = 'Q'
                    }
                    'T' -> {
                        val x = ox + s.num(); val y = oy + s.num()
                        val x1 = if (prev == 'Q' || prev == 'T') 2 * cx - lcx else cx
                        val y1 = if (prev == 'Q' || prev == 'T') 2 * cy - lcy else cy
                        sink.quadTo(x1, y1, x, y)
                        lcx = x1; lcy = y1; cx = x; cy = y; prev = 'T'
                    }
                    'A' -> {
                        val rx = s.num(); val ry = s.num(); val rot = s.num()
                        val large = s.flag(); val sweep = s.flag()
                        val x = ox + s.num(); val y = oy + s.num()
                        arc(sink, cx, cy, rx, ry, rot, large, sweep, x, y)
                        cx = x; cy = y; prev = 'A'
                    }
                    else -> return false
                }
            }
        } catch (e: Exception) {
            return false
        }
        return true
    }

    private fun arc(
        sink: PathSink, x1f: Float, y1f: Float, rxIn: Float, ryIn: Float, rotDeg: Float,
        large: Boolean, sweep: Boolean, x2f: Float, y2f: Float
    ) {
        val x1 = x1f.toDouble(); val y1 = y1f.toDouble(); val x2 = x2f.toDouble(); val y2 = y2f.toDouble()
        var rx = abs(rxIn.toDouble()); var ry = abs(ryIn.toDouble())
        if (x1 == x2 && y1 == y2) return
        if (rx == 0.0 || ry == 0.0) { sink.lineTo(x2f, y2f); return }
        val phi = Math.toRadians(rotDeg.toDouble() % 360.0)
        val cosP = cos(phi); val sinP = sin(phi)
        val dx2 = (x1 - x2) / 2.0; val dy2 = (y1 - y2) / 2.0
        val x1p = cosP * dx2 + sinP * dy2
        val y1p = -sinP * dx2 + cosP * dy2
        var rxs = rx * rx; var rys = ry * ry
        val lambda = x1p * x1p / rxs + y1p * y1p / rys
        if (lambda > 1.0) {
            val q = sqrt(lambda); rx *= q; ry *= q; rxs = rx * rx; rys = ry * ry
        }
        val num = rxs * rys - rxs * y1p * y1p - rys * x1p * x1p
        val den = rxs * y1p * y1p + rys * x1p * x1p
        var coef = if (den == 0.0) 0.0 else sqrt(max(0.0, num / den))
        if (large == sweep) coef = -coef
        val cxp = coef * rx * y1p / ry
        val cyp = coef * -ry * x1p / rx
        val ccx = cosP * cxp - sinP * cyp + (x1 + x2) / 2.0
        val ccy = sinP * cxp + cosP * cyp + (y1 + y2) / 2.0
        val theta1 = atan2((y1p - cyp) / ry, (x1p - cxp) / rx)
        val theta2 = atan2((-y1p - cyp) / ry, (-x1p - cxp) / rx)
        var dTheta = theta2 - theta1
        if (sweep && dTheta < 0) dTheta += 2 * PI
        else if (!sweep && dTheta > 0) dTheta -= 2 * PI
        val segs = max(1, ceil(abs(dTheta) / (PI / 2) - 1e-9).toInt())
        val delta = dTheta / segs
        val t = 4.0 / 3.0 * tan(delta / 4.0)
        var a = theta1
        for (i in 0 until segs) {
            val cA = cos(a); val sA = sin(a)
            val cB = cos(a + delta); val sB = sin(a + delta)
            val c1x = cA - t * sA; val c1y = sA + t * cA
            val c2x = cB + t * sB; val c2y = sB - t * cB
            fun mx(px: Double, py: Double) = (ccx + rx * px * cosP - ry * py * sinP).toFloat()
            fun my(px: Double, py: Double) = (ccy + rx * px * sinP + ry * py * cosP).toFloat()
            val last = i == segs - 1
            sink.cubicTo(
                mx(c1x, c1y), my(c1x, c1y), mx(c2x, c2y), my(c2x, c2y),
                if (last) x2f else mx(cB, sB), if (last) y2f else my(cB, sB)
            )
            a += delta
        }
    }

    private class Scan(val s: String) {
        var i = 0
        fun atEnd() = i >= s.length
        fun peek() = s[i]
        fun next() { i++ }
        fun skipSep() { while (i < s.length && (s[i] == ' ' || s[i] == ',' || s[i] == '\n' || s[i] == '\t' || s[i] == '\r')) i++ }

        fun flag(): Boolean {
            skipSep()
            if (atEnd()) throw IllegalStateException("flag")
            val c = s[i]
            if (c != '0' && c != '1') throw IllegalStateException("flag")
            i++
            return c == '1'
        }

        fun num(): Float {
            skipSep()
            val st = i
            if (i < s.length && (s[i] == '-' || s[i] == '+')) i++
            var digits = 0
            while (i < s.length && s[i].isDigit()) { i++; digits++ }
            if (i < s.length && s[i] == '.') {
                i++
                while (i < s.length && s[i].isDigit()) { i++; digits++ }
            }
            if (digits == 0) throw IllegalStateException("num")
            if (i < s.length && (s[i] == 'e' || s[i] == 'E')) {
                val save = i
                i++
                if (i < s.length && (s[i] == '-' || s[i] == '+')) i++
                var ed = 0
                while (i < s.length && s[i].isDigit()) { i++; ed++ }
                if (ed == 0) i = save
            }
            return s.substring(st, i).toFloat()
        }
    }
}
