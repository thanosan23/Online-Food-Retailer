package ca.uwaterloo.cs.dbmodels

// Standard
@kotlinx.serialization.Serializable
data class Quantity(
    val hasUnit: String,
    val value: String
)