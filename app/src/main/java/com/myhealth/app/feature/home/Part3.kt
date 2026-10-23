package com.myhealth.app.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.myhealth.app.core.Fmt
import com.myhealth.app.data.Repo
import com.myhealth.app.feature.fasting.FastingInfo
import com.myhealth.app.ui.kit.Ic
import com.myhealth.app.ui.kit.VGap
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt
import java.time.LocalDateTime

// Ramazan karti: iftara veya imsaka kalan sure (dakika, cihaz saatine gore).
@Composable
internal fun RamadanCard(info: FastingInfo, now: LocalDateTime, modifier: Modifier = Modifier) {
    val nowMin = now.hour * 60 + now.minute
    val toIftar = minutesUntil(info.iftarMin, nowMin)
    val toImsak = minutesUntil(info.imsakMin, nowMin)
    val iftarNext = toIftar <= toImsak
    val remain = if (iftarNext) toIftar else toImsak
    val targetMin = if (iftarNext) info.iftarMin else info.imsakMin
    val nextKey = if (iftarNext) "home.fast.iftar" else "home.fast.imsak"
    val day = info.ramadanDay
    HomeCard(icon = Ic.MOON, title = t("home.fast.title"), modifier = modifier) {
        if (day != null) {
            Txt(t("home.fast.day", day), sty = Sty.CAPTION, color = Lux.textDim, maxLines = 1, overflow = TextOverflow.Ellipsis)
            VGap(4.dp)
        }
        Txt(t(nextKey), sty = Sty.SMALL, color = Lux.textDim, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Txt(
            text = Fmt.hoursMinutes(remain, t("home.unit.h"), t("home.unit.m")),
            sty = Sty.H3,
            color = Lux.gold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Txt(Fmt.hhmm(targetMin), sty = Sty.CAPTION, color = Lux.textDim, maxLines = 1)
    }
}

// Hedef dakikaya (1440 dongusu icinde) kalan dakika; sonuc 0..1439.
private fun minutesUntil(targetMin: Int, nowMin: Int): Int {
    val d = (targetMin - nowMin) % 1440
    return if (d < 0) d + 1440 else d
}

// Bugunku kalori: bugunku ogunlerin toplami; ogun yoksa bilgi metni.
@Composable
internal fun KcalCard(modifier: Modifier = Modifier) {
    val meals by Repo.meals.collectAsState()
    val kcal = remember(meals) { Repo.mealsOn(Fmt.todayKey()).sumOf { it.kcal } }
    HomeCard(icon = Ic.FLAME, title = t("home.kcal.title"), modifier = modifier) {
        if (kcal > 0) {
            Txt(t("home.kcal.value", kcal), sty = Sty.H3, color = Lux.text, maxLines = 1, overflow = TextOverflow.Ellipsis)
        } else {
            Txt(t("home.kcal.none"), sty = Sty.SMALL, color = Lux.textDim, maxLines = 3, overflow = TextOverflow.Ellipsis)
        }
    }
}
