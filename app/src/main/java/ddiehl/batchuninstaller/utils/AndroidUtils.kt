package ddiehl.batchuninstaller.utils

import android.content.Intent
import android.net.Uri
import timber.log.Timber

public fun getUninstallIntent(packageName: String, returnResult: Boolean): Intent {
  val intent = Intent(
      Intent.ACTION_UNINSTALL_PACKAGE,
      Uri.parse("package:" + packageName));
  intent.putExtra(Intent.EXTRA_RETURN_RESULT, returnResult)
  return intent
}

public fun logAllExtras(data: Intent) {
  data.extras.keySet().forEach {
    Timber.d("EXTRA -> " + it)
  }
}