package ca.uwaterloo.cs.db

import android.content.Context
import ca.uwaterloo.cs.Listener
import ca.uwaterloo.cs.Singleton
import ca.uwaterloo.cs.StoreInformation
import ca.uwaterloo.cs.bemodels.SignUpFarmer
import ca.uwaterloo.cs.bemodels.SignUpWorker
import ca.uwaterloo.cs.bemodels.UserProfile
import ca.uwaterloo.cs.harvest.HarvestInformation
import ca.uwaterloo.cs.product.ProductInformation
import java.util.*

class DBManager(val context: Context?) {
    // it is important to pass the context if you are getting images
    private val idResolver = IdResolver()
    private val dbStoreInternal = DBStoreInternal(context)
    private val dbGetInternal = DBGetInternal(context)

    // returns true if there is no user with the username false otherwise
    fun storeSignUpFarmer(signUpFarmer: SignUpFarmer){
        val farmCodeId = idResolver.standardResolver(null, IdType.FarmCodeId)
        dbStoreInternal.storeSignUpFarmer(signUpFarmer, farmCodeId)
    }

    // returns true if the userName exists and it actually has that password
    fun authenticate(userName: String, password: String, listener: Listener<Boolean>){
        dbGetInternal.authenticate(userName, password, listener)
    }

    fun getUserType(userId: String){
        dbGetInternal.getUserType(userId)
    }

    // returns true if the userName exists and it actually has that password
    fun authenticateFarmCode(farmCode: String, beListener: Listener<String?>){
        val farmCodeId = Id(farmCode, IdType.FarmCodeId)
        dbGetInternal.authenticateFarmCode(farmCodeId, beListener)
    }

    // returns true if there is no user with the username
    // neither enterprise with the enterpriseName and the enterpriseId is true
    fun storeSignUpWorker(signUpWorker: SignUpWorker){
        dbStoreInternal.storeSignUpWorker(signUpWorker)
    }

    fun store(userId: String, productsInformation: List<ProductInformation>, storesInformation: List<StoreInformation>) {

        if (!Singleton.isFarmer){
            return
        }
        dbStoreInternal.storeInformation(userId, productsInformation, storesInformation);
    }
    // if the product is being created for the first time add productIdString to be null
    fun storeProductsInformation(userId: String, productsInformation: List<ProductInformation>){
        if (!Singleton.isFarmer){
            return
        }
        // Internal storage
        dbStoreInternal.storeProductsInformation(userId, productsInformation)
    }

    fun storeStoreInformation(userId: String, storeInformation: List<StoreInformation>){
        if (!Singleton.isFarmer){
            return
        }
        // Internal storage
        dbStoreInternal.storeStoreInformation(userId, storeInformation)
    }

    fun deleteProductInformation(farmerId: String, productIdString: String){
        if (!Singleton.isFarmer){
            return
        }
        dbStoreInternal.removeProductFromFarmer(farmerId, productIdString)
    }

    fun newRemoveProductFromFarmer(farmerIdString: String, productsToBeKeepIdString: List<String>){
        dbStoreInternal.newRemoveProductFromFarmer(farmerIdString, productsToBeKeepIdString)
    }

    fun newRemoveHarvestFromWorker(workerIdString: String, harvestToBeKeepIdString: List<String>){
        dbStoreInternal.newRemoveHarvestFromWorker(workerIdString, harvestToBeKeepIdString)
    }

    fun removeHarvestFromWorker(workerId: String, harvestId: String){
    dbStoreInternal.removeHarvestFromWorker(
        workerId, harvestId
        )
    }

    fun getProductsInformationFromFarmer(farmerId: String, beListener: Listener<List<ProductInformation>>){
        dbGetInternal.getProductInformationFromFarmer(farmerId, beListener)
    }


    fun getProductsInformationFromWorker(workerId: String, beListener: Listener<List<ProductInformation>>){
        dbGetInternal.getProductsInformationFromWorker(workerId, beListener)
    }

    // if the harvest is being created for the first time add harvestId to be null in the HarvestInformation
    fun storeHarvestInformation(harvestCreation: Boolean,
                                userId: String,
                                harvestInformation: HarvestInformation){
        val harvestIdString = harvestInformation.harvestId

        val harvestId = idResolver.standardResolver(harvestIdString, IdType.HarvestId)
        dbStoreInternal.storeHarvestInformation(
            harvestCreation,
            userId,
            harvestId,
            harvestInformation
        )
    }

    fun syncDFC(userIdString: String, platform1: Boolean){
        val dbGetDFC = DBGetDFC(context, userIdString, platform1)
        dbGetDFC.step1()
    }

    fun getHarvestInformationFromWorker(workerUserId: String, beListener: Listener<List<HarvestInformation>>){
        dbGetInternal.getHarvestInformation(workerUserId, beListener)
    }

    fun getAllHarvestsFromFarmer(farmerUserId: String, beListener: Listener<List<HarvestInformation>>){
        dbGetInternal.getHarvestInformationFromFarmer(farmerUserId, beListener)
    }

    fun storeUserProfile(userProfile: UserProfile){

    }

    fun getUserProfile(userName: String, listener: Listener<UserProfile>){
        dbGetInternal.getUserProfile(userName, listener)
    }

}

class DBManagerTest() {
    private val userId1 = "messinotcom"
    private val dbManager = DBManager(null)

    private fun testSignUp() {
        val signUpFarmer = SignUpFarmer(userId1,
            "messi2",
            "messi2",
            "messi land")

        dbManager.storeSignUpFarmer(signUpFarmer)
    }

    private fun simple1StoreProductTest() {
        val productInformation =
            ProductInformation(
                UUID.randomUUID().toString(),
                "new name",
                "vamos fugis",
                13,
                17L,
                "",
                platform1 = true,
                platform2 = false
            )
//        dbManager.storeProductInformation(userId1, productInformation)
    }

    private fun simple2StoreProductTest() {
        val productInformation =
            ProductInformation(
                UUID.randomUUID().toString(),
                "not new name",
                "nao vamos fugir",
                13,
                17L,
                "",
                platform1 = true,
                platform2 = false
            )
//        dbManager.storeProductInformation(userId1, productInformation)
    }

    private fun simpleGetProductTest(){
        class ListenerImpl() : Listener<List<ProductInformation>>() {
            override fun activate(input: List<ProductInformation>) {
            }
        }
        val listener = ListenerImpl()
        dbManager.getProductsInformationFromFarmer(userId1, listener)
    }

    fun part1ProductTest(){
        testSignUp()
        Thread.sleep(4_000)  // wait for 1 second
        simple1StoreProductTest()
    }

    fun part2ProductTest(){
        simple2StoreProductTest()
        Thread.sleep(4_000)  // wait for 1 second
        simpleGetProductTest()
    }
}
