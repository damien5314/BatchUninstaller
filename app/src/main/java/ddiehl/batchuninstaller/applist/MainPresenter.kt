package ddiehl.batchuninstaller.applist

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class MainPresenter(
        private val appDataLoader: IAppDataLoader
) {

    private var mainView: MainView? = null
    private val subscriptions = CompositeDisposable()

    fun onViewAttached(view: MainView) {
        mainView = view
        loadApplicationData()
    }

    fun onViewDetached(view: MainView) {
        subscriptions.clear()
        mainView = null
    }

    private fun loadApplicationData() {
        appDataLoader.getApps()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    subscriptions.add(it)
                    mainView?.showSpinner()
                }
                .doFinally { mainView?.dismissSpinner() }
                .subscribe(this::onAppsLoaded, this::onAppsLoadError)
    }

    private fun onAppsLoaded(apps: List<AppViewModel>) {
        mainView?.showApps(apps)
    }

    private fun onAppsLoadError(error: Throwable) {
        Timber.e(error, "Error processing packages")
        mainView?.showToast(error)
    }
}
