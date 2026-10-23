package com.myhealth.app.feature.weather

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.myhealth.app.core.Fmt
import com.myhealth.app.data.DayForecast
import com.myhealth.app.ui.kit.GlassCard
import com.myhealth.app.ui.kit.HGap
import com.myhealth.app.ui.kit.HairLine
import com.myhealth.app.ui.kit.LuxIcon
import com.myhealth.app.ui.kit.VGap
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt
import com.myhealth.app.util.CrashLog
import com.myhealth.app.util.Perms
import java.time.Instant
import java.time.ZoneId

/** Epoch ms -> HH:mm (yerel saat). Hata olursa bos metin. */
internal fun wxClock(ms: Long): String = try {
    val z = Instant.ofEpochMilli(ms).atZone(ZoneId.systemDefault())
    Fmt.hhmm(z.hour * 60 + z.minute)
} catch (e: Exception) {
    ""
}

/** Konum izni var mi (izin kontrolu hatasi uygulamayi cokertmez). */
internal fun wxHasLocation(ctx: Context): Boolean = try {
    Perms.hasLocation(ctx)
} catch (e: Exception) {
    CrashLog.note(ctx, "weather_perm", e)
    false
}

/** Anlik olcum kutucugu: ikon, deger, etiket. */
@Composable
internal fun WxStat(icon: String, label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        LuxIcon(path = icon, size = 20.dp, tint = Lux.textDim)
        VGap(4.dp)
        Txt(text = value, sty = Sty.BODY_STRONG, color = Lux.text, align = TextAlign.Center)
        Txt(text = label, sty = Sty.CAPTION, color = Lux.textDim, align = TextAlign.Center)
    }
}

/** Haftalik tablo: gun adi, ikon (WxIcons), en dusuk / en yuksek. */
@Composable
internal fun WxWeekTable(days: List<DayForecast>, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Txt(text = t("weather.week"), sty = Sty.TITLE, color = Lux.text)
        VGap(8.dp)
        days.forEach { d ->
            WxDayRow(d)
            HairLine()
        }
    }
}

@Composable
internal fun WxDayRow(d: DayForecast) {
    val dayName = t("weather.day." + d.dayOfWeek.coerceIn(1, 7))
    Row(
        modifier = Modifier.fillMaxWidth().height(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Txt(text = dayName, sty = Sty.BODY, color = Lux.text, modifier = Modifier.weight(1f))
        LuxIcon(path = WxIcons.path(d.condition), size = 20.dp, tint = Lux.textDim)
        HGap(12.dp)
        Txt(
            text = "${d.minC}°",
            sty = Sty.BODY,
            color = Lux.textDim,
            modifier = Modifier.width(44.dp),
            align = TextAlign.End
        )
        Txt(
            text = "${d.maxC}°",
            sty = Sty.BODY_STRONG,
            color = Lux.text,
            modifier = Modifier.width(44.dp),
            align = TextAlign.End
        )
    }
}
