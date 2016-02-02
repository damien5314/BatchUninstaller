package ddiehl.batchuninstaller.view;

import android.content.pm.PackageManager

interface MainView {
  fun showSpinner()
  fun dismissSpinner()
  fun getPackageManager(): PackageManager
  fun notifyDataSetChanged()
  fun activateSelectionMode()
  fun onDataUpdated(index: Int)
  fun getSelectedPositions(): List<Int>
  fun showUninstallForPackage(packageName: String)
}
