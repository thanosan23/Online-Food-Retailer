package ca.uwaterloo.cs.db

import ca.uwaterloo.cs.bemodels.SignUpFarmer
import ca.uwaterloo.cs.bemodels.SignUpWorker
import ca.uwaterloo.cs.dbmodels.*
import ca.uwaterloo.cs.product.ProductInformation

class ContentIngestion {
    fun getSuppliedProduct(productId: String, productInformation: ProductInformation, catalogItemId: List<String>): SuppliedProduct{
        return SuppliedProduct(
            productId,
            productInformation.description,
            getQuantity(productInformation),
            referencedBy = catalogItemId,
            platform1 = productInformation.platform1,
            platform2 = productInformation.platform2
        )
    }

    fun getOffer(offerId: String, productInformation: ProductInformation): Offer {
        return Offer(
            offerId,
            productInformation.price.toString()
        )
    }

    fun getCatalogItem(catalogItemId: String, productInformation: ProductInformation, offerId: Id): CatalogItem{
        return CatalogItem(
            catalogItemId,
            productInformation.amount.toString(),
            listOf(offerId.idValue)
        )
    }

    private fun getQuantity(productInformation: ProductInformation): Quantity{
        return Quantity(
            "u",
            productInformation.amount.toString()
        )
    }

    fun getCompleteUserProfile(signUpFarmer: SignUpFarmer): CompleteUserProfile{
        return CompleteUserProfile(
            signUpFarmer.firstName,
            signUpFarmer.familyName,
            "",
            null,
            "",
            true,
            "",
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
        )
    }

    fun getCompleteUserProfile(signUpWorker: SignUpWorker): CompleteUserProfile{
        return CompleteUserProfile(
            signUpWorker.firstName,
            signUpWorker.familyName,
            "",
            null,
            "",
            false,
            signUpWorker.farmerUserId,
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
        )
    }
}
