package com.myhealth.app.feature.notes

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.myhealth.app.nav.LocalNav
import com.myhealth.app.ui.kit.PlaceholderScreen

// TASLAK: ozellik modulu yazilirken bu dosyanin govdesi degistirilir; imza (ad ve parametreler) SABITTIR.
@Composable
fun NoteEditScreen(noteId: Long) {
    val nav = LocalNav.current
    PlaceholderScreen("Note", onBack = { nav.pop() }, tab = false)
}
