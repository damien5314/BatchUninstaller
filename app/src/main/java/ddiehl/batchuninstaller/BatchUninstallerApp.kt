package ddiehl.batchuninstaller

import android.app.Application
import timber.log.Timber

class BatchUninstallerApp : Application() {

    companion object {
        lateinit var app: BatchUninstallerApp
    }

    override fun onCreate() {
        super.onCreate()
        app = this

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
