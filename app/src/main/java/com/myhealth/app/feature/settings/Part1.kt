package com.myhealth.app.feature.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.myhealth.app.data.Repo
import com.myhealth.app.ui.kit.*
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt
import com.myhealth.app.ui.tr
import com.myhealth.app.util.CrashLog

/** Aile hekimi karti (ekranin en ustu) ve siddetli belirti uyarisi. */
@Composable
internal fun SettingsDoctor() {
    val ctx = LocalContext.current
    val s = Repo.settings.collectAsState().value
    var name by remember(s.doctorName) { mutableStateOf(s.doctorName) }
    var phone by remember(s.doctorPhone) { mutableStateOf(s.doctorPhone) }
    val severe = settingsSevereRecent()
    val unchanged = name.trim() == s.doctorName && phone.trim() == s.doctorPhone
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionTitle(text = t("settings.section.doctor"))
        if (severe) {
            GlassCard(warm = true) {
                Txt(t("settings.doctor.severe.title"), sty = Sty.H3, color = Lux.orange)
                VGap(6.dp)
                Txt(t("settings.doctor.severe.body"), sty = Sty.BODY, color = Lux.text)
            }
        }
        GlassCard(warm = true) {
            Txt(t("settings.doctor.hint"), sty = Sty.SMALL, color = Lux.textDim)
            VGap(10.dp)
            LuxTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = t("settings.doctor.name"),
                modifier = Modifier.fillMaxWidth()
            )
            VGap(8.dp)
            LuxTextField(
                value = phone,
                onValueChange = { phone = it },
                placeholder = t("settings.doctor.phone"),
                keyboardType = KeyboardType.Phone,
                modifier = Modifier.fillMaxWidth()
            )
            VGap(10.dp)
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                PrimaryButton(
                    text = t("settings.doctor.save"),
                    onClick = { saveDoctor(name, phone) },
                    modifier = Modifier.weight(1f),
                    enabled = !unchanged
                )
                HGap(10.dp)
                GhostButton(
                    text = t("settings.doctor.call"),
                    onClick = { dialDoctor(ctx, phone) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Panel {
            SettingsRow(
                icon = MedIcons.STETHOSCOPE,
                title = t("settings.doctor.always"),
                subtitle = t("settings.doctor.always.sub"),
                onClick = { Repo.updateSettings { it.copy(doctorAlways = !it.doctorAlways) } },
                trailing = {
                    LuxSwitch(
                        checked = s.doctorAlways,
                        onChange = { v -> Repo.updateSettings { it.copy(doctorAlways = v) } }
                    )
                }
            )
        }
    }
}

// DOGRULA: HealthLog API'si API.md'de yok. Son 14 gunde siddetli belirti varsa true donmeli; baglanana kadar false.
internal fun settingsSevereRecent(): Boolean = false

private fun saveDoctor(name: String, phone: String) {
    Repo.updateSettings { it.copy(doctorName = name.trim(), doctorPhone = phone.trim()) }
    Toasts.show(tr("settings.saved"))
}

/** Aile hekimini ACTION_DIAL ile acar; arama kullanici onayiyla baslar. */
internal fun dialDoctor(ctx: Context, phone: String) {
    val p = phone.trim()
    if (p.isEmpty()) {
        Toasts.show(tr("settings.doctor.nophone"))
        return
    }
    try {
        val i = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", p, null))
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ctx.startActivity(i)
    } catch (e: Exception) {
        CrashLog.note(ctx, "settings_dial", e)
        Toasts.show(tr("settings.doctor.dialfail"))
    }
}
