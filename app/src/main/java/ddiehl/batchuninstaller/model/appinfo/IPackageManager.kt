package ddiehl.batchuninstaller.model.appinfo

interface IPackageManager {

    fun getInstalledPackages(type: Int): List<IPackageInfo>

    fun getLaunchIntentForPackage(packageName: String): IIntent?

    fun getApplicationInfo(name: String, flags: Int): IApplicationInfo

    fun getApplicationLabel(applicationInfo: IApplicationInfo): String?
}
