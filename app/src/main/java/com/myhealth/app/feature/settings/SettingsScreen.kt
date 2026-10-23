package com.myhealth.app.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.myhealth.app.nav.LocalNav
import com.myhealth.app.ui.kit.LocalBottomClearance
import com.myhealth.app.ui.kit.ScreenScaffold
import com.myhealth.app.ui.t

// Ayarlar ekrani: bolumler Part1-6.kt icinde. Imza SABIT.
@Composable
fun SettingsScreen() {
    val nav = LocalNav.current
    ScreenScaffold(title = t("settings.title"), onBack = { nav.pop() }) {
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = LocalBottomClearance.current + 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item(key = "doctor") { SettingsDoctor() }
            item(key = "health") { SettingsHealth() }
            item(key = "ai") { SettingsAi() }
            item(key = "notif") { SettingsNotif() }
            item(key = "goals") { SettingsGoals() }
            item(key = "about") { SettingsAbout() }
            item(key = "diag") { SettingsDiag() }
            item(key = "danger") { SettingsDanger() }
        }
    }
}
