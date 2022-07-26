package ca.uwaterloo.cs.db

import android.content.Context
import ca.uwaterloo.cs.Listener
import ca.uwaterloo.cs.ValidFoodNames
import ca.uwaterloo.cs.dbmodels.*
import ca.uwaterloo.cs.product.ProductInformation
import com.google.gson.Gson

class DBGetDFC(context: Context?, val userIdString: String, val platform1: Boolean) {
    private val dbClient = DBClient()
    private val dbGetInternal = DBGetInternal(context)
    private val dfcContentIngestion = DFCContentIngestion()
    private var dfcPerson: DFCPerson? = null
    private val dfcSuppliedProductsAndProductInformation: MutableList<Pair<DFCSuppliedProduct, ProductInformation>> = mutableListOf()
    private val dfcCatalogItems: MutableList<DFCCatalogItem> = mutableListOf()
    private val dfcOffers: MutableList<DFCOffer> = mutableListOf()
    private var dfcEnterprise: DFCEnterprise? = null
    init {
        dbClient.context = context
    }

    fun step1 (){
        class ListenerImpl1() : Listener<CompleteUserProfile>() {
            override fun activate(input: CompleteUserProfile) {
                dfcPerson = dfcContentIngestion.getDFCPerson(userIdString, input)
                step2()
            }
        }
        val listener1 = ListenerImpl1()
        dbGetInternal.getCompleteUserProfile(userIdString, listener1)
    }

    private fun step2(){
        class ListenerImpl1() : Listener<List<ProductInformation>>() {
            override fun activate(input: List<ProductInformation>) {
                for (product in input) {
                    if (product.name !in ValidFoodNames().getValidFoodNames()){
                        continue
                    }
                    if (platform1) {
                        if (!product.platform1){
                            continue
                        }
                    }
                    else{
                        if (!product.platform2){
                            continue
                        }
                    }
                    dfcSuppliedProductsAndProductInformation.add(
                        Pair(dfcContentIngestion.getDFCSuppliedProduct(product),
                            product))
                }
                step3()
            }
        }
        val listener = ListenerImpl1()

        dbGetInternal.getProductInformationFromFarmer(userIdString, listener)
    }

    private fun step3(){
        generateCatalogueItemAndOfferForSuppliedProduct()
        step4()
    }

    private fun step4(){
        createEnterpriseWithLinkage()
        step5()
    }

    private fun step5(){
        dfcModelsToJSON()
    }

    private fun generateCatalogueItemAndOfferForSuppliedProduct(){
        for (dfcSuppliedProductAndProductInformation in dfcSuppliedProductsAndProductInformation){
            val dfcSuppliedProduct = dfcSuppliedProductAndProductInformation.first
            val product = dfcSuppliedProductAndProductInformation.second
            val dfcCatalogItem = dfcContentIngestion.getCatalogItem(product, platform1)
            dfcCatalogItems.add(dfcCatalogItem)
            val dfcOffer = dfcContentIngestion.getOffer(product, platform1)
            dfcOffers.add(dfcOffer)
            addLinkage(dfcSuppliedProduct, dfcCatalogItem, dfcOffer)
        }
    }

    private fun addLinkage(dfcSuppliedProduct: DFCSuppliedProduct,
                           dfcCatalogItem: DFCCatalogItem,
                           dfcOffer: DFCOffer){
        dfcSuppliedProduct.referencedBy.add(dfcCatalogItem.id)
        dfcCatalogItem.offeredThrough.add(dfcOffer.id)
        dfcOffer.offeres.add(dfcCatalogItem.id)
    }

    private fun createEnterpriseWithLinkage(){
        dfcEnterprise = dfcContentIngestion.getDFCEnterprise()
        for (x in dfcSuppliedProductsAndProductInformation){
            dfcEnterprise!!.supplies.add(x.first.id)
        }
        for (x in dfcCatalogItems){
            dfcEnterprise!!.manages.add(x.id)
        }
        dfcPerson!!.affiliates = listOf(dfcEnterprise!!.id)
    }

    private fun dfcModelsToJSON(){
        val gson = Gson()

        @kotlinx.serialization.Serializable
        data class Graph(
            val graph: List<String>
        )
        val graphString: MutableList<String> = mutableListOf()
        graphString.add(gson.toJson(dfcPerson))
        graphString.add(gson.toJson(dfcEnterprise))
        for (x in dfcSuppliedProductsAndProductInformation){
            graphString.add(gson.toJson(x.first))
        }
        for (x in dfcCatalogItems){
            graphString.add(gson.toJson(x))
        }
        for (x in dfcOffers){
            graphString.add(gson.toJson(x))
        }
        val graph = Graph(graphString)

        val dfcId =
            if (platform1) {
            Id("$userIdString,platform1", IdType.DFCStandardId)
        }
        else{
            Id("$userIdString,platform2", IdType.DFCStandardId)
        }
        val result = contextResolver(graph.graph.toString())
        dbClient.store(dfcId.getPath(),
        result)
    }


    private fun contextResolver(dfc2String: String): String{
        // this is not good practice because there is a chance that some of the
        // values on this json string that for for example type or id as a substring
        // so the correct practice is to replace only on the json fields and not on
        // on the json values too
        var dfcString = dfc2String
        dfcString = dfcString.replace("type", "@type")
        dfcString = dfcString.replace("id", "@id")
        dfcString = dfcString.replace("familyName", "dfc-b:familyName")
        dfcString = dfcString.replace("firstName", "dfc-b:firstName")
        dfcString = dfcString.replace("hasType", "dfc-p:hasType")
        dfcString = dfcString.replace("hasAddress", "dfc-b:hasAddress")
        dfcString = dfcString.replace("affiliates", "dfc-b:affiliates")
        dfcString = dfcString.replace("description", "dfc-b:description")
        dfcString = dfcString.replace("hasUnit", "dfc-p:hasUnit")
        dfcString = dfcString.replace("lifeTime", "dfc-b:lifeTime")
        dfcString = dfcString.replace("price", "dfc-b:price")
        dfcString = dfcString.replace("stockLimitation", "dfc-b:stockLimitation")
        dfcString = dfcString.replace("totalTheoreticalStock", "dfc-b:totalTheoreticalStock")
        dfcString = dfcString.replace("offeredThrough", "dfc-b:offeredThrough")
        return dfcString
    }
}