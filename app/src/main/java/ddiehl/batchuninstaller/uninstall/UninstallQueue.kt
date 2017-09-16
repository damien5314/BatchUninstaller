package ddiehl.batchuninstaller.uninstall

import ddiehl.batchuninstaller.applist.AppViewModel
import java.util.*

class UninstallQueue {

    private val data = LinkedList<AppViewModel>()

    fun addAll(apps: List<AppViewModel>) {
        data.addAll(apps)
    }

    fun next(): AppViewModel? = data.poll()

    fun size() = data.size
}
