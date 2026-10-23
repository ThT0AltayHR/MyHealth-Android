package com.myhealth.app.feature.weather

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import com.myhealth.app.core.Json
import com.myhealth.app.core.dbl
import com.myhealth.app.core.list
import com.myhealth.app.core.str
import com.myhealth.app.data.LatLng
import com.myhealth.app.data.Repo
import com.myhealth.app.ui.tr
import com.myhealth.app.util.CrashLog
import com.myhealth.app.util.Net
import com.myhealth.app.util.Perms
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.net.URLEncoder
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.math.abs

/** Bir yer. detail: ikincil bilgi (il, ulke); yalniz arama sonucunda dolu. */
data class WxPlace(val name: String, val lat: Double, val lng: Double, val detail: String = "")

/** Sehir secimi: manuel sehir (kv weather_city), GPS konumu ve sehir adi cozumleme. */
object WxCity {
    private fun f4(v: Double): String = String.format(Locale.US, "%.4f", v)

    fun manual(): WxPlace? {
        val m = Repo.kvJson("weather_city") as? Map<*, *> ?: return null
        val lat = m.dbl("lat", Double.NaN)
        val lng = m.dbl("lng", Double.NaN)
        val name = m.str("name")
        return if (name.isNotBlank() && lat.isFinite() && lng.isFinite()) WxPlace(name, lat, lng) else null
    }

    fun saveManual(p: WxPlace) {
        Repo.kvPutJson("weather_city", mapOf("name" to p.name, "lat" to p.lat, "lng" to p.lng))
    }

    fun clearManual() {
        Repo.kvRemove("weather_city")
    }

    /** Hava icin etkin yer: once manuel sehir, yoksa konum. Yer yoksa null. */
    suspend fun activePlace(ctx: Context): WxPlace? {
        manual()?.let { return it }
        val loc = withContext(Dispatchers.IO) { lastLocation(ctx) } ?: return null
        Repo.setLocation(loc)
        val found = cachedAuto(loc.lat, loc.lng) ?: cityName(ctx, loc.lat, loc.lng)?.also { n ->
            Repo.kvPutJson("weather_city_auto", mapOf("name" to n, "lat" to loc.lat, "lng" to loc.lng))
        }
        return WxPlace(found ?: tr("weather.loc_label"), loc.lat, loc.lng)
    }

    /** Open-Meteo ile dunya geneli arama. Kisa sorgu bos liste doner. */
    suspend fun search(query: String): List<WxPlace> {
        val q = query.trim()
        if (q.length < 2) return emptyList()
        val url = "https://geocoding-api.open-meteo.com/v1/search?name=" + URLEncoder.encode(q, "UTF-8") +
            "&count=8&language=" + Locale.getDefault().language + "&format=json"
        val text = withContext(Dispatchers.IO) { Net.getText(url) } ?: return emptyList()
        val root = Json.parseOrNull(text) as? Map<*, *> ?: return emptyList()
        return root.list("results").mapNotNull { r ->
            val m = r as? Map<*, *> ?: return@mapNotNull null
            val lat = m.dbl("latitude", Double.NaN)
            val lng = m.dbl("longitude", Double.NaN)
            val name = m.str("name")
            if (name.isBlank() || !lat.isFinite() || !lng.isFinite()) return@mapNotNull null
            val detail = listOf(m.str("admin1"), m.str("country")).filter { it.isNotBlank() }.joinToString(", ")
            WxPlace(name, lat, lng, detail)
        }
    }

    /** Sehir adi: Geocoder -> BigDataCloud. Hicbiri olmazsa null. */
    private suspend fun cityName(ctx: Context, lat: Double, lng: Double): String? =
        geocoderName(ctx, lat, lng) ?: bigDataName(ctx, lat, lng)

    @Suppress("MissingPermission")
    private fun lastLocation(ctx: Context): LatLng? {
        if (!Perms.hasLocation(ctx)) return null
        return try {
            val lm = ctx.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return null
            var best: LatLng? = null
            for (p in listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER, LocationManager.PASSIVE_PROVIDER)) {
                val l: Location = lm.getLastKnownLocation(p) ?: continue
                if (l.latitude.isFinite() && l.longitude.isFinite()) {
                    best = LatLng(l.latitude, l.longitude)
                    break
                }
            }
            best
        } catch (e: Exception) {
            CrashLog.note(ctx, "weather_loc", e)
            null
        }
    }

    /** Yakin konum icin onbellekteki sehir adi (0.05 derece icinde). */
    private fun cachedAuto(lat: Double, lng: Double): String? {
        val m = Repo.kvJson("weather_city_auto") as? Map<*, *> ?: return null
        val cl = m.dbl("lat", Double.NaN)
        val cg = m.dbl("lng", Double.NaN)
        val name = m.str("name")
        if (name.isBlank() || !cl.isFinite() || !cg.isFinite()) return null
        return if (abs(cl - lat) < 0.05 && abs(cg - lng) < 0.05) name else null
    }

    @Suppress("NewApi", "DEPRECATION")
    private suspend fun geocoderName(ctx: Context, lat: Double, lng: Double): String? {
        return try {
            if (!Geocoder.isPresent()) return null
            val g = Geocoder(ctx, Locale.getDefault())
            val list: List<Address>? = if (Build.VERSION.SDK_INT >= 33) {
                withTimeoutOrNull(8_000L) {
                    suspendCancellableCoroutine<List<Address>?> { cont ->
                        g.getFromLocation(lat, lng, 1, object : Geocoder.GeocodeListener {
                            override fun onGeocode(addresses: MutableList<Address>) {
                                if (cont.isActive) cont.resume(addresses.toList())
                            }

                            override fun onError(errorMessage: String?) {
                                if (cont.isActive) cont.resume(null)
                            }
                        })
                    }
                }
            } else {
                withContext(Dispatchers.IO) { g.getFromLocation(lat, lng, 1) }
            }
            val a = list?.firstOrNull() ?: return null
            listOf(a.locality, a.subAdminArea, a.adminArea).firstOrNull { !it.isNullOrBlank() }?.trim()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            CrashLog.note(ctx, "weather_geocoder", e)
            null
        }
    }

    private suspend fun bigDataName(ctx: Context, lat: Double, lng: Double): String? {
        return try {
            val url = "https://api.bigdatacloud.net/data/reverse-geocode-client?latitude=" + f4(lat) +
                "&longitude=" + f4(lng) + "&localityLanguage=" + Locale.getDefault().language
            val text = withContext(Dispatchers.IO) { Net.getText(url) } ?: return null
            val root = Json.parseOrNull(text) as? Map<*, *> ?: return null
            listOf(root.str("city"), root.str("locality"), root.str("principalSubdivision"))
                .firstOrNull { it.isNotBlank() }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            CrashLog.note(ctx, "weather_bigdata", e)
            null
        }
    }
}
