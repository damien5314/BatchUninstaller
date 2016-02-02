package ddiehl.batchuninstaller.view

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback
import com.bignerdranch.android.multiselector.MultiSelector
import ddiehl.batchuninstaller.R
import ddiehl.batchuninstaller.utils.getUninstallIntent
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.find
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.setContentView
import timber.log.Timber

/**
 * TODO
 * Implement a broadcast receiver for android.intent.action.PACKAGE_REMOVED
 *   to capture uninstall events
 */
class MainActivity : AppCompatActivity(), MainView {

  private val mMainPresenter: MainPresenter = MainPresenterImpl(this)

  private lateinit var mLoadingOverlay: ProgressDialog
  private lateinit var mRecyclerView: RecyclerView
  private val mMultiSelector: MultiSelector = MultiSelector()
  private lateinit var mAdapter: AppAdapter

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

  override fun onStart() {
    super.onStart()
    mMainPresenter.onStart()
  }

  override fun onStop() {
    mMainPresenter.onStop()
    super.onStop()
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
              mMainPresenter.onClickedBatchUninstall()
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

  override fun getSelectedPositions(): List<Int> {
    return mMultiSelector.selectedPositions
  }

  override fun activateSelectionMode() {
    startSupportActionMode(mSelectedMode)
  }

  override fun showUninstallForPackage(packageName: String) {
    startActivityForResult(
        getUninstallIntent(packageName, true), 0)
  }

  private val EXTRA_INSTALL_RESULT = "android.intent.extra.INSTALL_RESULT"

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (data != null) {
      val successful = data.extras.get(EXTRA_INSTALL_RESULT) == 1
      mMainPresenter.onItemUninstalled(successful)
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
}
