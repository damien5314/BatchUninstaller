package ddiehl.batchuninstaller.model.appinfo.impl

import android.content.pm.PackageManager
import ddiehl.batchuninstaller.model.appinfo.IApplicationInfo
import ddiehl.batchuninstaller.model.appinfo.IIntent
import ddiehl.batchuninstaller.model.appinfo.IPackageInfo
import ddiehl.batchuninstaller.model.appinfo.IPackageManager

class APackageManager(private val packageManager: PackageManager) : IPackageManager {

    override fun getInstalledPackages(type: Int): List<IPackageInfo> {
        return packageManager.getInstalledPackages(type)
            .map { APackageInfo(it.packageName) }
    }

    override fun getLaunchIntentForPackage(packageName: String): IIntent? {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        return intent?.let { AIntent(it) }
    }

    override fun getApplicationInfo(name: String, flags: Int): IApplicationInfo {
        return with(packageManager.getApplicationInfo(name, flags)) {
            AApplicationInfo(this)
        }
    }

    override fun getApplicationLabel(applicationInfo: IApplicationInfo): String? {
        if (applicationInfo !is AApplicationInfo) {
            throw IllegalArgumentException("$applicationInfo is of the wrong type")
        }

        return packageManager.getApplicationLabel(applicationInfo.delegate).toString()
    }

    override fun getInstallationTime(packageName: String): Long {
        return packageManager.getPackageInfo(packageName, 0)
            .firstInstallTime
    }
}
