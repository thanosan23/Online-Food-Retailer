package ca.uwaterloo.cs.harvest

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import java.util.*

data class HarvestInformation (
    val harvestId: String = UUID.randomUUID().toString(),
    val fromWorker: String,
    val productId: String,
    val name: String,
    val description: String,
    val image: String,
    var amount: Long,
) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(harvestId)
        parcel.writeString(fromWorker)
        parcel.writeString(productId)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(image)
        parcel.writeLong(amount)
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

}
