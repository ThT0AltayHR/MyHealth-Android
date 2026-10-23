package com.myhealth.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import com.myhealth.app.core.I18n
import com.myhealth.app.core.Lang
import com.myhealth.app.data.Repo

/** Gecerli arayuz dili. LuxTheme saglar; RTL (Arapca) yonu de ayni yerde ayarlanir. */
val LocalLang = staticCompositionLocalOf { Lang.EN }

/** Ceviri: t("anahtar"). Bulunamazsa Ingilizce, o da yoksa anahtarin kendisi doner (cokmez). */
@Composable
fun t(key: String): String = I18n.get(LocalLang.current, key)

/** Yer tutuculu ceviri: t("greet.hey", name) -> {0} yerine name yazilir. */
@Composable
fun t(key: String, vararg args: Any?): String = I18n.fmt(LocalLang.current, key, *args)

/** Composable disindan (bildirim, servis, geri cagirim) ceviri: gecerli uygulama dilini kullanir. */
fun tr(key: String): String = I18n.get(Repo.lang.value, key)

fun tr(key: String, vararg args: Any?): String = I18n.fmt(Repo.lang.value, key, *args)
