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
  private val mData: MutableList<App> = ArrayList()
  private val mUninstallQueue = LinkedList<App>()
  private var mUninstallApp: App? = null

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
        App(
            pm.getApplicationLabel(pm.getApplicationInfo(it, 0)),
            0,
            it
        )
      }))
      subscriber.onCompleted()
    }
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe { mMainView.showSpinner() }
        .doOnTerminate { mMainView.dismissSpinner() }
        .subscribe({
          mData.clear()
          mData.addAll(it)
          calculateApplicationSize()
        })
  }

  private fun calculateApplicationSize() {
    mData.forEach { getAppPackageSize(it) }
  }

  private fun getAppPackageSize(app: App) {
    try {
      val clz = mMainView.getPackageManager().javaClass
      if (Build.VERSION.SDK_INT >= 17) {
        val myUserId: Method = UserHandle::class.java
            .getDeclaredMethod("myUserId"); //ignore check this when u set ur min SDK < 17
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

  private var numSelected: Int = 0
  private var selectedSize: Long = 0

  override fun onItemSelected(position: Int, selected: Boolean) {
    mMainView.activateActionMode()
    val app = mData[position]
    if (selected) {
      numSelected++
      selectedSize += app.size
    } else {
      numSelected--
      selectedSize -= app.size
    }
    mMainView.setActionModeInfo(
        mContext.resources.getQuantityString(R.plurals.items_selected, numSelected, numSelected),
        formatFileSize(selectedSize, mContext))
  }

  override fun onClickedBatchUninstall() {
    val apps = mMainView.getSelectedPositions().map { mData[it] }
    mUninstallQueue.addAll(apps)
    processQueue()
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