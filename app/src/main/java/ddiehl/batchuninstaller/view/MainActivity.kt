package ddiehl.batchuninstaller.view

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.bignerdranch.android.multiselector.MultiSelector
import ddiehl.batchuninstaller.R
import ddiehl.batchuninstaller.utils.setBackgroundColor
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity(), MainView {

    companion object {
        private val LAYOUT_RES_ID = R.layout.main_activity
        private val EXTRA_INSTALL_RESULT = "android.intent.extra.INSTALL_RESULT"
        private val STATE_SELECTED_PACKAGES = "selectedPackages"
    }

    private val mainPresenter: MainPresenter = MainPresenter()

    private lateinit var adapter: AppAdapter
    private lateinit var loadingOverlay: ProgressDialog
    private val multiSelector = MultiSelector()

    override val appList = mutableListOf<AppViewModel>()
    private var selectedPackages: Array<String>? = null

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            val successful = data.extras.get(EXTRA_INSTALL_RESULT) == 1
//            mainPresenter.onItemUninstalled(successful)
        } else {
//            mainPresenter.onItemUninstalled(false)
        }
    }

    //region MainView

    override fun showApps(apps: List<AppViewModel>) {
        appList.clear()
        appList.addAll(apps)

        selectedPackages?.let { selectedPackages ->
            appList.forEachIndexed { index, appViewModel ->
                if (selectedPackages.contains(appViewModel.packageName)) {
                    multiSelector.setSelected(index, 0, true)
                }
            }
            this.selectedPackages = null
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

        //TODO launch uninstall flow
    }
}
