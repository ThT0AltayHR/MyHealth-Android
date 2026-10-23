package com.myhealth.app.feature.weather

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.myhealth.app.ui.kit.GlassCard
import com.myhealth.app.ui.kit.HGap
import com.myhealth.app.ui.kit.Ic
import com.myhealth.app.ui.kit.LuxIcon
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt

/** Yagmur uyari esigi: onumuzdeki 3 saatte en yuksek yagis olasiligi (yuzde). Bu esigin ustunde panel cikar. */
internal const val WX_RAIN_ALERT_PCT = 60

/** Yakin saatlerde yagmur ihtimali yuksekse gosterilen cam uyari paneli (pct: 0-100). */
@Composable
internal fun WxRainPanel(pct: Int, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier.fillMaxWidth(), warm = true) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            LuxIcon(path = Ic.RAIN, size = 24.dp, tint = Lux.gold)
            HGap(12.dp)
            Txt(
                text = t("weather.rain_alert", "$pct%"),
                sty = Sty.BODY,
                color = Lux.text,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
