package ddiehl.batchuninstaller.applist

import android.content.pm.PackageManager
import ddiehl.batchuninstaller.model.appinfo.IPackageManager
import io.reactivex.Observable

interface IAppDataLoader {

    /**
     * Returns a list of apps installed in the passed [IPackageManager].
     */
    fun getApps(): Observable<List<AppViewModel>>
}

class AppDataLoader(private val packageManager: IPackageManager) : IAppDataLoader {

    companion object {
        private const val ANDROID_PACKAGE_PREFIX = "com.android"
    }

    override fun getApps(): Observable<List<AppViewModel>> {
        return Observable.defer {
            val packageList = packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES)

            val apps = packageList
                    .filter { pkg -> !pkg.packageName.startsWith(ANDROID_PACKAGE_PREFIX) }
                    .filter { pkg -> packageManager.getLaunchIntentForPackage(pkg.packageName) != null }
                    .map { pkg -> pkg.packageName }
                    .map { name ->
                        val applicationInfo = packageManager.getApplicationInfo(name, 0)
                        val label = packageManager.getApplicationLabel(applicationInfo)
                        AppViewModel(label ?: "", name, 0)
                    }
                    .sortedBy { viewModel -> viewModel.name }
                    .toMutableList()

            Observable.just(apps)
        }
    }
}
