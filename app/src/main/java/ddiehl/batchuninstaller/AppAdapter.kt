package ddiehl.batchuninstaller

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.jetbrains.anko.*

class AppAdapter(val mPresenter: MainPresenter) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
    (holder as VH).bind(
        mPresenter.getItemAt(position))
  }

  override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
    return VH(VH.UI().createView(
        AnkoContext.create(
            parent!!.context, this)))
  }

  override fun getItemCount(): Int = mPresenter.getNumItems()
}

private class VH(val view: View) : RecyclerView.ViewHolder(view) {
  val mName = view.find<TextView>(R.id.app_name)
  val mSize = view.find<TextView>(R.id.app_size)
  val mIcon = view.find<ImageView>(R.id.app_icon)

  fun bind(app: App) {
    mName.text = app.name
    mSize.text = formatFileSize(app.size, view.context)
    mIcon.setImageDrawable(
        view.context.packageManager.getApplicationIcon(app.packageName))
  }

  class UI : AnkoComponent<AppAdapter> {
    override fun createView(ui: AnkoContext<AppAdapter>): View = ui.apply {
      linearLayout {
        verticalLayout {
          textView {
            id = R.id.app_name
          }
          textView {
            id = R.id.app_size
          }
          lparams {
            width = dip(0)
            weight = 1.0f
          }
        }
        imageView {
          id = R.id.app_icon
          lparams {
            width = dimen(R.dimen.app_icon_width)
            height = dimen(R.dimen.app_icon_width)
          }
        }
        lparams {
          width = matchParent
          margin = dimen(R.dimen.item_row_margin)
        }
      }
    }.view
  }
}
