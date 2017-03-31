package ddiehl.batchuninstaller.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import ddiehl.batchuninstaller.R
import org.jetbrains.anko.setContentView

/**
 * TODO
 * Maintain selections on rotation
 * Change background of selected items to lighter material color
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivity_UI().setContentView(this)
        if (supportFragmentManager.findFragmentById(R.id.fragment) == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment, ApplicationListFragment())
                    .commit()
        }
    }
}
