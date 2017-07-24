package ddiehl.batchuninstaller.view;

import android.content.pm.PackageManager
import ddiehl.batchuninstaller.model.AppViewModel

interface MainView {

    fun getPackageManager(): PackageManager

    fun showApps(apps: List<AppViewModel>)

    fun showToast(throwable: Throwable)

    fun showSpinner()

    fun dismissSpinner()
}
