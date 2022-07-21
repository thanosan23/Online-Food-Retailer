package ca.uwaterloo.cs

import android.content.Context
import android.os.Handler
import ca.uwaterloo.cs.db.DBManager
import ca.uwaterloo.cs.product.ProductInformation
import ca.uwaterloo.cs.product.copy
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

class ProductFileStorageDatabaseSynch(val context: Context) {
    private val dbManager = DBManager(context)

    init{
        if (Singleton.productReadFromDB == 0) {
            updateProductDataFromDB()
        }
        if (!Singleton.productJobScheduled){
            Singleton.productJobScheduled = true
            productInformationSyncJob()
        }
    }
    fun readProductFromFiles(): ArrayList<Pair<String, ProductInformation>> {
        // TODO: platform compatibility
        // TODO: load from platform
        val dir = File("${context.filesDir}/out2")
        if (!dir.exists()) {
            return ArrayList()
        }
        val productList = ArrayList<Pair<String, ProductInformation>>()
        for (saveFile in dir.walk()) {
            if (saveFile.isFile && saveFile.canRead() && saveFile.name.contains("Product-")) {
                try {
                    val productInformation =
                        Json.decodeFromString<ProductInformation>(saveFile.readText())
                    productList.add(Pair(productInformation.productId!!, productInformation))
                }
                catch (e: Throwable){
                    println("error deserializing the product")
                }
            }
        }
        return productList
    }

    fun updateProductDataFromDB(){
        class ListenerImpl() : Listener<List<ProductInformation>>() {
            override fun activate(input: List<ProductInformation>) {
                val fileProducts = readProductFromFiles()
                if (!checkIfProductsChanged(input, fileProducts)){
//                    return
                }
                val dir = File("${context.filesDir}/out2")
                if (dir.exists()){
                    dir.deleteRecursively()
                }
                for (product in input) {
                    val product2 = copy(product)
                    product2.exportData(context.filesDir.toString())
                }
                Singleton.productReadFromDB += 1
                Singleton.productBroadCast(fileProducts)
            }
        }
        val listener = ListenerImpl()
        if (Singleton.isFarmer) {
            dbManager.getProductsInformationFromFarmer(Singleton.userId, listener)
        }
        else{
            dbManager.getProductsInformationFromWorker(Singleton.userId, listener)
        }
    }

    private fun productInformationSyncJob(){
        val handler = Handler()
        val delay = 8000 // 1000 milliseconds == 1 second

        handler.postDelayed(object : Runnable {
            override fun run() {
                println("job started")
                updateProductDataFromDB()
                handler.postDelayed(this, delay.toLong())
            }
        }, delay.toLong())
    }

    private fun checkIfProductsChanged(
        dbProducts: List<ProductInformation>,
        filesProducts: ArrayList<Pair<String, ProductInformation>>,
    ): Boolean{
        val fileIds = mutableSetOf<String>()
        val dbIds = mutableSetOf<String>()

        for (dbProduct in dbProducts){
            dbIds.add(dbProduct.productId)
        }

        for (fileProduct in filesProducts){
            fileIds.add(fileProduct.second.productId)
        }
        return !(dbIds.containsAll(fileIds) and fileIds.containsAll(dbIds))
    }
}