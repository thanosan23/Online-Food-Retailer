package ca.uwaterloo.cs.simulateTransaction

import android.os.Parcelable
import ca.uwaterloo.cs.product.ProductInformation
import java.io.Serializable

@kotlinx.serialization.Serializable
data class Transaction(
    //the four fields in the table should be product name, platform sold, quantity sold, and revenue
    var product: ProductInformation,
    var platformSold: String, //will be either Socleo or Loblaws
    var quantitySold: Int,
    var revenue: Long,
) : Serializable{

}