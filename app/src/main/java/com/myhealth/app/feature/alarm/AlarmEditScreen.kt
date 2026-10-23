package com.myhealth.app.feature.alarm

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.myhealth.app.nav.LocalNav
import com.myhealth.app.ui.kit.PlaceholderScreen

// TASLAK: ozellik modulu yazilirken bu dosyanin govdesi degistirilir; imza (ad ve parametreler) SABITTIR.
@Composable
fun AlarmEditScreen(alarmId: Int) {
    val nav = LocalNav.current
    PlaceholderScreen("Alarm", onBack = { nav.pop() }, tab = false)
}
