package com.myhealth.app.feature.permissions

import android.content.Context
import com.myhealth.app.data.Repo
import com.myhealth.app.ui.kit.Ic
import com.myhealth.app.util.Perms

/** Izin listesi (arka plan): hareket algilama, muzik/ses dosyalari, pil ve HyperOS/MIUI otomatik baslatma. */
internal fun permBackground(ctx: Context): List<PermItem> {
    val list = ArrayList<PermItem>()
    list.add(
        PermItem(
            id = "act",
            icon = Ic.STEPS,
            titleKey = "perm.act.title",
            descKey = "perm.act.desc",
            granted = Perms.hasActivityRecognition(ctx),
            runtime = Perms.activityPermissions,
            asked = Repo.kvBool("perm.asked.act"),
            openSettings = { c -> Perms.openAppDetails(c) }
        )
    )
    list.add(
        PermItem(
            id = "audio",
            icon = Ic.MUSIC,
            titleKey = "perm.audio.title",
            descKey = "perm.audio.desc",
            granted = Perms.hasAudio(ctx),
            runtime = arrayOf(Perms.audioPermission),
            asked = Repo.kvBool("perm.asked.audio"),
            openSettings = { c -> Perms.openAppDetails(c) }
        )
    )
    // Pil optimizasyonu izni bildirilmedi: uygulama durumu okuyamaz; ayar listesine gonderilir.
    list.add(
        PermItem(
            id = "bat",
            icon = Ic.BATTERY,
            titleKey = "perm.bat.title",
            descKey = "perm.bat.desc",
            granted = null,
            runtime = emptyArray(),
            asked = false,
            openSettings = { c -> Perms.openBattery(c) }
        )
    )
    if (Perms.isXiaomiFamily()) {
        list.add(
            PermItem(
                id = "auto",
                icon = Ic.POWER,
                titleKey = "perm.auto.title",
                descKey = "perm.auto.desc",
                granted = null,
                runtime = emptyArray(),
                asked = false,
                openSettings = { c -> Perms.openAutostart(c) }
            )
        )
    }
    return list
}
