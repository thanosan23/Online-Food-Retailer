package ca.uwaterloo.cs.db

enum class IdType{
    PersonId,
    CatalogItemId,
    OfferId,
    SuppliedProductId,
    FarmerId
}
data class Id(
    val idValue: String,
    val idType: IdType)


fun dataBasePathResolver(idType: IdType): String{
    return when (idType) {
        IdType.PersonId -> {
            "person/"
        }
        IdType.SuppliedProductId -> {
            "suppliedProduct/"
        }
        IdType.OfferId -> {
            "offer/"
        }
        IdType.CatalogItemId -> {
            "catalogItem/"
        }
        IdType.FarmerId -> {
            "farmer"
        }
        else -> ""
    }

}
