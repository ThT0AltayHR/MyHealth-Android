package com.myhealth.app.feature.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.myhealth.app.core.Fmt
import com.myhealth.app.data.AlarmItem
import com.myhealth.app.data.Repo
import com.myhealth.app.data.WeatherCondition
import com.myhealth.app.nav.LocalNav
import com.myhealth.app.nav.Route
import com.myhealth.app.ui.kit.Chip
import com.myhealth.app.ui.kit.HGap
import com.myhealth.app.ui.kit.Ic
import com.myhealth.app.ui.kit.LuxIcon
import com.myhealth.app.ui.kit.ProgressRing
import com.myhealth.app.ui.kit.VGap
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt
import java.time.LocalDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Adim halkasi: bugunku adim sayisi ve hedef (ayarlardaki stepGoal).
@Composable
internal fun StepsCard(modifier: Modifier = Modifier) {
    val steps by Repo.steps.collectAsState()
    val settings by Repo.settings.collectAsState()
    val goal = settings.stepGoal.coerceAtLeast(1)
    val count = remember(steps) { Repo.stepsToday() }
    val progress = (count.toFloat() / goal.toFloat()).coerceIn(0f, 1f)
    HomeCard(icon = Ic.STEPS, title = t("home.steps.title"), modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ProgressRing(progress = progress, diameter = 76.dp, strokeWidth = 7.dp) {
                Txt(
                    text = count.toString(),
                    modifier = Modifier.align(Alignment.Center),
                    sty = Sty.BODY_STRONG,
                    color = Lux.text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            HGap(10.dp)
            Column(modifier = Modifier.weight(1f)) {
                Txt(t("home.steps.unit"), sty = Sty.CAPTION, color = Lux.textDim, maxLines = 1)
                VGap(2.dp)
                Txt(
                    text = t("home.steps.goal", goal),
                    sty = Sty.CAPTION,
                    color = Lux.textDim,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// Su: bugunku bardak sayisi ve +1 bardak dugmesi (yazma IO arka planda).
@Composable
internal fun WaterCard(modifier: Modifier = Modifier) {
    val water by Repo.water.collectAsState()
    val scope = rememberCoroutineScope()
    val glasses = remember(water) { Repo.waterToday() }
    HomeCard(icon = Ic.DROP, title = t("home.water.title"), modifier = modifier) {
        Txt(
            text = t("home.water.value", glasses),
            sty = Sty.H3,
            color = Lux.text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        VGap(10.dp)
        Chip(
            text = t("home.water.add"),
            selected = false,
            onClick = { scope.launch(Dispatchers.IO) { Repo.addWater(1) } }
        )
    }
}

// Son hava durumu: sicaklik ve sehir; veri yoksa konum izni aciklamasi. Dokununca hava durumu sayfasi.
@Composable
internal fun WeatherCard(modifier: Modifier = Modifier) {
    val nav = LocalNav.current
    val weather by Repo.weather.collectAsState()
    val wx = weather
    HomeCard(
        icon = Ic.CLOUD,
        title = t("home.weather.title"),
        modifier = modifier,
        onClick = { nav.push(Route.Weather) }
    ) {
        if (wx == null) {
            Txt(t("home.weather.none"), sty = Sty.SMALL, color = Lux.textDim, maxLines = 4, overflow = TextOverflow.Ellipsis)
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                LuxIcon(path = conditionIcon(wx.condition, wx.isDay), tint = Lux.text, size = 28.dp)
                HGap(8.dp)
                Txt(
                    text = t("home.weather.value", wx.tempC, wx.city),
                    modifier = Modifier.weight(1f),
                    sty = Sty.H3,
                    color = Lux.text,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// Siradaki alarm: en yakin etkin alarmin saati ve kalan sure. Dokununca alarmlar sayfasi.
@Composable
internal fun AlarmCard(now: LocalDateTime, modifier: Modifier = Modifier) {
    val nav = LocalNav.current
    val alarms by Repo.alarms.collectAsState()
    val next = nextAlarm(alarms, now)
    HomeCard(
        icon = Ic.ALARM,
        title = t("home.alarm.title"),
        modifier = modifier,
        onClick = { nav.push(Route.Alarms) }
    ) {
        if (next == null) {
            Txt(t("home.alarm.none"), sty = Sty.SMALL, color = Lux.textDim, maxLines = 3, overflow = TextOverflow.Ellipsis)
        } else {
            Txt(Fmt.hhmm(next.alarm.minuteOfDay), sty = Sty.H3, color = Lux.text, maxLines = 1)
            VGap(2.dp)
            Txt(
                text = Fmt.hoursMinutes(next.minutes, t("home.unit.h"), t("home.unit.m")),
                sty = Sty.CAPTION,
                color = Lux.textDim,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// Etkin alarmlar arasinda bir sonraki tetiklenme. Tek seferlik alarm icin bugun veya yarina bakar.
private class NextAlarm(val alarm: AlarmItem, val minutes: Int)

private fun nextAlarm(alarms: List<AlarmItem>, now: LocalDateTime): NextAlarm? {
    val nowMin = now.hour * 60 + now.minute
    val today = now.dayOfWeek.value
    var best: NextAlarm? = null
    for (a in alarms) {
        if (!a.enabled) continue
        for (k in 0..7) {
            val dow = ((today - 1 + k) % 7) + 1
            val onDay = if (a.days.isEmpty()) k <= 1 else dow in a.days
            if (!onDay) continue
            val diff = k * 1440 + a.minuteOfDay - nowMin
            if (diff <= 0) continue
            val b = best
            if (b == null || diff < b.minutes) best = NextAlarm(a, diff)
        }
    }
    return best
}

private fun conditionIcon(c: WeatherCondition, isDay: Boolean): String = when (c) {
    WeatherCondition.SUNNY -> if (isDay) Ic.SUN else Ic.MOON
    WeatherCondition.CLOUDY -> Ic.CLOUD
    WeatherCondition.LIGHT_RAIN -> Ic.RAIN
    WeatherCondition.HEAVY_RAIN -> Ic.RAIN
    WeatherCondition.STORM -> Ic.STORM
    WeatherCondition.SNOW -> Ic.SNOW
}
