package ddiehl.batchuninstaller

import android.app.ProgressDialog
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.find
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.setContentView
import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.observable
import rx.schedulers.Schedulers
import timber.log.Timber
import java.util.*

class MainActivity : AppCompatActivity(), MainView {

  private var mLoadingOverlay: ProgressDialog? = null
  private var mData: List<App> = emptyList()
  private var mRecyclerView: RecyclerView? = null
  private val mAdapter: AppAdapter = AppAdapter(this)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    UI().setContentView(this)
    mRecyclerView = find<RecyclerView>(R.id.recycler_view)
    mRecyclerView?.adapter = mAdapter
    mLoadingOverlay = ProgressDialog(this, R.style.ProgressDialog)
    mLoadingOverlay?.setCancelable(false)
    mLoadingOverlay?.setProgressStyle(ProgressDialog.STYLE_SPINNER)
    loadData()
  }

  private fun loadData() {
    observable<List<App>> { subscriber ->
      val packageList: List<PackageInfo>
          = packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES)
      val packageSet: MutableSet<String> = HashSet()
      packageList.forEach {
        val intent = packageManager.getLaunchIntentForPackage(it.packageName)
        if (intent != null && !it.packageName.startsWith("com.android")) {
          packageSet.add(it.packageName)
        }
      }
      subscriber.onNext(
          packageSet.map {
            App(
                packageManager.getApplicationLabel(packageManager.getApplicationInfo(it, 0)),
                0, // TODO Calculate size of app
                it
            )
          })
      subscriber.onCompleted()
    }
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe { showSpinner() }
        .doOnTerminate { dismissSpinner() }
        .subscribe({
          mData = it
          mAdapter.notifyDataSetChanged()
          Timber.d("Loaded data (%s)", mData.size)
        })
  }

  override fun getNumItems(): Int = mData.size

  override fun getItemAt(position: Int): App {
    return mData[position]
  }

  override fun showSpinner() {
    mLoadingOverlay?.show();
  }

  override fun dismissSpinner() {
    if (mLoadingOverlay != null && mLoadingOverlay!!.isShowing) {
      mLoadingOverlay?.dismiss();
    }
  }
}

private class UI : AnkoComponent<MainActivity> {
  override fun createView(ui: AnkoContext<MainActivity>) = ui.apply {
    recyclerView {
      id = R.id.recycler_view
      layoutManager = LinearLayoutManager(ui.owner)
    }
  }.view
}