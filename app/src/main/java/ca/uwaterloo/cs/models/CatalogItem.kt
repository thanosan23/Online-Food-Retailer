package ca.uwaterloo.cs.models

@kotlinx.serialization.Serializable
data class CatalogItem (
    val id: String,
    val stockLimitation: String,
    val offeredThrough: List<String>
)
