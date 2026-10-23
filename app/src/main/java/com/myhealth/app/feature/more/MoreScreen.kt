package com.myhealth.app.feature.more

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.myhealth.app.nav.LocalNav
import com.myhealth.app.nav.Route
import com.myhealth.app.ui.kit.GhostButton
import com.myhealth.app.ui.kit.HairLine
import com.myhealth.app.ui.kit.Ic
import com.myhealth.app.ui.kit.LocalBottomClearance
import com.myhealth.app.ui.kit.LuxDialog
import com.myhealth.app.ui.kit.MedIcons
import com.myhealth.app.ui.kit.Panel
import com.myhealth.app.ui.kit.ScreenScaffold
import com.myhealth.app.ui.kit.SettingsRow
import com.myhealth.app.ui.kit.VGap
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt

/** Daha sekmesi: kisayol satirlari. route null ise hakkinda penceresi acilir. */
@Composable
fun MoreScreen() {
    val nav = LocalNav.current
    var aboutOpen by remember { mutableStateOf(false) }
    // DOGRULA: LocalBottomClearance tipi Dp olmali (LocalBottomClearance.current + 24.dp).
    val entries = listOf(
        MoreEntry(Ic.CLOUD, t("home.weather.title"), Route.Weather),
        MoreEntry(MedIcons.LUNGS, t("mind.breath.title"), Route.Breathing),
        MoreEntry(Ic.EDIT, t("more.notes"), Route.Notes),
        MoreEntry(Ic.ALARM, t("more.alarms"), Route.Alarms),
        MoreEntry(Ic.MEAL, t("more.meals"), Route.Meals),
        MoreEntry(Ic.MOON, t("more.fasting"), Route.Fasting),
        MoreEntry(Ic.CHART, t("more.stats"), Route.Stats),
        MoreEntry(MedIcons.PILL, t("more.meds"), Route.Medications),
        MoreEntry(MedIcons.DOCUMENT, t("more.healthlog"), Route.HealthLog),
        MoreEntry(Ic.SETTINGS, t("more.settings"), Route.Settings),
        MoreEntry(Ic.SHIELD, t("more.permissions"), Route.Permissions),
        MoreEntry(Ic.USER, t("more.profile"), Route.Profile),
        MoreEntry(Ic.INFO, t("more.about"), null)
    )

    ScreenScaffold(clearBottomNav = true, title = t("more.title")) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Panel {
                entries.forEachIndexed { i, e ->
                    SettingsRow(
                        icon = e.icon,
                        title = e.title,
                        onClick = {
                            val r = e.route
                            if (r != null) {
                                nav.push(r)
                            } else {
                                aboutOpen = true
                            }
                        }
                    )
                    if (i < entries.lastIndex) HairLine()
                }
            }
            VGap(LocalBottomClearance.current + 24.dp)
        }
    }

    if (aboutOpen) {
        LuxDialog(onDismiss = { aboutOpen = false }) {
            Txt(text = t("more.about"), sty = Sty.H3, color = Lux.text)
            VGap(10.dp)
            Txt(text = t("more.about.body"), sty = Sty.BODY, color = Lux.textDim)
            VGap(18.dp)
            GhostButton(text = t("more.close"), onClick = { aboutOpen = false }, modifier = Modifier.fillMaxWidth())
        }
    }
}

private class MoreEntry(val icon: String, val title: String, val route: Route?)
