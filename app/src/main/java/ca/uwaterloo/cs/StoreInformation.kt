package ca.uwaterloo.cs

import android.os.Parcel
import android.os.Parcelable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.Serializable
import java.util.*
import kotlin.collections.HashMap

@kotlinx.serialization.Serializable
data class StoreInformation(
    var storeId: String = UUID.randomUUID().toString(),
    var name: String = "",
    var productAmount : Int = 0,
    var products: kotlin.collections.HashMap<String, Int> = hashMapOf()
) : Serializable, Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: UUID.randomUUID().toString(),
        parcel.readString() ?: "",
        parcel.readInt() ?: 0
    ) {
        for(i in 1..this.productAmount) {
            val key : String = parcel.readString()!!;
            val value : Int = parcel.readInt()!!;
            products[key] = value;
        }
    }

    fun exportData(fileDir: String) {
        val dir = File(fileDir)
        if (!dir.exists()) {
            dir.mkdir()
        }
        val file = File(dir, "Store-$storeId.txt")

        file.createNewFile()
        val stringData = Json.encodeToString(this)
        file.writeText(stringData)
    }

    fun deleteData(fileDir: String) {
        val file = File(fileDir, "Store-$storeId.txt")
        val boolean = file.delete()
        println("file deleted ${boolean}")
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(storeId)
        parcel.writeString(name)
        parcel.writeInt(productAmount);
        for((key, value) in this.products) {
            parcel.writeString(key);
            parcel.writeInt(value);
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StoreInformation> {
        override fun createFromParcel(parcel: Parcel): StoreInformation {
            return StoreInformation(parcel)
        }

        override fun newArray(size: Int): Array<StoreInformation?> {
            return arrayOfNulls(size)
        }
    }
}

fun copy(productInformation: StoreInformation): StoreInformation{
    return StoreInformation(
        productInformation.storeId,
        productInformation.name
    )
}
