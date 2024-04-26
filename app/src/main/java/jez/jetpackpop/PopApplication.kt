package jez.jetpackpop

import android.app.Application
import timber.log.Timber

class PopApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}
