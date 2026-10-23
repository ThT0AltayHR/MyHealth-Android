package com.myhealth.app.feature.mind

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.myhealth.app.R
import com.myhealth.app.core.Fmt
import com.myhealth.app.feature.health.ExerciseFinishFlow
import com.myhealth.app.nav.LocalNav
import com.myhealth.app.ui.kit.Chip
import com.myhealth.app.ui.kit.GhostButton
import com.myhealth.app.ui.kit.PrimaryButton
import com.myhealth.app.ui.kit.ScreenScaffold
import com.myhealth.app.ui.kit.VGap
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt
import kotlinx.coroutines.delay

// Yarim dongu suresi (ms): yavas, orta, hizli. Dakika secenekleri.
private val breathHalfMs = listOf(5000, 4000, 3000)
private val breathSpeedKeys = listOf("mind.breath.speed.slow", "mind.breath.speed.mid", "mind.breath.speed.fast")
private val breathMinutes = listOf(1, 3, 5, 10)
// 30 sn'den kisa erken cikista bitis akisi gosterilmez, ekran kapanir.
private const val BREATH_MIN_CREDIT_SEC = 30

@Composable
fun BreathingScreen() {
    val nav = LocalNav.current
    var speedIdx by remember { mutableStateOf(1) }
    var minIdx by remember { mutableStateOf(1) }
    var running by remember { mutableStateOf(false) }
    var inhale by remember { mutableStateOf(true) }
    var elapsed by remember { mutableStateOf(0) }
    var finishing by remember { mutableStateOf(false) }
    val total = breathMinutes[minIdx] * 60
    val half = breathHalfMs[speedIdx]

    // Nefes dongusu: her yarim surede bir nefes al / nefes ver degisir.
    LaunchedEffect(running, speedIdx) {
        inhale = true
        if (running) {
            while (true) {
                delay(half.toLong())
                inhale = !inhale
            }
        }
    }
    // Sayac: her saniye ilerler, sure dolunca bitis akisina gecer.
    LaunchedEffect(running) {
        if (running) {
            while (elapsed < total) {
                delay(1000L)
                if (!running) return@LaunchedEffect
                elapsed += 1
            }
            running = false
            finishing = true
        }
    }
    val start: () -> Unit = {
        elapsed = 0
        finishing = false
        running = true
    }
    val stop: () -> Unit = {
        running = false
        if (elapsed >= BREATH_MIN_CREDIT_SEC) {
            finishing = true
        } else {
            nav.pop()
        }
    }
    if (finishing) {
        ExerciseFinishFlow(
            exerciseId = "breathing",
            durationSec = elapsed,
            summaryLines = listOf(
                t("mind.breath.sum.time") to Fmt.clock(elapsed.toLong()),
                t("mind.breath.sum.speed") to t(breathSpeedKeys[speedIdx])
            ),
            onDone = {
                finishing = false
                nav.pop()
            },
            closingMessage = t("mind.breath.done")
        )
        return
    }
    val scale by animateFloatAsState(
        targetValue = if (!running) 0.85f else if (inhale) 1f else 0.7f,
        animationSpec = tween(durationMillis = half),
        label = "breathScale"
    )
    val left = if (running) total - elapsed else total

    ScreenScaffold(title = t("mind.breath.title"), onBack = { nav.pop() }) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                breathSpeedKeys.forEachIndexed { i, key ->
                    Chip(
                        text = t(key),
                        selected = speedIdx == i,
                        onClick = { if (!running) speedIdx = i },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            VGap(10.dp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                breathMinutes.forEachIndexed { i, m ->
                    Chip(
                        text = t("mind.breath.min", m),
                        selected = minIdx == i,
                        onClick = { if (!running) minIdx = i },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            VGap(28.dp)
            // DOGRULA: R.drawable.rings_only, ring_heart, heart_core kaynaklari res/drawable altinda olmali.
            Box(modifier = Modifier.size(260.dp), contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(R.drawable.rings_only),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().graphicsLayer { scaleX = scale; scaleY = scale }
                )
                Image(
                    painter = painterResource(R.drawable.ring_heart),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().graphicsLayer { scaleX = scale; scaleY = scale }
                )
                Image(
                    painter = painterResource(R.drawable.heart_core),
                    contentDescription = null,
                    modifier = Modifier.size(150.dp).graphicsLayer { scaleX = scale; scaleY = scale }
                )
            }
            VGap(20.dp)
            Txt(text = Fmt.clock(left.toLong()), sty = Sty.NUMBER, color = Lux.gold)
            VGap(6.dp)
            Txt(
                text = when {
                    !running -> t("mind.breath.ready")
                    inhale -> t("mind.breath.in")
                    else -> t("mind.breath.out")
                },
                sty = Sty.H3,
                color = Lux.text
            )
            VGap(24.dp)
            if (!running) {
                Txt(text = t("mind.breath.hint"), sty = Sty.BODY, color = Lux.textDim)
                VGap(20.dp)
                PrimaryButton(text = t("mind.breath.start"), onClick = start, modifier = Modifier.fillMaxWidth())
            } else {
                GhostButton(text = t("mind.breath.stop"), onClick = stop, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
