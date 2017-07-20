package ddiehl.batchuninstaller.view

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.support.v7.widget.RecyclerView
import android.view.*
import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback
import com.bignerdranch.android.multiselector.MultiSelector
import ddiehl.batchuninstaller.R
import ddiehl.batchuninstaller.utils.getUninstallIntent

class ApplicationListFragment : Fragment(), MainView {

    private val EXTRA_INSTALL_RESULT = "android.intent.extra.INSTALL_RESULT"

    private lateinit var loadingOverlay: ProgressDialog
    private lateinit var recyclerView: RecyclerView

    private val mainPresenter: MainPresenter = MainPresenterImpl(this)
    private val multiSelector: MultiSelector = MultiSelector()
    private var actionMode: ActionMode? = null
    private lateinit var adapter: AppAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        with(inflater.inflate(R.layout.app_list_fragment, container, false)) {
            initListView(this)
            initLoadingOverlay(this)
            return this
        }
    }

    private fun initListView(root: View) {
        recyclerView = root.findViewById(R.id.recycler_view)
        adapter = AppAdapter(mainPresenter, multiSelector)
        recyclerView.adapter = adapter
    }

    private fun initLoadingOverlay(root: View) {
        loadingOverlay = ProgressDialog(root.context, R.style.ProgressDialog)
        loadingOverlay.setCancelable(false)
        loadingOverlay.setProgressStyle(ProgressDialog.STYLE_SPINNER)
    }

    override fun onStart() {
        super.onStart()
        mainPresenter.onStart()
    }

    override fun onStop() {
        mainPresenter.onStop()
        super.onStop()
    }

    override fun showSpinner() {
        loadingOverlay.show()
    }

    override fun dismissSpinner() {
        if (loadingOverlay.isShowing) {
            loadingOverlay.dismiss()
        }
    }

    override fun notifyDataSetChanged() {
        adapter.notifyDataSetChanged()
    }

    override fun onDataUpdated(index: Int) {
        activity.runOnUiThread { adapter.notifyItemChanged(index) }
    }

    override fun getSelectedPositions(): List<Int> {
        return multiSelector.selectedPositions
    }

    override fun activateActionMode() {
        if (actionMode == null) {
            actionMode = (activity as AppCompatActivity).startSupportActionMode(
                    object : ModalMultiSelectorCallback(multiSelector) {
                        override fun onCreateActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
                            super.onCreateActionMode(actionMode, menu)
                            activity.menuInflater.inflate(R.menu.context_menu, menu)
                            multiSelector.isSelectable = true
                            return true
                        }

                        override fun onActionItemClicked(
                                mode: ActionMode, item: MenuItem): Boolean {
                            when (item.itemId) {
                                R.id.action_uninstall -> {
                                    mode.finish()
                                    mainPresenter.onClickedBatchUninstall()
                                    multiSelector.clearSelections()
                                    return true
                                }
                                else -> return false
                            }
                        }

                        override fun onDestroyActionMode(actionMode: ActionMode?) {
                            super.onDestroyActionMode(actionMode)
                            mainPresenter.onSelectionsCleared()
                            this@ApplicationListFragment.actionMode = null
                        }
                    })
        }
    }

    override fun finishActionMode() {
        if (actionMode != null) {
            actionMode!!.finish()
            actionMode = null
        }
    }

    override fun setActionModeInfo(title: String, subtitle: String) {
        if (actionMode == null) return
        actionMode!!.title = title
        actionMode!!.subtitle = subtitle
    }

    override fun showUninstallForPackage(packageName: String) {
        startActivityForResult(getUninstallIntent(packageName, true), 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            val successful = data.extras.get(EXTRA_INSTALL_RESULT) == 1
            mainPresenter.onItemUninstalled(successful)
        } else {
            mainPresenter.onItemUninstalled(false)
        }
    }

    override fun showToast(throwable: Throwable) {
        view?.let {
            Snackbar.make(it, R.string.error, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun getPackageManager(): PackageManager = activity.packageManager
}