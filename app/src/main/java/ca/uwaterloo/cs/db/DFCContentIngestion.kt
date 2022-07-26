package ca.uwaterloo.cs.db

import ca.uwaterloo.cs.dbmodels.*
import ca.uwaterloo.cs.product.ProductInformation

class DFCContentIngestion {

    fun getDFCSuppliedProduct(productInformation: ProductInformation): DFCSuppliedProduct {
        return DFCSuppliedProduct(
            id = "suppliedProduct/${productInformation.productId}",
            referencedBy = mutableListOf(),
            hasType = productInformation.name,
            description = productInformation.description,
            totalTheoreticalStock = productInformation.amount,
            image = productInformation.image,
            lifeTime = "20 days",
        )
    }

    fun getOffer(productInformation: ProductInformation, platform1: Boolean): DFCOffer {
        return DFCOffer(
            "offer/${IdResolver().standardResolver(null, IdType.DFCOfferId)}",
            stockLimitation = if (platform1){
                productInformation.platform1_amount.toString()
            }
            else{
                productInformation.platform2_amount.toString()
            },
            price =
            if (platform1){
                productInformation.platform1_amount.toString()
            }
            else{
                productInformation.platform2_amount.toString()
            },
            offeres = mutableListOf()
        )
    }

    fun getCatalogItem(productInformation: ProductInformation, platform1: Boolean): DFCCatalogItem {
        return DFCCatalogItem(
            "catalogItem/${IdResolver().standardResolver(null, IdType.DFCCatalogItemId)}",
            stockLimitation = if (platform1){
                productInformation.platform1_amount.toString()
            }
                    else{
                        productInformation.platform2_amount.toString()
            },
            offeredThrough = mutableListOf()
        )
    }

    fun getDFCPerson(userIdString: String, completeUserProfile: CompleteUserProfile): DFCPerson{
        return DFCPerson(
            id = "person/$userIdString",
            firstName = completeUserProfile.firstName,
            familyName = completeUserProfile.familyName,
            hasAddress = completeUserProfile.address,
            affiliates = listOf("enterprise/" + completeUserProfile.enterpriseName)
        )
    }

    fun getDFCEnterprise(): DFCEnterprise{
        return DFCEnterprise(
            "enterprise/${IdResolver().standardResolver(null, IdType.DFCEnterpriseId)}",
        )
    }
}