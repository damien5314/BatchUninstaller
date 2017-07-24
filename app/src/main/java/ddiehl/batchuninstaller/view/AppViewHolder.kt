package ddiehl.batchuninstaller.view

import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.bignerdranch.android.multiselector.MultiSelector
import com.bignerdranch.android.multiselector.MultiSelectorBindingHolder
import ddiehl.batchuninstaller.R
import ddiehl.batchuninstaller.utils.formatFileSize

class AppViewHolder(
        view: View,
        private val multiSelector: MultiSelector
) : MultiSelectorBindingHolder(view, multiSelector), View.OnClickListener {

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
        checkbox.isClickable = false
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

    //region MultiSelectorBindingHolder

    private var isActivated: Boolean = false

    override fun isActivated(): Boolean {
        return isActivated
    }

    override fun setActivated(isActivated: Boolean) {
        this.isActivated = isActivated
        checkbox.isChecked = isActivated
    }

    override fun isSelectable(): Boolean {
        return true
    }

    override fun setSelectable(p0: Boolean) {
        // no-op
    }

    //endregion
}
