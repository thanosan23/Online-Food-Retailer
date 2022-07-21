package ca.uwaterloo.cs.pushpull

import ca.uwaterloo.cs.Listener
import ca.uwaterloo.cs.Singleton
import ca.uwaterloo.cs.harvest.HarvestInformation
import android.content.Context
import ca.uwaterloo.cs.db.DBManager
import ca.uwaterloo.cs.product.ProductInformation
import ca.uwaterloo.cs.product.copy
import java.io.File

class PullFarmer(val context: Context) {
    val dbManager = DBManager(context)
    fun run(){
        pullHarvestDataFromDB(context, dbManager, Singleton.isFarmer, Singleton.userId)
    }
}


fun pullHarvestDataFromDB(context: Context,
                                  dbManager: DBManager,
                                  isUserFarmer: Boolean,
                                    userId: String){
    class ListenerImpl() : Listener<List<HarvestInformation>>() {
        override fun activate(input: List<HarvestInformation>) {
            println("started activate")
            val dir = File("${context.filesDir}/outharvest")
            if (dir.exists()){
                dir.deleteRecursively()
            }
            for (product in input) {
                product.exportData("${context.filesDir}/outharvest")
            }
        }
    }
    val listener = ListenerImpl()
    if (isUserFarmer){
        dbManager.getAllHarvestsFromFarmer(userId, listener)
    }
}



fun pullProductDataFromDB(
    context: Context,
    dbManager: DBManager,
    isUserFarmer: Boolean,
    userId: String
){
    assert(!isUserFarmer)
    class ListenerImpl() : Listener<List<ProductInformation>>() {
        override fun activate(input: List<ProductInformation>) {
            val dir = File("${context.filesDir}/out2")
            if (dir.exists()){
                dir.deleteRecursively()
            }
            for (product in input) {
                val product2 = copy(product)
                product2.exportData("${context.filesDir}/out2")
            }
        }
    }
    val listener = ListenerImpl()
    dbManager.getProductsInformationFromWorker(userId, listener)
}