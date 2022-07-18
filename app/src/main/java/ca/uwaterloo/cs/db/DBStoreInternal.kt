package ca.uwaterloo.cs.db

import ca.uwaterloo.cs.Listener
import ca.uwaterloo.cs.bemodels.SignUpFarmer
import ca.uwaterloo.cs.bemodels.SignUpWorker
import ca.uwaterloo.cs.dbmodels.CompleteUserProfile
import ca.uwaterloo.cs.harvest.HarvestInformation
import ca.uwaterloo.cs.product.ProductInformation

class DBStoreInternal {
    private val dbClient = DBClient()
    private val contentIngestion = ContentIngestion()

    fun storeProductInformation(productCreation: Boolean,
                                userId: String,
                                productId: Id,
                                productInformation: ProductInformation){
        if (productCreation){
            addProductToUser(userId, productId)
        }
        productInformation.productId = productId.idValue
        dbClient.store(
            productId.getPath(),
            productInformation
        )
    }

    fun storeHarvestInformation(harvestCreation: Boolean,
                                workerId: String,
                                harvestId: Id,
                                harvestInformation: HarvestInformation){
        if (harvestCreation){
            addHarvestToUser(workerId, harvestId)
        }
        harvestInformation.harvestId = harvestId.idValue
        dbClient.store(
            harvestId.getPath(),
            harvestInformation
        )
    }

    fun storeSignUpFarmer(signUpFarmer: SignUpFarmer, farmCodeId: Id){
        dbClient.store(
            farmCodeId.getPath(),
            signUpFarmer.userId
        )
        
        val completeFarmerProfile = contentIngestion.getCompleteUserProfile(signUpFarmer)
        val completeFarmerProfileId = Id(signUpFarmer.userId, IdType.CompleteUserProfileId)
        dbClient.store(
            completeFarmerProfileId.getPath(),
            completeFarmerProfile
        )
    }

    fun storeSignUpWorker(signUpWorker: SignUpWorker){
        val completeFarmerProfile = contentIngestion.getCompleteUserProfile(signUpWorker)
        val completeFarmerProfileId = Id(signUpWorker.userId, IdType.CompleteUserProfileId)
        dbClient.store(
            completeFarmerProfileId.getPath(),
            completeFarmerProfile
        )
    }

    private fun addProductToUser(userIdString: String, productId: Id){
        val userId = Id(userIdString, IdType.CompleteUserProfileId)
        class ListenerImpl() : Listener<CompleteUserProfile>() {
            override fun activate(input: CompleteUserProfile) {
                input.productIds.add(productId.idValue)
                dbClient.store(
                    userId.getPath(),
                    input
                )
            }
        }
        val listener = ListenerImpl()
        dbClient.get(
            userId.getPath(),
            listener
        )
    }

    private fun addHarvestToUser(workerIdString: String, harvestId: Id){
        val userId = Id(workerIdString, IdType.CompleteUserProfileId)
        class ListenerImpl() : Listener<CompleteUserProfile>() {
            override fun activate(input: CompleteUserProfile) {
                input.harvestIds.add(harvestId.idValue)
                dbClient.store(
                    userId.getPath(),
                    input
                )
            }
        }
        val listener = ListenerImpl()
        dbClient.get(
            userId.getPath(),
            listener
        )
    }

}