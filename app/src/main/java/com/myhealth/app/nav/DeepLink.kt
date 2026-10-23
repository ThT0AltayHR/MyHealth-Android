package com.myhealth.app.nav

import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Bildirimden gelen yonlendirmeler. Notif.contentIntent(route = "weather") gibi anahtarlar
 * MainActivity'de buraya yazilir, kabuk okuyup ilgili sayfayi acar.
 *
 * Anahtarlar: home, sports, assistant, music, more (sekmeler);
 * weather, breathing, notes, alarms, meals, fasting, stats, settings, permissions, profile, history,
 * nowplaying, healthlog, medications ve "sport:<sporId>" (canli spor ekrani).
 */
object DeepLink {
    val pending = MutableStateFlow<String?>(null)

    fun open(nav: Nav, key: String) {
        when (key) {
            "home" -> nav.go(Tab.HOME)
            "sports" -> nav.go(Tab.SPORTS)
            "assistant" -> nav.go(Tab.ASSISTANT)
            "music" -> nav.go(Tab.MUSIC)
            "more" -> nav.go(Tab.MORE)
            else -> {
                val r = routeFor(key) ?: return
                nav.push(r)
            }
        }
    }

    fun routeFor(key: String): Route? = when {
        key == "weather" -> Route.Weather
        key == "breathing" -> Route.Breathing
        key == "notes" -> Route.Notes
        key == "alarms" -> Route.Alarms
        key == "meals" -> Route.Meals
        key == "fasting" -> Route.Fasting
        key == "stats" -> Route.Stats
        key == "settings" -> Route.Settings
        key == "permissions" -> Route.Permissions
        key == "profile" -> Route.Profile
        key == "history" -> Route.SportHistory
        key == "nowplaying" -> Route.NowPlaying
        key == "healthlog" -> Route.HealthLog
        key == "medications" -> Route.Medications
        key.startsWith("sport:") && key.length > 6 -> Route.SportLive(key.substring(6))
        else -> null
    }
}
