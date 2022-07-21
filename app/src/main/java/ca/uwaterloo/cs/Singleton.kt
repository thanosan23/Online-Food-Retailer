package ca.uwaterloo.cs

import androidx.compose.runtime.MutableState
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
    var harvestReadFromDB = 0
    var harvestJobScheduled = false
    var productJobScheduled = false
    private val productScreenBroadCasters = mutableListOf<MutableState<ArrayList<Pair<String, ProductInformation>>>>()
    private val harvestScreenBroadCasters = mutableListOf<MutableState<Int>>()

    fun productBroadCast(product: ArrayList<Pair<String, ProductInformation>>){
        for (screenBroadcast in productScreenBroadCasters){
            screenBroadcast.value = product
        }
    }

    fun harvestBroadCast(){
        for (screenBroadcast in harvestScreenBroadCasters){
            screenBroadcast.value = screenBroadcast.value + 1
        }
    }

    fun productAttatch(updateVariables: MutableState<ArrayList<Pair<String, ProductInformation>>>){
        if (updateVariables in productScreenBroadCasters){
            return
        }
        productScreenBroadCasters.add(updateVariables)
    }

    fun harvestAttatch(updateVariables: MutableState<Int>){
        if (updateVariables in harvestScreenBroadCasters){
            return
        }
        harvestScreenBroadCasters.add(updateVariables)
    }
}