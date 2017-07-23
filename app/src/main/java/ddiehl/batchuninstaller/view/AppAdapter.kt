package ddiehl.batchuninstaller.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bignerdranch.android.multiselector.MultiSelector
import ddiehl.batchuninstaller.model.AppViewModel

class AppAdapter(
        private val mainView: MainView,
        private val multiSelector: MultiSelector
) : RecyclerView.Adapter<AppViewHolder>() {

    private val appList = mutableListOf<AppViewModel>()

    override fun getItemCount(): Int = appList.size

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val app = appList[position]
        val isSelected = multiSelector.isSelected(position, 0)
        holder.bind(app, isSelected)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(AppViewHolder.LAYOUT_RES_ID, parent, false)

        return AppViewHolder(view, multiSelector)
    }

    fun showApps(apps: List<AppViewModel>) {
        appList.clear()
        appList.addAll(apps)

        notifyDataSetChanged()
    }
}
