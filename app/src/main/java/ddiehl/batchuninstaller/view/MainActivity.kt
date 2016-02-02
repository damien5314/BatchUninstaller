package ddiehl.batchuninstaller.view

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback
import com.bignerdranch.android.multiselector.MultiSelector
import ddiehl.batchuninstaller.view.MainPresenter
import ddiehl.batchuninstaller.view.MainPresenterImpl
import ddiehl.batchuninstaller.R
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.find
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.setContentView
import timber.log.Timber

class MainActivity : AppCompatActivity(), MainView {

  private val mMainPresenter: MainPresenter = MainPresenterImpl(this)

  private lateinit var mLoadingOverlay: ProgressDialog
  private lateinit var mRecyclerView: RecyclerView
  private val mMultiSelector: MultiSelector = MultiSelector()
  private lateinit var mAdapter: AppAdapter

  private val mSelectedMode: ActionMode.Callback =
      object: ModalMultiSelectorCallback(mMultiSelector) {
        override fun onCreateActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
          super.onCreateActionMode(actionMode, menu)
          this@MainActivity.menuInflater.inflate(R.menu.context_menu, menu)
          mMultiSelector.isSelectable = true
          return true
        }

        override fun onActionItemClicked(
            mode: ActionMode, item: MenuItem?): Boolean {
          when (item!!.itemId) {
            R.id.action_uninstall -> {
              mode.finish()
              val positions = mMultiSelector.selectedPositions
              positions.forEach {
                Timber.d("Item uninstalled: " + mMainPresenter.getItemAt(it))
              }
              mMultiSelector.clearSelections()
              return true
            }
            else -> return false
          }
        }
      }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    UI().setContentView(this)
    mRecyclerView = find<RecyclerView>(R.id.recycler_view)
    mAdapter = AppAdapter(mMainPresenter, mMultiSelector)
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

  override fun onDataUpdated(index: Int) {
    runOnUiThread { mAdapter.notifyItemChanged(index) }
  }

  override fun activateSelectionMode() {
    startSupportActionMode(mSelectedMode)
  }

  private class UI : AnkoComponent<MainActivity> {
    override fun createView(ui: AnkoContext<MainActivity>) = ui.apply {
      recyclerView {
        id = R.id.recycler_view
        layoutManager = LinearLayoutManager(ui.owner)
      }
    }.view
  }
}
