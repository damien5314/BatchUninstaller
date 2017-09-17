package ddiehl.batchuninstaller.applist

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.bignerdranch.android.multiselector.MultiSelector
import ddiehl.batchuninstaller.R
import ddiehl.batchuninstaller.model.appinfo.impl.APackageManager
import ddiehl.batchuninstaller.utils.getUninstallIntent
import ddiehl.batchuninstaller.utils.itemRemoved
import ddiehl.batchuninstaller.utils.setBackgroundColor
import ddiehl.batchuninstaller.utils.tintAllIcons
import kotlinx.android.synthetic.main.app_list_activity.*
import java.util.*

class AppListActivity : AppCompatActivity(), AppListView {

    companion object {
        private val LAYOUT_RES_ID = R.layout.app_list_activity
        private val RC_UNINSTALL_APP = 10
        private val STATE_SELECTED_PACKAGES = "selectedPackages"
        private val STATE_PENDING_UNINSTALL = "pendingUninstall"

        // Values returned from Android uninstall Activity, cannot modify
        private val EXTRA_INSTALL_RESULT = "android.intent.extra.INSTALL_RESULT"
        private val EXTRA_UNINSTALL_RESULT_SUCCESS = 1
    }

    private lateinit var appListActivityPresenter: AppListActivityPresenter
    private lateinit var adapter: AppAdapter
    private lateinit var loadingOverlay: ProgressDialog
    private val multiSelector = MultiSelector()

    override val appList = mutableListOf<AppViewModel>()
    private val uninstallQueue: Queue<AppViewModel> = LinkedList()
    private var selectedPackages: Array<String>? = null
    private var appUninstallRequested: AppViewModel? = null

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        setContentView(LAYOUT_RES_ID)
        setBackgroundColor(R.color.gray)
        setSupportActionBar(toolbar)

        state?.let {
            selectedPackages = state.getStringArray(STATE_SELECTED_PACKAGES)
            appUninstallRequested = state.getParcelable(STATE_PENDING_UNINSTALL)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AppAdapter(this, multiSelector)
        recyclerView.adapter = adapter

        loadingOverlay = ProgressDialog(this, R.style.ProgressDialog)
        loadingOverlay.setCancelable(false)
        loadingOverlay.setProgressStyle(ProgressDialog.STYLE_SPINNER)

        val appDataLoader = AppDataLoader.Impl(APackageManager(packageManager))
        appListActivityPresenter = AppListActivityPresenter(appDataLoader)
    }

    override fun onStart() {
        super.onStart()
        appListActivityPresenter.onViewAttached(this)
    }

    override fun onStop() {
        appListActivityPresenter.onViewDetached(this)
        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val selectedPackages =
                multiSelector.selectedPositions.map { position -> appList[position] }
                        .map { app -> app.packageName }
                        .toTypedArray()

        outState.putStringArray(STATE_SELECTED_PACKAGES, selectedPackages)
        outState.putParcelable(STATE_PENDING_UNINSTALL, appUninstallRequested)
    }

    //region AppListView

    override fun showApps(apps: List<AppViewModel>) {
        appList.clear()
        appList.addAll(apps)

        selectedPackages?.let { packages ->
            appList.forEachIndexed { index, appViewModel ->
                if (packages.contains(appViewModel.packageName)) {
                    multiSelector.setSelected(index, 0, true)
                }
            }
            selectedPackages = null
        }

        adapter.notifyDataSetChanged()
    }

    override fun showToast(throwable: Throwable) {
        Snackbar.make(chromeView, R.string.error_getting_app_info, Snackbar.LENGTH_LONG).show()
    }

    override fun showSpinner() {
        loadingOverlay.show()
    }

    override fun dismissSpinner() {
        if (loadingOverlay.isShowing) {
            loadingOverlay.dismiss()
        }
    }

    //endregion

    //region Options menu

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.context_menu, menu)
        tintAllIcons(menu, ContextCompat.getColor(this, R.color.white))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_uninstall -> {
                onUninstallClicked()
                return true
            }
        }
        return false
    }

    //endregion

    private fun onUninstallClicked() {
        val selectedPositions = multiSelector.selectedPositions

        if (selectedPositions.isEmpty()) {
            Toast.makeText(this, R.string.error_no_apps_selected, Toast.LENGTH_SHORT).show()
            return
        }

        val appsToUninstall = selectedPositions.map { appList[it] }
        uninstallQueue.addAll(appsToUninstall)
        promptNextUninstall()
    }

    private fun promptNextUninstall() {
        val nextApp = uninstallQueue.poll()
        nextApp?.let {
            val uninstallIntent = getUninstallIntent(it.packageName, true)

            packageManager.resolveActivity(uninstallIntent, 0)
                    ?: throw NullPointerException("Unable to uninstall ${it.packageName}")

            appUninstallRequested = it
            startActivityForResult(uninstallIntent, RC_UNINSTALL_APP)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != RC_UNINSTALL_APP) {
            return
        }

        if (resultSuccessful(data)) {
            val appViewModel = appUninstallRequested ?: throw NullPointerException("No app was pending uninstall")
            val removedApp = appList.find { viewModel ->
                viewModel.packageName == appViewModel.packageName
            }
            val indexRemoved = appList.indexOf(removedApp)
            multiSelector.setSelected(indexRemoved, 0, false)
            appList.removeAt(indexRemoved)
            multiSelector.itemRemoved(indexRemoved)
            adapter.notifyItemRemoved(indexRemoved)
        }

        Handler().post {
            promptNextUninstall()
        }
    }

    private fun resultSuccessful(data: Intent?) =
            data != null && data.extras.getInt(EXTRA_INSTALL_RESULT) == EXTRA_UNINSTALL_RESULT_SUCCESS
}
