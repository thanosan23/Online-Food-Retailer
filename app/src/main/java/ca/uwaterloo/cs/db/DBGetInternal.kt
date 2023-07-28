package ca.uwaterloo.cs.db

import android.content.Context
import ca.uwaterloo.cs.Listener
import ca.uwaterloo.cs.Singleton
import ca.uwaterloo.cs.StoreInformation
import ca.uwaterloo.cs.bemodels.SignUpFarmer
import ca.uwaterloo.cs.bemodels.UserProfile
import ca.uwaterloo.cs.dbmodels.CompleteUserProfile
import ca.uwaterloo.cs.harvest.HarvestInformation
import ca.uwaterloo.cs.product.ProductInformation
import com.google.firebase.firestore.auth.User

class DBGetInternal(context: Context?) {
    private val dbClient = DBClient()
    init {
        dbClient.context = context
    }
    fun authenticate(userName:String, password: String, listener: Listener<Boolean>){
        class ListenerImpl() : Listener<SignUpFarmer>() {
            override fun activate(input: SignUpFarmer) {
                if (true){
                    listener.activate(true)
                }
                else{
                    listener.activate(false)
                }
            }
        }
        val authListener = ListenerImpl()
        dbClient.get(
            dataBasePathResolver(IdType.DFCPersonId) + userName,
            authListener
        )
    }

    fun getUserType(userIdString: String){
        val userId = Id(userIdString, IdType.CompleteUserProfileId)

        class ListenerImpl2() : Listener<CompleteUserProfile>() {
            override fun activate(input: CompleteUserProfile) {
                Singleton.isFarmer = input.isFarmer
            }
        }
        val listener = ListenerImpl2()

        dbClient.get(
            userId.getPath(),
            listener
        )
    }

    fun authenticateFarmCode(farmCodeId: Id, beListener: Listener<String?>){
        dbClient.getIfExists(
            farmCodeId.getPath(),
            beListener
        )
    }

    fun getCompleteUserProfile(userIdString: String, listener: Listener<CompleteUserProfile>){
        val id = Id(userIdString, IdType.CompleteUserProfileId)
        class ListenerImpl1() : Listener<CompleteUserProfile>() {
            override fun activate(input: CompleteUserProfile) {
                listener.activate(input)
            }
        }
        val listener1 = ListenerImpl1()
        dbClient.get(
            id.getPath(),
            listener1
        )
    }

   fun getUserProfile(userIdString: String, listener: Listener<UserProfile>){
        class ListenerImpl1() : Listener<CompleteUserProfile>() {
            override fun activate(input: CompleteUserProfile) {
                val userProfile =
                    UserProfile(
                        input.firstName,
                        input.familyName,
                        "",
                        userIdString.dropLast(9)+ "@gmail.com",
                        input.address,
                        ""
                    )
                listener.activate(userProfile)
            }
        }
        val listener1 = ListenerImpl1()
        getCompleteUserProfile(userIdString, listener1)
    }

    fun getProductInformationFromFarmer(farmerUserIdString: String, beListener: Listener<List<ProductInformation>>){
        val productsInformation = mutableListOf<ProductInformation>()
        var counter = 0
        var amount = 0

        class ListenerImpl1() : Listener<ProductInformation>() {
            override fun activate(input: ProductInformation) {
                productsInformation.add(input)
                counter += 1
                println("counter $counter")
                println("$amount")
                if (counter == amount){
                    beListener.activate(productsInformation)
                }
            }
        }
        val listener1 = ListenerImpl1()

        class ListenerImpl2() : Listener<CompleteUserProfile>() {
            override fun activate(input: CompleteUserProfile) {
                val productIds = input.productIds
                amount = input.productIds.size
                if (amount == 0){
                    beListener.activate(listOf())
                }
                if (amount == 0){
                    Singleton.productReadFromDB += 1
                }
                for (productId in productIds){
                    val id = Id(productId, IdType.ProductId)
                    dbClient.get(id.getPath(), listener1)
                }
            }
        }

        val listener2 = ListenerImpl2()

        val id = Id(farmerUserIdString, IdType.CompleteUserProfileId)

        dbClient.get(id.getPath(), listener2)
    }


