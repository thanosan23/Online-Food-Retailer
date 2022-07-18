package ca.uwaterloo.cs.bemodels

@kotlinx.serialization.Serializable
data class SignUpWorker (
    val workerUserId: String,
    val firstName: String,
    val familyName: String,
    var farmerUserId: String
)