package ddiehl.batchuninstaller.applist

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class AppListActivityPresenter(
        private val appDataLoader: AppDataLoader
) {

    private var appListView: AppListView? = null
    private val subscriptions = CompositeDisposable()

    fun onViewAttached(view: AppListView) {
        appListView = view
        loadApplicationData()
    }

    fun onViewDetached(view: AppListView) {
        subscriptions.clear()
        appListView = null
    }

    private fun loadApplicationData() {
        appDataLoader.getApps()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    subscriptions.add(it)
                    appListView?.showSpinner()
                }
                .doFinally { appListView?.dismissSpinner() }
                .subscribe(this::onAppsLoaded, this::onAppsLoadError)
    }

    private fun onAppsLoaded(apps: List<AppViewModel>) {
        appListView?.showApps(apps)
    }

    private fun onAppsLoadError(error: Throwable) {
        Timber.e(error, "Error processing packages")
        appListView?.showToast(error)
    }
}
