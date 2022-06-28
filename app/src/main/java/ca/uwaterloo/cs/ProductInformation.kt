package ca.uwaterloo.cs

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import java.io.Serializable

data class ProductInformation(
    val id: Int = 1, // Internal id number of product, should we store this?
    var name: String = "",
    var description: String = "",
    var price: Int = 0,
    var amount: Long = 0,
    var image: String = "",
    var platform1: Boolean = false,
    var platform2: Boolean = false
) : Serializable, Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readBoolean(),
        parcel.readBoolean()
    )

    fun exportData(context: Context) {
        // TODO: platform compatibility
        // TODO: save to platform
        val dir = File("${context.filesDir}/out")
        if (!dir.exists()) {
            dir.mkdir()
        }
        val file = File(dir, "Product-$id.txt")
        if (file.exists())
        {
            file.delete()
        }
        file.createNewFile()
        val fileOS = FileOutputStream(file)
        val outStream = ObjectOutputStream(fileOS)
        outStream.writeObject(this)
        outStream.close()
        fileOS.close()
    }

    fun deleteData(context: Context) {
        val file = File("${context.filesDir}/out", "$id.txt")
        if (file.exists())
        {
            file.delete()
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeInt(price)
        parcel.writeLong(amount)
        parcel.writeString(image)
        parcel.writeBoolean(platform1)
        parcel.writeBoolean(platform2)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductInformation> {
        override fun createFromParcel(parcel: Parcel): ProductInformation {
            return ProductInformation(parcel)
        }

        override fun newArray(size: Int): Array<ProductInformation?> {
            return arrayOfNulls(size)
        }
    }
}

// TODO: FIX images to image array
// TODO: load data
