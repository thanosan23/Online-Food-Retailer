package ca.uwaterloo.cs

import android.app.AlertDialog
import android.content.Context
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import ca.uwaterloo.cs.destinations.MainContentDestination
import ca.uwaterloo.cs.form.*
import ca.uwaterloo.cs.ui.theme.OnlineFoodRetailTheme
import coil.compose.rememberImagePainter
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


@Destination
@Composable
fun ProductForm(navigator: DestinationsNavigator, data: ProductInformation?) {
    OnlineFoodRetailTheme {
        Scaffold(
            content = { FullProductForm(navigator, data) },
            bottomBar = { NavigationBar() }
        )
    }
}

@Composable
fun FullProductForm(navigator: DestinationsNavigator, data: ProductInformation?) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(MaterialTheme.colors.background)
            .padding(20.dp),
    ) {
        Text(if (data == null) "ADD PRODUCT" else "EDIT PRODUCT")
        ShowProductForm(navigator, data ?: ProductInformation())
    }
}

@Composable
fun ShowProductForm(nav: DestinationsNavigator, data: ProductInformation) {
    val state by remember { mutableStateOf(FormState()) }
    val images = ArrayList<String>(data.images)
    val context = LocalContext.current

    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(20.dp),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Form(
            state = state,
            fields = listOf(
                Field(
                    name = "Name",
                    initValue = if (data.name == "") "" else data.name,
                    prompt = "Enter product name",
                    label = "Product Name",
                    validators = listOf(Required())
                ),
                Field(
                    name = "Description",
                    initValue = if (data.description == "") "" else data.description,
                    prompt = "Enter description",
                    label = "Product Description",
                    validators = listOf(Required())
                ),
                Field(
                    name = "Amount",
                    initValue = if (data.amount == 0L) "" else data.amount.toString(),
                    prompt = "Enter amount available",
                    label = "Product Amount",
                    validators = listOf(Required()),
                    inputType = KeyboardType.Number,
                    formatter = NumberTransformation()
                ),
                Field(
                    name = "Price",
                    initValue = if (data.price == 0) "" else data.price.toString(),
                    prompt = "Enter price",
                    label = "Product Price",
                    validators = listOf(Required(), NonZero()),
                    inputType = KeyboardType.Number,
                ),
            )
        )
        FormImages(images)
        Button(onClick = {
            if (state.validate()) {
                saveProduct(data, state.getData(), images, context)
                nav.navigate(MainContentDestination)
            }
        }) {
            Text("Submit")
        }
        Button(onClick = {
            nav.navigate(MainContentDestination)
        }) {
            Text("Cancel")
        }
        Button(onClick = {
            deleteProduct(data, context)
            nav.navigate(MainContentDestination)
        }) {
            Text("Delete")
        }
    }
}

@Composable
fun FormImages(images: ArrayList<String>) {
    var shouldShowPhoto by remember { mutableStateOf(images.isNotEmpty()) }
    var shouldShowCamera by remember { mutableStateOf(images.isEmpty()) }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .height(200.dp)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (shouldShowPhoto) {
                Column(
                    Modifier
                        .background(Color.Cyan)
                        .width(200.dp)
                        .height(200.dp)
                        .aspectRatio(1f)
                )
                {
                    Image(
                        painter = rememberImagePainter(images[0].toUri()),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize(1f)
                    )
                }
            }
            if (shouldShowCamera) {
                // TODO: REQUEST CAMERA PERMISSION
                // TODO: CHANGE PICTURE INTAKE METHOD
                Box(
                    modifier = Modifier
                        .background(Color.Cyan)
                        .width(200.dp)
                        .height(200.dp)
                        .aspectRatio(1f),
                    contentAlignment = Alignment.Center
                )
                {
                }
            }
        }
        Button(
            onClick = {
                images.remove(images[0])
                shouldShowPhoto = false
                shouldShowCamera = true
            },
        ) {
            Text("Change Image")
        }
    }
}

private fun saveProduct(
    data: ProductInformation,
    newData: Map<String, String>,
    newImages: ArrayList<String>,
    context: Context
) {
    data.name = newData["Name"]!!
    data.description = newData["Description"]!!
    data.price = (newData["Price"]!!.toDouble() * 100).toInt()
    data.amount = newData["Amount"]!!.toLong()
    data.images.clear()
    data.images.addAll(newImages)
    data.exportData(context)
}

private fun deleteProduct(data: ProductInformation, context: Context) {
    AlertDialog.Builder(context)
        .setTitle("Delete Product")
        .setMessage("Are you sure you want to delete this product? This operation is irreversible.")
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setPositiveButton(
            android.R.string.yes
        ) { _, _ ->
            data.deleteData(context)
        }
        .setNegativeButton(android.R.string.no, null).show()
}
