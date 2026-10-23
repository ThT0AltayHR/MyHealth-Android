package com.myhealth.app.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.myhealth.app.ui.t
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.myhealth.app.data.Repo
import com.myhealth.app.ui.kit.pressable
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt

/**
 * Yuvarlak profil avatari (SABIT SOZLESME): profil fotografi varsa onu, yoksa isim bas harfini altin
 * zeminde gosterir. Ana sayfa, daha ve profil ekraninda kullanilir. Profil ajani govdeyi foto destegiyle degistirir.
 */
@Composable
fun Avatar(size: Dp, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    val profile by Repo.profile.collectAsState()
    val version by ProfilePhoto.version.collectAsState()
    val ctx = LocalContext.current
    val px = with(LocalDensity.current) { size.roundToPx() }.coerceAtLeast(16)
    var photo by remember(version, px) { mutableStateOf(AvatarCache.peek(version, px)) }
    LaunchedEffect(version, px) {
        if (photo == null) {
            photo = withContext(Dispatchers.IO) { AvatarCache.load(ctx.applicationContext, version, px) }
        }
    }
    val initial = profile.name.trim().take(1).uppercase().ifEmpty { "M" }
    val base = modifier.size(size).clip(CircleShape).background(Lux.accentBrush)
    Box(
        if (onClick != null) base.pressable(onClick = onClick) else base,
        contentAlignment = Alignment.Center
    ) {
        val img = photo
        if (img != null) {
            Image(
                bitmap = img,
                contentDescription = t("health.photo.desc"),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Txt(initial, sty = Sty.H3, color = Color(0xFF0B0705))
        }
    }
}
