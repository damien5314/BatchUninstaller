package ddiehl.batchuninstaller.applist

import android.os.Parcel
import android.os.Parcelable

data class AppViewModel(
        val name: String,
        val packageName: String,
        var size: Long
) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readLong()
    )

    override fun writeToParcel(p0: Parcel, p1: Int) {
        p0.writeString(name)
        p0.writeString(packageName)
        p0.writeLong(size)
    }

    override fun describeContents(): Int = throw UnsupportedOperationException()

    override fun toString(): String = "$name :: $packageName :: $size"

    companion object CREATOR : Parcelable.Creator<AppViewModel> {
        override fun createFromParcel(parcel: Parcel): AppViewModel = AppViewModel(parcel)
        override fun newArray(size: Int): Array<AppViewModel?> = arrayOfNulls(size)
    }
}
