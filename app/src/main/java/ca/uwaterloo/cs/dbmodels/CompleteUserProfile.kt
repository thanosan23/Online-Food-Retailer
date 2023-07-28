package ca.uwaterloo.cs.dbmodels

@kotlinx.serialization.Serializable
data class CompleteUserProfile (
    val firstName: String,
    val familyName: String,
    val email: String,
    val address: Address?,
    val imageURI: String,
    val enterpriseName: String,
    val isFarmer: Boolean,

    // parentFarmer should be empty if isFarmer is true
    val parentFarmerId: String,
    // harvestIds should be empty if isFarmer is true
    var harvestIds: MutableList<String>,

    // productIds should be empty if isFarmer is false
    var productIds: MutableList<String>,
    // workersIds should be empty if isFarmer is false
    val workersIds: MutableList<String>,
    var storeIds: MutableList<String>
)