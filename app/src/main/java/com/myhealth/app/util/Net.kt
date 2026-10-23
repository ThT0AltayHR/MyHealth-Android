package com.myhealth.app.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/** Ag yardimcilari. Hicbiri istisna firlatmaz. */
object Net {
    /** Cihazin internete cikabilen bir agi var mi (Wi-Fi, mobil, ethernet). */
    fun isOnline(ctx: Context): Boolean = try {
        val cm = ctx.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val net = cm?.activeNetwork
        val caps = if (net != null) cm.getNetworkCapabilities(net) else null
        caps != null && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    } catch (e: Exception) {
        false
    }

    /** Uygulama genelinde paylasilan OkHttp istemcisi (baglanti havuzu ortak). */
    val http: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(12, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    /** Basit GET: govde metni veya hata durumunda null. Arka plan thread'inde cagirin. */
    fun getText(url: String, headers: Map<String, String> = emptyMap()): String? = try {
        val b = okhttp3.Request.Builder().url(url)
        b.header("User-Agent", "MyHealth/1.0 (Android)")
        for ((k, v) in headers) b.header(k, v)
        http.newCall(b.build()).execute().use { r -> if (r.isSuccessful) r.body?.string() else null }
    } catch (e: Exception) {
        null
    }
}
