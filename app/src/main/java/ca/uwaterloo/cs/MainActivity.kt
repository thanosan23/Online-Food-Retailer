package ca.uwaterloo.cs

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import ca.uwaterloo.cs.destinations.HarvestFormDestination
import ca.uwaterloo.cs.destinations.ProductFormDestination
import ca.uwaterloo.cs.product.ProductInformation
import ca.uwaterloo.cs.ui.theme.InstagramPurple
import ca.uwaterloo.cs.ui.theme.OnlineFoodRetailTheme
import coil.compose.rememberImagePainter
import com.google.firebase.database.FirebaseDatabase
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream
import java.util.*


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val mDatabase = FirebaseDatabase.getInstance().reference;
        mDatabase.child("users").child("test id").setValue("test user").addOnSuccessListener {
            println("MainActivity: Saved to Firebase Database")
        }.addOnFailureListener {
            println("MainActivity: FAILED")
        }
        super.onCreate(savedInstanceState)
        setContent {
            OnlineFoodRetailTheme {
                val context = LocalContext.current
                // generateMockData(1, context = context)
                DestinationsNavHost(navGraph = NavGraphs.root)
            }
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Destination(start = true)
@Composable
fun MainContent(nav: DestinationsNavigator) {
    Scaffold(
        content = { TableScreen(nav) },
        bottomBar = { NavigationBar(nav) }
    )
}

@Composable
fun TableScreen(nav: DestinationsNavigator) {
    val context = LocalContext.current
    CenterAlignedTopAppBar(
        title = { Text("Catalogue", color = Color.White) },
        navigationIcon = {
            IconButton(onClick = {
                addItem(nav, context)
            }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Catalogue",
                    tint = Color.White
                )
            }
        },
        actions = {
            IconButton(onClick = { /* doSomething() */ }) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Localized description"
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(Color.InstagramPurple)
    )
    // TODO: REMOVE / UPGRADE MOCK DATA GENERATION IN FINAL PRODUCT
    val tableData = readData(context)
    // Each cell of a column must have the same weight.
    // The LazyColumn will be our table. Notice the use of the weights below
    Row() {
        Spacer(Modifier.width(22.dp))
        LazyColumn(
            Modifier
                .padding(66.dp)
                .background(Color.White)
                .heightIn(0.dp, 640.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Here are all the lines of your table.
            items(tableData, key = { it }) {
                Spacer(Modifier.height(10.dp))

                Row(
                    Modifier
                        .height(IntrinsicSize.Min)
                        .border(BorderStroke(3.dp, Color.InstagramPurple)),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (it.second.image == "") {
                        Box(
                            modifier = Modifier
                                .width(200.dp)
                                .height(200.dp)
                                .clickable { editItem(nav, it.second) },
                            contentAlignment = Alignment.Center
                        )
                        {
                            Text(
                                text = it.second.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp
                            )
                        }
                    } else {
                        rememberImagePainter(it.second.image.toUri())
                        Image(
                            painter = rememberImagePainter(it.second.image.toUri()),
                            contentDescription = null,
                            modifier = Modifier
                                .width(200.dp)
                                .height(200.dp)
                                .clickable { editItem(nav, it.second) }
                        )
                    }
                    IconButton(
                        onClick = {
                            nav.navigate(HarvestFormDestination(it.second))
                        },
                        modifier = Modifier
                            .width(60.dp)
                            .height(60.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Catalogue",
                            tint = Color.Green,
                            modifier = Modifier.fillMaxSize(1.0f)
                        )
                    }

                }
            }
        }
    }
}

private fun mergeChanges(nav: DestinationsNavigator) {
    nav.navigate(ProductFormDestination())
}

private fun editItem(nav: DestinationsNavigator, data: ProductInformation) {
    nav.navigate(ProductFormDestination(data))
}

private fun addItem(nav: DestinationsNavigator, context: Context) {
    nav.navigate(ProductFormDestination())
}
/*
private fun addItem(nav: DestinationsNavigator, context: Context) {
    val options =
        arrayOf<CharSequence>(
            "From scratch",
            "From template",
            "Cancel"
        )
    val builder = android.app.AlertDialog.Builder(context)
    builder.setTitle("Create Product")
    builder.setItems(options) { dialog, item ->
        if (options[item] == "From scratch") {
            nav.navigate(ProductFormDestination())
        } else if (options[item] == "From template") {
            nav.navigate(ProductFormDestination(useTemplate = true))
        } else if (options[item] == "Cancel") {
            dialog.dismiss()
        }
    }
    builder.show()
}*/

private fun generateMockData(amount: Int = 7, context: Context) {
    val dir = File("${context.filesDir}/out")
    if (dir.exists()) {
        dir.deleteRecursively()
    }
    dir.mkdir()
    (1..amount).forEach { value ->
        ProductInformation(
            UUID.randomUUID().toString(),
            "apple $value",
            "apple $value description",
            100 * value + 1,
            10 * value + 1L,
            "",
            platform1 = false,
            platform2 = false
        ).exportData(context)
    }
}

private fun readData(context: Context): List<Pair<String, ProductInformation>> {
    // TODO: platform compatibility
    // TODO: load from platform
    val dir = File("${context.filesDir}/out")
    if (!dir.exists()) {
        return emptyList()
    }
    val list = ArrayList<Pair<String, ProductInformation>>()
    for (saveFile in dir.walk()) {
        if (saveFile.isFile && saveFile.canRead() && saveFile.name.contains("Product-")) {
            val fileIS = FileInputStream(saveFile)
            val inStream = ObjectInputStream(fileIS)
            val productInformation = inStream.readObject() as ProductInformation
            list.add(Pair(productInformation.productId, productInformation))
            inStream.close()
            fileIS.close()
        }
    }
    return list
}
