package com.myhealth.app.data

/**
 * Canli antrenman anlik durumu. WorkoutSession (feature.sports) yayinlar;
 * yapay zeka asistani, ana sayfa ve baloncuk okur.
 */
data class WorkoutState(
    val active: Boolean = false,
    val paused: Boolean = false,
    val sportId: String = "",
    val startMs: Long = 0L,
    val elapsedSec: Long = 0L,
    val distanceM: Double = 0.0,
    val kcal: Int = 0,
    val bpm: Int = 0,
    val speedKmh: Double = 0.0,
    val position: LatLng? = null
)
