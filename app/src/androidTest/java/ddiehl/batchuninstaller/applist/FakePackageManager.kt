package ddiehl.batchuninstaller.applist

import ddiehl.batchuninstaller.model.appinfo.IApplicationInfo
import ddiehl.batchuninstaller.model.appinfo.IIntent
import ddiehl.batchuninstaller.model.appinfo.IPackageInfo
import ddiehl.batchuninstaller.model.appinfo.IPackageManager
import ddiehl.batchuninstaller.model.appinfo.impl.APackageInfo

class FakePackageManager : IPackageManager {

    private enum class TestPackages(val packageName: String, val displayName: String) {
        Duo("com.google.android.apps.tachyon", "Duo"),
        PlayMoviesTv("com.google.android.videos", "Google Play Movies & TV"),
        Messenger("com.google.android.apps.messaging", "Messenger"),
        Calendar("com.google.android.calendar", "Calendar"),
        Hangouts("com.google.android.talk", "Hangouts"),
        HtmlViewer("com.android.htmlviewer", "HTML Viewer"),
        CarrierConfig("com.android.carrierconfig", "Carrier Config"),
    }

    override fun getInstalledPackages(type: Int): List<IPackageInfo> {
        return TestPackages.values()
            .map { testPackages -> APackageInfo(testPackages.packageName) }
    }

    override fun getLaunchIntentForPackage(packageName: String): IIntent? {
        if (TestPackages.values().find { pkg -> pkg.packageName == packageName } != null) {
            return object : IIntent {}
        } else return null
    }

    override fun getApplicationInfo(name: String, flags: Int): IApplicationInfo =
        FakeApplicationInfo(name)

    override fun getApplicationLabel(applicationInfo: IApplicationInfo): String? {
        if (applicationInfo !is FakeApplicationInfo) {
            throw IllegalArgumentException()
        }

        val pkg = TestPackages.values()
            .find { info -> info.packageName == applicationInfo.packageName }

        return pkg?.displayName
    }

    override fun getInstallationTime(packageName: String): Long {
        return System.currentTimeMillis()
    }

    private class FakeApplicationInfo(val packageName: String) : IApplicationInfo
}
