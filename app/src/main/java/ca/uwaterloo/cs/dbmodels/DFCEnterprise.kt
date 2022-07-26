package ca.uwaterloo.cs.dbmodels

data class DFCEnterprise(
    val id: String,
    val type: String = "dfc-b:Enterprise",
    val supplies: MutableList<String> = mutableListOf(),
    val manages: MutableList<String> = mutableListOf(),
)