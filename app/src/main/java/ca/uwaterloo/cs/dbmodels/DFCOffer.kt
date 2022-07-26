package ca.uwaterloo.cs.dbmodels

// Standard
@kotlinx.serialization.Serializable
data class DFCOffer (
    val id: String,
    val type: String = "dfc-b:Offer",
    val price: String,
    val stockLimitation: String,
    val offeres: MutableList<String>
    )