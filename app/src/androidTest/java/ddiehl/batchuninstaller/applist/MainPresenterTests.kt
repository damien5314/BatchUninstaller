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
class MainPresenterTests {

    @Mock lateinit var mainView: MainView

    fun getFakeAppDataLoader(): IAppDataLoader {
        val apps = listOf(
                AppViewModel(
                        name = "Duo",
                        packageName = "com.google.android.apps.tachyon",
                        size = 0
                ),
                AppViewModel(
                        name = "Google Play Movies & TV",
                        packageName = "com.google.android.videos",
                        size = 0
                ),
                AppViewModel(
                        name = "Messenger",
                        packageName = "com.google.android.apps.messaging",
                        size = 0
                ),
                AppViewModel(
                        name = "Calendar",
                        packageName = "com.google.android.calendar",
                        size = 0
                ),
                AppViewModel(
                        name = "Hangouts",
                        packageName = "com.google.android.talk",
                        size = 0
                ),
                AppViewModel(
                        name = "HTML Viewer",
                        packageName = "com.android.htmlviewer",
                        size = 0
                ),
                AppViewModel(
                        name = "Carrier Config",
                        packageName = "com.android.carrierconfig",
                        size = 0
                )
        )
        val observable = Observable.just(apps)

        val mock = mock<IAppDataLoader>()
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
        val mainPresenter = MainPresenter(appDataLoader)

        mainPresenter.onViewAttached(mainView)

        verify(appDataLoader).getApps()
    }
}
