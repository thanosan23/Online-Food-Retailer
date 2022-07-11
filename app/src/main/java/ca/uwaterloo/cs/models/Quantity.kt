package ca.uwaterloo.cs.models

@kotlinx.serialization.Serializable
data class Quantity(
    val hasUnit: String,
    val value: String
)