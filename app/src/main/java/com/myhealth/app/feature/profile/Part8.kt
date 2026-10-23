package com.myhealth.app.feature.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.myhealth.app.ui.kit.Toasts
import com.myhealth.app.ui.tr
import com.myhealth.app.util.CrashLog
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Profil ekraninin arka plan islemi: IO'da calisir, hatayi CrashLog'a yazar; iptal isaretini yeniden firlatir. */
internal fun profileIo(scope: CoroutineScope, ctx: Context, tag: String, block: () -> Unit) {
    scope.launch {
        try {
            withContext(Dispatchers.IO) { block() }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            CrashLog.note(ctx, tag, e)
        }
    }
}

/** Aile hekimini ACTION_DIAL ile acar (numara cevrilir, dogrudan arama yapilmaz). Numara yoksa bilgi verir. */
internal fun dialNumber(ctx: Context, phone: String) {
    val digits = phone.filter { it.isDigit() || it == '+' }
    if (digits.isEmpty()) {
        Toasts.show(tr("profile.doc.no_phone"))
        return
    }
    try {
        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", digits, null))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ctx.startActivity(intent)
    } catch (e: Exception) {
        CrashLog.note(ctx, "doctor_dial", e)
    }
}
