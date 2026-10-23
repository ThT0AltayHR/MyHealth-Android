package com.myhealth.app.ui.kit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Isik halkasi olan nabiz atan nokta (cevrimici/cevrimdisi gostergesi vb.). */
@Composable
fun PulseDot(color: Color, modifier: Modifier = Modifier, dot: Dp = 9.dp) {
    val tr = rememberInfiniteTransition(label = "pulse")
    val k by tr.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), RepeatMode.Restart),
        label = "k"
    )
    Box(modifier.size(dot * 2.6f), contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .size(dot * (1f + 1.6f * k))
                .alpha((1f - k) * 0.55f)
                .background(color, CircleShape)
        )
        Box(Modifier.size(dot).background(color, CircleShape))
    }
}

/** Yuzdelik halka: ortaya icerik konabilir. progress 0..1. */
@Composable
fun ProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    diameter: Dp = 120.dp,
    strokeWidth: Dp = 10.dp,
    track: Color = Lux.glassStrong,
    brush: Brush = Lux.accentBrush,
    content: @Composable BoxScope.() -> Unit = {}
) {
    val p by animateFloatAsState(progress.coerceIn(0f, 1f), animationSpec = tween(700), label = "ring")
    Box(modifier.size(diameter), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val sw = strokeWidth.toPx()
            val topLeft = Offset(sw / 2f, sw / 2f)
            val arcSize = Size(this.size.width - sw, this.size.height - sw)
            drawArc(
                color = track,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = sw, cap = StrokeCap.Round)
            )
            if (p > 0.004f) {
                drawArc(
                    brush = brush,
                    startAngle = -90f,
                    sweepAngle = 360f * p,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = sw, cap = StrokeCap.Round)
                )
            }
        }
        content()
    }
}

/** Hizalanmis cam diyalog penceresi. */
@Composable
fun LuxDialog(onDismiss: () -> Unit, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(28.dp)
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Column(
            modifier = modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .clip(shape)
                .background(Color(0xFF120D09))
                .border(1.dp, Lux.borderBrush, shape)
                .padding(20.dp),
            content = content
        )
    }
}

/** Onay diyalogu: danger=true ise onay dugmesi kirmizi vurgulu. */
@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    danger: Boolean = false
) {
    LuxDialog(onDismiss = onDismiss) {
        Txt(title, sty = Sty.H3)
        Gap(8.dp)
        Txt(message, sty = Sty.BODY, color = Lux.textDim)
        Gap(20.dp)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            GhostButton(t("common.cancel"), onClick = onDismiss, modifier = Modifier.weight(1f))
            if (danger) {
                GhostButton(confirmText, onClick = onConfirm, modifier = Modifier.weight(1f), tint = Lux.red)
            } else {
                PrimaryButton(confirmText, onClick = onConfirm, modifier = Modifier.weight(1f))
            }
        }
    }
}

/** Bos liste durumu. */
@Composable
fun EmptyState(icon: String, title: String, modifier: Modifier = Modifier, subtitle: String? = null) {
    Column(
        modifier = modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            Modifier.size(72.dp).clip(CircleShape).background(Lux.glass).border(1.dp, Lux.strokeSoft, CircleShape),
            contentAlignment = Alignment.Center
        ) { LuxIcon(icon, tint = Lux.goldSoft, size = 30.dp) }
        Txt(title, sty = Sty.TITLE, align = TextAlign.Center)
        if (subtitle != null) Txt(subtitle, sty = Sty.SMALL, color = Lux.textDim, align = TextAlign.Center)
    }
}

/** Sigmayan uzun metni (cihaz adi gibi) sagdan sola ritimli kaydiran tek satir. */
@Composable
fun MarqueeText(
    text: String,
    modifier: Modifier = Modifier,
    sty: Sty = Sty.BODY,
    color: Color = Lux.text,
    active: Boolean = true
) {
    val scroll = rememberScrollState()
    LaunchedEffect(text, active, scroll.maxValue) {
        if (!active || scroll.maxValue <= 0) return@LaunchedEffect
        while (true) {
            delay(1200)
            scroll.animateScrollTo(
                scroll.maxValue,
                tween(durationMillis = (scroll.maxValue * 20).coerceIn(1500, 14000), easing = LinearEasing)
            )
            delay(900)
            scroll.scrollTo(0)
        }
    }
    Box(modifier.horizontalScroll(scroll, enabled = false)) {
        Txt(text, sty = sty, color = color, maxLines = 1, overflow = TextOverflow.Clip)
    }
}

/** Kisa gecici bildirim (Snackbar yerine). Herhangi bir yerden Toasts.show(...) cagrilir. */
object Toasts {
    private val flow = MutableStateFlow<Pair<Long, String>?>(null)
    val message: StateFlow<Pair<Long, String>?> = flow.asStateFlow()

    fun show(text: String) {
        flow.value = System.nanoTime() to text
    }
}

/** Kabuk tarafindan en ustte bir kez yerlestirilir. */
@Composable
fun ToastHost(modifier: Modifier = Modifier) {
    val m by Toasts.message.collectAsState()
    var shown by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }
    LaunchedEffect(m) {
        val cur = m
        if (cur != null) {
            text = cur.second
            shown = true
            delay(2600)
            shown = false
        }
    }
    Box(modifier.fillMaxSize().navigationBarsPadding().padding(bottom = 108.dp), contentAlignment = Alignment.BottomCenter) {
        AnimatedVisibility(
            visible = shown,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut()
        ) {
            val shape = RoundedCornerShape(50)
            Box(
                Modifier
                    .padding(horizontal = 24.dp)
                    .clip(shape)
                    .background(Color(0xF2201610))
                    .border(1.dp, Lux.stroke, shape)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Txt(text, sty = Sty.LABEL, align = TextAlign.Center, maxLines = 3)
            }
        }
    }
}
