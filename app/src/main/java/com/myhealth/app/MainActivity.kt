package com.myhealth.app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.myhealth.app.nav.DeepLink
import com.myhealth.app.ui.AppRoot
import com.myhealth.app.util.CrashLog
import com.myhealth.app.util.Notif

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        readRoute(intent)
        setContent { AppRoot() }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        readRoute(intent)
    }

    override fun onResume() {
        super.onResume()
        try {
            Startup.onForeground(this)
        } catch (e: Throwable) {
            CrashLog.note(this, "foreground", e)
        }
    }

    private fun readRoute(i: Intent?) {
        val key = i?.getStringExtra(Notif.EXTRA_ROUTE)
        if (key != null) DeepLink.pending.value = key
    }
}
