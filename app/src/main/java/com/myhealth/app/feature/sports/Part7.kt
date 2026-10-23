package com.myhealth.app.feature.sports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.myhealth.app.data.LatLng
import com.myhealth.app.data.Sport
import com.myhealth.app.data.WorkoutState
import com.myhealth.app.feature.map.RouteMap
import com.myhealth.app.ui.kit.GhostButton
import com.myhealth.app.ui.kit.Ic
import com.myhealth.app.ui.kit.LuxIcon
import com.myhealth.app.ui.kit.PrimaryButton
import com.myhealth.app.ui.kit.SportIcons
import com.myhealth.app.ui.kit.VGap
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Lux

/** GPS'li sporda Harita sekmesi (RouteMap); digerlerinde ve 3B sekmesinde AthleteView. 3B yuklenemezse ikon. */
@Composable
internal fun SportLiveVisual(
    sport: Sport,
    st: WorkoutState,
    route: List<LatLng>,
    showMap: Boolean,
    athleteFailed: Boolean,
    onAthleteUnavailable: () -> Unit
) {
    if (showMap) {
        RouteMap(route = route, current = st.position, modifier = Modifier.fillMaxWidth().height(300.dp))
        return
    }
    if (athleteFailed) {
        Box(Modifier.fillMaxWidth().height(260.dp), contentAlignment = Alignment.Center) {
            LuxIcon(SportIcons.sport(sport.id), size = 120.dp, tint = Lux.gold)
        }
        return
    }
    val speed = if (st.speedKmh.isNaN()) 0.0 else st.speedKmh
    val tempo = (0.6 + speed / 12.0).toFloat().coerceIn(0.5f, 2.0f)
    AthleteView(
        motion = sport.motion,
        prop = sport.prop,
        active = st.active && !st.paused,
        tempo = tempo,
        modifier = Modifier.fillMaxWidth().height(300.dp),
        onUnavailable = onAthleteUnavailable
    )
}

/** Baslat / duraklat / devam / bitir ve sure hedefini duzenleme. */
@Composable
internal fun SportLiveControls(
    st: WorkoutState,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onFinish: () -> Unit,
    onTarget: () -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        if (!st.active) {
            PrimaryButton(
                text = t("sportlive.start"),
                onClick = onStart,
                modifier = Modifier.fillMaxWidth(),
                icon = Ic.PLAY
            )
        } else {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (st.paused) {
                    PrimaryButton(
                        text = t("sportlive.resume"),
                        onClick = onResume,
                        modifier = Modifier.weight(1f),
                        icon = Ic.PLAY
                    )
                } else {
                    PrimaryButton(
                        text = t("sportlive.pause"),
                        onClick = onPause,
                        modifier = Modifier.weight(1f),
                        icon = Ic.PAUSE
                    )
                }
                GhostButton(
                    text = t("sportlive.finish"),
                    onClick = onFinish,
                    modifier = Modifier.weight(1f),
                    icon = Ic.STOP
                )
            }
            VGap(10.dp)
            GhostButton(
                text = t("sportlive.target.edit"),
                onClick = onTarget,
                modifier = Modifier.fillMaxWidth(),
                icon = Ic.TARGET
            )
        }
    }
}
