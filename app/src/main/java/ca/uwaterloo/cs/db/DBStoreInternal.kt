package ca.uwaterloo.cs.db

import android.content.Context
import ca.uwaterloo.cs.Listener
import ca.uwaterloo.cs.bemodels.SignUpFarmer
import ca.uwaterloo.cs.bemodels.SignUpWorker
import ca.uwaterloo.cs.dbmodels.CompleteUserProfile
import ca.uwaterloo.cs.harvest.HarvestInformation
import ca.uwaterloo.cs.product.ProductInformation

class DBStoreInternal(context: Context?) {
    private val dbClient = DBClient()
    private val contentIngestion = ContentIngestion()
    init {
        dbClient.context = context
    }

    fun storeProductsInformation(userId: String,
                                productsInformation: List<ProductInformation>){
            addProductsToFarmer(userId, productsInformation)
        for (product in productsInformation) {
            val id = Id(product.productId, IdType.ProductId)
            dbClient.store(
                id.getPath(),
                product
            )
        }
    }

    fun storeHarvestInformation(harvestCreation: Boolean,
                                workerId: String,
                                harvestId: Id,
                                harvestInformation: HarvestInformation){
        if (harvestCreation){
            addHarvestToWorker(workerId, harvestId)
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
        signUpWorker.farmerUserId = signUpWorker.farmerUserId.replace("\"", "")
        val completeFarmerProfile = contentIngestion.getCompleteUserProfile(signUpWorker)
        val completeFarmerProfileId = Id(signUpWorker.workerUserId, IdType.CompleteUserProfileId)
        dbClient.store(
            completeFarmerProfileId.getPath(),
            completeFarmerProfile
        )
        val workerId = Id(signUpWorker.workerUserId, IdType.CompleteUserProfileId)
        val farmerId = Id(signUpWorker.farmerUserId, IdType.CompleteUserProfileId)
        attachWorkerToFarmer(workerId, farmerId)
    }

    private fun addProductsToFarmer(farmerIdString: String, products: List<ProductInformation>){
        val userId = Id(farmerIdString, IdType.CompleteUserProfileId)
        val productsIds = mutableListOf<String>()
        for (product in products){
            productsIds.add(product.productId)
        }
        class ListenerImpl() : Listener<CompleteUserProfile>() {
            override fun activate(input: CompleteUserProfile) {
                input.productIds = productsIds
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

    fun removeProductFromFarmer(farmerIdString: String, productIdString: String){
        val userId = Id(farmerIdString, IdType.CompleteUserProfileId)
        val productId = Id(productIdString, IdType.ProductId)
        class ListenerImpl() : Listener<CompleteUserProfile>() {
            override fun activate(input: CompleteUserProfile) {
                input.productIds.remove(productId.idValue)
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

    fun newRemoveProductFromFarmer(farmerIdString: String, productsToKeepIdString: List<String>){
        val userId = Id(farmerIdString, IdType.CompleteUserProfileId)
        class ListenerImpl() : Listener<CompleteUserProfile>() {
            override fun activate(input: CompleteUserProfile) {
                input.productIds = productsToKeepIdString as MutableList<String>
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

    fun removeHarvestFromWorker(workerIdString: String, harvestIdString: String){
        val userId = Id(workerIdString, IdType.CompleteUserProfileId)
        val harvestId = Id(harvestIdString, IdType.HarvestId)
        class ListenerImpl() : Listener<CompleteUserProfile>() {
            override fun activate(input: CompleteUserProfile) {
                input.harvestIds.remove(harvestId.idValue)
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

    fun storeUserProfile(){

    }

    fun newRemoveHarvestFromWorker(workerIdString: String, harvestToBeKeepIdString: List<String>){
        val userId = Id(workerIdString, IdType.CompleteUserProfileId)
        class ListenerImpl() : Listener<CompleteUserProfile>() {
            override fun activate(input: CompleteUserProfile) {
                input.harvestIds = harvestToBeKeepIdString as MutableList<String>
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

        private fun addHarvestToWorker(workerIdString: String, harvestId: Id){
        val userId = Id(workerIdString, IdType.CompleteUserProfileId)
        class ListenerImpl() : Listener<CompleteUserProfile>() {
            override fun activate(input: CompleteUserProfile) {
                for (storedHarvestIdString in input.harvestIds){
                    if (storedHarvestIdString == harvestId.idValue){
                        return
                    }
                }
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

    private fun attachWorkerToFarmer(workerId: Id, farmerId: Id){
        class ListenerImpl() : Listener<CompleteUserProfile>() {
            override fun activate(input: CompleteUserProfile) {
                if (workerId.idValue !in input.workersIds){
                    input.workersIds.add(workerId.idValue)
                    dbClient.store(
                        farmerId.getPath(),
                        input
                    )
                }
            }
        }
        val listener = ListenerImpl()
        dbClient.get(
            farmerId.getPath(),
            listener
        )
    }

}