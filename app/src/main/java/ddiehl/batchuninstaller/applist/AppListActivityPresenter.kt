package ddiehl.batchuninstaller.applist

import kotlinx.coroutines.*
import timber.log.Timber

class AppListActivityPresenter(
    private val appDataLoader: AppDataLoader
) {

    companion object {
        private val parentJob = Job()
        private val coroutineScope = CoroutineScope(Dispatchers.Main + parentJob)
    }

    private var appListView: AppListView? = null
    private var loadAppsAsync: Job? = null

    fun onViewAttached(view: AppListView) {
        appListView = view
        loadApplicationData()
    }

    fun onViewDetached(view: AppListView) {
        loadAppsAsync?.cancel()
        appListView = null
    }

    private fun loadApplicationData() {
        val deferred = coroutineScope.async { appDataLoader.getApps() }

        appListView?.showSpinner()

        loadAppsAsync = coroutineScope.launch(Dispatchers.Main) {
            appListView?.dismissSpinner()

            val apps = deferred.await()
            onAppsLoaded(apps)
        }
    }

    private fun onAppsLoaded(apps: List<AppViewModel>) {
        appListView?.showApps(apps)
    }

    private fun onAppsLoadError(error: Throwable) {
        Timber.e(error, "Error processing packages")
        appListView?.showToast(error)
    }
}
