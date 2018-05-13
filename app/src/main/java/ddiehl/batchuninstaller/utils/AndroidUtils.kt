package ddiehl.batchuninstaller.utils

import android.content.Intent
import android.net.Uri

fun getUninstallIntent(packageName: String, returnResult: Boolean): Intent =
    Intent().apply {
        action = Intent.ACTION_UNINSTALL_PACKAGE
        data = Uri.parse("package:" + packageName)
        putExtra(Intent.EXTRA_RETURN_RESULT, returnResult)
    }
