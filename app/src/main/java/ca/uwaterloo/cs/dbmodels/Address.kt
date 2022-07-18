package ca.uwaterloo.cs.dbmodels

// Standard
@kotlinx.serialization.Serializable
data class Address (
    val city: String,
    val country: String,
    val postcode: String,
    val street: String,
    )