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
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class MainPresenterImpl(val mMainView: MainView) : MainPresenter {

    private val mContext = CustomApplication.context
    private val mData: ArrayList<App> = ArrayList()

    override fun saveData(): ArrayList<App> = mData
    override fun restoreData(list: ArrayList<App>) {
        mData.clear()
        mData.addAll(list)
    }

    private val mUninstallQueue = LinkedList<App>()
    private var mUninstallApp: App? = null

    private var mNumSelected: Int = 0
    private var mSelectedSize: Long = 0

    override fun getNumItems(): Int = mData.size

    override fun getItemAt(position: Int): App {
        return mData[position]
    }

    override fun onStart() {
        if (mData.isEmpty()) loadApplicationData()
        if (mNumSelected > 0) {
            mMainView.activateActionMode()
            showActionModeInfo()
        }
    }

    override fun onStop() {

    }

    private fun loadApplicationData() {
        Observable.defer { Observable.just(getApps()) }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { mMainView.showSpinner() }
                .doFinally { mMainView.dismissSpinner() }
                .subscribe({ apps ->
                    mData.clear()
                    mData.addAll(apps)
                    mMainView.notifyDataSetChanged()
                }, { error ->
                    Timber.e(error, "Error processing packages")
                    mMainView.showToast(error)
                })
    }

    private fun getApps(): List<App> {
        val packageManager: PackageManager = mMainView.getPackageManager()
        val packageList: List<PackageInfo> = packageManager.getInstalledPackages(GET_ACTIVITIES)
        val packageSet: MutableSet<String> = HashSet()
        packageList.forEach { pkg ->
            val intent = packageManager.getLaunchIntentForPackage(pkg.packageName)
            if (intent != null && !pkg.packageName.startsWith("com.android")) {
                packageSet.add(pkg.packageName)
                Timber.d("app: ${pkg.packageName}")
            }
        }
        return packageSet.map {
            val applicationInfo = packageManager.getApplicationInfo(it, 0)
            val label = packageManager.getApplicationLabel(applicationInfo)
            App(label, 0, it)
        }
    }

    override fun onItemSelected(position: Int, selected: Boolean) {
        mMainView.activateActionMode()
        val app = mData[position]
        if (selected) {
            mNumSelected++
            mSelectedSize += app.size
        } else {
            mNumSelected--
            mSelectedSize -= app.size
        }
        if (mNumSelected == 0) {
            mMainView.finishActionMode()
        } else {
            showActionModeInfo()
        }
    }

    private fun showActionModeInfo() {
        mMainView.setActionModeInfo(
                mContext.resources.getQuantityString(R.plurals.items_selected, mNumSelected, mNumSelected),
                formatFileSize(mSelectedSize, mContext))
    }

    override fun onClickedBatchUninstall() {
        val apps = mMainView.getSelectedPositions().map { mData[it] }
        mUninstallQueue.addAll(apps)
        processQueue()
    }

    override fun onSelectionsCleared() {
        mNumSelected = 0
        mSelectedSize = 0
    }

    override fun onItemUninstalled(success: Boolean) {
        if (success) {
            mData.remove(mUninstallApp)
            mMainView.notifyDataSetChanged()
        }
        processQueue()
    }

    private fun processQueue() {
        if (mUninstallQueue.isEmpty()) return
        mUninstallApp = mUninstallQueue.pop()
        mMainView.showUninstallForPackage(mUninstallApp!!.packageName)
    }
}