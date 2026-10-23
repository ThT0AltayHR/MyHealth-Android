package com.myhealth.app.ui.kit

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.myhealth.app.core.PathSink
import com.myhealth.app.core.SvgPath
import com.myhealth.app.ui.theme.Lux

/** SVG yol metnini Compose Path'e cevirir ve onbellekler (ayni ikon bir kez ayristirilir). */
object IconCache {
    private val map = HashMap<String, Path>()

    fun get(d: String): Path = synchronized(map) {
        map.getOrPut(d) {
            val p = Path()
            SvgPath.parse(d, ComposeSink(p))
            p
        }
    }

    private class ComposeSink(private val p: Path) : PathSink {
        private var cx = 0f
        private var cy = 0f

        override fun moveTo(x: Float, y: Float) { p.moveTo(x, y); cx = x; cy = y }
        override fun lineTo(x: Float, y: Float) { p.lineTo(x, y); cx = x; cy = y }
        override fun quadTo(x1: Float, y1: Float, x2: Float, y2: Float) {
            // Ikinci derece egriyi kubik egriye cevir (surumler arasi API farkindan kacinmak icin).
            p.cubicTo(
                cx + 2f / 3f * (x1 - cx), cy + 2f / 3f * (y1 - cy),
                x2 + 2f / 3f * (x1 - x2), y2 + 2f / 3f * (y1 - y2),
                x2, y2
            )
            cx = x2; cy = y2
        }
        override fun cubicTo(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float) {
            p.cubicTo(x1, y1, x2, y2, x3, y3); cx = x3; cy = y3
        }
        override fun close() { p.close() }
    }
}

/**
 * Vektor ikon. path: Ic.* sabiti (24x24 izgara). filled=true ise ic de doldurulur.
 * autoMirror=true ise Arapca (RTL) duzende yatay aynalanir (ok/ileri/geri ikonlari icin).
 */
@Composable
fun LuxIcon(
    path: String,
    modifier: Modifier = Modifier,
    tint: Color = Lux.text,
    size: Dp = 24.dp,
    stroke: Float = 1.8f,
    filled: Boolean = false,
    autoMirror: Boolean = false
) {
    val compiled = remember(path) { IconCache.get(path) }
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Canvas(modifier = modifier.size(size)) {
        val k = this.size.minDimension / 24f
        val mirror = autoMirror && rtl
        scale(if (mirror) -1f else 1f, 1f, pivot = center) {
            scale(k, k, pivot = Offset.Zero) {
                if (filled) drawPath(compiled, color = tint, style = Fill)
                drawPath(
                    compiled,
                    color = tint,
                    style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }
        }
    }
}
