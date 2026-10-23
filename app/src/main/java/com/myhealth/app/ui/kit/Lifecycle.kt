package com.myhealth.app.ui.kit

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.myhealth.app.util.CrashLog

/** Context zincirinden Activity'yi bulur (yoksa null). */
fun Context.findActivity(): Activity? {
    var c: Context? = this
    var guard = 0
    while (c != null && guard < 16) {
        if (c is Activity) return c
        c = (c as? ContextWrapper)?.baseContext
        guard++
    }
    return null
}

/** Context zincirinden LifecycleOwner'i bulur (yoksa null). */
fun Context.findLifecycleOwner(): LifecycleOwner? {
    var c: Context? = this
    var guard = 0
    while (c != null && guard < 16) {
        if (c is LifecycleOwner) return c
        c = (c as? ContextWrapper)?.baseContext
        guard++
    }
    return null
}

/**
 * Ekran her on plana geldiginde (ilk acilis dahil) action calisir. Sistem ayarlarindan donuste izin
 * ve durum bilgisini tazelemek icin kullanilir. action icindeki hata uygulamayi dusurmez.
 */
@Composable
fun OnResume(action: () -> Unit) {
    val ctx = LocalContext.current
    val current by rememberUpdatedState(action)
    DisposableEffect(ctx) {
        val owner = ctx.findLifecycleOwner()
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                try {
                    current()
                } catch (e: Exception) {
                    CrashLog.note(ctx, "on_resume", e)
                }
            }
        }
        owner?.lifecycle?.addObserver(observer)
        onDispose { owner?.lifecycle?.removeObserver(observer) }
    }
}
