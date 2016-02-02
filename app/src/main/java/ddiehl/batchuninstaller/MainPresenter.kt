package ddiehl.batchuninstaller

interface MainPresenter {
  fun onResume(): Unit
  fun onPause(): Unit
  fun getNumItems(): Int
  fun getItemAt(position: Int): App
}