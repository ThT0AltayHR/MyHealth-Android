package com.myhealth.app.feature.sports

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * 3B sporcu gorunumu (assets/web3d). motion/prop degerleri SportsCatalog.Sport.motion/prop ile aynidir.
 * active=false: bekleme durusu, true: hareket. tempo: 0.5..2.0 hiz carpani.
 * WebView/WebGL kullanilamazsa onUnavailable() cagrilir.
 */
@Composable
fun AthleteView(
    motion: String,
    prop: String,
    active: Boolean,
    tempo: Float,
    modifier: Modifier = Modifier,
    onUnavailable: () -> Unit = {}
) {
    // TASLAK
}
