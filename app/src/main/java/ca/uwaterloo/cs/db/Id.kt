package ca.uwaterloo.cs.db

import java.util.*

enum class IdType{
    DFCPersonId,
    DFCCatalogItemId,
    DFCOfferId,
    DFCSuppliedProductId,
    DFCFarmerId,
    DFCEnterpriseId,
    CompleteUserProfileId,
    ProductId,
    HarvestId,
    FarmCodeId,
    DFCStandardId
}
class Id(
    val idValue: String,
    val idType: IdType) {

    fun getPath(): String {
        return dataBasePathResolver(idType) + idValue
    }
}


fun dataBasePathResolver(idType: IdType): String{
    return when (idType) {
        IdType.DFCPersonId -> {
            "DFCPerson/"
        }
        IdType.DFCSuppliedProductId -> {
            "DFCSuppliedProduct/"
        }
        IdType.DFCOfferId -> {
            "DFCOffer/"
        }
        IdType.DFCCatalogItemId -> {
            "DFCCatalogItem/"
        }
        IdType.DFCFarmerId -> {
            "DFCFarmer/"
        }
        IdType.CompleteUserProfileId -> {
            "CompleteUserProfile/"
        }
        IdType.ProductId -> {
            "Product/"
        }
        IdType.HarvestId -> {
            "Harvest/"
        }
        IdType.FarmCodeId -> {
            "FarmCode/"
        }
        IdType.DFCStandardId -> {
            "DFCUserData/"
        }
        else -> ""
    }

}

fun idGenerator(idType: IdType): Id{
    val id: String = UUID.randomUUID().toString()
    return Id(id, idType)
}

class IdResolver{
    fun standardResolver(key: String?, idType: IdType): Id{
        return if (key == null){
            idGenerator(idType)
        } else{
            Id(key, idType)
        }
    }

    fun getCatalogItemIdsGivenProductId(): List<Id>{
        return listOf(Id("messi", IdType.DFCCatalogItemId))
    }
}
