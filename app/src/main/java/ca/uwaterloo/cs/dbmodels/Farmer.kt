package ca.uwaterloo.cs.dbmodels

// Standard
@kotlinx.serialization.Serializable
data class Farmer (
    var id: String,
    val hasAddress: Address,
    val firstName: String,
    val familyName: String,
    val affiliates: List<String>
)