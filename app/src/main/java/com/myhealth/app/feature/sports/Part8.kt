package com.myhealth.app.feature.sports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.myhealth.app.core.Fmt
import com.myhealth.app.ui.kit.GhostButton
import com.myhealth.app.ui.kit.GlassCard
import com.myhealth.app.ui.kit.PrimaryButton
import com.myhealth.app.ui.kit.VGap
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt

/** Surec olumunden kalan yarim antrenman: devam et / sil. */
@Composable
internal fun SportLiveBackupCard(b: WkBackup, onRestore: () -> Unit, onDiscard: () -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth(), warm = true) {
        Txt(t("sportlive.backup.title"), sty = Sty.H3, color = Lux.text)
        VGap(6.dp)
        Txt(
            t("sportlive.backup.msg", Fmt.clock(b.elapsedSec), Fmt.oneDecimal(b.distanceM / 1000.0)),
            sty = Sty.BODY,
            color = Lux.textDim
        )
        VGap(12.dp)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PrimaryButton(
                text = t("sportlive.backup.restore"),
                onClick = onRestore,
                modifier = Modifier.weight(1f)
            )
            GhostButton(
                text = t("sportlive.backup.discard"),
                onClick = onDiscard,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/** Bilgi karti; actionText verilirse altinda ikincil dugme cikar. */
@Composable
internal fun SportLiveNotice(text: String, actionText: String?, onAction: () -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Txt(text, sty = Sty.BODY, color = Lux.textDim)
        if (actionText != null) {
            VGap(10.dp)
            GhostButton(text = actionText, onClick = onAction, modifier = Modifier.fillMaxWidth())
        }
    }
}
