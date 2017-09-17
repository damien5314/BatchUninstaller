package ddiehl.batchuninstaller.applist;

import android.content.pm.PackageManager

interface AppListView {

    val appList: List<AppViewModel>

    fun getPackageManager(): PackageManager

    fun showApps(apps: List<AppViewModel>)

    fun showToast(throwable: Throwable)

    fun showSpinner()

    fun dismissSpinner()
}
