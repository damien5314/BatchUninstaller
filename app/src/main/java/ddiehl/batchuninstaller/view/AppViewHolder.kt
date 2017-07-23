package ddiehl.batchuninstaller.view

import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.bignerdranch.android.multiselector.MultiSelector
import com.bignerdranch.android.multiselector.SwappingHolder
import ddiehl.batchuninstaller.R
import ddiehl.batchuninstaller.model.AppViewModel
import ddiehl.batchuninstaller.utils.formatFileSize

class AppViewHolder(
        view: View,
        private val multiSelector: MultiSelector
) : SwappingHolder(view, multiSelector), View.OnClickListener {

    companion object {
        val LAYOUT_RES_ID = R.layout.app_item
    }

    val name = view.findViewById<TextView>(R.id.app_name)
    val size = view.findViewById<TextView>(R.id.app_size)
    val icon = view.findViewById<ImageView>(R.id.app_icon)
    val checkbox = view.findViewById<CheckBox>(R.id.selection_check_box)

    var app: AppViewModel? = null

    init {
        itemView.setOnClickListener(this)
    }

    fun bind(app: AppViewModel, isSelected: Boolean) {
        this.app = app

        name.text = app.name
        size.text = formatFileSize(app.size, itemView.context)
        itemView.context.packageManager.getApplicationIcon(app.packageName).let {
            icon.setImageDrawable(it)
        }
        checkbox.isChecked = isSelected
    }

    override fun onClick(view: View) {
        if (adapterPosition == -1) return

        if (!multiSelector.isSelectable) {
            multiSelector.isSelectable = true
        }
        multiSelector.tapSelection(this)
    }
}
