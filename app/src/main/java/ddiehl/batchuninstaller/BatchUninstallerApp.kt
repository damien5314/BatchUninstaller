package ddiehl.batchuninstaller;

import android.app.Application
import android.content.Context
import timber.log.Timber

public class BatchUninstallerApp : Application() {
    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        Timber.plant(Timber.DebugTree())
    }
}
