package ddiehl.batchuninstaller.view

import android.support.v4.content.ContextCompat
import android.view.View
import ddiehl.batchuninstaller.R
import org.jetbrains.anko.*

class AppAdapter_VH_UI : AnkoComponent<AppAdapter> {
  override fun createView(ui: AnkoContext<AppAdapter>): View = ui.apply {
    linearLayout {
      lparams {
        width = matchParent
        padding = dimen(R.dimen.item_row_margin)
      }
      verticalLayout {
        lparams {
          width = dip(0)
          weight = 1.0f
        }
        textView {
          id = R.id.app_name
          setTextAppearance(R.style.TextAppearance_AppCompat_Medium)
          textColor = ContextCompat.getColor(ui.ctx, R.color.primary_text)
        }
        textView {
          id = R.id.app_size
          setTextAppearance(R.style.TextAppearance_AppCompat_Small)
          textColor = ContextCompat.getColor(ui.ctx, R.color.secondary_text)
        }
      }
      imageView {
        id = R.id.app_icon
        lparams {
          width = dimen(R.dimen.app_icon_width)
          height = dimen(R.dimen.app_icon_width)
        }
      }
    }
  }.view
}