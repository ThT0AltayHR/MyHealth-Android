package com.myhealth.app.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.myhealth.app.MainActivity
import com.myhealth.app.R
import com.myhealth.app.core.I18n
import com.myhealth.app.data.Repo

/**
 * Bildirim kanallari ve ortak gonderim. Tum ozellikler bildirimleri buradan yollar,
 * boylece izin kontrolu ve hata yakalama tek yerde olur (bildirim yuzunden cokme olmaz).
 */
object Notif {
    const val CH_WEATHER = "weather"
    const val CH_ACTIVITY = "activity"
    const val CH_MEAL = "meal"
    const val CH_ALARM = "alarm"
    const val CH_FASTING = "fasting"
    const val CH_WORKOUT = "workout"
    const val CH_MUSIC = "music"
    const val CH_GENERAL = "general"
    const val CH_AI = "ai"

    // Sabit bildirim kimlikleri (cakismamasi icin aralik ayrilmistir).
    const val ID_WEATHER = 1001
    const val ID_ACTIVITY = 1002
    const val ID_MEAL_BASE = 1100          // + MealType.ordinal
    const val ID_ALARM_BASE = 20000        // + alarm id
    const val ID_FASTING_SAHUR = 3001
    const val ID_FASTING_IFTAR = 3002
    const val ID_WORKOUT = 4001
    const val ID_MUSIC = 5001
    const val ID_FOLLOWUP = 6001           // egzersiz sonu yapay zeka destek bildirimi
    const val ID_DAILY_TIP = 6002          // gunluk yapay zeka ipucu

    /** MainActivity'ye gonderilen "git" anahtari (bkz. DeepLink). */
    const val EXTRA_ROUTE = "route"

    fun ensureChannels(ctx: Context) {
        try {
            val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
            val lang = Repo.lang.value
            fun name(key: String) = I18n.get(lang, key)
            fun ch(id: String, nameKey: String, imp: Int, silent: Boolean = false) {
                val c = NotificationChannel(id, name(nameKey), imp)
                if (silent) { c.setSound(null, null); c.enableVibration(false) }
                nm.createNotificationChannel(c)
            }
            ch(CH_WEATHER, "notif.ch.weather", NotificationManager.IMPORTANCE_DEFAULT)
            ch(CH_ACTIVITY, "notif.ch.activity", NotificationManager.IMPORTANCE_DEFAULT)
            ch(CH_MEAL, "notif.ch.meal", NotificationManager.IMPORTANCE_DEFAULT)
            // Alarm sesi AlarmRingService tarafindan calinir; kanal sessiz ama dikkat cekici (heads-up).
            ch(CH_ALARM, "notif.ch.alarm", NotificationManager.IMPORTANCE_HIGH, silent = true)
            ch(CH_FASTING, "notif.ch.fasting", NotificationManager.IMPORTANCE_HIGH, silent = true)
            ch(CH_WORKOUT, "notif.ch.workout", NotificationManager.IMPORTANCE_LOW)
            ch(CH_MUSIC, "notif.ch.music", NotificationManager.IMPORTANCE_LOW)
            ch(CH_GENERAL, "notif.ch.general", NotificationManager.IMPORTANCE_DEFAULT)
            ch(CH_AI, "notif.ch.ai", NotificationManager.IMPORTANCE_DEFAULT)
        } catch (e: Exception) {
            CrashLog.note(ctx, "notif_channels", e)
        }
    }

    /** Bildirim gonderilebilir mi (Android 13+ izni ve kullanicinin genel anahtari). */
    fun canPost(ctx: Context): Boolean = try {
        val permOk = Build.VERSION.SDK_INT < 33 ||
            ctx.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        permOk && NotificationManagerCompat.from(ctx).areNotificationsEnabled()
    } catch (e: Exception) {
        false
    }

    /** Bildirime dokunuldugunda MainActivity'yi acar; route verilirse ilgili sayfaya gider. */
    fun contentIntent(ctx: Context, route: String? = null, requestCode: Int = 0): PendingIntent {
        val i = Intent(ctx, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            if (route != null) putExtra(EXTRA_ROUTE, route)
        }
        return PendingIntent.getActivity(
            ctx, requestCode, i,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /** Standart metin bildirimi. route: "weather", "meals", "alarms", "fasting", "sport:run", "settings" ... */
    fun post(
        ctx: Context,
        channel: String,
        id: Int,
        title: String,
        text: String,
        route: String? = null,
        ongoing: Boolean = false,
        /** Bildirime ikinci bir eylem dugmesi ekler (orn. "Sohbeti baslat"); actionRoute yoksa route kullanilir. */
        actionTitle: String? = null,
        actionRoute: String? = null
    ) {
        if (!canPost(ctx)) return
        try {
            ensureChannels(ctx)
            val n = NotificationCompat.Builder(ctx, channel)
                .setSmallIcon(R.drawable.ic_stat_heart)
                .setColor(0xFFF2761B.toInt())
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(NotificationCompat.BigTextStyle().bigText(text))
                .setContentIntent(contentIntent(ctx, route, id))
                .setAutoCancel(!ongoing)
                .setOngoing(ongoing)
                .setOnlyAlertOnce(ongoing)
            if (actionTitle != null) {
                n.addAction(0, actionTitle, contentIntent(ctx, actionRoute ?: route, id + 90000))
            }
            NotificationManagerCompat.from(ctx).notify(id, n.build())
        } catch (e: SecurityException) {
            // Izin kullanici tarafindan alinmis olabilir; sessizce gec.
        } catch (e: Exception) {
            CrashLog.note(ctx, "notif_post", e)
        }
    }

    fun cancel(ctx: Context, id: Int) {
        try { NotificationManagerCompat.from(ctx).cancel(id) } catch (e: Exception) { /* yoksay */ }
    }
}
