package ddiehl.batchuninstaller.applist

import ddiehl.batchuninstaller.model.appinfo.IApplicationInfo
import ddiehl.batchuninstaller.model.appinfo.IIntent
import ddiehl.batchuninstaller.model.appinfo.IPackageInfo
import ddiehl.batchuninstaller.model.appinfo.IPackageManager
import ddiehl.batchuninstaller.model.appinfo.impl.APackageInfo

class FakePackageManager(private val testApps: List<TestApp>) : IPackageManager {

    override fun getInstalledPackages(type: Int): List<IPackageInfo> {
        return testApps.map { app -> APackageInfo(app.packageName) }
    }

    override fun getLaunchIntentForPackage(packageName: String): IIntent? {
        if (testApps.find { app -> app.packageName == packageName } != null) {
            return object : IIntent {}
        } else return null
    }

    override fun getApplicationInfo(name: String, flags: Int): IApplicationInfo =
        FakeApplicationInfo(name)

    override fun getApplicationLabel(applicationInfo: IApplicationInfo): String? {
        if (applicationInfo !is FakeApplicationInfo) {
            throw IllegalArgumentException()
        }

        val pkg = testApps.find { info -> info.packageName == applicationInfo.packageName }

        return pkg?.displayName
    }

    override fun getInstallationTime(packageName: String): Long {
        return System.currentTimeMillis()
    }

    private class FakeApplicationInfo(val packageName: String) : IApplicationInfo
}
