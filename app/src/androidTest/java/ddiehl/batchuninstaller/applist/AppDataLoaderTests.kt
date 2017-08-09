package ddiehl.batchuninstaller.applist

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppDataLoaderTests {

    fun getAppDataLoader(): AppDataLoader {
        return AppDataLoader()
    }

    @Test fun getApps_verifyCorrectCountIsReturned() {
        val appDataLoader = getAppDataLoader()
        val packageManager = FakePackageManager()

        val observer = appDataLoader.getApps(packageManager).test()

        observer.assertValueCount(1)
        val list = observer.values().first()
        assertEquals(5, list.size)
        observer.assertComplete()
    }

    @Test fun getApps_verifySortedByName() {
        val appDataLoader = getAppDataLoader()
        val packageManager = FakePackageManager()

        val observer = appDataLoader.getApps(packageManager).test()

        observer.assertValueCount(1)
        val list = observer.values().first()

        list.forEachIndexed { index, _ ->
            if (index >= list.size - 1) return@forEachIndexed

            assertTrue(list[index].name < list[index+1].name)
        }
    }
}
