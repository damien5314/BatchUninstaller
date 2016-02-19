package ddiehl.batchuninstaller.view;

import android.support.v7.widget.LinearLayoutManager
import android.util.TypedValue
import ddiehl.batchuninstaller.R
import ddiehl.batchuninstaller.utils.toolbar
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView

class ApplicationListFragment_UI : AnkoComponent<ApplicationListFragment> {
  override fun createView(ui: AnkoContext<ApplicationListFragment>) = ui.apply {
    verticalLayout {
      toolbar(R.style.ToolbarThemeLightText) {
        id = R.id.toolbar
        lparams(width = matchParent) {
          val tv = TypedValue()
          if (ui.owner.activity.theme.resolveAttribute(R.attr.actionBarSize, tv, true)) {
            height = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics);
          }
        }
        backgroundResource = R.color.primary
        popupTheme = R.style.Theme_AppCompat_Light
        elevation = dip(6).toFloat()
      }
      recyclerView {
        id = R.id.recycler_view
        layoutManager = LinearLayoutManager(ui.owner.activity)
      }
    }
  }.view
}