package ca.uwaterloo.cs.db

import android.content.Context
import ca.uwaterloo.cs.Listener
import ca.uwaterloo.cs.Singleton
import ca.uwaterloo.cs.bemodels.SignUpFarmer
import ca.uwaterloo.cs.dbmodels.CompleteUserProfile
import ca.uwaterloo.cs.harvest.HarvestInformation
import ca.uwaterloo.cs.product.ProductInformation

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


    fun getProductInformation(userId: String, beListener: Listener<List<ProductInformation>>){
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
                for (productId in productIds){
                    val id = Id(productId, IdType.ProductId)
                    dbClient.get(id.getPath(), listener1)
                }
            }
        }

        val listener2 = ListenerImpl2()

        val id = Id(userId, IdType.CompleteUserProfileId)

        dbClient.get(id.getPath(), listener2)
    }

    fun getHarvestInformation(workerId: String, beListener: Listener<List<HarvestInformation>>){
        val harvestsInformation = mutableListOf<HarvestInformation>()

        class ListenerImpl1() : Listener<HarvestInformation>() {
            override fun activate(input: HarvestInformation) {
                harvestsInformation.add(input)
            }
        }
        val listener1 = ListenerImpl1()

        class ListenerImpl2() : Listener<CompleteUserProfile>() {
            override fun activate(input: CompleteUserProfile) {
                val harvestIds = input.harvestIds
                for (harvestId in harvestIds){
                    val id = Id(harvestId, IdType.HarvestId)
                    dbClient.get(id.getPath(), listener1)
                }
                beListener.activate(harvestsInformation)
            }
        }

        val listener2 = ListenerImpl2()

        val id = Id(workerId, IdType.CompleteUserProfileId)

        dbClient.get(id.getPath(), listener2)
    }

    fun getHarvestInformationFromFarmer(farmerUserId: String, beListener: Listener<List<HarvestInformation>>){
        val harvestsInformation = mutableListOf<HarvestInformation>()

        class ListenerImpl1() : Listener<List<HarvestInformation>>() {
            override fun activate(input: List<HarvestInformation>) {
                harvestsInformation.addAll(input)
            }
        }
        val listener1 = ListenerImpl1()

        class ListenerImpl2() : Listener<List<HarvestInformation>>() {
            override fun activate(input: List<HarvestInformation>) {
                harvestsInformation.addAll(input)
            }
        }

        val listener2 = ListenerImpl2()
    }
}
