package ca.uwaterloo.cs.dbmodels

// Standard
@kotlinx.serialization.Serializable
data class CatalogItem (
    val id: String,
    val stockLimitation: String,
    val offeredThrough: List<String>
)
