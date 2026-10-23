package com.myhealth.app.feature.health

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.myhealth.app.ui.kit.Ic
import com.myhealth.app.ui.kit.LuxIcon
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Lux
import kotlinx.coroutines.launch

/**
 * Egzersiz bitisinin ilk evresi (~2,5 sn): yesil daire yaylanarak buyur, beyaz tik belirir ve kendi etrafinda doner,
 * halka doner ve parlar; tik yavasca kaybolunca onFinished bir kez cagrilir. Yalniz cizim/animasyon; ag ve dosya yok.
 */
@Composable
internal fun FinishCelebration(onFinished: () -> Unit, modifier: Modifier = Modifier) {
    val grow = remember { Animatable(0f) }
    val tickAlpha = remember { Animatable(0f) }
    val tickSpin = remember { Animatable(0f) }
    val ring = rememberInfiniteTransition(label = "finish_ring")
    val ringAngle by ring.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "finish_ring_angle"
    )
    val pulse by ring.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 700),
            repeatMode = RepeatMode.Reverse
        ),
        label = "finish_pulse"
    )
    val onFinishedNow by rememberUpdatedState(onFinished)
    val doneDesc = t("quiz.finish.done_desc")

    LaunchedEffect(Unit) {
        launch {
            grow.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
        }
        tickAlpha.animateTo(1f, tween(durationMillis = 450))
        tickSpin.animateTo(360f, tween(durationMillis = 1200, easing = FastOutSlowInEasing))
        tickAlpha.animateTo(0f, tween(durationMillis = 700))
        onFinishedNow()
    }

    Box(
        modifier = modifier.size(240.dp).semantics { contentDescription = doneDesc },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val g = grow.value.coerceIn(0f, 1f)
            val p = pulse.coerceIn(0f, 1f)
            val rad = size.minDimension / 2f
            val c = center
            val stroke = 6.dp.toPx()
            val rr = rad * 0.84f
            val tl = Offset(c.x - rr, c.y - rr)
            val sz = Size(rr * 2f, rr * 2f)
            // parlayan isik
            drawCircle(
                color = Lux.green.copy(alpha = (0.22f * p * g).coerceIn(0f, 1f)),
                radius = rad * 0.94f * g,
                center = c
            )
            // yesil daire
            drawCircle(color = Lux.green, radius = rad * 0.66f * g, center = c)
            // halka: genis soluk isima, donen ana yay ve karsi yay
            drawArc(
                color = Lux.gold.copy(alpha = (0.35f * p * g).coerceIn(0f, 1f)),
                startAngle = ringAngle,
                sweepAngle = 200f,
                useCenter = false,
                topLeft = tl,
                size = sz,
                style = Stroke(width = stroke * 3f, cap = StrokeCap.Round)
            )
            drawArc(
                color = Lux.gold.copy(alpha = g.coerceIn(0f, 1f)),
                startAngle = ringAngle,
                sweepAngle = 110f,
                useCenter = false,
                topLeft = tl,
                size = sz,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            drawArc(
                color = Lux.gold.copy(alpha = (0.6f * g).coerceIn(0f, 1f)),
                startAngle = ringAngle + 180f,
                sweepAngle = 60f,
                useCenter = false,
                topLeft = tl,
                size = sz,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }
        LuxIcon(
            path = Ic.CHECK,
            tint = Color.White,
            size = 104.dp,
            stroke = 2.4f,
            modifier = Modifier.graphicsLayer {
                alpha = tickAlpha.value.coerceIn(0f, 1f)
                rotationZ = tickSpin.value
            }
        )
    }
}
