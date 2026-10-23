package com.myhealth.app.nav

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.staticCompositionLocalOf

/** Alt gezinmedeki bes sekme. */
enum class Tab { HOME, SPORTS, ASSISTANT, MUSIC, MORE }

/** Sekmelerin ustune acilan tam ekran sayfalar. */
sealed class Route {
    object Weather : Route()
    object Breathing : Route()
    object Notes : Route()
    data class NoteEdit(val id: Long) : Route()          // 0 = yeni not
    object Alarms : Route()
    data class AlarmEdit(val id: Int) : Route()          // 0 = yeni alarm
    object Meals : Route()
    object Fasting : Route()
    object Stats : Route()
    object Settings : Route()
    object Permissions : Route()
    object Profile : Route()
    data class SportLive(val sportId: String) : Route()
    object SportHistory : Route()
    object NowPlaying : Route()
    object HealthLog : Route()      // egzersiz sonu saglik cevaplari belgesi (Ayarlar'dan acilir)
    object Medications : Route()    // ilaclarim (duzenli / ara sira)
}

/**
 * Basit durum tabanli gezinme. Sekme degistirmek sayfa yigitini temizler.
 * Sistem geri tusu: once sayfa yigini, sonra Ana Sayfa sekmesi, en son uygulamadan cikis.
 */
class Nav {
    var tab by mutableStateOf(Tab.HOME)
        private set

    val stack = mutableStateListOf<Route>()

    val top: Route? get() = stack.lastOrNull()

    fun go(t: Tab) {
        stack.clear()
        tab = t
    }

    fun push(r: Route) {
        if (stack.lastOrNull() != r) stack.add(r)
    }

    /** Sayfayi kapatir; yigin bossa false doner. */
    fun pop(): Boolean {
        if (stack.isEmpty()) return false
        stack.removeAt(stack.size - 1)
        return true
    }

    fun canGoBack(): Boolean = stack.isNotEmpty() || tab != Tab.HOME

    /** Sistem geri tusu davranisi. */
    fun back() {
        if (!pop() && tab != Tab.HOME) tab = Tab.HOME
    }

    /** Belirli bir sayfa acik mi (spor kucultme baloncugu gibi durumlar icin). */
    fun isOpen(predicate: (Route) -> Boolean): Boolean = stack.any(predicate)
}

val LocalNav = staticCompositionLocalOf { Nav() }
