package ca.uwaterloo.cs.dbmodels

// Standard
@kotlinx.serialization.Serializable
data class DFCSuppliedProduct(
    val id: String,
    val type: String = "dfc-b:SuppliedProduct",
    val referencedBy: MutableList<String>,
    val hasUnit: String = "dfc-u:u",
    val hasType: String,
    val description: String,
    val totalTheoreticalStock: Long,
    val image: String,
    val lifeTime: String,
    )