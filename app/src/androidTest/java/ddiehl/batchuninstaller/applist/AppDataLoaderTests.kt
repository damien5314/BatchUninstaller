package ddiehl.batchuninstaller.applist

import ddiehl.batchuninstaller.model.appinfo.IPackageManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppDataLoaderTests {

    fun getAppDataLoader(packageManager: IPackageManager): AppDataLoader {
        return AppDataLoader(packageManager)
    }

    @Test fun getApps_verifyCorrectCountIsReturned() {
        val appDataLoader = getAppDataLoader(FakePackageManager())

        val observer = appDataLoader.getApps().test()

        observer.assertValueCount(1)
        val list = observer.values().first()
        assertEquals(5, list.size)
        observer.assertComplete()
    }

    @Test fun getApps_verifySortedByName() {
        val appDataLoader = getAppDataLoader(FakePackageManager())

        val observer = appDataLoader.getApps().test()

        observer.assertValueCount(1)
        val list = observer.values().first()

        list.forEachIndexed { index, _ ->
            if (index >= list.size - 1) return@forEachIndexed

            assertTrue(list[index].name < list[index+1].name)
        }
    }
}
