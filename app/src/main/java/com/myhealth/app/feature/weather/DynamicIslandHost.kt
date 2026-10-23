package com.myhealth.app.feature.weather

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.myhealth.app.data.Repo
import com.myhealth.app.nav.LocalNav
import com.myhealth.app.nav.Route
import com.myhealth.app.ui.kit.GhostButton
import com.myhealth.app.ui.kit.GlassCard
import com.myhealth.app.ui.kit.HGap
import com.myhealth.app.ui.kit.PulseDot
import com.myhealth.app.ui.kit.VGap
import com.myhealth.app.ui.kit.pressable
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt
import com.myhealth.app.util.CrashLog
import com.myhealth.app.util.Net
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow

/** Ekranin ustunde (durum cubugunun altinda) duran dinamik ada. Kabuk her ekranin ustune koyar. */
@Composable
fun DynamicIslandHost(modifier: Modifier = Modifier) {
    val ctx = LocalContext.current
    val nav = LocalNav.current
    val weather by Repo.weather.collectAsState()
    val onlineFlow = remember { MutableStateFlow(Net.isOnline(ctx)) }
    val online by onlineFlow.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    var showHint by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(4000L)
        showHint = false
    }

    // DOGRULA: registerNetworkCallback icin ACCESS_NETWORK_STATE izni manifestte olmali (Net.isOnline ile ayni).
    DisposableEffect(Unit) {
        val app = ctx.applicationContext
        val cm = try {
            app.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        } catch (e: Exception) {
            CrashLog.note(app, "island_cm", e)
            null
        }
        val cb = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                onlineFlow.value = Net.isOnline(app)
            }

            override fun onLost(network: Network) {
                onlineFlow.value = Net.isOnline(app)
            }
        }
        try {
            val req = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
            cm?.registerNetworkCallback(req, cb)
        } catch (e: Exception) {
            CrashLog.note(app, "island_net_reg", e)
        }
        onDispose {
            try {
                cm?.unregisterNetworkCallback(cb)
            } catch (e: Exception) {
                CrashLog.note(app, "island_net_unreg", e)
            }
        }
    }

    val w = weather
    val compact = if (w != null) {
        w.tempC.toString() + "°  " + t(WxIcons.nameKey(w.condition))
    } else {
        t("island.no_data")
    }
    val pillText = if (showHint) t("island.hint") else compact

    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Lux.black)
                    .heightIn(min = 48.dp)
                    .pressable { expanded = !expanded }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PulseDot(color = if (online) Lux.green else Lux.red, dot = 8.dp)
                HGap(10.dp)
                Txt(
                    text = pillText,
                    sty = Sty.SMALL,
                    color = Lux.text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (expanded) {
                VGap(8.dp)
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    if (w != null) {
                        Txt(text = w.city, sty = Sty.BODY_STRONG, color = Lux.text)
                        Txt(text = "${w.tempC}°  " + t(WxIcons.nameKey(w.condition)), sty = Sty.H3, color = Lux.text)
                        Txt(
                            text = if (online) t("island.online") else t("island.offline"),
                            sty = Sty.SMALL,
                            color = if (online) Lux.green else Lux.amber
                        )
                    } else {
                        Txt(text = t("island.no_data"), sty = Sty.BODY, color = Lux.textDim)
                    }
                    VGap(10.dp)
                    GhostButton(
                        text = t("island.open"),
                        onClick = { nav.push(Route.Weather) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
