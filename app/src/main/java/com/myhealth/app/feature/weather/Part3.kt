package com.myhealth.app.feature.weather

import android.content.Context
import android.net.Uri
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.myhealth.app.R
import com.myhealth.app.data.WeatherCondition
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.util.CrashLog

// DOGRULA: ExoPlayer bagimliligi dogrulanamadi; yeni bagimlilik eklenmemesi icin framework VideoView kullanildi.

/** Hava kosuluna gore dongulu (raw) video kimligi. */
internal fun wxVideoRes(c: WeatherCondition): Int = when (c) {
    WeatherCondition.SUNNY -> R.raw.weather_sunny
    WeatherCondition.CLOUDY -> R.raw.weather_cloudy
    WeatherCondition.LIGHT_RAIN -> R.raw.weather_light_rain
    WeatherCondition.HEAVY_RAIN -> R.raw.weather_heavy_rain
    WeatherCondition.STORM -> R.raw.weather_storm
    WeatherCondition.SNOW -> R.raw.weather_snow
}

/** Sessiz, dongulu video. Acilamazsa onFail (ana thread'de) cagrilir; cagiran degrade zemin gosterir. */
internal fun wxCreateVideo(c: Context, resId: Int, onFail: () -> Unit): View {
    val frame = FrameLayout(c)
    frame.clipChildren = true
    frame.setBackgroundColor(android.graphics.Color.BLACK)
    val v = VideoView(c)
    frame.addView(
        v,
        FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT,
            Gravity.CENTER
        )
    )
    try {
        v.setOnErrorListener { _, _, _ ->
            onFail()
            true
        }
        v.setOnPreparedListener { mp ->
            try {
                mp.isLooping = true
                mp.setVolume(0f, 0f)
                frame.post { wxCover(v, frame) }
            } catch (e: Exception) {
                CrashLog.note(c, "weather_video_prep", e)
            }
        }
        v.setVideoURI(Uri.parse("android.resource://" + c.packageName + "/" + resId))
        v.start()
    } catch (e: Exception) {
        CrashLog.note(c, "weather_video", e)
        frame.post { onFail() }
    }
    return frame
}

/** Videoyu kapak (cover) olcegine getirir: kare dolar, tasan kisim kirpilir. */
internal fun wxCover(v: View, frame: View) {
    val w = v.width.toFloat()
    val h = v.height.toFloat()
    val cw = frame.width.toFloat()
    val ch = frame.height.toFloat()
    if (w <= 0f || h <= 0f || cw <= 0f || ch <= 0f) return
    val f = maxOf(cw / w, ch / h)
    if (f.isNaN() || f.isInfinite()) return
    v.scaleX = f
    v.scaleY = f
}

/** Oynaticiyi birakir (MediaPlayer kaynagi serbest kalir). */
internal fun wxStopVideo(c: Context, frame: View?) {
    try {
        val f = frame as? FrameLayout ?: return
        for (i in 0 until f.childCount) {
            (f.getChildAt(i) as? VideoView)?.stopPlayback()
        }
    } catch (e: Exception) {
        CrashLog.note(c, "weather_video_stop", e)
    }
}

/** Hava durumu arka plani: dongulu video, ustunde hafif karartma. Video acilmazsa degrade zemin. */
@Composable
internal fun WxVideo(cond: WeatherCondition, modifier: Modifier = Modifier) {
    val ctx = LocalContext.current
    var failed by remember { mutableStateOf(false) }
    Box(modifier = modifier.background(Lux.black)) {
        if (failed) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(Lux.brownTop, Lux.black)))
            )
        } else {
            key(cond) {
                val holder = remember { arrayOfNulls<View>(1) }
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { c ->
                        wxCreateVideo(c, wxVideoRes(cond)) { failed = true }.also { holder[0] = it }
                    }
                )
                DisposableEffect(Unit) {
                    onDispose {
                        wxStopVideo(ctx, holder[0])
                        holder[0] = null
                    }
                }
            }
        }
        Box(modifier = Modifier.fillMaxSize().background(Lux.black.copy(alpha = 0.35f)))
    }
}
