package ca.uwaterloo.cs.dbmodels

// Standard
@kotlinx.serialization.Serializable
data class Offer (
    val id: String,
    val price: String
    )