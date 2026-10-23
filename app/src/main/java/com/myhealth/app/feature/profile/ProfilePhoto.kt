package com.myhealth.app.feature.profile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import com.myhealth.app.util.CrashLog
import java.io.FileOutputStream
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

/**
 * PROFIL FOTOGRAFI (SABIT SOZLESME; govdeyi Profil ajani doldurur).
 * Dosya: filesDir/profile/avatar.jpg (en uzun kenar <= 640 px, JPEG). version: her degisiklikte artar
 * (Avatar yeniden yukler). Hicbir islev istisna firlatmaz.
 */
object ProfilePhoto {
    private val _version = MutableStateFlow(0)
    val version: StateFlow<Int> = _version.asStateFlow()

    /** Foto dosyasi (varsa); yoksa null. */
    private const val MAX_EDGE = 640
    private const val QUALITY = 88

    private fun dirOf(ctx: Context): File = File(ctx.filesDir, "profile")

    private fun targetOf(ctx: Context): File = File(dirOf(ctx), "avatar.jpg")

    fun file(ctx: Context): File? = try {
        val f = targetOf(ctx)
        if (f.isFile && f.length() > 0L) f else null
    } catch (e: Exception) {
        null
    }

    /** ImageDecoder (API 28+) EXIF yonunu kendisi uygular; yoksa BitmapFactory + ExifInterface. */
    private fun decodeUri(ctx: Context, uri: Uri): Bitmap? {
        if (Build.VERSION.SDK_INT >= 28) {
            try {
                val src = ImageDecoder.createSource(ctx.contentResolver, uri)
                return ImageDecoder.decodeBitmap(src) { decoder, info, _ ->
                    val w = info.size.width
                    val h = info.size.height
                    val longest = maxOf(w, h)
                    if (w > 0 && h > 0 && longest > MAX_EDGE) {
                        val s = MAX_EDGE.toFloat() / longest.toFloat()
                        decoder.setTargetSize(maxOf(1, Math.round(w * s)), maxOf(1, Math.round(h * s)))
                    }
                    decoder.setAllocator(ImageDecoder.ALLOCATOR_SOFTWARE)
                }
            } catch (e: Exception) {
                // desteklenmeyen bicim: BitmapFactory yoluna dus
            }
        }
        return decodeLegacy(ctx, uri)
    }

    private fun decodeLegacy(ctx: Context, uri: Uri): Bitmap? {
        val cr = ctx.contentResolver
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        cr.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
        var sample = 1
        val longest = maxOf(bounds.outWidth, bounds.outHeight)
        while (longest / (sample * 2) >= MAX_EDGE) sample *= 2
        val opts = BitmapFactory.Options().apply { inSampleSize = sample }
        val raw = cr.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opts) } ?: return null
        val orientation = try {
            // DOGRULA: android.media.ExifInterface(InputStream) API 24+ (minSdk >= 24 varsayildi)
            cr.openInputStream(uri)?.use {
                ExifInterface(it).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            } ?: ExifInterface.ORIENTATION_NORMAL
        } catch (e: Exception) {
            ExifInterface.ORIENTATION_NORMAL
        }
        return orient(raw, orientation)
    }

    private fun orient(src: Bitmap, orientation: Int): Bitmap {
        val m = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> m.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> m.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> m.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> m.postScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> m.postScale(1f, -1f)
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                m.postRotate(90f)
                m.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                m.postRotate(-90f)
                m.postScale(-1f, 1f)
            }
            else -> return src
        }
        val out = Bitmap.createBitmap(src, 0, 0, src.width, src.height, m, true)
        if (out !== src) src.recycle()
        return out
    }

    private fun scaleDown(src: Bitmap): Bitmap {
        val longest = maxOf(src.width, src.height)
        if (longest <= MAX_EDGE || longest <= 0) return src
        val s = MAX_EDGE.toFloat() / longest.toFloat()
        val out = Bitmap.createScaledBitmap(
            src, maxOf(1, Math.round(src.width * s)), maxOf(1, Math.round(src.height * s)), true
        )
        if (out !== src) src.recycle()
        return out
    }

    private fun writeJpeg(ctx: Context, bmp: Bitmap): Boolean {
        val dir = dirOf(ctx)
        if (!dir.exists() && !dir.mkdirs() && !dir.exists()) return false
        val tmp = File(dir, "avatar.tmp")
        val ok = FileOutputStream(tmp).use { bmp.compress(Bitmap.CompressFormat.JPEG, QUALITY, it) }
        if (!ok || tmp.length() <= 0L) {
            tmp.delete()
            return false
        }
        val dst = targetOf(ctx)
        if (dst.exists()) dst.delete()
        if (!tmp.renameTo(dst)) {
            tmp.delete()
            return false
        }
        _version.value = _version.value + 1
        return true
    }

    /** Galeri URI'sinden okuyup kucultur ve kaydeder (arka plan thread'inde cagirin). */
    fun saveFrom(ctx: Context, uri: Uri): Boolean {
        var bmp: Bitmap? = null
        return try {
            val decoded = decodeUri(ctx, uri) ?: return false
            bmp = decoded
            val fitted = scaleDown(decoded)
            bmp = fitted
            writeJpeg(ctx, fitted)
        } catch (e: OutOfMemoryError) {
            false
        } catch (e: Exception) {
            CrashLog.note(ctx, "profile_photo_save", e)
            false
        } finally {
            try {
                bmp?.recycle()
            } catch (e: Exception) {
                // yoksay
            }
        }
    }

    /** Yedekten geri yuklemede: ham JPEG baytlarini yazar (arka plan thread'inde cagirin). */
    fun saveBytes(ctx: Context, bytes: ByteArray): Boolean {
        if (bytes.isEmpty()) return false
        var bmp: Bitmap? = null
        return try {
            val decoded = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return false
            bmp = decoded
            val fitted = scaleDown(decoded)
            bmp = fitted
            writeJpeg(ctx, fitted)
        } catch (e: OutOfMemoryError) {
            false
        } catch (e: Exception) {
            CrashLog.note(ctx, "profile_photo_bytes", e)
            false
        } finally {
            try {
                bmp?.recycle()
            } catch (e: Exception) {
                // yoksay
            }
        }
    }

    fun remove(ctx: Context) {
        try {
            targetOf(ctx).delete()
        } catch (e: Exception) {
            CrashLog.note(ctx, "profile_photo_remove", e)
        }
        _version.value = _version.value + 1
    }
}
