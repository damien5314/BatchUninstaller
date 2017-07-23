package ddiehl.batchuninstaller.view

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import ddiehl.batchuninstaller.R
import ddiehl.batchuninstaller.model.AppViewModel
import ddiehl.batchuninstaller.utils.getUninstallIntent
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

    private val selectedPackages = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LAYOUT_RES_ID)
        setBackgroundColor(R.color.gray)

        savedInstanceState?.let {
            val packages = savedInstanceState.getStringArrayList(STATE_SELECTED_PACKAGES)
            selectedPackages.addAll(packages)
        }

        setSupportActionBar(toolbar)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AppAdapter(this, selectedPackages)
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

        val selectedApps = adapter.selectedPackages
        outState.putStringArrayList(STATE_SELECTED_PACKAGES, ArrayList(selectedApps))
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
        adapter.showApps(apps)
    }

    override fun showUninstallForPackage(packageName: String) {
        startActivityForResult(getUninstallIntent(packageName, true), 0)
    }

    override fun showToast(throwable: Throwable) {
        Snackbar.make(chromeView, R.string.error, Snackbar.LENGTH_LONG).show()
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
}
