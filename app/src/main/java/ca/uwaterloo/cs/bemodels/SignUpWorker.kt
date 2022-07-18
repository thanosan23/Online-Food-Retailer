package ca.uwaterloo.cs.bemodels

@kotlinx.serialization.Serializable
data class SignUpWorker (
    val userId: String,
    val password: String,
    val firstName: String,
    val familyName: String,
    val enterpriseName: String
)