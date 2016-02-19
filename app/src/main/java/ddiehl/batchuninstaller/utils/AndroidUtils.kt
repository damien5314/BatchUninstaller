package ddiehl.batchuninstaller.utils

import android.content.Intent
import android.content.pm.PackageStats
import android.net.Uri
import android.support.v7.view.ContextThemeWrapper
import android.support.v7.widget.Toolbar
import android.view.ViewManager
import ddiehl.batchuninstaller.R
import org.jetbrains.anko.custom.ankoView
import timber.log.Timber

fun getUninstallIntent(packageName: String, returnResult: Boolean): Intent {
  val intent = Intent(
      Intent.ACTION_UNINSTALL_PACKAGE,
      Uri.parse("package:" + packageName));
  intent.putExtra(Intent.EXTRA_RETURN_RESULT, returnResult)
  return intent
}

fun logAllExtras(data: Intent) {
  data.extras.keySet().forEach {
    Timber.d("EXTRA -> " + it)
  }
}

inline fun ViewManager.toolbar(styleRes: Int, init: Toolbar.() -> Unit): Toolbar {
  return ankoView({
    if (styleRes == 0) Toolbar(it)
    else Toolbar(ContextThemeWrapper(it, styleRes), null, R.attr.toolbarStyle)
  }) { init() }
}

fun PackageStats.getTotalSize(): Long {
  return cacheSize + codeSize + dataSize +
      externalCacheSize + externalCodeSize + externalDataSize+ externalMediaSize + externalObbSize
}
