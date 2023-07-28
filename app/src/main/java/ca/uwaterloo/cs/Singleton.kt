package ca.uwaterloo.cs

import androidx.compose.runtime.MutableState
import ca.uwaterloo.cs.harvest.HarvestInformation
import ca.uwaterloo.cs.product.ProductInformation

object Singleton
{
    init
    {
        println("Singleton class invoked.")
    }

    var userId = ""
    var isFarmer = true
    var isNewUser = false
    var productReadFromDB = 0
    var storeReadFromDB = 0
    var workerIdAndHarvestIdDeleted = mutableListOf<Pair<String, String>>()
    // both of these lists should have the same size

    var syncInProgress = false
    private val productScreenBroadCasters = mutableListOf<MutableState<ArrayList<Pair<String, ProductInformation>>>>()
    private val harvestScreenBroadCasters = mutableListOf<MutableState<ArrayList<HarvestInformation>>>()

    fun productBroadCast(product: ArrayList<Pair<String, ProductInformation>>){
        for (screenBroadcast in productScreenBroadCasters){
            if (product.isEmpty()){
                screenBroadcast.value = ArrayList()
                continue
            }
            screenBroadcast.value = product
        }
    }

    fun harvestBroadCast(harvest: List<HarvestInformation>){
        for (screenBroadcast in harvestScreenBroadCasters){
            if (harvest.isEmpty()){
                screenBroadcast.value = ArrayList()
                continue
            }
            screenBroadcast.value = harvest as ArrayList<HarvestInformation>
        }
    }

    fun productAttatch(updateVariables: MutableState<ArrayList<Pair<String, ProductInformation>>>){
        if (updateVariables in productScreenBroadCasters){
            return
        }
        productScreenBroadCasters.add(updateVariables)
    }

    fun harvestAttatch(updateVariables: MutableState<ArrayList<HarvestInformation>>){
        if (updateVariables in harvestScreenBroadCasters){
            return
        }
        harvestScreenBroadCasters.add(updateVariables)
    }
}