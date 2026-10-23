package com.myhealth.app.feature.onboarding

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.myhealth.app.ui.kit.*
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt
import com.myhealth.app.util.Perms

/** Izin tanitim ekranlari sirasi (ONB 11..16). */
internal enum class OnbPerm(val key: String) {
    LOC("loc"), NOTIF("notif"), BT("bt"), ACT("act"), AUDIO("audio"), BG("bg")
}

private fun OnbPerm.runtime(): Array<String> = when (this) {
    OnbPerm.LOC -> Perms.locationPermissions
    OnbPerm.NOTIF -> Perms.notificationPermissions
    OnbPerm.BT -> Perms.bluetoothPermissions
    OnbPerm.ACT -> Perms.activityPermissions
    OnbPerm.AUDIO -> arrayOf(Perms.audioPermission)
    OnbPerm.BG -> emptyArray<String>()
}

private fun OnbPerm.granted(ctx: Context): Boolean = when (this) {
    OnbPerm.LOC -> Perms.hasLocation(ctx)
    OnbPerm.NOTIF -> Perms.hasNotifications(ctx)
    OnbPerm.BT -> Perms.hasBluetooth(ctx)
    OnbPerm.ACT -> Perms.hasActivityRecognition(ctx)
    OnbPerm.AUDIO -> Perms.hasAudio(ctx)
    OnbPerm.BG -> false
}

private fun permIcon(p: OnbPerm): String = when (p) {
    OnbPerm.LOC -> Ic.PIN
    OnbPerm.NOTIF -> Ic.BELL
    OnbPerm.BT -> Ic.BLUETOOTH
    OnbPerm.ACT -> Ic.STEPS
    OnbPerm.AUDIO -> Ic.HEADPHONES
    OnbPerm.BG -> Ic.BATTERY
}

/**
 * 11..16. adimlar: her izin icin aciklama. "Izin ver" sistem istegini acar (ya da arka plan icin ayar ekranini);
 * sonuc ne olursa olsun akis devam eder. Zaten verilmis ya da platformda olmayan izinde istek atlanir.
 */
@Composable
internal fun StepPermissions(index: Int, stepNo: Int, onBack: () -> Unit, onNext: () -> Unit) {
    val perm = OnbPerm.values().getOrElse(index) { OnbPerm.LOC }
    val ctx = LocalContext.current
    val already = remember(perm) { perm.granted(ctx) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        onNext()
    }
    val allow: () -> Unit = {
        if (perm == OnbPerm.BG) {
            try {
                Perms.openAutostart(ctx)
            } catch (e: Exception) {
                onbLog("perm_autostart", e)
            }
            onNext()
        } else if (already || perm.runtime().isEmpty()) {
            onNext()
        } else {
            try {
                launcher.launch(perm.runtime())
            } catch (e: Exception) {
                onbLog("perm_launch", e)
                onNext()
            }
        }
    }
    OnbFrame(
        title = t("ob.perm.${perm.key}.title"),
        subtitle = t("ob.perm.${perm.key}.sub"),
        stepNo = stepNo,
        onBack = onBack,
        bottom = {
            OnbCta(
                text = when {
                    perm == OnbPerm.BG -> t("ob.perm.open")
                    already -> t("ob.next")
                    else -> t("ob.perm.allow")
                },
                onClick = allow,
                secondaryText = if (already) null else t("ob.perm.later"),
                onSecondary = onNext
            )
        }
    ) {
        LuxIcon(
            path = permIcon(perm),
            size = 72.dp,
            tint = Lux.orange,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        VGap(16.dp)
        if (already && perm != OnbPerm.BG) {
            Txt(t("ob.perm.granted"), sty = Sty.BODY_STRONG, color = Lux.green)
        }
    }
}
