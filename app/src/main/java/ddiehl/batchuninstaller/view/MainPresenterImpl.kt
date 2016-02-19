package ddiehl.batchuninstaller.view

import android.content.pm.IPackageStatsObserver
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageStats
import android.os.Build
import android.os.UserHandle
import ddiehl.batchuninstaller.CustomApplication
import ddiehl.batchuninstaller.R
import ddiehl.batchuninstaller.model.App
import ddiehl.batchuninstaller.utils.formatFileSize
import ddiehl.batchuninstaller.utils.getTotalSize
import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.observable
import rx.schedulers.Schedulers
import timber.log.Timber
import java.lang.reflect.Method
import java.util.*

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
  }

  override fun onStop() {

  }

  private fun loadApplicationData() {
    observable<MutableList<App>> { subscriber ->
      val pm: PackageManager = mMainView.getPackageManager()
      val packageList: List<PackageInfo>
          = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES)
      val packageSet: MutableSet<String> = HashSet()
      packageList.forEach {
        val intent = pm.getLaunchIntentForPackage(it.packageName)
        if (intent != null && !it.packageName.startsWith("com.android")) {
          packageSet.add(it.packageName)
        }
      }
      subscriber.onNext(ArrayList(packageSet.map {
        val label = pm.getApplicationLabel(pm.getApplicationInfo(it, 0))
        App(label, 0, it)
      }))
      subscriber.onCompleted() }
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe { mMainView.showSpinner() }
        .doOnTerminate { mMainView.dismissSpinner() }
        .subscribe({
          mData.clear()
          mData.addAll(it)
        }, { mMainView.showToast(it) }, { calculateApplicationSize() })
  }

  private fun calculateApplicationSize() {
    mData.forEach { getAppPackageSize(it) }
  }

  private fun getAppPackageSize(app: App) {
    try {
      val clz = mMainView.getPackageManager().javaClass
      if (Build.VERSION.SDK_INT >= 17) {
        val myUserId: Method = UserHandle::class.java
            .getDeclaredMethod("myUserId");
        val userID: Int = myUserId.invoke(mMainView.getPackageManager()) as Int
        clz.getDeclaredMethod(
            "getPackageSizeInfo",
            String::class.java,
            Int::class.java,
            IPackageStatsObserver::class.java)
            .invoke(mMainView.getPackageManager(), app.packageName, userID,
                getPackageStatsObserver(app))
      } else {
        clz.getDeclaredMethod(
            "getPackageSizeInfo",
            String::class.java,
            IPackageStatsObserver::class.java)
            .invoke(mMainView.getPackageManager(), app.packageName,
                getPackageStatsObserver(app))
      }
    } catch (ex: Exception) {
      Timber.e(ex, "An error occurred");
      throw ex;
    }
  }

  fun getPackageStatsObserver(app: App) : IPackageStatsObserver {
    return object : IPackageStatsObserver.Stub() {
      override fun onGetStatsCompleted(ps: PackageStats?, succeeded: Boolean) {
        if (succeeded && ps != null) {
          app.size = ps.getTotalSize()
          mMainView.onDataUpdated(
              mData.indexOf(app))
        }
      }
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
      mMainView.setActionModeInfo(
          mContext.resources.getQuantityString(R.plurals.items_selected, mNumSelected, mNumSelected),
          formatFileSize(mSelectedSize, mContext))
    }
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