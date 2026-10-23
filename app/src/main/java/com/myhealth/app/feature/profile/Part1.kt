package com.myhealth.app.feature.profile

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.myhealth.app.util.CrashLog

/**
 * Avatar onbellegi: (profil foto surumu, piksel boyutu) anahtariyla en fazla 4 kucuk bitmap tutar.
 * Yukleme arka plan thread'inde yapilir; inSampleSize ile bellek kullanimi sinirlidir (OOM yok).
 */
internal object AvatarCache {
    private val map = LinkedHashMap<String, ImageBitmap>()

    @Synchronized
    fun peek(version: Int, px: Int): ImageBitmap? = map[version.toString() + "x" + px.toString()]

    @Synchronized
    private fun put(key: String, bmp: ImageBitmap) {
        map[key] = bmp
        while (map.size > 4) {
            val first = map.keys.firstOrNull() ?: break
            map.remove(first)
        }
    }

    fun load(ctx: Context, version: Int, px: Int): ImageBitmap? {
        peek(version, px)?.let { return it }
        return try {
            val f = ProfilePhoto.file(ctx) ?: return null
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(f.path, bounds)
            if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
            var sample = 1
            val shortest = minOf(bounds.outWidth, bounds.outHeight)
            while (shortest / (sample * 2) >= px) sample *= 2
            val opts = BitmapFactory.Options().apply { inSampleSize = sample }
            val bmp = BitmapFactory.decodeFile(f.path, opts) ?: return null
            val img = bmp.asImageBitmap()
            put(version.toString() + "x" + px.toString(), img)
            img
        } catch (e: OutOfMemoryError) {
            null
        } catch (e: Exception) {
            CrashLog.note(ctx, "avatar_load", e)
            null
        }
    }
}
