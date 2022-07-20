package ca.uwaterloo.cs

import android.content.Context
import android.os.Handler
import androidx.compose.runtime.MutableState
import ca.uwaterloo.cs.db.DBManager
import ca.uwaterloo.cs.harvest.HarvestInformation
import ca.uwaterloo.cs.product.ProductInformation
import ca.uwaterloo.cs.product.copy
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

class FileStorageDatabaseSynch(val context: Context) {
    private val dbManager = DBManager(context)

    fun readProductFromFiles(): ArrayList<Pair<String, ProductInformation>> {
        // TODO: platform compatibility
        // TODO: load from platform
        val dir = File("${context.filesDir}/out2")
        if (!dir.exists()) {
            return ArrayList()
        }
        val list = ArrayList<Pair<String, ProductInformation>>()
        for (saveFile in dir.walk()) {
            if (saveFile.isFile && saveFile.canRead() && saveFile.name.contains("Product-")) {
//            val fileIS = FileInputStream(saveFile)
//            val inStream = ObjectInputStream(fileIS)
//            val productInformation = inStream.readObject() as ProductInformation
                try {
                    val productInformation =
                        Json.decodeFromString<ProductInformation>(saveFile.readText())
                    list.add(Pair(productInformation.productId!!, productInformation))
                }
                catch (e: Throwable){
                    println("error deserializing the product")
                }
//            inStream.close()
//            fileIS.close()
            }
        }
        return list
    }

    fun readHarvestFromFiles(): ArrayList<HarvestInformation>{
        // TODO: platform compatibility
        // TODO: load from platform
        val dir = File("${context.filesDir}/out2")
        if (!dir.exists()) {
            return ArrayList()
        }
        val list = ArrayList<HarvestInformation>()
        for (saveFile in dir.walk()) {
            if (saveFile.isFile && saveFile.canRead() && saveFile.name.contains("Harvest-")) {
//            val fileIS = FileInputStream(saveFile)
//            val inStream = ObjectInputStream(fileIS)
//            val productInformation = inStream.readObject() as ProductInformation
                try {
                    val harvestInformation =
                        Json.decodeFromString<HarvestInformation>(saveFile.readText())
                    list.add(harvestInformation)
                }
                catch (e: Throwable){
                    println("error deserializing the product")
                }
//            inStream.close()
//            fileIS.close()
            }
        }
        return list
    }

    fun readProductDataFromDB(
        tableData: MutableState<ArrayList<Pair<String, ProductInformation>>>){
        class ListenerImpl() : Listener<List<ProductInformation>>() {
            override fun activate(input: List<ProductInformation>) {
                val fileProducts = readProductFromFiles()
                if (!checkIfProductsChanged(input, fileProducts)){
                    return
                }
                val dir = File("${context.filesDir}/out2")
                if (dir.exists()){
                    dir.deleteRecursively()
                }
                for (product in input) {
                    val product2 = copy(product)
                    product2.exportData(context.filesDir.toString())
                }
                Singleton.readFromDB += 1
                tableData.value = readProductFromFiles()
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

    fun productInformationSynchJob(
        tableData: MutableState<ArrayList<Pair<String, ProductInformation>>>){
        val handler = Handler()
        val delay = 15000 // 1000 milliseconds == 1 second

        handler.postDelayed(object : Runnable {
            override fun run() {
//            if (Singleton.forTesting){
//                createMockProduct(context)
//            }
//            Singleton.forTesting = false
                println("job started")
                readProductDataFromDB(tableData)
                handler.postDelayed(this, delay.toLong())
            }
        }, delay.toLong())
    }

    fun HarvestInformationSynchJob(
        tableData: MutableState<ArrayList<Pair<String, ProductInformation>>>){
        val handler = Handler()
        val delay = 15000 // 1000 milliseconds == 1 second

        handler.postDelayed(object : Runnable {
            override fun run() {
//            if (Singleton.forTesting){
//                createMockProduct(context)
//            }
//            Singleton.forTesting = false
                println("harvest job started")

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