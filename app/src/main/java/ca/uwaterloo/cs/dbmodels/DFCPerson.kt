package ca.uwaterloo.cs.dbmodels

data class DFCPerson (
    val id: String,
    val type: String = "dfc-b:Person",
    val firstName: String,
    val familyName: String,
    val hasAddress: Address?,
    val affiliates: List<String>,
    )