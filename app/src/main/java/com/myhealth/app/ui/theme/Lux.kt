package com.myhealth.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.myhealth.app.core.Lang
import com.myhealth.app.ui.LocalLang

/** Marka renkleri: siyah zemin, turuncu/altin vurgu, yari saydam "cam" yuzeyler. */
object Lux {
    val black = Color(0xFF000000)
    val bg = Color(0xFF050403)
    val surface = Color(0xFF0E0B08)
    val surface2 = Color(0xFF17110C)
    val brownTop = Color(0xFF34190A)
    val brownMid = Color(0xFF1B0F06)

    val orange = Color(0xFFF2761B)
    val orangeDeep = Color(0xFFD9560B)
    val amber = Color(0xFFFFA52E)
    val gold = Color(0xFFFFB43A)
    val goldSoft = Color(0xFFFFD27A)

    val text = Color(0xFFF6EFE6)
    val textDim = Color(0xFFA39A8F)
    val textFaint = Color(0xFF6E675F)

    val glass = Color(0x14FFFFFF)
    val glassStrong = Color(0x26FFFFFF)
    val stroke = Color(0x33FFB43A)
    val strokeSoft = Color(0x1FFFFFFF)

    val green = Color(0xFF34D27B)
    val red = Color(0xFFFF4D4D)
    val blue = Color(0xFF5BB8FF)

    /** Turuncu -> altin yatay/capraz vurgu. */
    val accentBrush: Brush = Brush.linearGradient(listOf(orange, gold))

    /** Cam kart dolgusu (ustten alta soluklasan beyaz). */
    val cardBrush: Brush = Brush.verticalGradient(listOf(Color(0x26FFFFFF), Color(0x0AFFFFFF)))

    /** Sicak (kahverengi/turuncu) cam kart dolgusu. */
    val warmCardBrush: Brush = Brush.verticalGradient(listOf(Color(0x59F2761B), Color(0x1A8A3A0A)))

    /** Kart kenari: ustte altin, altta neredeyse saydam. */
    val borderBrush: Brush = Brush.verticalGradient(listOf(Color(0x59FFB43A), Color(0x14FFFFFF)))
}

/** Sabit olculer. */
object Dim {
    val pad = 20.dp
    val gap = 12.dp
    val radius = 26.dp
    val radiusSmall = 18.dp

    /** Dinamik ada icin ekran ustunde (durum cubugunun altinda) ayrilan serit. */
    val islandBand = 46.dp

    /** Yuzen alt gezinme cubugunun kapladigi yukseklik (sistem gezinme cubugu haric). */
    val navClearance = 88.dp

    /** Mini muzik oynatici yuksekligi. */
    val miniPlayer = 68.dp
}

/** Tum uygulamayi saran tema: dil, yazi yonu (RTL) ve Material renk semasi. */
@Composable
fun LuxTheme(lang: Lang, content: @Composable () -> Unit) {
    val dir = if (lang.rtl) LayoutDirection.Rtl else LayoutDirection.Ltr
    val scheme = darkColorScheme(
        primary = Lux.orange,
        onPrimary = Color.White,
        secondary = Lux.gold,
        background = Lux.black,
        onBackground = Lux.text,
        surface = Lux.surface,
        onSurface = Lux.text,
        outline = Lux.stroke
    )
    MaterialTheme(colorScheme = scheme) {
        CompositionLocalProvider(
            LocalLang provides lang,
            LocalLayoutDirection provides dir,
            content = content
        )
    }
}
