package com.myhealth.app.feature.sports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.myhealth.app.data.Sport
import com.myhealth.app.ui.kit.GlassCard
import com.myhealth.app.ui.kit.HGap
import com.myhealth.app.ui.kit.Ic
import com.myhealth.app.ui.kit.LuxIcon
import com.myhealth.app.ui.kit.SectionTitle
import com.myhealth.app.ui.kit.SmallTag
import com.myhealth.app.ui.kit.SportIcons
import com.myhealth.app.ui.kit.VGap
import com.myhealth.app.ui.theme.Dim
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt
import com.myhealth.app.ui.t

/** Katalog satiri: ikon, spor adi, kategori etiketi ve GPS rozeti. Dokununca canli ekrana gider. */
@Composable
internal fun CatalogSportRow(sport: Sport, onOpen: () -> Unit) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        radius = Dim.radiusSmall,
        onClick = onOpen,
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            LuxIcon(path = SportIcons.sport(sport.id), tint = Lux.gold, size = 26.dp)
            HGap(12.dp)
            Column(modifier = Modifier.weight(1f)) {
                Txt(text = t(sport.nameKey), sty = Sty.BODY_STRONG, maxLines = 1, overflow = TextOverflow.Ellipsis)
                VGap(4.dp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SmallTag(text = t(sport.category.nameKey))
                    if (sport.gps) {
                        HGap(6.dp)
                        SmallTag(text = t("sports.gps"))
                    }
                }
            }
            LuxIcon(path = Ic.FORWARD, tint = Lux.textDim, size = 20.dp, autoMirror = true)
        }
    }
}

/** Son yapilanlar seridi: en cok yapilan sporlar (ikon, ad ve kac kez yapildigi). */
@Composable
internal fun RecentSportsStrip(items: List<Pair<Sport, Int>>, onOpen: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionTitle(text = t("sports.recent"))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            for ((sport, count) in items) {
                GlassCard(
                    modifier = Modifier.weight(1f),
                    radius = Dim.radiusSmall,
                    onClick = { onOpen(sport.id) },
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 10.dp)
                ) {
                    LuxIcon(path = SportIcons.sport(sport.id), tint = Lux.gold, size = 22.dp)
                    VGap(6.dp)
                    Txt(
                        text = t(sport.nameKey),
                        sty = Sty.CAPTION,
                        align = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Txt(text = t("sports.times", count), sty = Sty.CAPTION, color = Lux.textDim, align = TextAlign.Center)
                }
            }
        }
    }
}

/** Canli antrenman karti: calisan oturuma geri donmek icin ("Devam et"). */
@Composable
internal fun LiveResumeCard(sport: Sport, onOpen: () -> Unit) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        warm = true,
        radius = Dim.radius,
        onClick = onOpen,
        contentPadding = PaddingValues(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            LuxIcon(path = SportIcons.sport(sport.id), tint = Lux.gold, size = 28.dp)
            HGap(12.dp)
            Column(modifier = Modifier.weight(1f)) {
                Txt(text = t("sports.resume"), sty = Sty.LABEL, color = Lux.gold)
                Txt(text = t(sport.nameKey), sty = Sty.BODY_STRONG, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            LuxIcon(path = Ic.FORWARD, tint = Lux.textDim, size = 20.dp, autoMirror = true)
        }
    }
}
