package jez.jetpackpop

import android.app.Application
import timber.log.Timber

class PopApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
