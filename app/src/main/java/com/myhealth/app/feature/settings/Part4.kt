package com.myhealth.app.feature.settings

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.myhealth.app.core.Lang
import com.myhealth.app.data.Repo
import com.myhealth.app.feature.report.BackupCard
import com.myhealth.app.feature.report.ShareHelper
import com.myhealth.app.nav.LocalNav
import com.myhealth.app.nav.Route
import com.myhealth.app.ui.kit.Chip
import com.myhealth.app.ui.kit.GhostButton
import com.myhealth.app.ui.kit.GlassCard
import com.myhealth.app.ui.kit.HGap
import com.myhealth.app.ui.kit.HairLine
import com.myhealth.app.ui.kit.IconBtn
import com.myhealth.app.ui.kit.Ic
import com.myhealth.app.ui.kit.Panel
import com.myhealth.app.ui.kit.SectionTitle
import com.myhealth.app.ui.kit.SegmentedTabs
import com.myhealth.app.ui.kit.SettingsRow
import com.myhealth.app.ui.kit.VGap
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt
import com.myhealth.app.util.CrashLog

// Depo adresi verilmedi: doldurulunca "depoya yildiz ver" dugmesi gorunur (yalniz http/https).
private val SOURCE_REPO_URL: String? = null

// DOGRULA: musicSource degerleri ("audius" / "youtube") muzik modulu ile eslesmeli; API ozetinde yok.
/** Hedefler: adim hedefi ve muzik kaynagi; yedek ve izinler. */
@Composable
internal fun SettingsGoals() {
    val nav = LocalNav.current
    val s = Repo.settings.collectAsState().value
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionTitle(text = t("settings.section.goals"))
        GlassCard {
            Txt(t("settings.goal.steps"), sty = Sty.BODY_STRONG)
            VGap(4.dp)
            Txt(t("settings.goal.steps.sub"), sty = Sty.SMALL, color = Lux.textDim)
            VGap(10.dp)
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconBtn(
                    icon = Ic.MINUS,
                    onClick = { setStepGoal(s.stepGoal - 500) },
                    description = t("settings.goal.less")
                )
                Txt(
                    text = t("settings.goal.value", s.stepGoal),
                    modifier = Modifier.weight(1f),
                    sty = Sty.H3,
                    align = TextAlign.Center
                )
                IconBtn(
                    icon = Ic.PLUS,
                    onClick = { setStepGoal(s.stepGoal + 500) },
                    description = t("settings.goal.more")
                )
            }
            VGap(14.dp)
            Txt(t("settings.music.source"), sty = Sty.BODY_STRONG)
            VGap(8.dp)
            SegmentedTabs(
                options = listOf(t("settings.music.audius"), t("settings.music.youtube")),
                selected = if (s.musicSource == "youtube") 1 else 0,
                onSelect = { i ->
                    Repo.updateSettings { it.copy(musicSource = if (i == 1) "youtube" else "audius") }
                }
            )
            VGap(10.dp)
            Txt(t("settings.music.tos"), sty = Sty.SMALL, color = Lux.amber)
        }
        SectionTitle(text = t("settings.section.backup"))
        BackupCard()
        SectionTitle(text = t("settings.section.perms"))
        Panel {
            SettingsRow(
                icon = Ic.SHIELD,
                title = t("settings.perms"),
                subtitle = t("settings.perms.sub"),
                onClick = { nav.push(Route.Permissions) }
            )
        }
    }
}

private fun setStepGoal(v: Int) {
    Repo.updateSettings { it.copy(stepGoal = v.coerceIn(2000, 30000)) }
}

/** Hakkinda: dil secimi (aninda uygulanir), surum, gizlilik ozeti ve lisanslar / atiflar. */
@Composable
internal fun SettingsAbout() {
    val ctx = LocalContext.current
    val lang = Repo.lang.collectAsState().value
    val version = remember(ctx) { appVersionName(ctx) }
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionTitle(text = t("settings.section.lang"))
        GlassCard {
            Lang.values().toList().chunked(3).forEach { chunk ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    chunk.forEachIndexed { i, l ->
                        if (i > 0) HGap(8.dp)
                        Chip(
                            text = l.nativeName,
                            selected = l == lang,
                            onClick = { Repo.setLang(l) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                VGap(8.dp)
            }
        }
        SectionTitle(text = t("settings.section.about"))
        Panel {
            SettingsRow(
                icon = Ic.INFO,
                title = t("settings.about.version"),
                subtitle = version.takeIf { it.isNotEmpty() }
            )
            HairLine()
            SettingsRow(
                icon = Ic.LOCK,
                title = t("settings.about.privacy"),
                subtitle = t("settings.about.privacy.sub")
            )
        }
        GlassCard {
            Txt(t("settings.about.privacy.body"), sty = Sty.SMALL, color = Lux.textDim)
            VGap(12.dp)
            Txt(t("settings.about.licenses"), sty = Sty.BODY_STRONG)
            VGap(6.dp)
            Txt(t("settings.about.lic.fonts"), sty = Sty.SMALL, color = Lux.textDim)
            VGap(4.dp)
            Txt(t("settings.about.lic.osm"), sty = Sty.SMALL, color = Lux.textDim)
            VGap(4.dp)
            Txt(t("settings.about.lic.meteo"), sty = Sty.SMALL, color = Lux.textDim)
            VGap(4.dp)
            Txt(t("settings.about.lic.audius"), sty = Sty.SMALL, color = Lux.textDim)
            VGap(4.dp)
            Txt(t("settings.about.lic.wiki"), sty = Sty.SMALL, color = Lux.textDim)
        }
        SOURCE_REPO_URL?.let { url ->
            GhostButton(
                text = t("settings.about.star"),
                onClick = { ShareHelper.openUrl(ctx, url) },
                modifier = Modifier.fillMaxWidth(),
                icon = Ic.STAR
            )
        }
    }
}

private fun appVersionName(ctx: Context): String = try {
    ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName.orEmpty()
} catch (e: Exception) {
    CrashLog.note(ctx, "settings_version", e)
    ""
}
