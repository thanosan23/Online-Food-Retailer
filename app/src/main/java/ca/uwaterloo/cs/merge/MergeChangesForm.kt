package ca.uwaterloo.cs.merge

import android.app.AlertDialog
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import ca.uwaterloo.cs.*
import ca.uwaterloo.cs.destinations.MainContentDestination
import ca.uwaterloo.cs.destinations.ProductFormDestination
import ca.uwaterloo.cs.harvest.HarvestInformation
import ca.uwaterloo.cs.product.ProductInformation
import ca.uwaterloo.cs.pushpull.readHarvestFromFiles
import ca.uwaterloo.cs.pushpull.readProductFromFiles
import ca.uwaterloo.cs.ui.theme.InstagramPurple
import ca.uwaterloo.cs.ui.theme.OnlineFoodRetailTheme
import coil.compose.rememberImagePainter
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


private lateinit var saveDirHarvest: String
private lateinit var saveDirProduct: String

@Destination
@Composable
fun MergeForm(
    nav: DestinationsNavigator
) {
    OnlineFoodRetailTheme {
        Scaffold(
            content = { MergeScreen(nav = nav) },
            bottomBar = { NavigationBar(nav) }
        )
    }
}

@Composable
fun MergeScreen(nav: DestinationsNavigator) {
    val context = LocalContext.current
    saveDirHarvest = "${context.filesDir}/outharvest"
    saveDirProduct = "${context.filesDir}/out2"

    Column {
        CenterAlignedTopAppBar(
            title = { Text("Harvest Updates", color = Color.White) },
            navigationIcon = {
                IconButton(onClick = {
                    nav.navigate(ProductFormDestination())
                }) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Catalogue",
                        tint = Color.White
                    )
                }
            },
            actions = {
                IconButton(onClick = { nav.navigate(MainContentDestination) }) {
                    Icon(
                        imageVector = Icons.Filled.Home,
                        contentDescription = "Catalogue",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(Color.InstagramPurple)
        )


        val harvestListFromFiles = remember{
            mutableStateOf(ArrayList<HarvestInformation>())
        }
        harvestListFromFiles.value = readHarvestFromFiles(LocalContext.current)
        Singleton.harvestAttatch(harvestListFromFiles)

        val productListFromFiles = localCasting1(readProductFromFiles(LocalContext.current))

        val processedData =
            remember { mutableStateMapOf<String, Pair<ProductInformation?, List<HarvestInformation>>>() }
        val processedDataFromFiles = processData(harvestListFromFiles.value, productListFromFiles)

        for (entry in processedDataFromFiles) {
            if (entry.key != "" && entry.value.first != null) {
                processedData[entry.value.first!!.name] = entry.value
            } else {
                processedData[""] = entry.value
            }
        }
        var selectedHarvest by remember { mutableStateOf<HarvestInformation?>(null) }
        var selectedEntry by remember {
            mutableStateOf<Pair<String, Pair<ProductInformation?, List<HarvestInformation>>>?>(
                null
            )
        }
        var numberChangeVisible by remember { mutableStateOf(false) }
        var associationChangeVisible by remember { mutableStateOf(false) }
        val linkedHarvests = remember { mutableStateListOf<String>() }
        val unlinkedHarvests = remember { mutableStateListOf<String>() }

        @Composable
        fun MergeEntryHeader(productData: ProductInformation) {
            Spacer(Modifier.height(20.dp))
            Row(modifier = Modifier.height(100.dp)) {
                if (productData.image != "") {
                    Image(
                        painter = rememberImagePainter(productData.image.toUri()),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxHeight()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.ImageNotSupported,
                        contentDescription = "No Image",
                        tint = Color.Black,
                        modifier = Modifier.fillMaxHeight()
                    )
                }
                Spacer(modifier = Modifier.width(40.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = productData.name, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(text = productData.description, fontSize = 16.sp)
                }
            }
        }

        @Composable
        fun MergeEntryHarvest(
            harvestData: HarvestInformation,
            productData: ProductInformation,
            context: Context
        ) {
            if (!linkedHarvests.contains(harvestData.harvestId)) {
                linkedHarvests.add(harvestData.harvestId!!)
            }

            Spacer(Modifier.height(10.dp).fillMaxWidth())
            Row(
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth()
                    .padding(0.dp, 0.dp, 20.dp, 0.dp)
                    .background(Color.LightGray),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = harvestData.fromWorker.dropLast(9))
                }
                Spacer(modifier = Modifier.width(20.dp).fillMaxHeight())
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Amount:")
                    Text(harvestData.amount.toString())
                }
                Spacer(modifier = Modifier.width(20.dp).fillMaxHeight())
                if (Singleton.isFarmer) {
                    IconButton(onClick = {
                        productData.amount += harvestData.amount
                        productData.exportData(saveDirProduct)
                        harvestData.deleteData(saveDirHarvest)
                        val harvestList = processedData[productData.name]!!.second
                        val updatedList = harvestList.toMutableList()
                        updatedList.remove(harvestData)
                        linkedHarvests.remove(harvestData.harvestId)
                        processedData[productData.name] = Pair(productData, updatedList)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Accept change",
                            tint = Color.Green,
                            modifier = Modifier
                        )
                    }
                }
                Spacer(modifier = Modifier.width(5.dp).fillMaxHeight())
                IconButton(onClick = {
                    harvestData.deleteData(saveDirHarvest)
                    val harvestList = processedData[productData.name]!!.second
                    val updatedList = harvestList.toMutableList()
                    updatedList.remove(harvestData)
                    linkedHarvests.remove(harvestData.harvestId)
                    processedData[productData.name] = Pair(productData, updatedList)
                }) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "Remove change",
                        tint = Color.Red,
                        modifier = Modifier
                    )
                }
                Spacer(modifier = Modifier.width(5.dp).fillMaxHeight())
                IconButton(onClick = {
                    val options =
                        arrayOf<CharSequence>(
                            "Change Amount",
                            "Change Product Association",
                            "Cancel"
                        )
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Add Photo")
                    builder.setItems(options) { dialog, item ->
                        if (options[item] == "Change Amount") {
                            dialog.dismiss()
                            selectedEntry =
                                Pair(productData.name, processedData[productData.name]!!)
                            selectedHarvest = harvestData
                            numberChangeVisible = true
                        } else if (options[item] == "Change Product Association") {
                            dialog.dismiss()
                            selectedEntry =
                                Pair(productData.name, processedData[productData.name]!!)
                            selectedHarvest = harvestData
                            associationChangeVisible = true
                        } else if (options[item] == "Cancel") {
                            dialog.dismiss()
                        }
                    }
                    builder.show()
                }) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit change",
                        tint = Color.Black,
                        modifier = Modifier
                    )
                }
            }
        }

        @Composable
        fun MergeEntry(
            productData: ProductInformation,
            harvests: List<HarvestInformation>,
            context: Context
        ) {
            if (harvests.isNotEmpty()) {
                MergeEntryHeader(productData)
                harvests.forEach {
                    MergeEntryHarvest(it, productData, context)
                }
            }
        }

        @Composable
        fun MergeUnknownEntry(harvestData: HarvestInformation) {
            if (!unlinkedHarvests.contains(harvestData.harvestId)) {
                unlinkedHarvests.add(harvestData.harvestId!!)
            }
            Spacer(Modifier.height(20.dp).fillMaxWidth())
            Row(modifier = Modifier.height(100.dp)) {
                if (harvestData.image != "") {
                    Image(
                        painter = rememberImagePainter(harvestData.image.toUri()),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxHeight()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.ImageNotSupported,
                        contentDescription = "No Image",
                        tint = Color.Black,
                        modifier = Modifier.fillMaxHeight()
                    )
                }
                Spacer(modifier = Modifier.width(40.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = harvestData.name, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(text = harvestData.description, fontSize = 16.sp)
                }
            }
            Spacer(Modifier.height(10.dp).fillMaxWidth())
            Row(
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth()
                    .background(Color.LightGray),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = harvestData.fromWorker.dropLast(9))
                }
                Spacer(modifier = Modifier.width(20.dp))
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Amount:")
                    Text(harvestData.amount.toString())
                }
                Spacer(modifier = Modifier.width(20.dp))
                IconButton(onClick = {
                    val options =
                        arrayOf<CharSequence>(
                            "Change Amount",
                            "Change Product Association",
                            "Cancel"
                        )
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Add Photo")
                    builder.setItems(options) { dialog, item ->
                        if (options[item] == "Change Amount") {
                            dialog.dismiss()
                            selectedEntry =
                                Pair("", processedData[""]!!)
                            selectedHarvest = harvestData
                            numberChangeVisible = true
                        } else if (options[item] == "Change Product Association") {
                            dialog.dismiss()
                            selectedEntry =
                                Pair("", processedData[""]!!)
                            selectedHarvest = harvestData
                            associationChangeVisible = true
                        } else if (options[item] == "Cancel") {
                            dialog.dismiss()
                        }
                    }
                    builder.show()
                }) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit change",
                        tint = Color.Black,
                        modifier = Modifier
                    )
                }
                Spacer(modifier = Modifier.width(5.dp))
                IconButton(onClick = {
                    harvestData.deleteData(saveDirHarvest)
                    val harvestList = processedData[""]!!.second
                    val updatedList = harvestList.toMutableList()
                    updatedList.remove(harvestData)
                    unlinkedHarvests.remove(harvestData.harvestId)
                    processedData[""] = Pair(null, updatedList)
                }) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "Remove change",
                        tint = Color.Red,
                        modifier = Modifier
                    )
                }
                Spacer(modifier = Modifier.width(5.dp))
            }
        }

        @Composable
        fun showAmountDialog() {
            if (numberChangeVisible) {
                var amount by remember { mutableStateOf(selectedHarvest!!.amount) }
                AlertDialog(
                    onDismissRequest = { numberChangeVisible = false },
                    title = { Text("Enter New Amount") },
                    text = {
                        Column {
                            TextField(
                                value = amount.toString(),
                                onValueChange = {
                                    amount = try {
                                        it.toInt()
                                    } catch (e: Exception) {
                                        0
                                    }
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            selectedHarvest!!.amount = amount
                            selectedHarvest!!.exportData(saveDirHarvest)
                            processedData[selectedEntry!!.first] =
                                processedData[selectedEntry!!.first]!!
                            numberChangeVisible = false
                        }) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { numberChangeVisible = false }) {
                            Text("Cancel")
                        }
                    },
                    backgroundColor = Color.White
                )
            }
        }

        @Composable
        fun showAssociationDialog() {
            if (associationChangeVisible) {
                var association by remember { mutableStateOf("") }
                var associationId by remember { mutableStateOf("") }
                val isSelectedItem: (String, String) -> Boolean =
                    { _: String, id: String -> associationId == id }
                val onChangeState: (String, String) -> Unit = { name: String, id: String ->
                    associationId = id
                    association = name
                }
                AlertDialog(
                    onDismissRequest = { associationChangeVisible = false },
                    title = { Text("Select Product") },
                    text = {
                        Column {
                            productListFromFiles.map { (_, v) -> Pair(v.name, v) }.sortedBy { (k, _) -> k }
                                .forEach { item ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .selectable(
                                                selected = isSelectedItem(
                                                    item.second.name,
                                                    item.second.productId!!
                                                ),
                                                onClick = {
                                                    onChangeState(
                                                        item.second.name,
                                                        item.second.productId!!
                                                    )
                                                },
                                                role = Role.RadioButton
                                            )
                                            .padding(8.dp)
                                    ) {
                                        RadioButton(
                                            selected = isSelectedItem(
                                                item.second.name,
                                                item.second.productId!!
                                            ),
                                            onClick = null
                                        )
                                        Text(
                                            text = item.first,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            val prevList =
                                processedData[selectedEntry!!.first]!!.second
                            val updatedPrevList = prevList.toMutableList()
                            updatedPrevList.remove(selectedHarvest!!)
                            if (updatedPrevList.isEmpty()) {
                                processedData.remove(selectedEntry!!.first)
                            } else {
                                processedData[selectedEntry!!.first] =
                                    Pair(selectedEntry!!.second.first!!, updatedPrevList)
                            }

                            selectedHarvest!!.productId = associationId
                            selectedHarvest!!.exportData(saveDirHarvest)

                            if (processedData.containsKey(association)) {
                                val nextList = processedData[association]!!.second
                                val updatedNextList = nextList.toMutableList()
                                updatedNextList.add(selectedHarvest!!)
                                processedData[association] =
                                    Pair(processedData[association]!!.first, updatedNextList)
                            } else {
                                processedData[association] = Pair(
                                    productListFromFiles[associationId],
                                    arrayListOf(selectedHarvest!!)
                                )
                            }

                            if (selectedEntry!!.first == "") {
                                unlinkedHarvests.remove(selectedHarvest!!.harvestId)
                                linkedHarvests.add(selectedHarvest!!.harvestId!!)
                            }

                            associationChangeVisible = false
                        }) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { associationChangeVisible = false }) {
                            Text("Cancel")
                        }
                    },
                    backgroundColor = Color.White
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.width(22.dp))
            LazyColumn(
                Modifier
                    .background(Color.White)
                    .heightIn(0.dp, 640.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (linkedHarvests.isEmpty() && unlinkedHarvests.isEmpty()) {
                    item {
                        Spacer(Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.height(60.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Column {
                                Text(
                                    text = "No Harvest Requests",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp
                                )
                            }
                        }
                    }
                }
                if (linkedHarvests.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.height(60.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Column {
                                Text(
                                    text = "Linked Harvest Requests",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp
                                )
                            }
                        }
                    }
                }
                processedData.filter { it.key != "" }.toSortedMap().forEach { (_, value) ->
                    item { MergeEntry(value.first!!, value.second, context) }
                }
                if (unlinkedHarvests.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.height(60.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Column {
                                Text(
                                    text = "Manual Correction Needed",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp
                                )
                            }
                        }
                    }
                }
                processedData.filter { it.key == "" }.forEach { (_, value) ->
                    items(value.second, key = { it.harvestId!! }) {
                        MergeUnknownEntry(it)
                    }
                }
            }
            showAmountDialog()
            showAssociationDialog()
        }
    }
}

private fun localCasting1(it: ArrayList<Pair<String, ProductInformation>>): HashMap<String, ProductInformation>{
    val map1 = mutableMapOf<String, ProductInformation>()
    for (thing in it){
        map1[thing.first] = thing.second
    }
    return HashMap(map1)
}


private fun processData(
    data: ArrayList<HarvestInformation>,
    products: HashMap<String, ProductInformation>
): HashMap<String, Pair<ProductInformation?, ArrayList<HarvestInformation>>> {
    val processedData =
        HashMap<String, Pair<ProductInformation?, ArrayList<HarvestInformation>>>()
    for (productEntry in products) {
        processedData[productEntry.key] = Pair(productEntry.value, arrayListOf())
    }
    processedData[""] = Pair(null, arrayListOf())
    for (harvest in data) {
        if (processedData[harvest.productId] == null){
            println("null pointer exception in processed data")
            println("arguments, $data,  $products")
            continue
        }
        processedData[harvest.productId]!!.second.add(harvest)
    }
    return processedData
}