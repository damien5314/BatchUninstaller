package ddiehl.batchuninstaller

import android.content.Context
import android.text.format.Formatter

public fun formatFileSize(size: Long, context: Context): String {
  return Formatter.formatFileSize(context, size)
}