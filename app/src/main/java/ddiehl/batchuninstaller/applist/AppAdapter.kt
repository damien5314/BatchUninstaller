package ddiehl.batchuninstaller.applist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bignerdranch.android.multiselector.MultiSelector

class AppAdapter(
        private val mainView: MainView,
        private val multiSelector: MultiSelector
) : RecyclerView.Adapter<AppViewHolder>() {

    override fun getItemCount(): Int = mainView.appList.size

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val app = mainView.appList[position]
        val isSelected = multiSelector.isSelected(position, 0)
        holder.bind(app, isSelected)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(AppViewHolder.LAYOUT_RES_ID, parent, false)

        return AppViewHolder(view, multiSelector)
    }
}
