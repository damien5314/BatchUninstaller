package ddiehl.batchuninstaller.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.support.v7.widget.RecyclerView
import android.util.StateSet
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
                .inflate(R.layout.app_item, parent, false)
        return VH(view, presenter, multiSelector)
    }

    class VH(val view: View, val mainPresenter: MainPresenter, val multiSelector: MultiSelector)
        : SwappingHolder(view, multiSelector), View.OnClickListener {

        val name = view.findViewById(R.id.app_name) as TextView
        val size = view.findViewById(R.id.app_size) as TextView
        val icon = view.findViewById(R.id.app_icon) as ImageView

        init {
            itemView.setOnClickListener(this)
            selectionModeBackgroundDrawable = getAccentStateDrawable(view.context)
        }

        private fun getAccentStateDrawable(context: Context): Drawable {
            val stateListDrawable = StateListDrawable()
            stateListDrawable.addState(StateSet.WILD_CARD, null)
            return stateListDrawable
        }

        fun bind(app: App) {
            name.text = app.name
            size.text = formatFileSize(app.size, view.context)
            icon.setImageDrawable(
                    view.context.packageManager.getApplicationIcon(app.packageName))
        }

        override fun onClick(v: View) {
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
