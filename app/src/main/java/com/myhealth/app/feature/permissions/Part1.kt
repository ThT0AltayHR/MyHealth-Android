package com.myhealth.app.feature.permissions

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.myhealth.app.ui.kit.*
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt
import com.myhealth.app.ui.tr
import com.myhealth.app.util.CrashLog

/** Tek izin satiri verisi. granted: null = uygulama okuyamaz (kullanici ayarlardan kontrol eder). */
internal class PermItem(
    val id: String,
    val icon: String,
    val titleKey: String,
    val descKey: String,
    val granted: Boolean?,
    val runtime: Array<String>,
    val asked: Boolean,
    val openSettings: (Context) -> Boolean
)

/** Durum etiketi, aciklama ve eylemler. Izin verildiyse eylem dugmesi gostermez. */
@Composable
internal fun PermCard(item: PermItem, onAllow: () -> Unit, onSettings: () -> Unit) {
    GlassCard {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            LuxIcon(path = item.icon, tint = Lux.orange, size = 24.dp)
            HGap(12.dp)
            Column(modifier = Modifier.weight(1f)) {
                Txt(t(item.titleKey), sty = Sty.BODY_STRONG)
                VGap(2.dp)
                Txt(t(item.descKey), sty = Sty.SMALL, color = Lux.textDim)
            }
            HGap(8.dp)
            SmallTag(text = permStateLabel(item.granted))
        }
        if (item.granted != true) {
            VGap(12.dp)
            val allowShown = item.runtime.isNotEmpty()
            val settingsShown = item.runtime.isEmpty() || item.asked
            Row(modifier = Modifier.fillMaxWidth()) {
                if (allowShown) {
                    PrimaryButton(text = t("perm.allow"), onClick = onAllow, modifier = Modifier.weight(1f))
                }
                if (allowShown && settingsShown) HGap(10.dp)
                if (settingsShown) {
                    GhostButton(text = t("perm.open"), onClick = onSettings, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun permStateLabel(granted: Boolean?): String = when (granted) {
    true -> t("perm.state.granted")
    false -> t("perm.state.denied")
    null -> t("perm.state.check")
}

/** Ayar ekranini acar; acilamazsa kullaniciya bildirir. */
internal fun openPermSettings(ctx: Context, item: PermItem) {
    val ok = try {
        item.openSettings(ctx)
    } catch (e: Exception) {
        CrashLog.note(ctx, "perm_open_settings", e)
        false
    }
    if (!ok) Toasts.show(tr("perm.open.fail"))
}

/** Niyeti guvenle baslatir; basarisizsa false doner. */
internal fun startIntentSafe(ctx: Context, i: Intent): Boolean = try {
    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    ctx.startActivity(i)
    true
} catch (e: Exception) {
    CrashLog.note(ctx, "perm_start_intent", e)
    false
}
