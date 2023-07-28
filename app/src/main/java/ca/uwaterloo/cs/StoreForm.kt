package ca.uwaterloo.cs

import android.app.AlertDialog
import android.content.Context
import android.widget.Spinner
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ca.uwaterloo.cs.bemodels.UserProfile
import ca.uwaterloo.cs.db.DBGetInternal
import ca.uwaterloo.cs.db.DBManager
import ca.uwaterloo.cs.dbmodels.CompleteUserProfile
import ca.uwaterloo.cs.destinations.MainContentDestination
import ca.uwaterloo.cs.form.*
import ca.uwaterloo.cs.product.ProductInformation
import ca.uwaterloo.cs.pushpull.readProductFromFiles
import ca.uwaterloo.cs.pushpull.readStoreFromFiles
import ca.uwaterloo.cs.pushpull.sync
import ca.uwaterloo.cs.ui.theme.InstagramPurple
import ca.uwaterloo.cs.ui.theme.OnlineFoodRetailTheme
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*

@Destination
@Composable
fun StoreForm(
    navigator : DestinationsNavigator,
    data: StoreInformation?,
) {
    val context = LocalContext.current

    OnlineFoodRetailTheme {
        Scaffold(
            content = {
                val focusManager = LocalFocusManager.current
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.background)
                        .padding(20.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = {
                                focusManager.clearFocus()
                            })
                        }
                        .systemBarsPadding(),
                ) {
                    Text(if (data == null) "ADD STORE" else "EDIT STORE")
                    ShowStoreForm(navigator,
                        data ?: StoreInformation(),
                        context = context
                    )
                }
            },
            bottomBar = { NavigationBar(navigator) }
        )
    }
}

@Composable
fun ShowStoreForm(
    nav: DestinationsNavigator,
    data: StoreInformation,
    context: Context
) {
    val formState by remember { mutableStateOf(FormState()) }
    val productState by remember { mutableStateOf(FormState()) }
    var productNum by remember { mutableStateOf( data.products.size) }

    var options = mutableListOf<String>();

    readProductFromFiles(LocalContext.current).forEach {
        options.add(it.second.name);
    }

    val productNames = mutableListOf<String>();
    val productAmounts = mutableListOf<Int>();

    data.products.forEach { (name, amount) ->
        productNames.add(name);
        productAmounts.add(amount);
    }
    for(i in 1..(productNum - data.products.size) ) {
        productNames.add("");
        productAmounts.add(0);
    }

    var fields = mutableListOf<Field>();


    repeat(productNum) { index ->
        fields.add(Field(
            name = "Name$index",
            initValue = productState.getData()
                .getOrDefault("Name$index", productNames[index]),
            prompt = "Enter product name",
            label = "Product Name",
            validators = listOf(Required()),
            dropdownList = options
        ))
        fields.add(Field(
            name = "Amount$index",
            initValue = productState.getData()
                .getOrDefault("Amount$index", productAmounts[index].toString()),
            prompt = "Enter amount available",
            label = "Product Amount",
            validators = listOf(Required(), IsNumber()),
            inputType = KeyboardType.Number,
            formatter = NumberTransformation()
        ))
    }

    Spacer(Modifier.height(20.dp))
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Form(
            state=formState,
            fields=listOf(
                Field(
                    name="Name",
                    initValue=formState.getData().getOrDefault(
                        "Name",
                        if (data.name == "") "" else data.name
                    ),
                    prompt="Enter store name",
                    label="Store name",
                    validators= listOf(Required())
                )
            )
        )

        Column() {
            Row() {
                    Form(
                        state = productState,
                        fields=fields,
                    )
                }
            Row() {
                Button(onClick = {
                    // adds product to store
                    productAmounts.add(0);
                    productNames.add("");
                    System.out.println(productAmounts);
                    productNum += 1;
                }) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Allocate Product To Store",
                        tint = Color.InstagramPurple
                    )
                }
                Button(onClick = {
                    // remove product from store
                    if(productNum > 0) {
                        productAmounts.removeLast();
                        productNames.removeLast();
                        productNum -= 1;
                    }
                }) {
                    Icon(
                        imageVector = Icons.Filled.Remove,
                        contentDescription = "Allocate Product To Store",
                        tint = Color.InstagramPurple
                    )
                }
            }
            Row() {
                Button(onClick = {
                    if (formState.validate()) {
//                        var manager = DBManager(context);
//                        var products: MutableMap<String, Long> = mutableMapOf();
//                        class ListenerImpl() : Listener<List<ProductInformation>>() {
//                            override fun activate(input: List<ProductInformation>) {
//                                var error = false;
//                                for(product in input) {
//                                    products[product.name] = product.amount;
//                                }
//                                for(i in 0 until productNum) {
//                                    var name = productState.getData()["Name${i}"];
//                                    var amount = productState.getData()["Amount${i}"]!!.toInt();
//                                    if(products.containsKey(name)) {
//                                        if(products[name]!!.toInt() < amount.toInt()) {
//                                            AlertDialog.Builder(context).setMessage("Allocated amount must not be greater than amount in catalogue!").show()
//                                            error = true;
//                                        }
//                                    } else {
//                                        AlertDialog.Builder(context).setMessage("${name} is not in catalogue! Please make sure you have not made any spelling mistakes!").show()
//                                        error = true;
//                                    }
//                                }
//                                if(!error) {
//                                    saveStore(data, formState.getData(), productNum, productState.getData(), context)
//                                    nav.navigate(MainContentDestination)
//                                }
//                            }
//                        }
//                        val listener = ListenerImpl()
//                        manager.getProductsInformationFromFarmer(Singleton.userId, listener);
                    saveStore(data, formState.getData(), productNum, productState.getData(), context)
                    sync(context);
                    nav.navigate(MainContentDestination)
                    }
                }) {
                    Icon(
                        imageVector = Icons.Filled.Save,
                        contentDescription = "Save Store",
                        tint = Color.InstagramPurple
                    )
                }
                Button(onClick = {
                    if (formState.validate()) {
                        deleteStore(data, context, nav);
                    }
                }) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete Store",
                        tint = Color.InstagramPurple
                    )
                }
            }
        }
    }
}

private fun saveStore(
    data : StoreInformation,
    newData : Map<String, String>,
    productNum: Int,
    newProductData: Map<String, String>,
    context: Context
) {
    data.name = newData["Name"]!!;
    data.productAmount = 0;
    data.products = hashMapOf();
    repeat(productNum) {index ->
        var amount = newProductData["Amount$index"];
        data.products.put(newProductData["Name$index"]!!, amount!!.toInt());
    }
    data.exportData("${context.filesDir}/outstore");
}

private fun deleteStore(data: StoreInformation, context: Context, nav: DestinationsNavigator) {
    android.app.AlertDialog.Builder(context)
        .setTitle("Delete Store")
        .setMessage("Are you sure you want to delete this store? This operation is irreversible.")
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setPositiveButton(
            android.R.string.yes
        ) { _, _ ->
            data.deleteData("${context.filesDir}/outstore")
            sync(context);
            nav.navigate(MainContentDestination)
        }
        .setNegativeButton(android.R.string.no, null).show()
}
