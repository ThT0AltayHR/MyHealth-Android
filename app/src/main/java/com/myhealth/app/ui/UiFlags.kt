package com.myhealth.app.ui

import kotlinx.coroutines.flow.MutableStateFlow

/** Ozellikler arasi kucuk arayuz bayraklari (kabuk bunlari okur). */
object UiFlags {
    /** Mini muzik oynatici ekranda gorunuyorsa true (MiniPlayerHost gunceller). Icerik alt boslugu buna gore artar. */
    val miniPlayerVisible = MutableStateFlow(false)
}
