package com.myhealth.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.myhealth.app.data.Repo
import com.myhealth.app.feature.assistant.AssistantScreen
import com.myhealth.app.feature.alarm.AlarmEditScreen
import com.myhealth.app.feature.alarm.AlarmsScreen
import com.myhealth.app.feature.fasting.FastingScreen
import com.myhealth.app.feature.health.HealthLogScreen
import com.myhealth.app.feature.meds.MedicationsScreen
import com.myhealth.app.feature.home.HomeScreen
import com.myhealth.app.feature.meals.MealsScreen
import com.myhealth.app.feature.mind.BreathingScreen
import com.myhealth.app.feature.more.MoreScreen
import com.myhealth.app.feature.music.MiniPlayerHost
import com.myhealth.app.feature.music.MusicScreen
import com.myhealth.app.feature.music.NowPlayingScreen
import com.myhealth.app.feature.notes.NoteEditScreen
import com.myhealth.app.feature.notes.NotesScreen
import com.myhealth.app.feature.onboarding.OnboardingFlow
import com.myhealth.app.feature.permissions.PermissionsScreen
import com.myhealth.app.feature.profile.ProfileScreen
import com.myhealth.app.feature.settings.SettingsScreen
import com.myhealth.app.feature.sports.SportBubbleHost
import com.myhealth.app.feature.sports.SportHistoryScreen
import com.myhealth.app.feature.sports.SportLiveScreen
import com.myhealth.app.feature.sports.SportsScreen
import com.myhealth.app.feature.stats.StatsScreen
import com.myhealth.app.feature.weather.DynamicIslandHost
import com.myhealth.app.feature.weather.WeatherScreen
import com.myhealth.app.nav.DeepLink
import com.myhealth.app.nav.LocalNav
import com.myhealth.app.nav.Nav
import com.myhealth.app.nav.Route
import com.myhealth.app.nav.Tab
import com.myhealth.app.ui.kit.Ic
import com.myhealth.app.ui.kit.LocalBottomClearance
import com.myhealth.app.ui.kit.LuxIcon
import com.myhealth.app.ui.kit.ToastHost
import com.myhealth.app.ui.kit.pressable
import com.myhealth.app.ui.theme.Dim
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.LuxTheme

/** Uygulamanin kok bileseni: tema + dil, ilk giris akisi veya ana kabuk. */
@Composable
fun AppRoot() {
    val lang by Repo.lang.collectAsState()
    val profile by Repo.profile.collectAsState()
    val nav = remember { Nav() }
    LuxTheme(lang) {
        CompositionLocalProvider(LocalNav provides nav) {
            Box(Modifier.fillMaxSize().background(Lux.black)) {
                if (profile.onboarded) MainShell(nav) else OnboardingFlow()
                ToastHost()
            }
        }
    }
}

@Composable
private fun MainShell(nav: Nav) {
    val miniVisible by UiFlags.miniPlayerVisible.collectAsState()
    val pending by DeepLink.pending.collectAsState()
    val top = nav.top
    val showNav = top == null
    val allowMini = top == null || (top !is Route.Breathing && top !is Route.SportLive && top !is Route.NowPlaying)
    val clearance = if (showNav) Dim.navClearance + (if (miniVisible && allowMini) Dim.miniPlayer else 0.dp) else 0.dp

    BackHandler(enabled = nav.canGoBack()) { nav.back() }

    LaunchedEffect(pending) {
        val key = pending
        if (key != null) {
            DeepLink.open(nav, key)
            DeepLink.pending.value = null
        }
    }

    CompositionLocalProvider(LocalBottomClearance provides clearance) {
        Box(Modifier.fillMaxSize()) {
            Crossfade(targetState = top, animationSpec = tween(220), label = "page") { r ->
                if (r == null) TabContent(nav.tab) else PageContent(r)
            }
            Column(Modifier.align(Alignment.BottomCenter).navigationBarsPadding()) {
                if (allowMini) MiniPlayerHost(Modifier.padding(horizontal = 12.dp))
                if (showNav) BottomNav(nav.tab) { nav.go(it) }
            }
            DynamicIslandHost(Modifier.align(Alignment.TopCenter))
            SportBubbleHost()
        }
    }
}

@Composable
private fun TabContent(tab: Tab) {
    when (tab) {
        Tab.HOME -> HomeScreen()
        Tab.SPORTS -> SportsScreen()
        Tab.ASSISTANT -> AssistantScreen()
        Tab.MUSIC -> MusicScreen()
        Tab.MORE -> MoreScreen()
    }
}

@Composable
private fun PageContent(route: Route) {
    when (route) {
        Route.Weather -> WeatherScreen()
        Route.Breathing -> BreathingScreen()
        Route.Notes -> NotesScreen()
        is Route.NoteEdit -> NoteEditScreen(route.id)
        Route.Alarms -> AlarmsScreen()
        is Route.AlarmEdit -> AlarmEditScreen(route.id)
        Route.Meals -> MealsScreen()
        Route.Fasting -> FastingScreen()
        Route.Stats -> StatsScreen()
        Route.Settings -> SettingsScreen()
        Route.Permissions -> PermissionsScreen()
        Route.Profile -> ProfileScreen()
        is Route.SportLive -> SportLiveScreen(route.sportId)
        Route.SportHistory -> SportHistoryScreen()
        Route.NowPlaying -> NowPlayingScreen()
        Route.HealthLog -> HealthLogScreen()
        Route.Medications -> MedicationsScreen()
    }
}

/** Yuzen alt gezinme: yuvarlak dugmeler, secili olan beyaz (referans tasarim). */
@Composable
private fun BottomNav(selected: Tab, onSelect: (Tab) -> Unit) {
    val shape = RoundedCornerShape(50)
    Box(Modifier.fillMaxWidth().padding(bottom = 12.dp), contentAlignment = Alignment.Center) {
        Row(
            Modifier
                .clip(shape)
                .background(Color(0xE60B0705))
                .border(1.dp, Lux.strokeSoft, shape)
                .padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (tab in Tab.entries) {
                NavItem(tab, tab == selected) { onSelect(tab) }
            }
        }
    }
}

@Composable
private fun NavItem(tab: Tab, selected: Boolean, onClick: () -> Unit) {
    val icon = when (tab) {
        Tab.HOME -> Ic.HOME
        Tab.SPORTS -> Ic.SPORTS
        Tab.ASSISTANT -> Ic.ASSISTANT
        Tab.MUSIC -> Ic.MUSIC
        Tab.MORE -> Ic.MORE
    }
    val key = when (tab) {
        Tab.HOME -> "nav.home"
        Tab.SPORTS -> "nav.sports"
        Tab.ASSISTANT -> "nav.assistant"
        Tab.MUSIC -> "nav.music"
        Tab.MORE -> "nav.more"
    }
    val label = t(key)
    val bg by animateColorAsState(if (selected) Lux.text else Color.Transparent, label = "navBg")
    val fg by animateColorAsState(if (selected) Color(0xFF0B0705) else Lux.textDim, label = "navFg")
    Box(
        Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(bg)
            .semantics { contentDescription = label }
            .pressable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        LuxIcon(icon, tint = fg, size = 24.dp)
    }
}
