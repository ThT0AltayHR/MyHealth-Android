package com.myhealth.app.feature.permissions

import android.content.Context
import android.os.Build
import com.myhealth.app.data.Repo
import com.myhealth.app.ui.kit.Ic
import com.myhealth.app.util.Perms

/** Izin listesi: ekran bunu OnResume ile yeniden hesaplar. Konum, bildirim, tam ekran (14+), Bluetooth (12+). */
internal fun permItems(ctx: Context): List<PermItem> = permCore(ctx) + permBackground(ctx)

private fun permCore(ctx: Context): List<PermItem> {
    val list = ArrayList<PermItem>()
    list.add(
        PermItem(
            id = "loc",
            icon = Ic.PIN,
            titleKey = "perm.loc.title",
            descKey = "perm.loc.desc",
            granted = Perms.hasLocation(ctx),
            runtime = Perms.locationPermissions,
            asked = Repo.kvBool("perm.asked.loc"),
            openSettings = { c -> Perms.openAppDetails(c) }
        )
    )
    list.add(
        PermItem(
            id = "notif",
            icon = Ic.BELL,
            titleKey = "perm.notif.title",
            descKey = "perm.notif.desc",
            granted = Perms.hasNotifications(ctx),
            runtime = Perms.notificationPermissions,
            asked = Repo.kvBool("perm.asked.notif"),
            openSettings = { c -> Perms.openNotificationSettings(c) }
        )
    )
    if (Build.VERSION.SDK_INT >= 34) {
        list.add(
            PermItem(
                id = "fsi",
                icon = Ic.ALARM,
                titleKey = "perm.fsi.title",
                descKey = "perm.fsi.desc",
                granted = Perms.canFullScreenIntent(ctx),
                runtime = emptyArray(),
                asked = false,
                openSettings = { c ->
                    val i = Perms.fullScreenIntentSettings(c)
                    if (i != null) startIntentSafe(c, i) else Perms.openAppDetails(c)
                }
            )
        )
    }
    if (Build.VERSION.SDK_INT >= 31) {
        list.add(
            PermItem(
                id = "bt",
                icon = Ic.BLUETOOTH,
                titleKey = "perm.bt.title",
                descKey = "perm.bt.desc",
                granted = Perms.hasBluetooth(ctx),
                runtime = Perms.bluetoothPermissions,
                asked = Repo.kvBool("perm.asked.bt"),
                openSettings = { c -> Perms.openAppDetails(c) }
            )
        )
    }
    return list
}
