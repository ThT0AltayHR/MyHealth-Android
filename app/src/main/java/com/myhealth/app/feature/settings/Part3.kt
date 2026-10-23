package com.myhealth.app.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.myhealth.app.data.AppSettings
import com.myhealth.app.data.Repo
import com.myhealth.app.ui.kit.Chip
import com.myhealth.app.ui.kit.HGap
import com.myhealth.app.ui.kit.HairLine
import com.myhealth.app.ui.kit.Ic
import com.myhealth.app.ui.kit.LuxSwitch
import com.myhealth.app.ui.kit.Panel
import com.myhealth.app.ui.kit.SectionTitle
import com.myhealth.app.ui.kit.SettingsRow
import com.myhealth.app.ui.kit.TimeWheelDialog
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt

// DOGRULA: ayar degisince bildirim/alarm yeniden planlamasi (Startup.rescheduleAll benzeri) API ozetinde yok; yalniz Repo.updateSettings cagrilir.
/** Bildirimler: hava, hareket, ogunler ve oruc (sahur/iftar). Yapay zeka bildirimleri SettingsAi icinde. */
@Composable
internal fun SettingsNotif() {
    val s = Repo.settings.collectAsState().value
    var editingSlot by remember { mutableStateOf(-1) }
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionTitle(text = t("settings.section.notif"))
        Panel {
            SettingsRow(
                icon = Ic.CLOUD,
                title = t("settings.notif.weather"),
                subtitle = t("settings.notif.weather.sub"),
                onClick = { Repo.updateSettings { it.copy(weatherNotif = !it.weatherNotif) } },
                trailing = {
                    LuxSwitch(
                        checked = s.weatherNotif,
                        onChange = { v -> Repo.updateSettings { it.copy(weatherNotif = v) } }
                    )
                }
            )
            if (s.weatherNotif) {
                Txt(
                    text = t("settings.notif.every"),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    sty = Sty.SMALL,
                    color = Lux.textDim
                )
                NotifChoices(
                    labels = listOf(
                        t("settings.unit.min", 15),
                        t("settings.unit.min", 30),
                        t("settings.unit.hour", 1),
                        t("settings.unit.hour", 2),
                        t("settings.unit.hour", 3)
                    ),
                    values = listOf(15, 30, 60, 120, 180),
                    selected = s.weatherEveryMin,
                    onSelect = { v -> Repo.updateSettings { it.copy(weatherEveryMin = v) } }
                )
            }
            HairLine()
            SettingsRow(
                icon = Ic.ROUTE,
                title = t("settings.notif.activity"),
                subtitle = t("settings.notif.activity.sub"),
                onClick = { Repo.updateSettings { it.copy(activityNotif = !it.activityNotif) } },
                trailing = {
                    LuxSwitch(
                        checked = s.activityNotif,
                        onChange = { v -> Repo.updateSettings { it.copy(activityNotif = v) } }
                    )
                }
            )
            HairLine()
            SettingsRow(
                icon = Ic.MEAL,
                title = t("settings.notif.meal"),
                subtitle = t("settings.notif.meal.sub"),
                onClick = { Repo.updateSettings { it.copy(mealNotif = !it.mealNotif) } },
                trailing = {
                    LuxSwitch(
                        checked = s.mealNotif,
                        onChange = { v -> Repo.updateSettings { it.copy(mealNotif = v) } }
                    )
                }
            )
            if (s.mealNotif) {
                val slots = listOf(
                    t("settings.notif.meal.breakfast") to s.breakfastMin,
                    t("settings.notif.meal.lunch") to s.lunchMin,
                    t("settings.notif.meal.dinner") to s.dinnerMin
                )
                slots.forEachIndexed { i, entry ->
                    HairLine()
                    SettingsRow(
                        icon = Ic.CLOCK,
                        title = entry.first,
                        onClick = { editingSlot = i },
                        trailing = { Txt(notifClock(entry.second), sty = Sty.BODY_STRONG, color = Lux.orange) }
                    )
                }
            }
            HairLine()
            SettingsRow(
                icon = Ic.TIMER,
                title = t("settings.notif.fast.auto"),
                subtitle = t("settings.notif.fast.auto.sub"),
                onClick = { Repo.updateSettings { it.copy(fastingAuto = !it.fastingAuto) } },
                trailing = {
                    LuxSwitch(
                        checked = s.fastingAuto,
                        onChange = { v -> Repo.updateSettings { it.copy(fastingAuto = v) } }
                    )
                }
            )
            HairLine()
            SettingsRow(
                icon = Ic.MOON,
                title = t("settings.notif.fast.force"),
                subtitle = t("settings.notif.fast.force.sub"),
                onClick = { Repo.updateSettings { it.copy(fastingForce = !it.fastingForce) } },
                trailing = {
                    LuxSwitch(
                        checked = s.fastingForce,
                        onChange = { v -> Repo.updateSettings { it.copy(fastingForce = v) } }
                    )
                }
            )
            HairLine()
            SettingsRow(
                icon = Ic.ALARM,
                title = t("settings.notif.sahur"),
                subtitle = t("settings.notif.sahur.sub"),
                onClick = { Repo.updateSettings { it.copy(sahurAlarm = !it.sahurAlarm) } },
                trailing = {
                    LuxSwitch(
                        checked = s.sahurAlarm,
                        onChange = { v -> Repo.updateSettings { it.copy(sahurAlarm = v) } }
                    )
                }
            )
            if (s.sahurAlarm) {
                Txt(
                    text = t("settings.notif.sahur.offset"),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    sty = Sty.SMALL,
                    color = Lux.textDim
                )
                NotifChoices(
                    labels = listOf(
                        t("settings.unit.min", 15),
                        t("settings.unit.min", 30),
                        t("settings.unit.min", 45),
                        t("settings.unit.min", 60),
                        t("settings.unit.min", 90)
                    ),
                    values = listOf(15, 30, 45, 60, 90),
                    selected = s.sahurOffsetMin,
                    onSelect = { v -> Repo.updateSettings { it.copy(sahurOffsetMin = v) } }
                )
            }
            HairLine()
            SettingsRow(
                icon = Ic.SUN,
                title = t("settings.notif.iftar"),
                subtitle = t("settings.notif.iftar.sub"),
                onClick = { Repo.updateSettings { it.copy(iftarAlarm = !it.iftarAlarm) } },
                trailing = {
                    LuxSwitch(
                        checked = s.iftarAlarm,
                        onChange = { v -> Repo.updateSettings { it.copy(iftarAlarm = v) } }
                    )
                }
            )
        }
    }
    if (editingSlot >= 0) {
        val slot = editingSlot
        TimeWheelDialog(
            initialMinutes = slotMinutes(s, slot),
            onDismiss = { editingSlot = -1 },
            onConfirm = { m ->
                val v = m.coerceIn(0, 1439)
                Repo.updateSettings { st -> setSlotMinutes(st, slot, v) }
                editingSlot = -1
            }
        )
    }
}

@Composable
private fun NotifChoices(labels: List<String>, values: List<Int>, selected: Int, onSelect: (Int) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        values.forEachIndexed { i, v ->
            if (i > 0) HGap(6.dp)
            Chip(
                text = labels.getOrElse(i) { "" },
                selected = v == selected,
                onClick = { onSelect(v) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private fun notifClock(minutes: Int): String {
    val m = minutes.coerceIn(0, 1439)
    return "%02d:%02d".format(m / 60, m % 60)
}

private fun slotMinutes(s: AppSettings, slot: Int): Int = when (slot) {
    0 -> s.breakfastMin
    1 -> s.lunchMin
    else -> s.dinnerMin
}

private fun setSlotMinutes(s: AppSettings, slot: Int, v: Int): AppSettings = when (slot) {
    0 -> s.copy(breakfastMin = v)
    1 -> s.copy(lunchMin = v)
    else -> s.copy(dinnerMin = v)
}
