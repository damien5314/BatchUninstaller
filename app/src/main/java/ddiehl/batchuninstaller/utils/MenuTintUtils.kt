package ddiehl.batchuninstaller.utils

import android.support.annotation.ColorInt
import android.support.v4.graphics.drawable.DrawableCompat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import ddiehl.batchuninstaller.R

/**
 * Tints menu icons similar to the AppCompat style
 *
 *
 * http://stackoverflow.com/a/33697621/3238938
 */

fun tintAllIcons(menu: Menu, @ColorInt color: Int) {
    for (i in 0..menu.size() - 1) {
        val item = menu.getItem(i)
        tintMenuItemIcon(color, item)
        tintShareIconIfPresent(color, item)
    }
}

private fun tintMenuItemIcon(color: Int, item: MenuItem) {
    val drawable = item.icon
    if (drawable != null) {
        val wrapped = DrawableCompat.wrap(drawable)
        drawable.mutate()
        DrawableCompat.setTint(wrapped, color)
        item.icon = drawable
    }
}

private fun tintShareIconIfPresent(color: Int, item: MenuItem) {
    if (item.actionView != null) {
        val actionView = item.actionView
        val expandActivitiesButton = actionView.findViewById<View>(R.id.expand_activities_button)
        if (expandActivitiesButton != null) {
            val image = expandActivitiesButton.findViewById<ImageView>(R.id.image)
            if (image != null) {
                val drawable = image.drawable
                val wrapped = DrawableCompat.wrap(drawable)
                drawable.mutate()
                DrawableCompat.setTint(wrapped, color)
                image.setImageDrawable(drawable)
            }
        }
    }
}
