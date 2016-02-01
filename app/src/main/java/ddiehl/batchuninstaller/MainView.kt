package ddiehl.batchuninstaller;

interface MainView {
  fun getNumItems(): Int
  fun getItemAt(position: Int): App
  fun showSpinner()
  fun dismissSpinner()
}
