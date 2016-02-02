package ddiehl.batchuninstaller.utils

import android.content.Context
import android.text.format.Formatter

public fun formatFileSize(size: Long, context: Context): String {
  return Formatter.formatShortFileSize(context, size)
}