    fun getStoreInformationFromFarmer(farmerUserIdString: String, beListener: Listener<List<StoreInformation>>){
        val storeInformation = mutableListOf<StoreInformation>()
        var counter = 0
        var amount = 0

        class ListenerImpl1() : Listener<StoreInformation>() {
            override fun activate(input: StoreInformation) {
                storeInformation.add(input)
                counter += 1
                println("counter $counter")
                println("$amount")
                if (counter == amount){
                    beListener.activate(storeInformation)
                }
            }
        }
        val listener1 = ListenerImpl1()

        class ListenerImpl2() : Listener<CompleteUserProfile>() {
            override fun activate(input: CompleteUserProfile) {
                val storeIds = input.storeIds
                amount = input.storeIds.size
                if (amount == 0){
                    beListener.activate(listOf())
                }
                if (amount == 0){
                    Singleton.storeReadFromDB += 1
                }
                for (storeId in storeIds){
                    val id = Id(storeId, IdType.StoreId)
                    dbClient.get(id.getPath(), listener1)
                }
            }
        }

        val listener2 = ListenerImpl2()

        val id = Id(farmerUserIdString, IdType.CompleteUserProfileId)

        dbClient.get(id.getPath(), listener2)
    }


    fun getProductsInformationFromWorker(workerIdString: String, beListener: Listener<List<ProductInformation>>){
        class ListenerImpl1() : Listener<CompleteUserProfile>() {
            override fun activate(input: CompleteUserProfile) {
                getProductInformationFromFarmer(input.parentFarmerId, beListener)
            }
        }
        val listener1 = ListenerImpl1()
        getCompleteUserProfile(workerIdString, listener1)
    }

    fun getStoreInformationFromWorker(workerIdString: String, beListener: Listener<List<StoreInformation>>){
        class ListenerImpl1() : Listener<CompleteUserProfile>() {
            override fun activate(input: CompleteUserProfile) {
                getStoreInformationFromFarmer(input.parentFarmerId, beListener)
            }
        }
        val listener1 = ListenerImpl1()
        getCompleteUserProfile(workerIdString, listener1)
    }

    fun getHarvestInformation(workerId: String, beListener: Listener<List<HarvestInformation>>){
        val harvestsInformation = mutableListOf<HarvestInformation>()
        var counter = 0
        var amount = 0

        class ListenerImpl1() : Listener<HarvestInformation>() {
            override fun activate(input: HarvestInformation) {
                harvestsInformation.add(input)
                counter += 1
                if (counter == amount){
                    beListener.activate(harvestsInformation)
                }
            }
        }
        val listener1 = ListenerImpl1()

        class ListenerImpl2() : Listener<CompleteUserProfile>() {
            override fun activate(input: CompleteUserProfile) {
                val harvestIds = input.harvestIds
                amount = harvestIds.size
                if (amount == 0){
                    beListener.activate(listOf())
                }
                for (harvestId in harvestIds){
                    val id = Id(harvestId, IdType.HarvestId)
                    dbClient.get(id.getPath(), listener1)
                }
            }
        }

        val listener2 = ListenerImpl2()
        val id = Id(workerId, IdType.CompleteUserProfileId)
        dbClient.get(id.getPath(), listener2)
    }

    fun getHarvestInformationFromFarmer(farmerUserId: String, beListener: Listener<List<HarvestInformation>>){
        val harvestsInformation = mutableListOf<HarvestInformation>()
        var amount = 0
        var counter = 0

        class ListenerImpl1() : Listener<List<HarvestInformation>>() {
            override fun activate(input: List<HarvestInformation>) {
                harvestsInformation.addAll(input)
                counter += 1
                if (amount == counter){
                    beListener.activate(input)
                }
            }
        }

        val listener1 = ListenerImpl1()

        class ListenerImpl2() : Listener<CompleteUserProfile>() {
            override fun activate(input: CompleteUserProfile) {
                amount = input.workersIds.size
                if (amount == 0){
                    beListener.activate(listOf())
                }
                for (workerId in input.workersIds){
                    getHarvestInformation(workerId, listener1)
                }
            }
        }

        val listener2 = ListenerImpl2()
        dbClient.get(
            Id(farmerUserId, IdType.CompleteUserProfileId).getPath(),
            listener2
        )
    }
}
