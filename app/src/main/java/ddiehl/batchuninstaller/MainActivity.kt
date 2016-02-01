package ddiehl.batchuninstaller

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.setContentView
import java.util.*

class MainActivity : AppCompatActivity(), MainView {
  private val _data: MutableList<App> = ArrayList()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    UI().setContentView(this)
    showSpinner()
    loadData()
    dismissSpinner()
  }

  private fun loadData() {
    val packageList: List<PackageInfo>
        = packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES)
    val packageSet: MutableSet<String> = HashSet()
    val pm = packageManager
    packageList.forEach {
      val intent = pm.getLaunchIntentForPackage(it.packageName)
      if (intent != null && !it.packageName.startsWith("com.android")) {
//        Timber.d(it.packageName + "  -  " + intent.component.className)
        packageSet.add(it.packageName)
      }
    }
    for (packageName in packageSet) {
      val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
      _data.add(
          App(
              packageManager.getApplicationLabel(applicationInfo),
              0, // TODO Calculate size of app
              packageName
          ))
    }
  }

  override fun getNumItems(): Int = _data.size

  override fun getItemAt(position: Int): App {
    return _data[position]
  }

  override fun showSpinner() {
    // TODO
  }

  override fun dismissSpinner() {
    // TODO
  }
}

private class UI : AnkoComponent<MainActivity> {
  override fun createView(ui: AnkoContext<MainActivity>) = ui.apply {
    val recyclerView = recyclerView() {
      layoutManager = LinearLayoutManager(ui.owner)
      adapter = AppAdapter(ui.owner)
    }
  }.view
}