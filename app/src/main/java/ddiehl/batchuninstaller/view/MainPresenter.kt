package ddiehl.batchuninstaller.view

import ddiehl.batchuninstaller.model.App
import java.util.*

interface MainPresenter {
  fun onStart(): Unit
  fun onStop(): Unit
  fun getNumItems(): Int
  fun getItemAt(position: Int): App
  fun onItemSelected(position: Int, selected: Boolean)
  fun onClickedBatchUninstall()
  fun onItemUninstalled(success: Boolean)
  fun onSelectionsCleared()
  fun saveData(): ArrayList<App>
  fun restoreData(list: ArrayList<App>)
}