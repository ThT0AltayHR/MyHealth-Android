package com.myhealth.app

import android.app.Application
import com.myhealth.app.data.Repo
import com.myhealth.app.util.CrashLog

class MyHealthApp : Application() {
    override fun onCreate() {
        super.onCreate()
        CrashLog.install(this)
        Repo.init(this)
        try {
            Startup.onAppStart(this)
        } catch (e: Throwable) {
            CrashLog.note(this, "startup", e)
        }
    }
}
