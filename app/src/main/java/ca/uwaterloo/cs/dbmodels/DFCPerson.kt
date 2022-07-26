package ca.uwaterloo.cs.dbmodels

data class DFCPerson (
    val id: String,
    val type: String = "dfc-b:Person",
    val firstName: String,
    val familyName: String,
    val hasAddress: Address?,
    var affiliates: List<String>,
    )