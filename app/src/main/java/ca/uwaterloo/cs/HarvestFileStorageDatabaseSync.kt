package ca.uwaterloo.cs

import android.content.Context
import android.os.Handler
import androidx.compose.runtime.MutableState
import ca.uwaterloo.cs.db.DBManager
import ca.uwaterloo.cs.harvest.HarvestInformation
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

class HarvestFileStorageDatabaseSync(val context: Context) {
    private val dbManager = DBManager(context)

    init{
        if (Singleton.harvestReadFromDB == 0) {
            updateHarvestDataFromDB()
        }
        if (!Singleton.harvestJobScheduled){
            Singleton.productJobScheduled = true
            harvestInformationSyncJob()
        }
    }

    fun readHarvestFromFiles(): ArrayList<HarvestInformation>{
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

    private fun updateHarvestDataFromDB(){
            class ListenerImpl() : Listener<List<HarvestInformation>>() {
                override fun activate(input: List<HarvestInformation>) {
                    val fileHarvests = readHarvestFromFiles()
                    return
                    if (!checkIfHarvestsChanged(input, fileHarvests)){
//                        return
                    }
                    val dir = File("${context.filesDir}/outharvest")
                    if (dir.exists()){
                        dir.deleteRecursively()
                    }
                    for (product in input) {
                        product.exportData("${context.filesDir}/outharvest")
                    }
                    Singleton.harvestReadFromDB += 1
                    Singleton.harvestBroadCast(input)
                }
            }
            val listener = ListenerImpl()
            if (Singleton.isFarmer) {
                dbManager.getAllHarvestsFromFarmer(Singleton.userId, listener)
            }
            else{
                dbManager.getHarvestInformationFromWorker(Singleton.userId, listener)
            }
    }

    private fun harvestInformationSyncJob(
    ){
        val handler = Handler()
        val delay = 15000 // 1000 milliseconds == 1 second

        handler.postDelayed(object : Runnable {
            override fun run() {
//            if (Singleton.forTesting){
//                createMockProduct(context)
//            }
//            Singleton.forTesting = false
                println("harvest job started")
                updateHarvestDataFromDB()
                handler.postDelayed(this, delay.toLong())
            }
        }, delay.toLong())
    }

    private fun checkIfHarvestsChanged(
        dbHarvests: List<HarvestInformation>,
        filesHarvests: List<HarvestInformation>,
    ): Boolean{
        val fileIds = mutableSetOf<String>()
        val dbIds = mutableSetOf<String>()

        for (dbProduct in dbHarvests){
            dbIds.add(dbProduct.harvestId!!)
        }

        for (fileProduct in filesHarvests){
            fileIds.add(fileProduct.harvestId!!)
        }
        return !(dbIds.containsAll(fileIds) and fileIds.containsAll(dbIds))
    }
}