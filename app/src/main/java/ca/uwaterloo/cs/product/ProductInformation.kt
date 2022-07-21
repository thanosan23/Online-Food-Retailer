package ca.uwaterloo.cs.product

import android.os.Parcel
import android.os.Parcelable
import ca.uwaterloo.cs.bemodels.HasOneImage
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.util.*

@kotlinx.serialization.Serializable
data class ProductInformation(
    var productId: String? = UUID.randomUUID().toString(), // Internal id number of product, should we store this?
    var name: String = "",
    var description: String = "",
    var price: Int = 0,
    var amount: Long = 0,
    override var image: String = "",
    var platform1: Boolean = false,
    var platform2: Boolean = false,
    var platform1_price: Int=0,
    var platform2_price: Int=0,
    var platform1_amount: Long=0,
    var platform2_amount: Long=0
) : Serializable, Parcelable, HasOneImage {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: UUID.randomUUID().toString(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readBoolean(),
        parcel.readBoolean(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readLong(),
        parcel.readLong()
    )

    fun exportData(fileDir: String) {
        // TODO: platform compatibility
        // TODO: save to platform
        val dir = File("${fileDir}/out")
        if (!dir.exists()) {
            dir.mkdir()
        }
        val file = File(dir, "Product-$productId.txt")
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

    fun deleteData(fileDir: String) {
        val file = File("${fileDir}/out", "Product-$productId.txt")
        if (file.exists())
        {
            file.delete()
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(productId)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeInt(price)
        parcel.writeLong(amount)
        parcel.writeString(image)
        parcel.writeBoolean(platform1)
        parcel.writeBoolean(platform2)
        parcel.writeInt(platform1_price)
        parcel.writeInt(platform2_price)
        parcel.writeLong(platform1_amount)
        parcel.writeLong(platform2_amount)
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
