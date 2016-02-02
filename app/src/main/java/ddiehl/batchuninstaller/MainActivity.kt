package ddiehl.batchuninstaller

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.find
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.setContentView

class MainActivity : AppCompatActivity(), MainView {

  private lateinit var mLoadingOverlay: ProgressDialog
  private lateinit var mRecyclerView: RecyclerView
  private val mMainPresenter: MainPresenter = MainPresenterImpl(this)
  private val mAdapter: AppAdapter = AppAdapter(mMainPresenter)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    UI().setContentView(this)
    mRecyclerView = find<RecyclerView>(R.id.recycler_view)
    mRecyclerView.adapter = mAdapter
    mLoadingOverlay = ProgressDialog(this, R.style.ProgressDialog)
    mLoadingOverlay.setCancelable(false)
    mLoadingOverlay.setProgressStyle(ProgressDialog.STYLE_SPINNER)
  }

  override fun onResume() {
    super.onResume()
    mMainPresenter.onResume()
  }

  override fun onPause() {
    mMainPresenter.onPause()
    super.onPause()
  }

  override fun showSpinner() {
    mLoadingOverlay.show();
  }

  override fun dismissSpinner() {
    if (mLoadingOverlay.isShowing) {
      mLoadingOverlay.dismiss();
    }
  }

  override fun notifyDataSetChanged() {
    mAdapter.notifyDataSetChanged()
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
