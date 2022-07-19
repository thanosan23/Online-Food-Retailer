package ca.uwaterloo.cs.db

import android.content.Context
import ca.uwaterloo.cs.Listener
import ca.uwaterloo.cs.bemodels.SignUpFarmer
import ca.uwaterloo.cs.bemodels.SignUpWorker
import ca.uwaterloo.cs.bemodels.UserProfileFarmer
import ca.uwaterloo.cs.harvest.HarvestInformation
import ca.uwaterloo.cs.product.ProductInformation

class DBManager(context: Context?) {
    // it is important to pass the context if you are getting images
    private val idResolver = IdResolver()
    private val dbStoreDFCManager = DBStoreDFC()
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

    // if the product is being created for the first time add productIdString to be null
    fun storeProductInformation(userId: String, productInformation: ProductInformation){
        val productIdString = productInformation.productId
        val productCreation = productIdString == null

        // DFC storage
        val DFCSuppliedProductId = idResolver.standardResolver(productIdString, IdType.DFCSuppliedProductId)
        dbStoreDFCManager.storeProductInformation(productCreation, DFCSuppliedProductId, productInformation)

        // Internal storage
        val productId = Id(DFCSuppliedProductId.idValue, IdType.ProductId)
        dbStoreInternal.storeProductInformation(productCreation, userId, productId, productInformation)
    }

    fun deleteProductInformation(farmerId: String, productIdString: String){
        dbStoreInternal.removeProductFromFarmer(farmerId, productIdString)
    }

    fun getProductsInformation(userId: String, belistener: Listener<List<ProductInformation>>){
        dbGetInternal.getProductInformation(userId, belistener)
    }

    // if the harvest is being created for the first time add harvestId to be null in the HarvestInformation
    fun storeHarvestInformation(userId: String, harvestInformation: HarvestInformation){
        val harvestIdString = harvestInformation.harvestId
        val harvestCreation = harvestIdString == null

        val harvestId = idResolver.standardResolver(harvestIdString, IdType.HarvestId)
        dbStoreInternal.storeHarvestInformation(
            harvestCreation,
            userId,
            harvestId,
            harvestInformation
        )
    }

    fun getHarvestInformation(workerUserId: String, beListener: Listener<List<HarvestInformation>>){
        dbGetInternal.getHarvestInformation(workerUserId, beListener)
    }

    fun getAllHarvestsFromFarmer(farmerUserId: String, beListener: Listener<List<HarvestInformation>>){

    }

    fun storeUserProfile(userProfileFarmer: UserProfileFarmer){
    }

    fun getUserProfile(userName: String, listener: Listener<UserProfileFarmer>){
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
                null,
                "new name",
                "vamos fugis",
                13,
                17L,
                "",
                platform1 = true,
                platform2 = false
            )
        dbManager.storeProductInformation(userId1, productInformation)
    }

    private fun simple2StoreProductTest() {
        val productInformation =
            ProductInformation(
                null,
                "not new name",
                "nao vamos fugir",
                13,
                17L,
                "",
                platform1 = true,
                platform2 = false
            )
        dbManager.storeProductInformation(userId1, productInformation)
    }

    private fun simpleGetProductTest(){
        class ListenerImpl() : Listener<List<ProductInformation>>() {
            override fun activate(input: List<ProductInformation>) {
            }
        }
        val listener = ListenerImpl()
        dbManager.getProductsInformation(userId1, listener)
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
