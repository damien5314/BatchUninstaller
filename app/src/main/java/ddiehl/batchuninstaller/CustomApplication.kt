package ddiehl.batchuninstaller;

import android.app.Application
import timber.log.Timber

public class CustomApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    Timber.plant(Timber.DebugTree())
  }
}
