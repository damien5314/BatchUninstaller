package ddiehl.batchuninstaller;

import android.content.pm.PackageManager

interface MainView {
  fun showSpinner()
  fun dismissSpinner()
  fun getPackageManager(): PackageManager
  fun notifyDataSetChanged()
}
