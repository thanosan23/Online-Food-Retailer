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
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import ca.uwaterloo.cs.destinations.ProductFormDestination
import ca.uwaterloo.cs.ui.theme.InstagramPurple
import ca.uwaterloo.cs.ui.theme.OnlineFoodRetailTheme
import coil.compose.rememberImagePainter
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OnlineFoodRetailTheme {
                val context = LocalContext.current
                generateMockData(context = context)
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
        bottomBar = { NavigationBar() }
    )
}

@Composable
fun TableScreen(nav: DestinationsNavigator) {
    val context = LocalContext.current
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
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(Color.InstagramPurple)
    )
    // Just a fake data... a Pair of Int and String
    // TODO: REMOVE / UPGRADE MOCK DATA GENERATION IN FINAL PRODUCT
    val tableData = readData(context)
    // Each cell of a column must have the same weight.
    // The LazyColumn will be our table. Notice the use of the weights below
    Spacer(Modifier.height(70.dp))
    LazyColumn(
        Modifier
            .padding(61.dp)
            .background(Color.White)
            .border(BorderStroke(5.dp, Color.InstagramPurple))
            .heightIn(0.dp, 640.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Here are all the lines of your table.
        items(tableData, key = { it }) {
            Divider(
                Modifier
                    .border(BorderStroke(0.dp, Color.InstagramPurple))
            )
            Row(
                Modifier
                    .height(IntrinsicSize.Min)
                    .clickable { editItem(nav, it.second) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                val painter = if (it.second.image == "") {
                    painterResource(id = R.drawable.apple_fruit)
                }
                else {
                    rememberImagePainter(it.second.image.toUri())
                }
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .width(200.dp)
                        .height(200.dp)
                )
            }
        }
    }
}


private fun editItem(nav: DestinationsNavigator, data: ProductInformation) {
    nav.navigate(ProductFormDestination(data))
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
    (1..amount).forEach { value ->
        ProductInformation(
            value,
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

private fun readData(context: Context): List<Pair<Int, ProductInformation>> {
    // TODO: platform compatibility
    // TODO: load from platform
    val dir = File("${context.filesDir}/out")
    if (!dir.exists()) {
        return emptyList()
    }
    val list = ArrayList<Pair<Int, ProductInformation>>()
    for (saveFile in dir.walk()) {
        if (saveFile.isFile && saveFile.canRead() && saveFile.name.contains("Product-")) {
            val fileIS = FileInputStream(saveFile)
            val inStream = ObjectInputStream(fileIS)
            val productInformation = inStream.readObject() as ProductInformation
            list.add(Pair(productInformation.id, productInformation))
            inStream.close()
            fileIS.close()
        }
    }
    return list
}
