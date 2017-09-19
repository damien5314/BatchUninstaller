package ddiehl.batchuninstaller.applist

import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.bignerdranch.android.multiselector.MultiSelector
import com.bignerdranch.android.multiselector.MultiSelectorBindingHolder
import com.ddiehl.timesincetextview.TimeSinceTextView
import ddiehl.batchuninstaller.R
import ddiehl.batchuninstaller.utils.formatFileSize
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AppViewHolder(
        view: View,
        private val multiSelector: MultiSelector
) : MultiSelectorBindingHolder(view, multiSelector), View.OnClickListener {

    companion object {
        val LAYOUT_RES_ID = R.layout.app_item
    }

    val name = view.findViewById<TextView>(R.id.app_name)
    val installationDate = view.findViewById<TimeSinceTextView>(R.id.installation_date)
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
        installationDate.date = app.installationDate / 1000
        size.text = formatFileSize(app.size, itemView.context)
        checkbox.isChecked = isSelected

        getAppIcon(app)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { icon.setImageDrawable(null) }
                .subscribe { icon.setImageDrawable(it) }
    }

    private fun getAppIcon(appViewModel: AppViewModel) = Maybe.defer {
        val applicationIcon = itemView.context.packageManager
                .getApplicationIcon(appViewModel.packageName)
        applicationIcon?.let { Maybe.just(it) } ?: Maybe.empty()
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

    override fun isSelectable(): Boolean = true

    override fun setSelectable(p0: Boolean) = Unit // no-op

    //endregion
}
