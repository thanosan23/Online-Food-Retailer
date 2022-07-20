package ca.uwaterloo.cs.harvest

import android.os.Parcel
import android.os.Parcelable
import ca.uwaterloo.cs.bemodels.HasOneImage
import ca.uwaterloo.cs.product.ProductInformation
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.util.*

@kotlinx.serialization.Serializable
data class HarvestInformation (
    var harvestId: String?,
    val fromWorker: String,
    var productId: String?,
    val name: String,
    val description: String,
    override var image: String,
    var amount: Int,
) : Serializable, Parcelable, HasOneImage {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt()
    )

    constructor(fromWorker: String, product: ProductInformation, amount: Int): this (
        UUID.randomUUID().toString(),
        fromWorker,
        product.productId,
        product.name,
        product.description,
        product.image,
        amount
    )

    constructor(fromWorker: String, name: String, description: String, image: String, amount: Int): this (
        UUID.randomUUID().toString(),
        fromWorker,
        "",
        name,
        description,
        image,
        amount
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(harvestId)
        parcel.writeString(fromWorker)
        parcel.writeString(productId)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(image)
        parcel.writeInt(amount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HarvestInformation> {
        override fun createFromParcel(parcel: Parcel): HarvestInformation {
            return HarvestInformation(parcel)
        }

        override fun newArray(size: Int): Array<HarvestInformation?> {
            return arrayOfNulls(size)
        }
    }

    fun exportData(fileDir: String) {
        val dir = File("${fileDir}/out2")
        if (!dir.exists()) {
            dir.mkdir()
        }
        val file = File(dir, "Harvest-$harvestId.txt")
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
        val file = File("${fileDir}/out2", "Harvest-$harvestId.txt")
        if (file.exists())
        {
            file.delete()
        }
    }
}
