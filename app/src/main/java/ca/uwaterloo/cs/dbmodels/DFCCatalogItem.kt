package ca.uwaterloo.cs.dbmodels

// Standard
@kotlinx.serialization.Serializable
data class DFCCatalogItem (
    val id: String,
    val type: String = "dfc-b:CatalogItem",
    val sku: String = "catalog item gtin or sku",
    val stockLimitation: String,
    val offeredThrough: MutableList<String>
)
