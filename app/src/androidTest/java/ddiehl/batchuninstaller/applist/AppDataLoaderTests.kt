package ddiehl.batchuninstaller.applist

import org.junit.Assert.assertEquals
import org.junit.Test

class AppDataLoaderTests {

    fun getAppDataLoader(): AppDataLoader {
        return AppDataLoader()
    }

    @Test fun foo() {
        val appDataLoader = getAppDataLoader()
        val packageManager = FakePackageManager()

        val observer = appDataLoader.getApps(packageManager).test()

        observer.assertValueCount(1)
        val list = observer.values()[0]
        assertEquals(5, list.size)
        observer.assertComplete()
    }
}
