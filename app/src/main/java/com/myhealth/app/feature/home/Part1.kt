package com.myhealth.app.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.myhealth.app.R
import com.myhealth.app.data.Repo
import com.myhealth.app.feature.assistant.AiInsights
import com.myhealth.app.feature.assistant.AssistantContext
import com.myhealth.app.feature.profile.Avatar
import com.myhealth.app.nav.LocalNav
import com.myhealth.app.nav.Route
import com.myhealth.app.nav.Tab
import com.myhealth.app.ui.kit.AiDisclaimer
import com.myhealth.app.ui.kit.GlassCard
import com.myhealth.app.ui.kit.HGap
import com.myhealth.app.ui.kit.Ic
import com.myhealth.app.ui.kit.LuxIcon
import com.myhealth.app.ui.kit.MedIcons
import com.myhealth.app.ui.kit.RoundPlay
import com.myhealth.app.ui.kit.VGap
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt
import java.time.LocalTime

// Ust serit: zamana gore selamlama (ad yoksa misafir) ve profil avatari (dokununca Profil).
@Composable
internal fun HomeHeader(modifier: Modifier = Modifier) {
    val nav = LocalNav.current
    val profile by Repo.profile.collectAsState()
    val name = profile.name.trim()
    val shown = if (name.isEmpty()) t("home.guest") else name
    val greetKey = when (LocalTime.now().hour) {
        in 5..11 -> "home.greet.morning"
        in 12..17 -> "home.greet.day"
        in 18..21 -> "home.greet.evening"
        else -> "home.greet.night"
    }
    Box(modifier = modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(R.drawable.bg_arcs), // DOGRULA: bg_arcs drawable'i res altinda olmali.
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.3f
        )
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Txt(
                text = t(greetKey, shown),
                modifier = Modifier.weight(1f),
                sty = Sty.H2,
                color = Lux.text,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            HGap(12.dp)
            Avatar(size = 48.dp, onClick = { nav.push(Route.Profile) })
        }
    }
}

// Gunun onerisi karti: yapay zeka metni (yoksa bekleme metni) ve tibbi uyari; dokununca asistan sekmesi.
@Composable
internal fun AiTipCard(modifier: Modifier = Modifier) {
    val nav = LocalNav.current
    val profile by Repo.profile.collectAsState()
    val workouts by Repo.workouts.collectAsState()
    val tip = remember(profile, workouts) { homeTip().trim() }
    val shown = if (tip.isEmpty()) t("home.ai.wait") else tip
    Box(modifier = modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(R.drawable.hero_arcs), // DOGRULA: hero_arcs drawable'i res altinda olmali.
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.45f
        )
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            warm = true,
            onClick = { nav.go(Tab.ASSISTANT) }
        ) {
            Txt(t("home.ai.title"), sty = Sty.LABEL, color = Lux.gold)
            VGap(6.dp)
            Txt(shown, sty = Sty.BODY, color = Lux.text, maxLines = 6, overflow = TextOverflow.Ellipsis)
            VGap(12.dp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                LuxIcon(path = MedIcons.SPARKLES, tint = Lux.gold, size = 18.dp)
                HGap(8.dp)
                Txt(t("home.ai.open"), sty = Sty.BUTTON, color = Lux.gold)
            }
            VGap(10.dp)
            AiDisclaimer(compact = true)
        }
    }
}

// DOGRULA: AssistantContext kurucusu bilinmiyor (feature/assistant); parametre gerekiyorsa burada uyarlayin.
private fun homeTip(): String = AiInsights.dailyTip(AssistantContext())

// Ana sayfa kartlarinin ortak cam cercevesi: ust satirda ikon ve baslik, altinda icerik.
@Composable
internal fun HomeCard(
    icon: String,
    title: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    GlassCard(
        modifier = modifier,
        onClick = onClick,
        contentPadding = PaddingValues(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            LuxIcon(path = icon, tint = Lux.gold, size = 20.dp)
            HGap(8.dp)
            Txt(
                text = title,
                modifier = Modifier.weight(1f),
                sty = Sty.LABEL,
                color = Lux.textDim,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        VGap(10.dp)
        content()
    }
}

// Spor karti: spor sekmesine gider (devam et veya basla).
@Composable
internal fun SportCard(modifier: Modifier = Modifier) {
    val nav = LocalNav.current
    HomeCard(
        icon = Ic.SPORTS,
        title = t("home.sport.title"),
        modifier = modifier,
        onClick = { nav.go(Tab.SPORTS) }
    ) {
        Txt(t("home.sport.sub"), sty = Sty.SMALL, color = Lux.textDim, maxLines = 2, overflow = TextOverflow.Ellipsis)
        VGap(10.dp)
        RoundPlay(playing = false, onClick = { nav.go(Tab.SPORTS) })
    }
}

// Nefes karti: nefes egzersizi ekranini acar.
@Composable
internal fun BreathCard(modifier: Modifier = Modifier) {
    val nav = LocalNav.current
    HomeCard(
        icon = MedIcons.LUNGS,
        title = t("home.breath.title"),
        modifier = modifier,
        onClick = { nav.push(Route.Breathing) }
    ) {
        Txt(t("home.breath.sub"), sty = Sty.SMALL, color = Lux.textDim, maxLines = 3, overflow = TextOverflow.Ellipsis)
    }
}
