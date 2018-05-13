package ddiehl.batchuninstaller.applist

import android.support.test.runner.AndroidJUnit4
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class AppListActivityPresenterTests {

    @Mock
    lateinit var appListView: AppListView

    fun getFakeAppDataLoader(): AppDataLoader {
        val apps = listOf(
            AppViewModel(
                name = "Duo",
                packageName = "com.google.android.apps.tachyon",
                installationDate = System.currentTimeMillis(),
                size = 0
            ),
            AppViewModel(
                name = "Google Play Movies & TV",
                packageName = "com.google.android.videos",
                installationDate = System.currentTimeMillis(),
                size = 0
            ),
            AppViewModel(
                name = "Messenger",
                packageName = "com.google.android.apps.messaging",
                installationDate = System.currentTimeMillis(),
                size = 0
            ),
            AppViewModel(
                name = "Calendar",
                packageName = "com.google.android.calendar",
                installationDate = System.currentTimeMillis(),
                size = 0
            ),
            AppViewModel(
                name = "Hangouts",
                packageName = "com.google.android.talk",
                installationDate = System.currentTimeMillis(),
                size = 0
            ),
            AppViewModel(
                name = "HTML Viewer",
                packageName = "com.android.htmlviewer",
                installationDate = System.currentTimeMillis(),
                size = 0
            ),
            AppViewModel(
                name = "Carrier Config",
                packageName = "com.android.carrierconfig",
                installationDate = System.currentTimeMillis(),
                size = 0
            )
        )
        val observable = Observable.just(apps)

        val mock = mock<AppDataLoader>()
        whenever(mock.getApps()).thenReturn(observable)

        return mock
    }

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun onViewAttached_appDataLoader_getAppsCalled() {
        val appDataLoader = getFakeAppDataLoader()
        val mainPresenter = AppListActivityPresenter(appDataLoader)

        mainPresenter.onViewAttached(appListView)

        verify(appDataLoader).getApps()
    }
}
