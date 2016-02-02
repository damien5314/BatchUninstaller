package ddiehl.batchuninstaller

import android.content.pm.IPackageStatsObserver
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageStats
import android.os.UserHandle
import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.observable
import rx.schedulers.Schedulers
import timber.log.Timber
import java.lang.reflect.Method
import java.util.*

class MainPresenterImpl(val mMainView: MainView) : MainPresenter {

  private var mData: MutableList<App> = ArrayList()

  override fun getNumItems(): Int = mData.size

  override fun getItemAt(position: Int): App {
    return mData[position]
  }

  override fun onResume() {
    if (mData.isEmpty()) loadApplicationData()
  }

  override fun onPause() {
    mData.clear()
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
          mData = it
          mMainView.notifyDataSetChanged()
          calculateApplicationSize()
          Timber.d("Loaded data (%s)", mData.size)
        })
  }

  private fun calculateApplicationSize() {
    var counter = 0
    mData.forEach { app ->
      getAppPackageSize(app,
          { ps, b ->
            if (b && ps != null) {
              app.size =
                  ps.cacheSize
              + ps.codeSize
              + ps.dataSize
              + ps.externalCacheSize
              + ps.externalCodeSize
              + ps.externalDataSize
              + ps.externalMediaSize
              + ps.externalObbSize
              Timber.d("%s -> %s bytes", app.name, app.size)
            }
            counter++
            if (counter == mData.size) {
              mMainView.notifyDataSetChanged()
            }
          })
    }
  }

  private fun getAppPackageSize(app: App, onGetStatsCompleted: (PackageStats?, Boolean) -> Unit) {
    try {
      val clz = mMainView.getPackageManager().javaClass
      val myUserId: Method = UserHandle::class.java
          .getDeclaredMethod("myUserId"); //ignore check this when u set ur min SDK < 17
      val userID: Int = myUserId.invoke(mMainView.getPackageManager()) as Int;
      val clzInt = javaClass<Int>()
      val getPackageSizeInfo: Method = clz.getDeclaredMethod(
          "getPackageSizeInfo",
          String::class.java,
          clzInt,
          IPackageStatsObserver::class.java);
      getPackageSizeInfo.invoke(mMainView.getPackageManager(), app.packageName, userID,
          object : IPackageStatsObserver.Stub() {
            override fun onGetStatsCompleted(pStats: PackageStats?, succeeded: Boolean) {
              onGetStatsCompleted.invoke(pStats, succeeded)
            }
          });
    } catch (ex: Exception) {
      Timber.e(ex, "An error occurred");
      throw ex;
    }
  }
}