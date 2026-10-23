package com.myhealth.app.feature.weather

import android.content.Context
import com.myhealth.app.data.Repo
import com.myhealth.app.util.CrashLog
import com.myhealth.app.util.Net
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Hava durumunu aginiz uzerinden getirip Repo'ya yazar. true = basarili. */
object WeatherRepo {
    suspend fun refresh(ctx: Context): Boolean {
        return try {
            val place = WxCity.activePlace(ctx) ?: return false
            if (!Net.isOnline(ctx)) return false
            val text = withContext(Dispatchers.IO) { Net.getText(WxApi.url(place.lat, place.lng)) } ?: return false
            val nowMs = System.currentTimeMillis()
            val data = WxApi.parse(text, place.name, nowMs) ?: return false
            Repo.setWeather(data.now, data.days)
            Repo.kvPutLong("weather_last_ok_ms", nowMs)
            Repo.kvPutInt("weather_rain3h", data.rain3h)
            Repo.kvPutJson("weather_hourly", WxApi.hoursToJson(data.hours))
            true
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            CrashLog.note(ctx, "weather_refresh", e)
            false
        }
    }
}
