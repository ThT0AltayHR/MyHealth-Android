package com.myhealth.app.feature.music

import android.content.Context
import com.myhealth.app.data.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/*
 * MUZIK MODULU ARAYUZLERI (SABIT SOZLESME).
 *
 * Motor ajani (MusicEngine) bu nesnelerin GOVDELERINI yazar; arayuz ajani (MusicUi) yalnizca bu
 * imzalari kullanarak ekranlari yazar. Imzalar iki taraf icin de degistirilemez; eklenecek yeni
 * bir ihtiyac olursa motor ajani KENDI dosyalarinda yeni uye ekleyebilir ama mevcutlari bozmaz.
 *
 * Genel kurallar: tum cagrilar ana thread'den yapilir, hicbiri istisna firlatmaz, izin yoksa
 * veya hata varsa sessizce durum bayraklarini gunceller.
 * Parca kimlikleri: yerel "l:<MediaStore id>", cevrimici "o:<kaynak>:<id>".
 */

enum class RepeatMode { OFF, ALL, ONE }

/** Oynatici anlik durumu. Konum burada degil, positionMs akisindadir (sik guncellendigi icin). */
data class PlayerState(
    val queue: List<Track> = emptyList(),
    val index: Int = -1,
    val isPlaying: Boolean = false,
    val buffering: Boolean = false,
    val durationMs: Long = 0L,
    val shuffle: Boolean = false,
    val repeat: RepeatMode = RepeatMode.OFF,
    /** Kisa hata kodu ("offline", "unplayable", "no_permission" ...); kullaniciya t("music.err.<kod>") ile gosterilir. */
    val error: String? = null
) {
    val current: Track? get() = queue.getOrNull(index)
}

/** Tek paylasimli oynatici (ExoPlayer + MediaSession). */
object MusicController {
    private val _state = MutableStateFlow(PlayerState())
    val state: StateFlow<PlayerState> = _state.asStateFlow()

    private val _position = MutableStateFlow(0L)

    /** Anlik konum (ms); calarken ~4 kez/sn guncellenir. */
    val positionMs: StateFlow<Long> = _position.asStateFlow()

    /** Kuyrugu degistirip startIndex'ten calmaya baslar. shuffle=true ise karisik sirada. */
    fun playQueue(ctx: Context, tracks: List<Track>, startIndex: Int = 0, shuffle: Boolean = false) {}

    /** Mevcut kuyrukta verilen siradaki parcayi calar. */
    fun playIndex(ctx: Context, index: Int) {}

    fun toggle(ctx: Context) {}
    fun next(ctx: Context) {}
    fun previous(ctx: Context) {}
    fun seekTo(ms: Long) {}
    fun setShuffle(on: Boolean) {}

    /** OFF -> ALL -> ONE -> OFF */
    fun cycleRepeat() {}

    /** Parcayi kuyrugun sonuna ekler (bos kuyruktaysa calmaya baslamaz, yalnizca ekler). */
    fun enqueue(ctx: Context, track: Track) {}

    fun removeAt(index: Int) {}

    /** Calmayi durdurur, kuyrugu temizler ve servisi kapatir. */
    fun stop(ctx: Context) {}
}

data class LibraryState(
    val tracks: List<Track> = emptyList(),
    val scanning: Boolean = false,
    val permissionMissing: Boolean = false,
    val scannedAtMs: Long = 0L
)

/** Cihazdaki muzik dosyalari (MediaStore). Yeni dosyalar canli taranir. */
object MusicLibrary {
    private val _state = MutableStateFlow(LibraryState())
    val state: StateFlow<LibraryState> = _state.asStateFlow()

    /** Arka planda tarar; izin yoksa permissionMissing=true yapar. */
    fun rescan(ctx: Context) {}

    /** ContentObserver ile degisiklikleri izler (cagrilar tekrarlanabilir, guvenlidir). */
    fun startWatching(ctx: Context) {}

    fun stopWatching(ctx: Context) {}
}

enum class BtStatus { UNSUPPORTED, NO_PERMISSION, OFF, DISCONNECTED, CONNECTED }

data class BtState(val status: BtStatus = BtStatus.DISCONNECTED, val deviceName: String = "")

/** Bluetooth ses cihazi (A2DP/kulaklik) baglanti durumu. */
object BluetoothMonitor {
    private val _state = MutableStateFlow(BtState())
    val state: StateFlow<BtState> = _state.asStateFlow()

    /** Yayin alicisini kaydeder ve ilk durumu okur (tekrar cagrilar guvenli). */
    fun start(ctx: Context) {}

    fun stop(ctx: Context) {}

    fun refresh(ctx: Context) {}
}

/** error: kisa kod ("offline", "failed", "unavailable", "empty_query"); null = basarili. */
data class OnlineResult(val tracks: List<Track> = emptyList(), val error: String? = null)

/**
 * Cevrimici arama. Kaynak AppSettings.musicSource ile secilir: "audius" (varsayilan, yasal) veya
 * "youtube" (deneysel; Piped/Invidious benzeri acik API ornekleri). Hicbir zaman istisna firlatmaz.
 */
object OnlineMusic {
    suspend fun search(ctx: Context, query: String): OnlineResult = OnlineResult(error = "unavailable")
}
