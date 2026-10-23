package com.myhealth.app.ui.kit

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt

/**
 * Ana buton: siyah hap, ortada turuncu isik lekesi ve parlayan turuncu kenar
 * (referanstaki "Get Started" butonu).
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: String? = null
) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp)
            .alpha(if (enabled) 1f else 0.4f)
            .clip(shape)
            .background(Color(0xFF0B0705))
            .drawBehind {
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, Color(0xCCF2761B), Color(0xFFFF9A3C), Color(0xCCF2761B), Color.Transparent)
                    )
                )
                drawRect(
                    brush = Brush.verticalGradient(listOf(Color(0x33000000), Color(0x66000000)))
                )
            }
            .border(1.5.dp, Brush.horizontalGradient(listOf(Color(0x66F2761B), Lux.orange, Color(0x66F2761B))), shape)
            .pressable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (icon != null) LuxIcon(icon, tint = Color.White, size = 20.dp)
            Txt(text, sty = Sty.BUTTON, color = Color.White)
        }
    }
}

/** Ikincil buton: koyu cam hap, soluk yazi (referanstaki "Sign In"). */
@Composable
fun GhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: String? = null,
    tint: Color = Lux.textDim
) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .alpha(if (enabled) 1f else 0.4f)
            .clip(shape)
            .background(Lux.glass)
            .border(1.dp, Lux.strokeSoft, shape)
            .pressable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (icon != null) LuxIcon(icon, tint = tint, size = 20.dp)
            Txt(text, sty = Sty.BUTTON, color = tint)
        }
    }
}

/** Yuvarlak cam ikon dugmesi (geri, menu, bildirim). */
@Composable
fun IconBtn(
    icon: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    description: String = "",
    size: Dp = 44.dp,
    tint: Color = Lux.text,
    filled: Boolean = false,
    autoMirror: Boolean = false,
    background: Color = Lux.glass
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(background)
            .border(1.dp, Lux.strokeSoft, CircleShape)
            .semantics { contentDescription = description }
            .pressable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        LuxIcon(icon, tint = tint, size = size * 0.46f, filled = filled, autoMirror = autoMirror)
    }
}

/** Secim hapi: secili = beyaz dolgu / siyah yazi; degilse cam / soluk yazi. */
@Composable
fun Chip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: String? = null
) {
    val shape = RoundedCornerShape(50)
    val bg = if (selected) Lux.text else Lux.glass
    val fg = if (selected) Color(0xFF0B0705) else Lux.textDim
    Row(
        modifier = modifier
            .height(40.dp)
            .clip(shape)
            .background(bg)
            .border(1.dp, if (selected) Lux.text else Lux.strokeSoft, shape)
            .pressable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (icon != null) LuxIcon(icon, tint = fg, size = 18.dp)
        Txt(text, sty = Sty.LABEL, color = fg, maxLines = 1)
    }
}

/** Turuncu cerceveli kucuk etiket ("Asistanin" gibi). */
@Composable
fun TagPill(text: String, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier = modifier
            .height(32.dp)
            .clip(shape)
            .background(Color(0x14F2761B))
            .border(1.dp, Color(0x99F2761B), shape)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Txt(text, sty = Sty.SMALL, color = Lux.goldSoft, maxLines = 1)
    }
}

/** Liste satirlarindaki kucuk cam etiket ("45 dk", "Aksam"). */
@Composable
fun SmallTag(text: String, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier = modifier
            .height(24.dp)
            .clip(shape)
            .background(Lux.glass)
            .border(1.dp, Lux.strokeSoft, shape)
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Txt(text, sty = Sty.CAPTION, color = Lux.textDim, maxLines = 1)
    }
}

/** Turuncu yuvarlak oynat/duraklat dugmesi (referanstaki liste satiri dugmesi). */
@Composable
fun RoundPlay(
    playing: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(Lux.amber, Lux.orange)))
            .pressable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        LuxIcon(
            if (playing) Ic.PAUSE else Ic.PLAY,
            tint = Color(0xFF1A0E05),
            size = size * 0.42f,
            filled = true
        )
    }
}

/**
 * Ilk giris akisinin "Go" kapsulu: ustte dalgalanan oklar, altta yuvarlak dugme.
 * Tiklayinca veya yukari kaydirinca onClick calisir.
 */
@Composable
fun GoControl(label: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    val shape = RoundedCornerShape(50)
    val current by rememberUpdatedState(onClick)
    val thresholdPx = with(LocalDensity.current) { 36.dp.toPx() }
    val transition = rememberInfiniteTransition(label = "go")
    val wave by transition.animateFloat(
        initialValue = 0f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing), RepeatMode.Restart),
        label = "wave"
    )
    Column(
        modifier = modifier
            .width(76.dp)
            .height(132.dp)
            .alpha(if (enabled) 1f else 0.4f)
            .clip(shape)
            .background(Brush.verticalGradient(listOf(Color(0x33F2761B), Color(0x0DFFFFFF))))
            .border(1.dp, Brush.verticalGradient(listOf(Color(0x80F2761B), Color(0x22FFFFFF))), shape)
            .pointerInput(enabled) {
                var total = 0f
                detectVerticalDragGestures(
                    onDragStart = { total = 0f },
                    onDragEnd = { if (enabled && total < -thresholdPx) current() },
                    onDragCancel = { total = 0f },
                    onVerticalDrag = { _, dy -> total += dy }
                )
            }
            .padding(top = 12.dp, bottom = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy((-9).dp)) {
            for (i in 0 until 3) {
                // Alttaki ok once, ustteki en son parlar: yukari akan bir dalga.
                val phase = (wave - (2 - i)).let { if (it < 0f) it + 3f else it }
                val a = (1f - phase / 3f).coerceIn(0.25f, 1f)
                LuxIcon(Ic.UP, tint = Lux.orange.copy(alpha = a), size = 20.dp, stroke = 2f)
            }
        }
        Box(
            modifier = Modifier
                .size(62.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(Color(0xFF7A3A12), Color(0xFF1A0E05))))
                .border(1.5.dp, Brush.linearGradient(listOf(Lux.gold, Color(0x55F2761B))), CircleShape)
                .pressable(enabled = enabled, onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Txt(label, sty = Sty.TITLE, color = Color.White, maxLines = 1)
        }
    }
}

/** Dikey bosluk kisayolu. */
@Composable
fun VGap(h: Dp) { Spacer(Modifier.height(h)) }

/** Yatay bosluk kisayolu. */
@Composable
fun HGap(w: Dp) { Spacer(Modifier.width(w)) }
