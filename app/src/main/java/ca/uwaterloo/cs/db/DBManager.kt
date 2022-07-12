package ca.uwaterloo.cs.db

import ca.uwaterloo.cs.models.Farmer
import ca.uwaterloo.cs.models.Offer
import ca.uwaterloo.cs.product.ProductInformation

class DBManager {
    private val dbStoreManager = DBStoreManager()
    private val dbGetManager = DBGetManager()

    fun storeFarmer(farmerIdString: String?, farmer: Farmer){
        dbStoreManager.storeFarmer(farmerIdString, farmer)
    }

    fun storeProductInformation(productIdString: String?,
                                productInformation: ProductInformation){
        dbStoreManager.storeProductInformation(productIdString, productInformation)
    }

}

class DBGetManager(){
    fun getStoreProductInformation(){

    }

}

class DBStoreManager {
    private val dbClient = DBClient()
    private val contentIngestion = ProductInformationContentIngestion()
    private val idResolver = IdResolver()

    fun storeFarmer(farmerIdString: String?, farmer: Farmer){
        val farmerId = idResolver.standardResolver(farmerIdString, IdType.FarmerId)

        farmer.id = farmerId.idValue

        dbClient.store(
            dataBasePathResolver(farmerId.idType) + farmerId.idValue,
            farmer
        )
    }

    fun storeProductInformation(productIdString: String?,
                                productInformation: ProductInformation){
        val suppliedProductId = idResolver.standardResolver(productIdString, IdType.SuppliedProductId)

        if (productIdString == null){
            val pair = storeOfferGivenProductInformation(null, productInformation)
            val offerId = pair.second
            storeCatalogItemGivenProductInformation(null, productInformation, offerId)
        }

        val suppliedProduct = contentIngestion.getSuppliedProduct(
                                suppliedProductId.idValue,
                                productInformation,
                                idResolver.getCatalogItemIdsGivenProductId().map {
                                    it.idValue
                                })
        dbClient.store(
            dataBasePathResolver(suppliedProductId.idType) + suppliedProductId.idValue,
            suppliedProduct)
    }

    private fun storeCatalogItemGivenProductInformation(
                                                catalogItemIdString: String?,
                                                productInformation: ProductInformation,
                                                offerId: Id){
        val catalogItemId = idResolver.standardResolver(catalogItemIdString, IdType.CatalogItemId)

        val catalogItem = contentIngestion.getCatalogItem(catalogItemId.idValue, productInformation, offerId)

        dbClient.store(
            dataBasePathResolver(catalogItemId.idType) + catalogItemId.idValue,
            catalogItem
        )
    }

    private fun storeOfferGivenProductInformation(offerIdString: String?, productInformation: ProductInformation): Pair<Offer, Id>{
        val offerId = idResolver.standardResolver(offerIdString, IdType.OfferId)

        val offer = contentIngestion.getOffer(offerId.idValue, productInformation)

        dbClient.store(
            dataBasePathResolver(offerId.idType) + offerId.idValue,
            offer
        )

        return Pair(offer, offerId)
    }

}

class DBInterFaceTest(){
    fun test(){
        val dbManager = DBManager()
        val productInformation =
            ProductInformation(
                "id1",
                "name",
                "description",
                12,
                13L,
                "aas",
                platform1 = true,
                platform2 = false
            )
        dbManager.storeProductInformation(null, productInformation)
    }
}

class IdResolver{

    fun standardResolver(key: String?, idType: IdType): Id{
        return if (key == null){
            idGenerator(IdType.FarmerId)
        } else{
            Id(key, IdType.FarmerId)
        }
    }

    fun getCatalogItemIdsGivenProductId(): List<Id>{
        return listOf(Id("messi", IdType.CatalogItemId))
    }
}
