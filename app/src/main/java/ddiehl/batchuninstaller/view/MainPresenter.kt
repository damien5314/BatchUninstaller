package ddiehl.batchuninstaller.view

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.GET_ACTIVITIES
import ddiehl.batchuninstaller.CustomApplication
import ddiehl.batchuninstaller.R
import ddiehl.batchuninstaller.model.App
import ddiehl.batchuninstaller.utils.formatFileSize
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class MainPresenter {
    
    private var mainView: MainView? = null

    private val context = CustomApplication.context
    private val appList: ArrayList<App> = ArrayList()

    private val subscriptions = CompositeDisposable()

    fun saveData(): ArrayList<App> = appList
    
    fun restoreData(list: ArrayList<App>) {
        appList.clear()
        appList.addAll(list)
    }

    private val uninstallQueue = LinkedList<App>()
    private var uninstallApp: App? = null

    private var numSelected: Int = 0
    private var selectedSize: Long = 0

    fun getNumItems(): Int = appList.size

    fun getItemAt(position: Int): App {
        return appList[position]
    }

    fun onViewAttached(view: MainView) {
        mainView = view
        
        if (appList.isEmpty()) {
            loadApplicationData()
        }

        if (numSelected > 0) {
            view.activateActionMode()
            showActionModeInfo()
        }
    }

    fun onViewDetached(view: MainView) {
        subscriptions.clear()
        mainView = null
    }

    private fun loadApplicationData() {
        mainView?.let { mainView ->
            val packageManager: PackageManager = mainView.getPackageManager()
            getApps(packageManager)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe {
                        subscriptions.add(it)
                        mainView.showSpinner()
                    }
                    .doFinally { mainView.dismissSpinner() }
                    .subscribe({ apps ->
                        appList.clear()
                        appList.addAll(apps)
                        mainView.notifyDataSetChanged()
                    }, { error ->
                        Timber.e(error, "Error processing packages")
                        mainView.showToast(error)
                    })
        }
    }

    private fun getApps(packageManager: PackageManager): Observable<List<App>> {
        return Observable.defer {
            val packageList: List<PackageInfo> = packageManager.getInstalledPackages(GET_ACTIVITIES)
            val packageSet: MutableSet<String> = HashSet()

            packageList.forEach { pkg ->
                val intent = packageManager.getLaunchIntentForPackage(pkg.packageName)
                if (intent != null && !pkg.packageName.startsWith("com.android")) {
                    packageSet.add(pkg.packageName)
                    Timber.d("app: ${pkg.packageName}")
                }
            }

            val apps = packageSet.map {
                val applicationInfo = packageManager.getApplicationInfo(it, 0)
                val label = packageManager.getApplicationLabel(applicationInfo)
                App(label, 0, it)
            }

            Observable.just(apps)
        }
    }

    //FIXME this should probably be in the view, not presenter
    fun onItemSelected(position: Int, selected: Boolean) {
        mainView?.activateActionMode()
        val app = appList[position]
        if (selected) {
            numSelected++
            selectedSize += app.size
        } else {
            numSelected--
            selectedSize -= app.size
        }
        if (numSelected == 0) {
            mainView?.finishActionMode()
        } else {
            showActionModeInfo()
        }
    }

    private fun showActionModeInfo() {
        mainView?.setActionModeInfo(
                context.resources.getQuantityString(R.plurals.items_selected, numSelected, numSelected),
                formatFileSize(selectedSize, context))
    }

    fun onClickedBatchUninstall() {
        mainView?.let { view ->
            val apps = view.getSelectedPositions().map { appList[it] }
            uninstallQueue.addAll(apps)
            processQueue()
        }
    }

    fun onSelectionsCleared() {
        numSelected = 0
        selectedSize = 0
    }

    fun onItemUninstalled(success: Boolean) {
        if (success) {
            appList.remove(uninstallApp)
            mainView?.notifyDataSetChanged()
        }
        processQueue()
    }

    private fun processQueue() {
        if (uninstallQueue.isEmpty()) return
        uninstallApp = uninstallQueue.pop()
        mainView?.showUninstallForPackage(uninstallApp!!.packageName)
    }
}