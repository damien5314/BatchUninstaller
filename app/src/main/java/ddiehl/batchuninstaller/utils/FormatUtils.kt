package ddiehl.batchuninstaller.utils

import android.content.Context
import android.text.format.Formatter

fun formatFileSize(size: Long, context: Context): String {
  return Formatter.formatShortFileSize(context, size)
}