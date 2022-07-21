package ca.uwaterloo.cs.pushpull

import ca.uwaterloo.cs.product.ProductInformation
import android.content.Context
import ca.uwaterloo.cs.Singleton
import ca.uwaterloo.cs.db.DBManager
import ca.uwaterloo.cs.harvest.HarvestInformation
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

class PushFarmer(val context: Context) {
    private val dbManager = DBManager(context)
    fun run(){
        pushProductData(context, dbManager)
        pushHarvestData(context, dbManager)
    }
}

fun pushProductData(context: Context, dbManager: DBManager){
    val productList = readProductFromFiles(context)
    val productsListIds = mutableListOf<String>()
    if (Singleton.isFarmer){
        for (product in productList) {
            productsListIds.add(product.second.productId)
            dbManager.storeProductInformation(Singleton.userId, product.second)
        }
        dbManager.newRemoveProductFromFarmer(Singleton.userId, productsListIds)
    }
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
            dbManager.storeHarvestInformation(
                true,
                Singleton.userId,
                harvest)
            dbManager.newRemoveHarvestFromWorker(Singleton.userId, harvestListIds)
        }
        else{
            // TODO needs to be implemented
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