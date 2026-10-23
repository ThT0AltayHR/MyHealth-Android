package com.myhealth.app.ui.kit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.myhealth.app.ui.theme.Dim
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt

/** Alt gezinme (ve mini oynatici) icin icerigin altinda birakilacak bosluk. Kabuk saglar. */
val LocalBottomClearance = staticCompositionLocalOf { 0.dp }

/**
 * Standart ekran iskeleti: arka plan + ust serit (dinamik ada icin yer) + baslik + icerik.
 * clearBottomNav = true: alt sekme ekranlari icin icerik yuzen gezinmenin altinda kalmaz.
 * Icerigi kaydirmak ekranin kendi sorumlulugundadir (LazyColumn veya verticalScroll).
 */
@Composable
fun ScreenScaffold(
    modifier: Modifier = Modifier,
    title: String? = null,
    onBack: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    glow: Glow = Glow.WARM_TOP,
    reserveIsland: Boolean = true,
    clearBottomNav: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val clearance = if (clearBottomNav) LocalBottomClearance.current else 0.dp
    Box(modifier.fillMaxSize()) {
        LuxBackdrop(Modifier.fillMaxSize(), glow)
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(top = if (reserveIsland) Dim.islandBand else 0.dp)
        ) {
            if (title != null || onBack != null || trailing != null) {
                ScreenHeader(title = title, onBack = onBack, trailing = trailing)
            }
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = clearance),
                content = content
            )
        }
    }
}

/** Ust baslik: solda geri dugmesi, ortada baslik, sagda istege bagli eylem. */
@Composable
fun ScreenHeader(
    title: String?,
    onBack: (() -> Unit)?,
    trailing: (@Composable () -> Unit)?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dim.pad, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (onBack != null) {
            IconBtn(Ic.BACK, onClick = onBack, autoMirror = true)
        } else {
            Spacer(Modifier.size(44.dp))
        }
        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
            if (title != null) {
                Txt(title, sty = Sty.H3, align = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        if (trailing != null) {
            Box(Modifier.size(44.dp), contentAlignment = Alignment.Center) { trailing() }
        } else {
            Spacer(Modifier.size(44.dp))
        }
    }
}

/** Bolum basligi; actionText verilirse sagda soluk bir "Tumunu gor" baglantisi cikar. */
@Composable
fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = Dim.pad),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Txt(text, sty = Sty.H3, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
        if (actionText != null && onAction != null) {
            Txt(
                actionText,
                sty = Sty.SMALL,
                color = Lux.textDim,
                modifier = Modifier.pressable(onClick = onAction).padding(vertical = 6.dp, horizontal = 4.dp)
            )
        }
    }
}

/** Dikey boslugu Dp ile veren kisayol. */
@Composable
fun Gap(h: Dp) { Spacer(Modifier.size(width = 0.dp, height = h)) }
