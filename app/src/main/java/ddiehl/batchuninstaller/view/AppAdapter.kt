package ddiehl.batchuninstaller.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bignerdranch.android.multiselector.MultiSelector
import com.bignerdranch.android.multiselector.SwappingHolder
import ddiehl.batchuninstaller.R
import ddiehl.batchuninstaller.model.App
import ddiehl.batchuninstaller.utils.formatFileSize

class AppAdapter(val presenter: MainPresenter, val multiSelector: MultiSelector)
    : RecyclerView.Adapter<AppAdapter.VH>() {

    override fun getItemCount(): Int = presenter.getNumItems()

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(presenter.getItemAt(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
                .inflate(VH.LAYOUT_RES_ID, parent, false)
        return VH(view, presenter, multiSelector)
    }

    class VH(val view: View, val mainPresenter: MainPresenter, val multiSelector: MultiSelector)
        : SwappingHolder(view, multiSelector) {

        companion object {
            val LAYOUT_RES_ID = R.layout.app_item
        }

        val name = view.findViewById<TextView>(R.id.app_name)
        val size = view.findViewById<TextView>(R.id.app_size)
        val icon = view.findViewById<ImageView>(R.id.app_icon)

        init {
            itemView.setOnClickListener { onItemClick() }
        }

        fun bind(app: App) {
            name.text = app.name
            size.text = formatFileSize(app.size, view.context)
            view.context.packageManager.getApplicationIcon(app.packageName).let {
                icon.setImageDrawable(it)
            }
        }

        private fun onItemClick() {
            if (adapterPosition == -1) return

            if (!multiSelector.isSelectable) {
                mainPresenter.onItemSelected(adapterPosition, true)
                multiSelector.setSelected(this, true)
            } else if (multiSelector.tapSelection(this)) {
                mainPresenter.onItemSelected(adapterPosition, multiSelector.isSelected(adapterPosition, 0))
            } else {
                // OnClick behavior for the individual view
            }
        }
    }
}
