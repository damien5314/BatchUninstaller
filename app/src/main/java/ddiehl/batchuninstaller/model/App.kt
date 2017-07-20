package ddiehl.batchuninstaller.model

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils

data class App(
        val name: CharSequence,
        var size: Long,
        val packageName: String
) : Parcelable {

    constructor(p0: Parcel) : this(
            TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(p0),
            p0.readLong(),
            p0.readString()
    )

    override fun writeToParcel(p0: Parcel, p1: Int) {
        TextUtils.writeToParcel(name, p0, 0)
        p0.writeLong(size)
        p0.writeString(packageName)
    }

    override fun describeContents(): Int {
        throw UnsupportedOperationException()
    }

    override fun toString(): String {
        return "$name :: $size"
    }

    companion object CREATOR : Parcelable.Creator<App> {
        override fun createFromParcel(parcel: Parcel): App {
            return App(parcel)
        }

        override fun newArray(size: Int): Array<App?> {
            return arrayOfNulls(size)
        }
    }
}