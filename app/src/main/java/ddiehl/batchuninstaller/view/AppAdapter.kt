package ddiehl.batchuninstaller.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ddiehl.batchuninstaller.R
import ddiehl.batchuninstaller.model.AppViewModel
import ddiehl.batchuninstaller.utils.formatFileSize
import timber.log.Timber

class AppAdapter(val mainView: MainView) : RecyclerView.Adapter<AppAdapter.VH>() {

    private val appList = mutableListOf<AppViewModel>()

    override fun getItemCount(): Int = appList.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(appList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
                .inflate(VH.LAYOUT_RES_ID, parent, false)
        return VH(view)
    }

    class VH(view: View): RecyclerView.ViewHolder(view) {

        companion object {
            val LAYOUT_RES_ID = R.layout.app_item
        }

        val name = view.findViewById<TextView>(R.id.app_name)
        val size = view.findViewById<TextView>(R.id.app_size)
        val icon = view.findViewById<ImageView>(R.id.app_icon)

        init {
            itemView.setOnClickListener { onItemClick() }
        }

        fun bind(app: AppViewModel) {
            name.text = app.name
            size.text = formatFileSize(app.size, itemView.context)
            itemView.context.packageManager.getApplicationIcon(app.packageName).let {
                icon.setImageDrawable(it)
            }
        }

        private fun onItemClick() {
            Timber.d("Item clicked @ $adapterPosition")
            if (adapterPosition == -1) return
        }
    }

    fun showApps(apps: List<AppViewModel>) {
        appList.clear()
        appList.addAll(apps)
        notifyDataSetChanged()
    }
}
