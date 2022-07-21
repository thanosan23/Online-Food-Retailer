package ca.uwaterloo.cs

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import ca.uwaterloo.cs.db.DBClient
import ca.uwaterloo.cs.db.DBManagerTest
import ca.uwaterloo.cs.destinations.HarvestFormDestination
import ca.uwaterloo.cs.destinations.MergeFormDestination
import ca.uwaterloo.cs.destinations.ProductFormDestination
import ca.uwaterloo.cs.product.ProductInformation
import ca.uwaterloo.cs.ui.theme.InstagramPurple
import ca.uwaterloo.cs.ui.theme.OnlineFoodRetailTheme
import coil.compose.rememberImagePainter
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream
import java.util.*


class MainActivity : ComponentActivity() {
    // for testing

    fun logout(){
        AuthUI.getInstance().signOut(this)
    }
    private val dbManagerTest = DBManagerTest()
    // See: https://developer.android.com/training/basics/intents/result
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        val userId = res.idpResponse?.email?.replace(".","")?.replace("\"", "")!!
        println("first time $userId")
        Singleton.userId = userId
        Singleton.isNewUser = res.idpResponse?.isNewUser!!
        setContent {
            OnlineFoodRetailTheme {
                val context = LocalContext.current
                generateMockData(context = context)
                DestinationsNavHost(navGraph = NavGraphs.root)
            }
        }
    }

    // Choose authentication providers
    private val providers = arrayListOf(
        //AuthUI.IdpConfig.EmailBuilder().build(),
//        AuthUI.IdpConfig.PhoneBuilder().build())
        AuthUI.IdpConfig.GoogleBuilder().build())

    // Create and launch sign-in intent
    private val signInIntent = AuthUI.getInstance()
        .createSignInIntentBuilder()
        .setAvailableProviders(providers)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        logout()
//        dbManagerTest.part2ProductTest()
        signInLauncher.launch(signInIntent)
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Destination
@Composable
fun MainContent(nav: DestinationsNavigator) {
    val useTemplate = true //farmer:true,worker:false
    val context = LocalContext.current
    val startLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        val spokenText: String =
            it.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).let { results ->
                results?.get(0) ?: ""
            }
        Toast.makeText(context, spokenText, Toast.LENGTH_SHORT).show()
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            startLauncher.launch(intent)
        } else {
            Toast.makeText(context, "Permission Denied!", Toast.LENGTH_SHORT).show()
        }
    }
    Scaffold(
        content = {
            val context = LocalContext.current
            val tableData = readData(context)
            TableScreen(nav, useTemplate, tableData) },
        bottomBar = { NavigationBar(nav) }),
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp),
                onClick = {
                    when (PackageManager.PERMISSION_GRANTED) {
                        ContextCompat.checkSelfPermission(
                            context, Manifest.permission.RECORD_AUDIO
                        ) -> {
                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                            intent.putExtra(
                                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                            )
                            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Talk")
                            startLauncher.launch(intent)
                        }
                        else -> {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    }
                }) {
                Icon(
                    imageVector = Icons.Filled.Mic,
                    modifier = Modifier.fillMaxSize(0.5f),
                    contentDescription = "Voice Search"
                )
            }
        },
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun TableScreen(nav: DestinationsNavigator, useTemplate: Boolean, table: ArrayList<Pair<String, ProductInformation>>) {
    val context = LocalContext.current
    val tableData = mutableStateListOf<Pair<String, ProductInformation>>()
    for (item in table) {
        tableData.add(item)
    }
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
                val openDialog = remember { mutableStateOf(false) }
                var text by remember { mutableStateOf(TextFieldValue("")) }
                IconButton(onClick = { openDialog.value = true }) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Localized description",
                        tint = Color.White
                    )
                }
                if (openDialog.value) {
                    AlertDialog(
                        onDismissRequest = { openDialog.value = false },
                        title = { Text(text = "Search") },
                        text = {
                            Column() {
                                TextField(
                                    value = text,
                                    onValueChange = {
                                        text = it
                                    }
                                )
                                //Log.d("", text.toString())
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    //val tableData = readData(context)
                                    val tmpTable = ArrayList<Pair<String, ProductInformation>>()
                                    for (item in tableData) {
//                                        Log.d("item", item.second.name)
//                                        Log.d("text", text.text)
                                        if (item.second.name.indexOf(text.text) != -1) {
//                                            Log.d("", "HI")
                                            tmpTable.add(item)
                                            //tableData.remove(item)
                                            //editItem(nav, item.second, useTemplate)
                                        }
                                    }
                                    tableData.clear()
                                    for (item in tmpTable) {
                                        tableData.add(item)
                                    }
                                    text = TextFieldValue("")
                                    openDialog.value = false

                                }) {
                                Text("Filter")
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = {
                                    tableData.clear()
                                    for (item in table) {
                                        tableData.add(item)
                                    }
                                    openDialog.value = false
                                    text = TextFieldValue("")
                                }) {
                                Text("Clear")
                            }
                        }
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
    // val tableData = readData(context)
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
                        Image(
                            painter = rememberImagePainter(it.second.image.toUri()),
                            contentDescription = null,
                            modifier = Modifier
                                .width(200.dp)
                                .height(200.dp)
                                .clickable { editItem(nav, it.second, useTemplate) }
                        )
                    }
//                    IconButton(
//                        onClick = {
//                            nav.navigate(HarvestFormDestination(it.second))
//                        },
//                        modifier = Modifier
//                            .width(60.dp)
//                            .height(60.dp)
//                    ) {
//                        Icon(
//                            imageVector = Icons.Filled.Add,
//                            contentDescription = "Catalogue",
//                            tint = Color.Green,
//                            modifier = Modifier.fillMaxSize(1.0f)
//                        )
//                    }

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

private fun generateMockData(context: Context) {
    val dir = File("${context.filesDir}/out")
    if (dir.exists()) {
        dir.deleteRecursively()
    }
    val dbClient = DBClient()
    dbClient.context = LocalContext.current

    ProductInformation(
        UUID.randomUUID().toString(),
        "apple",
        "apple description",
        100,
        100,
        dbClient.getImage().toString(),
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

private fun readData(context: Context): ArrayList<Pair<String, ProductInformation>> {
    // TODO: platform compatibility
    // TODO: load from platform
    val dir = File("${context.filesDir}/out")
    if (!dir.exists()) {
        return ArrayList()
    }
    val list = ArrayList<Pair<String, ProductInformation>>()
    for (saveFile in dir.walk()) {
        if (saveFile.isFile && saveFile.canRead() && saveFile.name.contains("Product-")) {
            val fileIS = FileInputStream(saveFile)
            val inStream = ObjectInputStream(fileIS)
            val productInformation = inStream.readObject() as ProductInformation
            list.add(Pair(productInformation.productId!!, productInformation))
            inStream.close()
            fileIS.close()
        }
    }
    return list
}
