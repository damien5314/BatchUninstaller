package ddiehl.batchuninstaller.utils

import android.content.Context
import android.text.format.DateUtils
import android.text.format.Formatter

fun formatInstallationDate(timestamp: Long): String {
    return DateUtils.getRelativeTimeSpanString(
            timestamp,
            System.currentTimeMillis(),
            DateUtils.MINUTE_IN_MILLIS,
            0
    ).toString()
}

fun formatFileSize(size: Long, context: Context): String {
    return Formatter.formatShortFileSize(context, size)
}