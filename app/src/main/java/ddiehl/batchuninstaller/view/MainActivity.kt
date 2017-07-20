package ddiehl.batchuninstaller.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import ddiehl.batchuninstaller.R

/**
 * TODO
 * Maintain selections on rotation
 * Change background of selected items to lighter material color
 */
class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

//        actionMode = null
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

//        if (supportFragmentManager.findFragmentById(R.id.fragment) == null) {
//            supportFragmentManager.beginTransaction()
//                    .replace(R.id.fragment, ApplicationListFragment())
//                    .commit()
//        }
    }

}
