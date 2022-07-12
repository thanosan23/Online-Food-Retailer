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
import androidx.compose.material.icons.filled.Receipt
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
import ca.uwaterloo.cs.destinations.MergeFormDestination
import ca.uwaterloo.cs.destinations.ProductFormDestination
import ca.uwaterloo.cs.product.ProductInformation
import ca.uwaterloo.cs.ui.theme.InstagramPurple
import ca.uwaterloo.cs.ui.theme.OnlineFoodRetailTheme
import coil.compose.rememberImagePainter
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream
import java.util.*


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OnlineFoodRetailTheme {
                val context = LocalContext.current
                generateMockData(1, context = context)
                DestinationsNavHost(navGraph = NavGraphs.root)
            }
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Destination(start = true)
@Composable
fun MainContent(nav: DestinationsNavigator) {
    val useTemplate: Boolean = true //farmer:true,worker:false
    Scaffold(
        content = { TableScreen(nav, useTemplate) },
        bottomBar = { NavigationBar(nav) })
}

@Composable
fun TableScreen(nav: DestinationsNavigator, useTemplate: Boolean) {
    val context = LocalContext.current
    if (useTemplate) {
        CenterAlignedTopAppBar(
            title = { Text("Catalogue", color = Color.White) },
            navigationIcon = {
                IconButton(onClick = {
                    addItem(nav)
                }) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Catalogue",
                        tint = Color.White
                    )
                }
            },
            actions = {
                IconButton(onClick = { nav.navigate(MergeFormDestination()) }) {
                    Icon(
                        imageVector = Icons.Filled.Receipt,
                        contentDescription = "Localized description",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(Color.InstagramPurple)
        )
    } else {
        CenterAlignedTopAppBar(
            title = { Text("Catalogue", color = Color.White) },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(Color.InstagramPurple)
        )
    }
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
                        .clickable { editItem(nav, it.second, useTemplate) }
                        .border(BorderStroke(3.dp, Color.InstagramPurple)),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (it.second.image == "") {
                        Box(
                            modifier = Modifier
                                .width(200.dp)
                                .height(200.dp)
                                .clickable { editItem(nav, it.second, useTemplate) },
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
                                .clickable { editItem(nav, it.second, useTemplate) }
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
            item() {
                Spacer(Modifier.height(10.dp))

                Row(
                    Modifier
                        .height(IntrinsicSize.Min)
                        .border(BorderStroke(3.dp, Color.InstagramPurple)),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = {
                            nav.navigate(HarvestFormDestination())
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


private fun editItem(nav: DestinationsNavigator, data: ProductInformation, useTemplate: Boolean) {
    nav.navigate(ProductFormDestination(data, useTemplate))
}

private fun addItem(nav: DestinationsNavigator) {
    nav.navigate(ProductFormDestination())
}

private fun generateMockData(amount: Int = 7, context: Context) {
    val dir = File("${context.filesDir}/out")
    if (dir.exists()) {
        dir.deleteRecursively()
    }
    dir.mkdir()
    ProductInformation(
        UUID.randomUUID().toString(),
        "apple",
        "apple description",
        100,
        100,
        "",
        platform1 = false,
        platform2 = false
    ).exportData(context.filesDir.toString())
    ProductInformation(
        UUID.randomUUID().toString(),
        "carrot",
        "carrot description",
        200,
        200,
        "",
        platform1 = false,
        platform2 = false
    ).exportData(context.filesDir.toString())
    ProductInformation(
        UUID.randomUUID().toString(),
        "banana",
        "banana description",
        300,
        300,
        "",
        platform1 = false,
        platform2 = false
    ).exportData(context.filesDir.toString())
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
