package com.myhealth.app.feature.music

import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

/** Muzik calma servisi (bildirim ve kilit ekrani kontrolleri). TASLAK. */
class MusicService : MediaSessionService() {
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = null
}
