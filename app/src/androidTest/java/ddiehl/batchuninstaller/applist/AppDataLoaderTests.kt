package ddiehl.batchuninstaller.applist

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppDataLoaderTests {

    private fun getAppDataLoader() = AppDataLoader.Impl(FakePackageManager())

    @Test fun getApps_verifyCorrectCountIsReturned() {
        val appDataLoader = getAppDataLoader()

        val observer = appDataLoader.getApps().test()

        observer.assertValueCount(1)
        val list = observer.values().first()
        assertEquals(5, list.size)
        observer.assertComplete()
    }

    @Test fun getApps_verifySortedByName() {
        val appDataLoader = getAppDataLoader()

        val observer = appDataLoader.getApps().test()

        observer.assertValueCount(1)
        val list: List<AppViewModel> = observer.values().first()
        assertTrue(list.isSorted { it.name })
    }
}
