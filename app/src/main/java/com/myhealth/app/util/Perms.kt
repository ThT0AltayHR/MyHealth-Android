package com.myhealth.app.util

import android.Manifest
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings

/**
 * Izin kontrolleri ve sistem ayar ekranlarina gitme yardimcilari (HyperOS/MIUI dahil).
 * Hicbiri istisna firlatmaz; ekran acilamazsa uygulama ayrintilari sayfasina duser.
 */
object Perms {
    private fun granted(ctx: Context, p: String): Boolean =
        try { ctx.checkSelfPermission(p) == PackageManager.PERMISSION_GRANTED } catch (e: Exception) { false }

    // ------------------------------------------------------------------ kontroller
    val locationPermissions: Array<String> =
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

    fun hasLocation(ctx: Context): Boolean =
        granted(ctx, Manifest.permission.ACCESS_FINE_LOCATION) || granted(ctx, Manifest.permission.ACCESS_COARSE_LOCATION)

    fun hasPreciseLocation(ctx: Context): Boolean = granted(ctx, Manifest.permission.ACCESS_FINE_LOCATION)

    fun hasNotifications(ctx: Context): Boolean = Notif.canPost(ctx)

    val notificationPermissions: Array<String> =
        if (Build.VERSION.SDK_INT >= 33) arrayOf(Manifest.permission.POST_NOTIFICATIONS) else emptyArray()

    /** Muzik dosyalarini okuma izni (Android 13+: READ_MEDIA_AUDIO, oncesi: READ_EXTERNAL_STORAGE). */
    val audioPermission: String
        get() = if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_AUDIO else Manifest.permission.READ_EXTERNAL_STORAGE

    fun hasAudio(ctx: Context): Boolean = granted(ctx, audioPermission)

    /** Bluetooth baglanti izni (Android 12+: BLUETOOTH_CONNECT; oncesi manifest izni yeterli). */
    val bluetoothPermissions: Array<String> =
        if (Build.VERSION.SDK_INT >= 31) arrayOf(Manifest.permission.BLUETOOTH_CONNECT) else emptyArray()

    fun hasBluetooth(ctx: Context): Boolean =
        Build.VERSION.SDK_INT < 31 || granted(ctx, Manifest.permission.BLUETOOTH_CONNECT)

    val activityPermissions: Array<String> =
        if (Build.VERSION.SDK_INT >= 29) arrayOf(Manifest.permission.ACTIVITY_RECOGNITION) else emptyArray()

    fun hasActivityRecognition(ctx: Context): Boolean =
        Build.VERSION.SDK_INT < 29 || granted(ctx, Manifest.permission.ACTIVITY_RECOGNITION)

    /** Alarm calarken tam ekran bildirim gosterebilir mi (Android 14+ kullanici onayi gerekebilir). */
    fun canFullScreenIntent(ctx: Context): Boolean = try {
        if (Build.VERSION.SDK_INT >= 34) {
            (ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).canUseFullScreenIntent()
        } else true
    } catch (e: Exception) {
        true
    }

    // ------------------------------------------------------------------ cihaz
    fun isXiaomiFamily(): Boolean {
        val m = Build.MANUFACTURER.lowercase()
        val b = Build.BRAND.lowercase()
        return m.contains("xiaomi") || b.contains("xiaomi") || b.contains("redmi") || b.contains("poco")
    }

    // ------------------------------------------------------------------ ayar ekranlari
    fun appDetailsIntent(ctx: Context): Intent =
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", ctx.packageName, null))

    fun notificationSettingsIntent(ctx: Context): Intent =
        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, ctx.packageName)

    fun batterySettingsIntent(): Intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)

    fun fullScreenIntentSettings(ctx: Context): Intent? =
        if (Build.VERSION.SDK_INT >= 34)
            Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT, Uri.fromParts("package", ctx.packageName, null))
        else null

    /** Otomatik baslatma ekranlari (HyperOS/MIUI ve diger ureticiler); sirayla denenir. */
    fun autostartIntents(ctx: Context): List<Intent> {
        fun comp(pkg: String, cls: String) = Intent().setComponent(ComponentName(pkg, cls))
        val list = ArrayList<Intent>()
        if (isXiaomiFamily()) {
            list.add(comp("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"))
            list.add(
                comp("com.miui.powerkeeper", "com.miui.powerkeeper.ui.HiddenAppsConfigActivity")
                    .putExtra("package_name", ctx.packageName)
                    .putExtra("package_label", "MyHealth")
            )
        }
        val m = Build.MANUFACTURER.lowercase()
        if (m.contains("huawei") || m.contains("honor")) {
            list.add(comp("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"))
        }
        if (m.contains("oppo") || m.contains("realme") || m.contains("oneplus")) {
            list.add(comp("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"))
        }
        if (m.contains("vivo")) {
            list.add(comp("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"))
        }
        if (m.contains("samsung")) {
            list.add(comp("com.samsung.android.lool", "com.samsung.android.sm.battery.ui.BatteryActivity"))
        }
        list.add(appDetailsIntent(ctx))
        return list
    }

    /** Verilen niyetlerden ilk acilabileni baslatir. Hicbiri acilmazsa uygulama ayrintilarina gider. */
    fun startFirst(ctx: Context, intents: List<Intent>): Boolean {
        for (i in intents) {
            try {
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                ctx.startActivity(i)
                return true
            } catch (e: Exception) {
                // siradaki niyeti dene
            }
        }
        return try {
            ctx.startActivity(appDetailsIntent(ctx).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            true
        } catch (e: Exception) {
            false
        }
    }

    fun openAppDetails(ctx: Context): Boolean = startFirst(ctx, listOf(appDetailsIntent(ctx)))

    fun openAutostart(ctx: Context): Boolean = startFirst(ctx, autostartIntents(ctx))

    fun openBattery(ctx: Context): Boolean = startFirst(ctx, listOf(batterySettingsIntent(), appDetailsIntent(ctx)))

    fun openNotificationSettings(ctx: Context): Boolean =
        startFirst(ctx, listOf(notificationSettingsIntent(ctx), appDetailsIntent(ctx)))
}
