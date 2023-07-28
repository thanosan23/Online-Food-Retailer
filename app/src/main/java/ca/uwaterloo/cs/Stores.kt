package ca.uwaterloo.cs

import android.annotation.SuppressLint
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ca.uwaterloo.cs.destinations.ProductFormDestination
import ca.uwaterloo.cs.destinations.StoreFormDestination
import ca.uwaterloo.cs.product.ProductInformation
import ca.uwaterloo.cs.pushpull.readProductFromFiles
import ca.uwaterloo.cs.pushpull.readStoreFromFiles
import ca.uwaterloo.cs.ui.theme.*
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlin.collections.ArrayList

@Destination
@Composable
fun Stores(nav: DestinationsNavigator) {

    val useTemplate = Singleton.isFarmer

    val tableData = remember {
        mutableStateOf(ArrayList<Pair<String, StoreInformation>>())
    }
    tableData.value = readStoreFromFiles(LocalContext.current)

    OnlineFoodRetailTheme {
        Scaffold(content = {
            StoreTableScreen(nav, tableData)
        },
            bottomBar = { NavigationBar(nav )},
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text("Stores", color= Color.White)
                    },
                    navigationIcon = {
                      IconButton(onClick = {
                          // add store
                          addStore(nav);
                      }) {
                          Icon(
                              imageVector = Icons.Filled.Add,
                              contentDescription = "Stores",
                              tint = Color.White
                          )
                      }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(Color.InstagramPurple)
                )
            }
        )
    }
}
private fun addStore(nav: DestinationsNavigator) {
    nav.navigate(StoreFormDestination());
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun StoreTableScreen(
    nav: DestinationsNavigator,
    tableData: MutableState<ArrayList<Pair<String, StoreInformation>>>
) {
    val context = LocalContext.current
    val table = mutableStateListOf<Pair<String, StoreInformation>>()
    for (item in tableData.value) {
        table.add(item)
    }
    val startLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { it ->
        val spokenText: String =
            it.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).let { results ->
                results?.get(0) ?: ""
            }
        val tmpTable = java.util.ArrayList<Pair<String, StoreInformation>>()
        for (item in tableData.value.filter { entry -> entry.second.name.startsWith(spokenText, ignoreCase = true)}) {
            tmpTable.add(item)
        }
        table.clear()
        for (item in tmpTable) {
            table.add(item)
        }
    }
    Scaffold(content = {
        LazyColumn(
            Modifier
                .background(Color.White)
                .fillMaxWidth()
                .heightIn(0.dp, 640.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Here are all the lines of your table.
            items(table, key = { it }) {
                Spacer(Modifier.height(10.dp))

                Row(
                    Modifier
                        .height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Card(
                        shape = Shapes.medium,
                        backgroundColor = Color.BG,
                        modifier = Modifier
                            .padding(10.dp)
                            .width(200.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                        ) {
                            Text(it.second.name)
                            IconButton(
                                onClick = {
                                    editStore(nav, it.second)
                                },
                                modifier = Modifier
                                    .background(
                                        color = Color.Green1,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .size(30.dp)
                            ) {
                                Icon(
                                    painterResource(R.drawable.edit),
                                    tint = Color.White,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }
    })
}
private fun editStore(nav: DestinationsNavigator, data: StoreInformation) {
    nav.navigate(StoreFormDestination(data))
}