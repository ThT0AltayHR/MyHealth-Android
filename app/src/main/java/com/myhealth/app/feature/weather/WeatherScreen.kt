package com.myhealth.app.feature.weather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.myhealth.app.data.Repo
import com.myhealth.app.nav.LocalNav
import com.myhealth.app.ui.kit.EmptyState
import com.myhealth.app.ui.kit.GhostButton
import com.myhealth.app.ui.kit.GlassCard
import com.myhealth.app.ui.kit.Ic
import com.myhealth.app.ui.kit.LocalBottomClearance
import com.myhealth.app.ui.kit.OnResume
import com.myhealth.app.ui.kit.ScreenScaffold
import com.myhealth.app.ui.kit.Toasts
import com.myhealth.app.ui.kit.VGap
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt
import com.myhealth.app.ui.tr
import com.myhealth.app.util.CrashLog
import com.myhealth.app.util.Net
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@Composable
fun WeatherScreen() {
    val nav = LocalNav.current
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val now by Repo.weather.collectAsState()
    val days by Repo.forecast.collectAsState()
    var busy by remember { mutableStateOf(false) }
    var online by remember { mutableStateOf(Net.isOnline(ctx)) }

    fun refresh() {
        if (busy) return
        busy = true
        scope.launch {
            try {
                val ok = WeatherRepo.refresh(ctx)
                online = Net.isOnline(ctx)
                if (!ok && online) Toasts.show(tr("weather.refresh_failed"))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                CrashLog.note(ctx, "weather_screen_refresh", e)
            } finally {
                busy = false
            }
        }
    }

    OnResume {
        online = Net.isOnline(ctx)
        val last = Repo.kvLong("weather_last_ok_ms", 0L)
        if (online && System.currentTimeMillis() - last > 20L * 60L * 1000L) refresh()
    }

    val w = now
    val hasLoc = wxHasLocation(ctx)
    ScreenScaffold(title = t("weather.title"), onBack = { nav.pop() }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = LocalBottomClearance.current + 24.dp)
        ) {
            if (!online) {
                Txt(text = t("weather.offline"), sty = Sty.SMALL, color = Lux.amber)
                VGap(10.dp)
            }
            if (w == null) {
                EmptyState(
                    icon = Ic.CLOUD,
                    title = t("weather.no_data"),
                    subtitle = if (hasLoc) t("weather.no_data_hint") else t("weather.need_location")
                )
            } else {
                Box(modifier = Modifier.fillMaxWidth().height(300.dp).clip(RoundedCornerShape(28.dp))) {
                    WxVideo(cond = w.condition, modifier = Modifier.fillMaxSize())
                    Column(
                        modifier = Modifier.fillMaxSize().padding(20.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Txt(text = t(WxIcons.nameKey(w.condition)), sty = Sty.H3, color = Lux.text)
                        Txt(text = "${w.tempC}°", sty = Sty.DISPLAY, color = Lux.text)
                    }
                }
                VGap(14.dp)
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        WxStat(
                            icon = WxIcons.feelsLike,
                            label = t("weather.feels"),
                            value = "${w.feelsC}°",
                            modifier = Modifier.weight(1f)
                        )
                        WxStat(
                            icon = WxIcons.humidity,
                            label = t("weather.humidity"),
                            value = "${w.humidity}%",
                            modifier = Modifier.weight(1f)
                        )
                        WxStat(
                            icon = WxIcons.wind,
                            label = t("weather.wind"),
                            value = t("weather.wind_value", w.windKmh),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                VGap(12.dp)
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Txt(
                        text = t("weather.updated", wxClock(w.updatedMs)),
                        sty = Sty.CAPTION,
                        color = Lux.textDim,
                        modifier = Modifier.weight(1f)
                    )
                    GhostButton(
                        text = t("weather.refresh"),
                        onClick = { refresh() },
                        enabled = !busy,
                        icon = Ic.REFRESH
                    )
                }
                VGap(18.dp)
                val rain = Repo.kvInt("weather_rain3h", 0)
                if (rain >= WX_RAIN_ALERT_PCT) {
                    WxRainPanel(pct = rain)
                    VGap(18.dp)
                }
                if (days.isNotEmpty()) {
                    WxWeekTable(days = days)
                    VGap(18.dp)
                }
                Txt(text = w.city, sty = Sty.H1, color = Lux.text)
            }
        }
    }
}
