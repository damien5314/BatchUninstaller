package ddiehl.batchuninstaller.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import ddiehl.batchuninstaller.R
import ddiehl.batchuninstaller.model.AppViewModel
import ddiehl.batchuninstaller.utils.formatFileSize
import timber.log.Timber

class AppAdapter(
        private val mainView: MainView,
        selectedPackages: List<String> = emptyList()
) : RecyclerView.Adapter<AppAdapter.VH>() {

    interface Listener {
        fun onItemSelected(app: AppViewModel)
    }

    private val appList = mutableListOf<AppViewModel>()
    val selectedPackages = selectedPackages.toMutableList()

    override fun getItemCount(): Int = appList.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val app = appList[position]
        val isSelected = selectedPackages.contains(app.packageName)
        holder.bind(app, isSelected)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
                .inflate(VH.LAYOUT_RES_ID, parent, false)

        val listener = object : Listener {
            override fun onItemSelected(app: AppViewModel) {
                Timber.d("Clicked :: ${app.packageName}")

                if (selectedPackages.contains(app.packageName)) {
                    selectedPackages -= app.packageName
                } else {
                    selectedPackages += app.packageName
                }
                notifyItemChanged(appList.indexOf(app))
            }
        }

        return VH(view, listener)
    }

    fun showApps(apps: List<AppViewModel>) {
        appList.clear()
        appList.addAll(apps)

        val loadedPackages = appList.map { app -> app.packageName }
        selectedPackages.removeAll { !loadedPackages.contains(it) }

        notifyDataSetChanged()
    }

    class VH(view: View, private val listener: Listener)
        : RecyclerView.ViewHolder(view) {

        companion object {
            val LAYOUT_RES_ID = R.layout.app_item
        }

        val name = view.findViewById<TextView>(R.id.app_name)
        val size = view.findViewById<TextView>(R.id.app_size)
        val icon = view.findViewById<ImageView>(R.id.app_icon)
        val checkbox = view.findViewById<CheckBox>(R.id.selection_check_box)

        var app: AppViewModel? = null

        init {
            itemView.setOnClickListener {
                if (adapterPosition == -1) return@setOnClickListener

                app?.let { listener.onItemSelected(it) }
            }
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
    }
}
