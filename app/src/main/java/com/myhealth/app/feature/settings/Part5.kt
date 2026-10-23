package com.myhealth.app.feature.settings

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.myhealth.app.data.AppSettings
import com.myhealth.app.data.Repo
import com.myhealth.app.data.UserProfile
import com.myhealth.app.feature.meds.MedStore
import com.myhealth.app.ui.kit.ConfirmDialog
import com.myhealth.app.ui.kit.GhostButton
import com.myhealth.app.ui.kit.GlassCard
import com.myhealth.app.ui.kit.HGap
import com.myhealth.app.ui.kit.Ic
import com.myhealth.app.ui.kit.Panel
import com.myhealth.app.ui.kit.PrimaryButton
import com.myhealth.app.ui.kit.SectionTitle
import com.myhealth.app.ui.kit.SettingsRow
import com.myhealth.app.ui.kit.Toasts
import com.myhealth.app.ui.kit.VGap
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt
import com.myhealth.app.ui.tr
import com.myhealth.app.util.CrashLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Teshis kaydi: son cokme ve hata kaydini paylas veya temizle. Dosya islemleri IO thread'inde. */
@Composable
internal fun SettingsDiag() {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var hasLog by remember { mutableStateOf<Boolean?>(null) }
    LaunchedEffect(Unit) {
        hasLog = withContext(Dispatchers.IO) { diagText(ctx).isNotEmpty() }
    }
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionTitle(text = t("settings.section.diag"))
        GlassCard {
            Txt(t("settings.diag.hint"), sty = Sty.SMALL, color = Lux.textDim)
            VGap(8.dp)
            hasLog?.let { has ->
                Txt(
                    text = if (has) t("settings.diag.has") else t("settings.diag.none"),
                    sty = Sty.BODY_STRONG
                )
            }
            VGap(10.dp)
            Row(modifier = Modifier.fillMaxWidth()) {
                PrimaryButton(
                    text = t("settings.diag.share"),
                    onClick = {
                        scope.launch {
                            val text = withContext(Dispatchers.IO) { diagText(ctx) }
                            if (text.isEmpty()) Toasts.show(tr("settings.diag.none")) else diagShare(ctx, text)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = hasLog == true
                )
                HGap(10.dp)
                GhostButton(
                    text = t("settings.diag.clear"),
                    onClick = {
                        scope.launch {
                            withContext(Dispatchers.IO) { diagClear(ctx) }
                            hasLog = false
                            Toasts.show(tr("settings.diag.cleared"))
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = hasLog == true
                )
            }
        }
    }
}

/** Verilerimi sil: onayli islem; silme isi IO thread'inde calisir. */
@Composable
internal fun SettingsDanger() {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var confirm by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionTitle(text = t("settings.section.danger"))
        Panel {
            SettingsRow(
                icon = Ic.TRASH,
                title = t("settings.danger.wipe"),
                subtitle = t("settings.danger.wipe.sub"),
                onClick = { confirm = true }
            )
        }
    }
    if (confirm) {
        ConfirmDialog(
            title = t("settings.danger.title"),
            message = t("settings.danger.body"),
            confirmText = t("settings.danger.confirm"),
            onConfirm = {
                confirm = false
                scope.launch {
                    withContext(Dispatchers.IO) { wipeAllData(ctx) }
                    Toasts.show(tr("settings.danger.done"))
                }
            },
            onDismiss = { confirm = false },
            danger = true
        )
    }
}

private fun diagText(ctx: Context): String = try {
    listOf(CrashLog.readCrash(ctx).orEmpty(), CrashLog.readErrors(ctx).orEmpty())
        .filter { it.isNotBlank() }
        .joinToString("\n\n")
} catch (e: Exception) {
    CrashLog.note(ctx, "diag_text", e)
    ""
}

private fun diagClear(ctx: Context) {
    try {
        CrashLog.clear(ctx)
    } catch (e: Exception) {
        CrashLog.note(ctx, "diag_clear", e)
    }
}

private fun diagShare(ctx: Context, text: String) {
    try {
        val send = Intent(Intent.ACTION_SEND)
        send.type = "text/plain"
        send.putExtra(Intent.EXTRA_TEXT, text)
        val chooser = Intent.createChooser(send, tr("settings.diag.share"))
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ctx.startActivity(chooser)
    } catch (e: Exception) {
        CrashLog.note(ctx, "diag_share", e)
        Toasts.show(tr("settings.diag.sharefail"))
    }
}

// DOGRULA: tek cagrida tum veriyi silen Repo API'si API ozetinde yok; mevcut silme islevleriyle yapildi (adim ve su kayitlari kalabilir).
private fun wipeAllData(ctx: Context) {
    try {
        Repo.workouts.value.forEach { Repo.deleteWorkout(it.id) }
        Repo.notes.value.forEach { Repo.deleteNote(it.id) }
        Repo.alarms.value.forEach { Repo.deleteAlarm(it.id) }
        Repo.meals.value.forEach { Repo.deleteMeal(it.id) }
        Repo.playlists.value.forEach { Repo.deletePlaylist(it.id) }
        Repo.clearChat()
        Repo.clearMemory()
        MedStore.set(emptySet(), emptySet())
        Repo.updateSettings { s -> AppSettings(langCode = s.langCode) }
        Repo.updateProfile { UserProfile() }
        CrashLog.clear(ctx)
    } catch (e: Exception) {
        CrashLog.note(ctx, "danger_wipe", e)
    }
}
