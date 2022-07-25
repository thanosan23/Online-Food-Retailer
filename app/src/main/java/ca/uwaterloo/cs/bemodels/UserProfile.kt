package ca.uwaterloo.cs.bemodels

import ca.uwaterloo.cs.dbmodels.Address

@kotlinx.serialization.Serializable
data class UserProfile (
    val firstName: String,
    val familyName: String,
    val password: String,
    val email: String,
    val address: Address?,
    val imageURI: String
        )