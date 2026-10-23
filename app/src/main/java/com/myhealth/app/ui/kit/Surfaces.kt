package com.myhealth.app.ui.kit

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.myhealth.app.R
import com.myhealth.app.ui.theme.Dim
import com.myhealth.app.ui.theme.Lux

/** Basinca hafifce kuculen tiklanabilir yuzey (dalga efekti yok, daha "premium" his). */
@Composable
fun Modifier.pressable(enabled: Boolean = true, onClick: () -> Unit): Modifier {
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed && enabled) 0.97f else 1f, label = "press")
    return this
        .graphicsLayer { scaleX = scale; scaleY = scale }
        .clickable(interactionSource = source, indication = null, enabled = enabled, onClick = onClick)
}

enum class Glow { NONE, WARM_TOP, ARCS }

/**
 * Tum ekranlarin arka plani: saf siyah zemin, ustte sicak kahverengi/turuncu isik.
 * ARCS: referanstaki parlayan yay gorseli (karsilama / asistan ekranlari).
 */
@Composable
fun LuxBackdrop(modifier: Modifier = Modifier.fillMaxSize(), glow: Glow = Glow.WARM_TOP) {
    Box(modifier.background(Lux.black)) {
        when (glow) {
            Glow.NONE -> Unit
            Glow.WARM_TOP -> Canvas(Modifier.fillMaxSize()) {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Lux.brownTop, Lux.brownMid, Color.Transparent),
                        startY = 0f,
                        endY = size.height * 0.55f
                    )
                )
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x66FF8A1E), Color.Transparent),
                        center = Offset(size.width * 0.92f, size.height * 0.04f),
                        radius = size.width * 0.9f
                    )
                )
            }
            Glow.ARCS -> Image(
                painter = painterResource(R.drawable.bg_arcs),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter
            )
        }
    }
}

/** Cam kart: yari saydam dolgu + ince altin kenar. onClick verilirse tiklanabilir. */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    radius: Dp = Dim.radius,
    warm: Boolean = false,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(radius)
    var m = modifier
        .clip(shape)
        .background(if (warm) Lux.warmCardBrush else Lux.cardBrush)
        .border(1.dp, Lux.borderBrush, shape)
    if (onClick != null) m = m.pressable(onClick = onClick)
    Column(modifier = m.padding(contentPadding), content = content)
}

/** Satir listeleri icin koyu panel (ayarlar, profil gibi gruplu listeler). */
@Composable
fun Panel(
    modifier: Modifier = Modifier,
    radius: Dp = Dim.radius,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(radius)
    Column(
        modifier = modifier
            .clip(shape)
            .background(Lux.surface2.copy(alpha = 0.92f))
            .border(1.dp, Lux.strokeSoft, shape),
        content = content
    )
}

/** Panel icindeki satirlari ayiran ince cizgi. */
@Composable
fun HairLine(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().height(1.dp).background(Lux.strokeSoft))
}

/** Ortak yatay dolgu ile bolum kutusu. */
@Composable
fun Section(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(modifier.padding(horizontal = Dim.pad), content = content)
}
