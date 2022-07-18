package ca.uwaterloo.cs.dbmodels

// Standard
@kotlinx.serialization.Serializable
data class SuppliedProduct (
    val id: String,
    val description: String,
    val quantity: Quantity,
    val referencedBy: List<String>,
    // TODO handle images
    // data not from the standard
    val platform1: Boolean,
    val platform2: Boolean
    )