package com.myhealth.app.feature.alarm

import android.app.Service
import android.content.Intent
import android.os.IBinder

/** Alarm sesini ve titresimi calan on plan servisi. TASLAK. */
class AlarmRingService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null
}
