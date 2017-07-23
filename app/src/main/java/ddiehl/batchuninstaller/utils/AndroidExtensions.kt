package ddiehl.batchuninstaller.utils

import android.app.Activity
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat

fun Activity.setBackgroundColor(@ColorRes colorRes: Int) {
    val colorInt = ContextCompat.getColor(this, colorRes)
    this.window.decorView.setBackgroundColor(colorInt)
}
