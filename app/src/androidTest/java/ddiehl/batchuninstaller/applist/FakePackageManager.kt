package ddiehl.batchuninstaller.applist

import ddiehl.batchuninstaller.model.appinfo.IApplicationInfo
import ddiehl.batchuninstaller.model.appinfo.IIntent
import ddiehl.batchuninstaller.model.appinfo.IPackageInfo
import ddiehl.batchuninstaller.model.appinfo.IPackageManager
import ddiehl.batchuninstaller.model.appinfo.impl.APackageInfo

class FakePackageManager : IPackageManager {

    override fun getInstalledPackages(type: Int): List<IPackageInfo> {
        return listOf(
                APackageInfo("com.google.android.apps.tachyon"), // Duo
                APackageInfo("com.google.android.videos"), // Google Play Movies & TV
                APackageInfo("com.google.android.apps.messaging"), // Messenger
                APackageInfo("com.google.android.calendar"), // Calendar
                APackageInfo("com.google.android.talk"), // Hangouts
                APackageInfo("com.android.htmlviewer"), // HTML Viewer
                APackageInfo("com.android.carrierconfig") // Carrier Config
        )
    }

    override fun getLaunchIntentForPackage(packageName: String): IIntent? {

        when (packageName) {
            "com.google.android.apps.tachyon",
            "com.google.android.videos",
            "com.google.android.apps.messaging",
            "com.google.android.calendar",
            "com.google.android.talk",
            "com.android.htmlviewer",
            "com.android.carrierconfig" -> return object : IIntent { }
            else -> return null
        }
    }

    override fun getApplicationInfo(name: String, flags: Int): IApplicationInfo
            = FakeApplicationInfo(name)

    override fun getApplicationLabel(applicationInfo: IApplicationInfo): String? {
        if (applicationInfo !is FakeApplicationInfo) {
            throw IllegalArgumentException()
        }

        when (applicationInfo.packageName) {
            "com.google.android.apps.tachyon" -> return "Duo"
            "com.google.android.videos" -> return "Google Play Movies & TV"
            "com.google.android.apps.messaging" -> return "Messenger"
            "com.google.android.calendar" -> return "Calendar"
            "com.google.android.talk" -> return "Hangouts"
            "com.android.htmlviewer" -> return "HTML Viewer"
            "com.android.carrierconfig" -> return "Carrier Config"
            else -> return null
        }
    }

    private class FakeApplicationInfo(val packageName: String) : IApplicationInfo
}
