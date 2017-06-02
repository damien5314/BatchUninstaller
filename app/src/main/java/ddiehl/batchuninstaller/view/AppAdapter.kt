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
import org.jetbrains.anko.find

class AppAdapter(val mPresenter: MainPresenter, val mMultiSelector: MultiSelector) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int = mPresenter.getNumItems()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        (holder as VH).bind(
                mPresenter.getItemAt(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        val view = LayoutInflater.from(parent?.context)
                .inflate(R.layout.app_item, parent, false)
        return VH(view, mPresenter, mMultiSelector)
    }

    class VH(
            val mView: View,
            val mMainPresenter: MainPresenter,
            val mMultiSelector: MultiSelector) :
            SwappingHolder(mView, mMultiSelector), View.OnClickListener {
        val mName = mView.find<TextView>(R.id.app_name)
        val mSize = mView.find<TextView>(R.id.app_size)
        val mIcon = mView.find<ImageView>(R.id.app_icon)

        init {
            itemView.setOnClickListener(this)
            selectionModeBackgroundDrawable = getAccentStateDrawable(mView.context)
//      selectionModeBackgroundDrawable = ContextCompat.getDrawable(
//          mView.context, R.drawable.selectable_item_background_blue)
        }

        private fun getAccentStateDrawable(context: Context): Drawable {
//      val typedValue = TypedValue();
//      val theme: Resources.Theme = context.theme;
//      theme.resolveAttribute(R.attr.colorAccent, typedValue, true);
//      val colorDrawable = ColorDrawable(typedValue.data);
//      val colorDrawable = ColorDrawable(R.color.blue);
            val stateListDrawable = StateListDrawable();
//      stateListDrawable.addState(IntArray(1) { android.R.attr.state_activated }, colorDrawable);
            stateListDrawable.addState(StateSet.WILD_CARD, null);
            return stateListDrawable;
        }

        fun bind(app: App) {
            mName.text = app.name
            mSize.text = formatFileSize(app.size, mView.context)
            mIcon.setImageDrawable(
                    mView.context.packageManager.getApplicationIcon(app.packageName))
        }

        override fun onClick(v: View?) {
            if (adapterPosition == -1) return
            if (!mMultiSelector.isSelectable) {
                mMainPresenter.onItemSelected(adapterPosition, true)
                mMultiSelector.setSelected(this, true);
            } else if (mMultiSelector.tapSelection(this)) {
                mMainPresenter.onItemSelected(adapterPosition, mMultiSelector.isSelected(adapterPosition, 0))
            } else {
                // OnClick behavior for the individual view
            }
        }
    }
}
