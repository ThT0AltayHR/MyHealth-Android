package com.myhealth.app.ui.kit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt

/** Henuz yazilmamis ekranlar icin gecici iskelet (ozellik yazildikca kaldirilir). */
@Composable
fun PlaceholderScreen(name: String, onBack: (() -> Unit)? = null, tab: Boolean = false) {
    ScreenScaffold(title = name, onBack = onBack, clearBottomNav = tab) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Txt(name, sty = Sty.H2, color = Lux.textDim)
        }
    }
}
