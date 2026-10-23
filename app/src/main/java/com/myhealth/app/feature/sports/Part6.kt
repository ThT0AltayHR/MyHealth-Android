package com.myhealth.app.feature.sports

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.myhealth.app.core.Fmt
import com.myhealth.app.data.Repo
import com.myhealth.app.data.WeatherCondition
import com.myhealth.app.data.WorkoutRecord
import com.myhealth.app.ui.kit.GlassCard
import com.myhealth.app.ui.kit.Ic
import com.myhealth.app.ui.kit.LuxIcon
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt
import com.myhealth.app.ui.tr
import java.time.Instant
import java.time.ZoneId

/** Son cekilen hava karti; cevrimdisiyken de son guncelleme saatiyle gorunur. */
@Composable
internal fun SportLiveWeather() {
    val w by Repo.weather.collectAsState()
    val now = w
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        if (now == null) {
            Txt(t("sportlive.wx.none"), sty = Sty.BODY, color = Lux.textDim)
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                LuxIcon(wxIcon(now.condition), size = 30.dp, tint = Lux.gold)
                Column(Modifier.padding(start = 12.dp)) {
                    Txt(
                        t("sportlive.wx.line", now.city, now.tempC, t(wxKey(now.condition))),
                        sty = Sty.BODY_STRONG,
                        color = Lux.text
                    )
                    Txt(t("sportlive.wx.feels", now.feelsC), sty = Sty.CAPTION, color = Lux.textDim)
                    Txt(t("sportlive.wx.updated", clockOf(now.updatedMs)), sty = Sty.CAPTION, color = Lux.textFaint)
                }
            }
        }
    }
}

/** Motivasyon karti; n = 0 ise hic cizilmez. Adli mesajlar WkMotivation.next(.., hasName) ile secilir. */
@Composable
internal fun SportLiveMotiv(n: Int, name: String) {
    if (n <= 0) return
    GlassCard(modifier = Modifier.fillMaxWidth(), warm = true) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            LuxIcon(Ic.BOLT, size = 22.dp, tint = Lux.gold)
            Txt(
                t(WkMotivation.key(n), name), // DOGRULA: adsiz anahtarda fazla arguman yok sayilmali (t vararg)
                modifier = Modifier.weight(1f).padding(start = 10.dp),
                sty = Sty.BODY,
                color = Lux.text
            )
        }
    }
}

/** Bitis ozeti satirlari (Composable disi oldugu icin tr kullanir). */
internal fun sportLiveSummary(rec: WorkoutRecord): List<Pair<String, String>> {
    val out = ArrayList<Pair<String, String>>()
    out.add(tr("sportlive.sum.time") to Fmt.clock(rec.durationSec.toLong()))
    out.add(tr("sportlive.lbl.kcal") to (rec.calories.toString() + " " + tr("sportlive.unit.kcal")))
    if (rec.distanceM > 0.0) {
        out.add(tr("sportlive.lbl.dist") to (Fmt.oneDecimal(rec.distanceM / 1000.0) + " " + tr("sportlive.unit.km")))
        if (rec.durationSec > 0) {
            val kmh = rec.distanceM / 1000.0 / (rec.durationSec / 3600.0)
            out.add(tr("sportlive.lbl.speed") to (Fmt.oneDecimal(kmh) + " " + tr("sportlive.unit.kmh")))
        }
    }
    return out
}

private fun wxKey(c: WeatherCondition): String = when (c) {
    WeatherCondition.SUNNY -> "sportlive.wx.sun"
    WeatherCondition.CLOUDY -> "sportlive.wx.cloud"
    WeatherCondition.LIGHT_RAIN -> "sportlive.wx.lrain"
    WeatherCondition.HEAVY_RAIN -> "sportlive.wx.hrain"
    WeatherCondition.STORM -> "sportlive.wx.storm"
    WeatherCondition.SNOW -> "sportlive.wx.snow"
}

private fun wxIcon(c: WeatherCondition): String = when (c) {
    WeatherCondition.SUNNY -> Ic.SUN
    WeatherCondition.CLOUDY -> Ic.CLOUD
    WeatherCondition.LIGHT_RAIN, WeatherCondition.HEAVY_RAIN -> Ic.RAIN
    WeatherCondition.STORM -> Ic.STORM
    WeatherCondition.SNOW -> Ic.SNOW
}

// DOGRULA: java.time (Instant) minSdk >= 26 varsayimi; Fmt de java.time kullanir
private fun clockOf(ms: Long): String = try {
    val z = Instant.ofEpochMilli(ms).atZone(ZoneId.systemDefault())
    Fmt.hhmm(z.hour * 60 + z.minute)
} catch (e: Exception) {
    "--:--"
}
