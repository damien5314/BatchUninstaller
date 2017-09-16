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
import ddiehl.batchuninstaller.uninstall.UninstallQueue
import ddiehl.batchuninstaller.utils.getUninstallIntent
import ddiehl.batchuninstaller.utils.setBackgroundColor
import ddiehl.batchuninstaller.utils.tintAllIcons
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity(), MainView {

    companion object {
        private val LAYOUT_RES_ID = R.layout.main_activity
        private val RC_UNINSTALL_APP = 10
        private val STATE_SELECTED_PACKAGES = "selectedPackages"

        // Values returned from Android uninstall Activity, cannot modify
        private val EXTRA_INSTALL_RESULT = "android.intent.extra.INSTALL_RESULT"
        private val EXTRA_UNINSTALL_RESULT_SUCCESS = 1
    }

    private lateinit var mainPresenter: MainPresenter
    private lateinit var adapter: AppAdapter
    private lateinit var loadingOverlay: ProgressDialog
    private val multiSelector = MultiSelector()

    override val appList = mutableListOf<AppViewModel>()
    private val uninstallQueue = UninstallQueue()
    private var selectedPackages: Array<String>? = null
    private var appUninstallRequested: AppViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LAYOUT_RES_ID)
        setBackgroundColor(R.color.gray)
        setSupportActionBar(toolbar)

        savedInstanceState?.let {
            selectedPackages = savedInstanceState.getStringArray(STATE_SELECTED_PACKAGES)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AppAdapter(this, multiSelector)
        recyclerView.adapter = adapter

        loadingOverlay = ProgressDialog(this, R.style.ProgressDialog)
        loadingOverlay.setCancelable(false)
        loadingOverlay.setProgressStyle(ProgressDialog.STYLE_SPINNER)

        val appDataLoader = AppDataLoader(APackageManager(packageManager))
        mainPresenter = MainPresenter(appDataLoader)
    }

    override fun onStart() {
        super.onStart()
        mainPresenter.onViewAttached(this)
    }

    override fun onStop() {
        mainPresenter.onViewDetached(this)
        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val selectedPackages =
                multiSelector.selectedPositions.map { position -> appList[position] }
                        .map { app -> app.packageName }
                        .toTypedArray()

        outState.putStringArray(STATE_SELECTED_PACKAGES, selectedPackages)
    }

    //region MainView

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
        uninstallQueue.next()?.let { appToUninstall ->
            val uninstallIntent = getUninstallIntent(appToUninstall.packageName, true)

            packageManager.resolveActivity(uninstallIntent, 0)
                    ?: throw NullPointerException("Unable to uninstall ${appToUninstall.packageName}")

            appUninstallRequested = appToUninstall
            startActivityForResult(uninstallIntent, RC_UNINSTALL_APP)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != RC_UNINSTALL_APP || data == null) {
            return
        }

        if (resultSuccessful(data)) {
            val appViewModel = appUninstallRequested
            val indexRemoved = appList.indexOf(appViewModel)
            multiSelector.setSelected(indexRemoved, 0, false)
            appList.removeAt(indexRemoved)
            adapter.notifyItemRemoved(indexRemoved)
        }

        Handler().post {
            promptNextUninstall()
        }
    }

    private fun resultSuccessful(data: Intent) =
            data.extras.getInt(EXTRA_INSTALL_RESULT) == EXTRA_UNINSTALL_RESULT_SUCCESS
}
