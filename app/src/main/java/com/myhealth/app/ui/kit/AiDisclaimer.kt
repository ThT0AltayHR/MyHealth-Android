package com.myhealth.app.ui.kit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt

/**
 * Yapay zeka metinlerinin ALTINA konan tibbi isaret (asa uzerinde iki yilan) ve soluk altin uyari.
 * Asistan sohbeti, gunluk ipucu, egzersiz sonu yorumu, istatistik/profil yapay zeka panelleri ve ogun
 * onerileri dahil HER yapay zeka metninin altinda gosterilir. compact=true: kisa metin (dar kartlar).
 * Entegratorun dosyasi: ajanlar kullanir, DEGISTIRMEZ.
 */
@Composable
fun AiDisclaimer(modifier: Modifier = Modifier, compact: Boolean = false) {
    Row(
        modifier = modifier.fillMaxWidth().padding(top = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        LuxIcon(
            path = MedIcons.CADUCEUS,
            modifier = Modifier.padding(top = 2.dp),
            tint = Lux.gold.copy(alpha = 0.72f),
            size = 24.dp,
            stroke = 1.4f
        )
        Txt(
            text = t(if (compact) "ai.disclaimer.short" else "ai.disclaimer"),
            modifier = Modifier.weight(1f),
            sty = Sty.CAPTION,
            color = Lux.goldSoft.copy(alpha = 0.62f)
        )
    }
}
