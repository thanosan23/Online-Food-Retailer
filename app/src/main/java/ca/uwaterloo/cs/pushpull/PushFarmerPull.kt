package ca.uwaterloo.cs.pushpull

import ca.uwaterloo.cs.product.ProductInformation
import android.content.Context
import ca.uwaterloo.cs.Listener
import ca.uwaterloo.cs.Singleton
import ca.uwaterloo.cs.db.DBManager
import ca.uwaterloo.cs.harvest.HarvestInformation
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

class PushFarmerPull(val context: Context) {
    private val dbManager = DBManager(context)
    fun run(){
        class ListenerImpl() : Listener<List<HarvestInformation>>() {
            override fun activate(input: List<HarvestInformation>) {
                val pullResult = input
                val removedIds = Singleton.deletedHarvestIds
                val result = mutableListOf<HarvestInformation>()
                for (harvest in pullResult){
                    if (harvest.harvestId in removedIds){
                        result.add(harvest)
                    }
                }
                for (harvest in pullResult) {
                    dbManager.removeHarvestFromWorker(harvest.fromWorker, harvest)
                }
                overrideHarvestsFiles(context, result)
            }
        }
        val listener = ListenerImpl()
        dbManager.getAllHarvestsFromFarmer(Singleton.userId, listener)

        pushProductData(context, dbManager)
        Thread.sleep(5000)
        pullProductDataFromDB(context, dbManager, Singleton.isFarmer, )
        pushHarvestData(context, dbManager)
    }
}

fun pushProductData(context: Context, dbManager: DBManager){
    val productList = localCasting(readProductFromFiles(context))
    if (Singleton.isFarmer){
        dbManager.storeProductsInformation(Singleton.userId, productList)
//        dbManager.newRemoveProductFromFarmer(Singleton.userId, productsListIds)
    }
}

private fun localCasting(products: ArrayList<Pair<String, ProductInformation>>): List<ProductInformation>{
    val avs = mutableListOf<ProductInformation>()
    for (product in products){
        avs.add(product.second)
    }
    return avs

}

fun readProductFromFiles(context: Context): ArrayList<Pair<String, ProductInformation>> {
    // TODO: platform compatibility
    // TODO: load from platform
    val dir = File("${context.filesDir}/out2")
    if (!dir.exists()) {
        return ArrayList()
    }
    val productList = ArrayList<Pair<String, ProductInformation>>()
    for (saveFile in dir.walk()) {
        if (saveFile.isFile && saveFile.canRead() && saveFile.name.contains("Product-") && saveFile.exists()) {
            try {
                val productInformation =
                    Json.decodeFromString<ProductInformation>(saveFile.readText())
                productList.add(Pair(productInformation.productId, productInformation))
            }
            catch (e: Throwable){
                println("error deserializing the product")
            }
        }
    }
    return productList
}

fun pushHarvestData(context: Context, dbManager: DBManager){
    val harvestList = readHarvestFromFiles(context)
    val harvestListIds = mutableListOf<String>()
    for (harvest in harvestList) {
        if (!Singleton.isFarmer){
            dbManager.newRemoveHarvestFromWorker(Singleton.userId, harvestListIds)
            Thread.sleep(5000)
            dbManager.storeHarvestInformation(
                true,
                Singleton.userId,
                harvest)
        }
        else{
            dbManager.removeHarvestFromWorker(harvest.fromWorker, harvest)
        }
    }
}

fun readHarvestFromFiles(context: Context): ArrayList<HarvestInformation>{
    // TODO: platform compatibility
    // TODO: load from platform
    val dir = File("${context.filesDir}/outharvest")
    if (!dir.exists()) {
        return ArrayList()
    }
    val list = ArrayList<HarvestInformation>()
    for (saveFile in dir.walk()) {
        if (saveFile.isFile && saveFile.canRead() && saveFile.name.contains("Harvest-")) {
            try {
                val harvestInformation =
                    Json.decodeFromString<HarvestInformation>(saveFile.readText())
                list.add(harvestInformation)
            }
            catch (e: Throwable){
                println("error deserializing the product")
            }
        }
    }
    return list
}