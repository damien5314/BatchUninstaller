package ddiehl.batchuninstaller.utils

import android.content.Intent
import android.content.pm.IPackageStatsObserver
import android.content.pm.PackageManager
import android.content.pm.PackageStats
import android.net.Uri
import android.os.Build
import android.os.UserHandle
import timber.log.Timber
import java.lang.reflect.Method

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

//inline fun ViewManager.toolbar(styleRes: Int, init: Toolbar.() -> Unit): Toolbar {
//    return ankoView({
//        if (styleRes == 0) Toolbar(it)
//        else Toolbar(ContextThemeWrapper(it, styleRes), null, R.attr.toolbarStyle)
//    }) { init() }
//}

fun PackageStats.getTotalSize(): Long {
    return cacheSize + codeSize + dataSize +
            externalCacheSize + externalCodeSize + externalDataSize + externalMediaSize + externalObbSize
}

fun getAppPackageSize(pm: PackageManager, packageName: String, observer: IPackageStatsObserver) {
    try {
        val clz = pm.javaClass
        if (Build.VERSION.SDK_INT >= 17) {
            val myUserId: Method = UserHandle::class.java
                    .getDeclaredMethod("myUserId");
            val userID: Int = myUserId.invoke(pm) as Int
            val func = clz.getDeclaredMethod(
                    "getPackageSizeInfo",
                    String::class.java,
                    Int::class.java,
                    IPackageStatsObserver::class.java)
            func.invoke(pm, packageName, userID, observer)
        } else {
            val func = clz.getDeclaredMethod(
                    "getPackageSizeInfo",
                    String::class.java,
                    IPackageStatsObserver::class.java)
            func.invoke(pm, packageName, observer)
        }
    } catch (ex: Exception) {
        Timber.e(ex, "An error occurred");
        throw ex;
    }
}
