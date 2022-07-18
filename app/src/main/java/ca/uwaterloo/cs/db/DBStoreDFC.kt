package ca.uwaterloo.cs.db

import ca.uwaterloo.cs.dbmodels.Offer
import ca.uwaterloo.cs.product.ProductInformation

class DBStoreDFC {
    private val dbClient = DBClient()
    private val contentIngestion = ContentIngestion()
    private val idResolver = IdResolver()

    fun storeProductInformation(productCreation: Boolean,
                                suppliedProductId: Id,
                                productInformation: ProductInformation
    ){

        if (productCreation){
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
            suppliedProductId.getPath(),
            suppliedProduct)
    }

    private fun storeCatalogItemGivenProductInformation(
        catalogItemIdString: String?,
        productInformation: ProductInformation,
        offerId: Id){
        val DFCCatalogItemId = idResolver.standardResolver(catalogItemIdString, IdType.DFCCatalogItemId)

        val catalogItem = contentIngestion.getCatalogItem(DFCCatalogItemId.idValue, productInformation, offerId)

        dbClient.store(
            dataBasePathResolver(DFCCatalogItemId.idType) + DFCCatalogItemId.idValue,
            catalogItem
        )
    }

    private fun storeOfferGivenProductInformation(offerIdString: String?, productInformation: ProductInformation): Pair<Offer, Id>{
        val DFCOfferId = idResolver.standardResolver(offerIdString, IdType.DFCOfferId)

        val offer = contentIngestion.getOffer(DFCOfferId.idValue, productInformation)

        dbClient.store(
            dataBasePathResolver(DFCOfferId.idType) + DFCOfferId.idValue,
            offer
        )

        return Pair(offer, DFCOfferId)
    }

}