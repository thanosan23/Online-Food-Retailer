package ca.uwaterloo.cs.product

import android.os.Parcel
import android.os.Parcelable
import ca.uwaterloo.cs.bemodels.HasOneImage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.Serializable
import java.util.*

@kotlinx.serialization.Serializable
data class ProductInformation(
    var productId: String = UUID.randomUUID().toString(), // Internal id number of product, should we store this?
    var name: String = "",
    var description: String = "",
    var price: Int = 0,
    var amount: Long = 0,
    override var image: String = "",
    var platform1: Boolean = false,
    var platform2: Boolean = false
) : Serializable, Parcelable, HasOneImage {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: UUID.randomUUID().toString(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readBoolean(),
        parcel.readBoolean()
    )

    fun exportData(fileDir: String) {
        // TODO: platform compatibility
        // TODO: save to platform
        val dir = File(fileDir)
        if (!dir.exists()) {
            dir.mkdir()
        }
        val file = File(dir, "Product-$productId.txt")

        file.createNewFile()
        val stringData = Json.encodeToString(this)
        file.writeText(stringData)
    }

    fun deleteData(fileDir: String) {
        val file = File(fileDir, "Product-$productId.txt")
        val boolean = file.delete()
        println("file deleted ${boolean}")
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

fun copy(productInformation: ProductInformation): ProductInformation{
    return ProductInformation(
        productInformation.productId,
        productInformation.name,
        productInformation.description,
        productInformation.price,
        productInformation.amount,
        productInformation.image,
        productInformation.platform1,
        productInformation.platform2
    )
}
