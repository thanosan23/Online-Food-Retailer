package ca.uwaterloo.cs.bemodels

@kotlinx.serialization.Serializable
data class SignUpWorker (
    val userId: String,
    val firstName: String,
    val familyName: String,
    val farmerUserId: String
)