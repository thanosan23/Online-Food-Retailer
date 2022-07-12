package ca.uwaterloo.cs.models

@kotlinx.serialization.Serializable
data class Offer (
    val id: String,
    val price: String
    )