package com.myhealth.app.feature.settings

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.myhealth.app.data.AiMode
import com.myhealth.app.data.Repo
import com.myhealth.app.feature.health.DailyTip
import com.myhealth.app.feature.meds.MedStore
import com.myhealth.app.feature.report.ReportExportCard
import com.myhealth.app.nav.LocalNav
import com.myhealth.app.nav.Route
import com.myhealth.app.ui.kit.*
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt
import com.myhealth.app.ui.tr

/** Saglik: saglik gunlugu, ilaclar ve doktor raporu. */
@Composable
internal fun SettingsHealth() {
    val nav = LocalNav.current
    val medCount = MedStore.regular.collectAsState().value.size +
        MedStore.occasional.collectAsState().value.size
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionTitle(text = t("settings.section.health"))
        Panel {
            SettingsRow(
                icon = MedIcons.DOCUMENT,
                title = t("settings.health.log"),
                subtitle = t("settings.health.log.sub"),
                onClick = { nav.push(Route.HealthLog) }
            )
            HairLine()
            SettingsRow(
                icon = MedIcons.PILL,
                title = t("settings.meds"),
                subtitle = t("settings.meds.count", medCount),
                onClick = { nav.push(Route.Medications) }
            )
        }
        SectionTitle(text = t("settings.section.report"))
        ReportExportCard()
    }
}

/** Yapay zeka: calisma modu, Groq anahtari ve yapay zeka bildirimleri. */
@Composable
internal fun SettingsAi() {
    val ctx = LocalContext.current
    val s = Repo.settings.collectAsState().value
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionTitle(text = t("settings.section.ai"))
        GlassCard {
            Txt(t("settings.ai.mode"), sty = Sty.BODY_STRONG)
            VGap(8.dp)
            SegmentedTabs(
                options = listOf(t("settings.ai.mode.local"), t("settings.ai.mode.groq")),
                selected = if (s.aiMode == AiMode.GROQ) 1 else 0,
                onSelect = { i ->
                    Repo.updateSettings { it.copy(aiMode = if (i == 1) AiMode.GROQ else AiMode.LOCAL) }
                }
            )
            VGap(14.dp)
            Txt(t("settings.ai.groq.key"), sty = Sty.BODY_STRONG)
            VGap(8.dp)
            GroqKeyField(saved = s.groqKey)
            VGap(12.dp)
            AiDisclaimer(compact = true)
        }
        Panel {
            SettingsRow(
                icon = MedIcons.SPARKLES,
                title = t("settings.ai.followup"),
                subtitle = t("settings.ai.followup.sub"),
                onClick = { Repo.updateSettings { it.copy(aiFollowUp = !it.aiFollowUp) } },
                trailing = {
                    LuxSwitch(
                        checked = s.aiFollowUp,
                        onChange = { v -> Repo.updateSettings { it.copy(aiFollowUp = v) } }
                    )
                }
            )
            HairLine()
            SettingsRow(
                icon = Ic.BELL,
                title = t("settings.ai.dailytip"),
                subtitle = t("settings.ai.dailytip.sub"),
                onClick = { setDailyTip(ctx, !s.aiDailyTip) },
                trailing = {
                    LuxSwitch(
                        checked = s.aiDailyTip,
                        onChange = { v -> setDailyTip(ctx, v) }
                    )
                }
            )
        }
    }
}

@Composable
private fun GroqKeyField(saved: String) {
    var draft by remember(saved) { mutableStateOf(saved) }
    var show by remember { mutableStateOf(false) }
    LuxTextField(
        value = draft,
        onValueChange = { draft = it },
        placeholder = t("settings.ai.groq.hint"),
        modifier = Modifier.fillMaxWidth(),
        password = !show,
        trailing = {
            IconBtn(
                icon = if (show) Ic.EYE_OFF else Ic.EYE,
                onClick = { show = !show },
                description = if (show) t("settings.ai.groq.hide") else t("settings.ai.groq.show")
            )
        }
    )
    VGap(8.dp)
    PrimaryButton(
        text = t("settings.ai.groq.save"),
        onClick = {
            Repo.updateSettings { it.copy(groqKey = draft.trim()) }
            Toasts.show(tr("settings.saved"))
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = draft.trim() != saved
    )
}

/** Gunluk ipucu ayari degisince zamanlayiciyi gunceller. */
internal fun setDailyTip(ctx: Context, on: Boolean) {
    Repo.updateSettings { it.copy(aiDailyTip = on) }
    DailyTip.ensure(ctx)
}
