package ddiehl.batchuninstaller.applist

import ddiehl.batchuninstaller.model.appinfo.IPackageManager
import org.junit.Assert.assertEquals
import org.junit.Test

class AppDataLoaderTests {

    private fun getAppDataLoader(pm: IPackageManager) = AppDataLoader.Impl(pm)

    @Test
    fun getApps_verifyCorrectCountIsReturned() {
        val packageManager = FakePackageManager(listOf(
            TestApp("com.google.android.apps.tachyon", "Duo"),
            TestApp("com.google.android.videos", "Google Play Movies & TV"),
            TestApp("com.google.android.apps.messaging", "Messenger"),
            TestApp("com.google.android.calendar", "Calendar"),
            TestApp("com.google.android.talk", "Hangouts"),
            TestApp("com.android.htmlviewer", "HTML Viewer"),
            TestApp("com.android.carrierconfig", "Carrier Config")
        ))
        val appDataLoader = getAppDataLoader(packageManager)

        val observer = appDataLoader.getApps().test()

        observer.assertValueCount(1)
        val list = observer.values().first()
        assertEquals(5, list.size)
        observer.assertComplete()
    }

    @Test
    fun getApps_verifySortedByName() {
        val packageManager = FakePackageManager(listOf(
            TestApp("com.testapp.apple", "apple"),
            TestApp("com.testapp.grapefruit", "grapefruit"),
            TestApp("com.testapp.orange", "orange"),
            TestApp("com.testapp.bob", "Bob"),
            TestApp("com.testapp.sophia", "Sophia"),
            TestApp("com.testapp.jacob", "Jacob")
        ))
        val appDataLoader = getAppDataLoader(packageManager)

        val observer = appDataLoader.getApps().test()

        observer.assertValueCount(1)
        val list: List<String> = observer.values()
            .first()
            .map { it.packageName }
        assertEquals(listOf(
            "com.testapp.apple",
            "com.testapp.bob",
            "com.testapp.grapefruit",
            "com.testapp.jacob",
            "com.testapp.orange",
            "com.testapp.sophia"
        ), list)
    }
}
