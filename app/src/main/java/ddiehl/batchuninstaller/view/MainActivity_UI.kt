package ddiehl.batchuninstaller.view;

import ddiehl.batchuninstaller.R
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.verticalLayout

class MainActivity_UI : AnkoComponent<MainActivity> {
    override fun createView(ui: AnkoContext<MainActivity>) = ui.apply {
        verticalLayout { id = R.id.fragment }
    }.view
}