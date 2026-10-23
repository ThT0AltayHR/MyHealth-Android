package com.myhealth.app.feature.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.myhealth.app.data.LatLng
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt

enum class MapStatus { LOADING, READY, UNAVAILABLE }

/**
 * Koyu + altin temali harita (kendi yazdigimiz karo haritasi). SABIT SOZLESME.
 *
 * - route: izlenen yol (bos olabilir); current: anlik konum (null olabilir).
 * - follow=true: harita current'i izler. interactive=true: surukle/yakinlastir (gecmis ekraninda).
 * - Internet/konum/karo yoksa harita yerine t("map.unavailable") metnini gosterir
 *   ("harita bilgisi alinamadi lutfen konumunuzu etkinlestirdiginizden veya internet baglantinizin
 *   acik oldugundan emin olun"). Daha once indirilen karolar internet yokken de gosterilir.
 * TASLAK: Harita ajani govdeyi yazar; imza SABITTIR.
 */
@Composable
fun RouteMap(
    route: List<LatLng>,
    current: LatLng?,
    modifier: Modifier = Modifier,
    follow: Boolean = true,
    interactive: Boolean = false,
    onStatus: (MapStatus) -> Unit = {}
) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Txt("map", sty = Sty.SMALL, color = Lux.textDim)
    }
}
