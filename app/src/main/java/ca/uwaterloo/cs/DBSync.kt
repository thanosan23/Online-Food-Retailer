//package ca.uwaterloo.cs
//
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import ca.uwaterloo.cs.db.DBManager
//import ca.uwaterloo.cs.product.ProductInformation
//import android.content.Context
//import androidx.compose.runtime.MutableState
//import ca.uwaterloo.cs.harvest.HarvestInformation
//
//class DBSync {
//    var internalProductInformation = mutableListOf<ProductInformation>()
//    val dbManager= DBManager(null)
//    var startedListeningProduct = false
//    var tableData: MutableState<ArrayList<Pair<String, ProductInformation>>>? = null
//    var updateVariable: MutableState<Int>? = null
//    var attatched = false
//
//    fun listenDBUserProductInformation(context: Context){
//        dbManager.updateContext(context)
//        if (!startedListeningProduct){
//            startedListeningProduct = true
//        }
//        else{
//            return
//        }
//        class ListenerImpl: Listener<List<ProductInformation>>(){
//            override fun incompleteUpdate(input: List<ProductInformation>) {
//                internalProductInformation = input as MutableList<ProductInformation>
//                tableData?.value = localCastingProductInformation(internalProductInformation)
//                if (updateVariable != null){
//                    updateVariable!!.value += 1
//                }
//            }
//
//            override fun imageUpdate(){
//                if (updateVariable != null){
//                    updateVariable!!.value += 1
//                }
//            }
//
//            override fun deleted(input: List<ProductInformation>) {
//                assert(false)
//            }
//        }
//        val beListener = ListenerImpl()
//        if (Singleton.isFarmer) {
//            dbManager.getProductsInformationFromFarmer(Singleton.userId, beListener)
//        }
//        else{
//            dbManager.getProductsInformationFromWorker(Singleton.userId, beListener)
//        }
//    }
//
//    fun attachProductInformationObserver(
//        tableData: MutableState<ArrayList<Pair<String, ProductInformation>>>,
//        updateVariable: MutableState<Int>
//    ){
//        this.tableData = tableData
//        this.updateVariable = updateVariable
//        tableData.value = localCastingProductInformation(internalProductInformation)
//        updateVariable.value += 1
//    }
//
//}
//
//private fun localCastingProductInformation(productsInformation: List<ProductInformation>): ArrayList<Pair<String, ProductInformation>>{
//    val list = ArrayList<Pair<String, ProductInformation>>()
//    for (product in productsInformation){
//        list.add(Pair(product.productId, product))
//    }
//    return list
//}