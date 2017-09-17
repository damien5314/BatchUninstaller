package ddiehl.batchuninstaller.utils

import com.bignerdranch.android.multiselector.MultiSelector

fun MultiSelector.itemRemoved(position: Int) {
    selectedPositions
            .filter { it >= position }
            .forEach {
                setSelected(it, 0, false)
                setSelected(it - 1, 0, true)
            }
}
