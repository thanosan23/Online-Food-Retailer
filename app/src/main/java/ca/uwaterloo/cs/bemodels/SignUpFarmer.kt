package ca.uwaterloo.cs.bemodels

@kotlinx.serialization.Serializable
data class SignUpFarmer (
    val userId: String,
    val firstName: String,
    val familyName: String,
    val enterpriseName: String,
)