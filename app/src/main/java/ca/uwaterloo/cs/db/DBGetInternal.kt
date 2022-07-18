package ca.uwaterloo.cs.db

import ca.uwaterloo.cs.Listener
import ca.uwaterloo.cs.bemodels.SignUpFarmer
import ca.uwaterloo.cs.dbmodels.CompleteUserProfile
import ca.uwaterloo.cs.product.ProductInformation

class DBGetInternal {
    private val dbClient = DBClient()

    fun authenticate(userName:String, password:String, listener: Listener<Boolean>){
        class ListenerImpl() : Listener<SignUpFarmer>() {
            override fun activate(input: SignUpFarmer) {
                if (input.password == password){
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

    fun getProductInformation(userId: String, beListener: Listener<List<ProductInformation>>){
        val productsInformation = mutableListOf<ProductInformation>()

        class ListenerImpl1() : Listener<ProductInformation>() {
            override fun activate(input: ProductInformation) {
                productsInformation.add(input)
            }
        }
        val listener1 = ListenerImpl1()

        class ListenerImpl2() : Listener<CompleteUserProfile>() {
            override fun activate(input: CompleteUserProfile) {
                val productIds = input.productIds
                for (productId in productIds){
                    val id = Id(productId, IdType.ProductId)
                    dbClient.get(id.getPath(), listener1)
                }
                beListener.activate(productsInformation)
            }
        }

        val listener2 = ListenerImpl2()

        val id = Id(userId, IdType.CompleteUserProfileId)

        dbClient.get(id.getPath(), listener2)
    }
}
