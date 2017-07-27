package ddiehl.batchuninstaller.model.appinfo.impl

import android.content.pm.PackageInfo
import ddiehl.batchuninstaller.model.appinfo.IPackageInfo

class APackageInfo(private val packageInfo: PackageInfo) : IPackageInfo {

    override val packageName: String
        get() = packageInfo.packageName
}
