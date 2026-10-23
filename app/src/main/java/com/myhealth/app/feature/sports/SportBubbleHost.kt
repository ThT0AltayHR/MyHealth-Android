package com.myhealth.app.feature.sports

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.myhealth.app.core.Fmt
import com.myhealth.app.nav.LocalNav
import com.myhealth.app.nav.Route
import com.myhealth.app.ui.kit.LuxIcon
import com.myhealth.app.ui.kit.SportIcons
import com.myhealth.app.ui.kit.pressable
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt
import kotlin.math.roundToInt

/** Antrenman kucultulunce gorunen yuvarlak baloncuk (kabuk her ekranin ustune koyar). Imza SABIT. */
@Composable
fun SportBubbleHost() {
    val nav = LocalNav.current
    val st by WorkoutSession.state.collectAsState()
    if (!st.active) return
    // DOGRULA: nav.isOpen Compose'da okunabilir bir durumdan besleniyorsa baloncuk kendiliginden yenilenir
    if (nav.isOpen { it is Route.SportLive }) return
    var dx by remember { mutableStateOf(0f) }
    var dy by remember { mutableStateOf(0f) }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopEnd) {
        Box(
            Modifier
                .padding(top = 120.dp, end = 12.dp)
                .offset { IntOffset(dx.roundToInt(), dy.roundToInt()) }
                .size(64.dp)
                .pointerInput(Unit) {
                    detectDragGestures { change, drag ->
                        change.consume() // DOGRULA: PointerInputChange.consume() surume gore farkli olabilir
                        dx += drag.x
                        dy += drag.y
                    }
                }
                .clip(CircleShape)
                .background(Lux.accentBrush, CircleShape)
                .pressable { nav.push(Route.SportLive(st.sportId)) },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LuxIcon(SportIcons.sport(st.sportId), size = 20.dp, tint = Lux.text)
                Txt(Fmt.clock(st.elapsedSec), sty = Sty.CAPTION, color = Lux.text)
            }
        }
    }
}
