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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import ca.uwaterloo.cs.db.DBManager
import ca.uwaterloo.cs.destinations.ProductFormDestination
import ca.uwaterloo.cs.product.ProductInformation
import ca.uwaterloo.cs.pushpull.readProductFromFiles
import ca.uwaterloo.cs.ui.theme.*
import coil.compose.rememberImagePainter
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.util.*


class MainActivity : ComponentActivity() {
    val dbManager = DBManager(null)
    fun logout() {
        AuthUI.getInstance().signOut(this)
    }

    // See: https://developer.android.com/training/basics/intents/result
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        val userId = res.idpResponse?.email?.replace(".", "")?.replace("\"", "")!!
        println("first time $userId")
        Singleton.userId = userId
        Singleton.isNewUser = res.idpResponse?.isNewUser!!
        dbManager.getUserType(Singleton.userId)
        setContent {
            OnlineFoodRetailTheme {
                val context = LocalContext.current
                generateMockData(1, context = context)
                DestinationsNavHost(navGraph = NavGraphs.root)
            }
        }
    }

    // Choose authentication providers
    private val providers = arrayListOf(
        //AuthUI.IdpConfig.EmailBuilder().build(),
//        AuthUI.IdpConfig.PhoneBuilder().build())
        AuthUI.IdpConfig.GoogleBuilder().build()
    )

    // Create and launch sign-in intent
    private val signInIntent = AuthUI.getInstance()
        .createSignInIntentBuilder()
        .setAvailableProviders(providers)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        logout()
        signInLauncher.launch(signInIntent)
    }
}

@Destination
@Composable
fun MainContent(
    nav: DestinationsNavigator
) {
    val useTemplate = Singleton.isFarmer

    val tableData = remember {
        mutableStateOf(ArrayList<Pair<String, ProductInformation>>())
    }
    tableData.value = readProductFromFiles(LocalContext.current)
    TableScreen(nav, useTemplate, tableData)
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun TableScreen(
    nav: DestinationsNavigator, useTemplate: Boolean,
    tableData: MutableState<ArrayList<Pair<String, ProductInformation>>>
) {
    val context = LocalContext.current
    val table = mutableStateListOf<Pair<String, ProductInformation>>()
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
        val tmpTable = ArrayList<Pair<String, ProductInformation>>()
        for (item in tableData.value.filter { entry -> entry.second.name.startsWith(spokenText, ignoreCase = true)}) {
                tmpTable.add(item)
        }
        table.clear()
        for (item in tmpTable) {
            table.add(item)
        }
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
    Scaffold(content = {
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
                                        val tmpTable = ArrayList<Pair<String, ProductInformation>>()
                                        for (item in table) {
                                            if (item.second.name.startsWith(text.text, ignoreCase = true)) {
                                                tmpTable.add(item)
                                            }
                                        }
                                        table.clear()
                                        for (item in tmpTable) {
                                            table.add(item)
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
                                        table.clear()
                                        for (item in tableData.value) {
                                            table.add(item)
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
        Row() {
            Spacer(Modifier.width(30.dp))
            LazyColumn(
                Modifier
                    .padding(66.dp)
                    .background(Color.White)
                    .heightIn(0.dp, 640.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Here are all the lines of your table.
                items(table, key = { it }) {
                    Spacer(Modifier.height(10.dp))

                    Row(
                        Modifier
                            .height(IntrinsicSize.Min)
                            .clickable { editItem(nav, it.second, useTemplate) },
                            //.border(BorderStroke(3.dp, Color.InstagramPurple)),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (it.second.image == "") {
                            /*Box(
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
                            }*/
                            Card(
                                shape = Shapes.medium,
                                backgroundColor = Color.BG,
                                modifier = Modifier
                                    .padding(10.dp)
                                    .width(200.dp)
                                    .clickable { editItem(nav, it.second, useTemplate) },

                                ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                                ) {
                                    Image(
                                        painter = painterResource(ca.uwaterloo.cs.R.drawable.unknown),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(200.dp)
                                            .clip(RoundedCornerShape(36.dp)),
                                    )
                                    Row(modifier = Modifier.padding(top = 20.dp)) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            val amount = it.second.amount.toString()
                                            val name = it.second.name
                                            Text(
                                                text = name,
                                                style = TextStyle(
                                                    color = Color.TextPrimary,
                                                    fontSize = 16.sp
                                                )
                                            )
                                            Text(
                                                text = "Amount: $amount",
                                                style = TextStyle(
                                                    color = Color.TextSec,
                                                    fontSize = 16.sp
                                                )
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                editItem(nav, it.second, useTemplate)
                                            },
                                            modifier = Modifier
                                                .background(
                                                    color = Color.Green1,
                                                    shape = RoundedCornerShape(10.dp))
                                                .size(48.dp)
                                        ) {
                                            Icon(
                                                painterResource(ca.uwaterloo.cs.R.drawable.edit),
                                                tint = Color.White,
                                                contentDescription = null,
                                                modifier = Modifier.size(30.dp))
                                        }
                                    }
                                }
                            }
                        } else {
                            /*Image(
                                painter = rememberImagePainter(it.second.image.toUri()),
                                contentDescription = null,
                                modifier = Modifier
                                    .width(200.dp)
                                    .height(200.dp)
                                    .clickable { editItem(nav, it.second, useTemplate) }
                            )*/
                            Card(
                                shape = Shapes.medium,
                                backgroundColor = Color.BG,
                                modifier = Modifier
                                    .padding(10.dp)
                                    .width(200.dp)
                                    .clickable { editItem(nav, it.second, useTemplate) },

                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                                ) {
                                    Image(
                                        painter = rememberImagePainter(it.second.image.toUri()),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(200.dp)
                                            .clip(RoundedCornerShape(36.dp)),
                                    )
                                    Row(modifier = Modifier.padding(top = 20.dp)) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            val amount = it.second.amount.toString()
                                            val name = it.second.name
                                            Text(
                                                text = name,
                                                style = TextStyle(
                                                    color = Color.TextPrimary,
                                                    fontSize = 16.sp
                                                )
                                            )
                                            Text(
                                                text = "Amount: $amount",
                                                style = TextStyle(
                                                    color = Color.TextSec,
                                                    fontSize = 16.sp
                                                )
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                editItem(nav, it.second, useTemplate)
                                            },
                                            modifier = Modifier
                                                .background(
                                                color = Color.Green1,
                                                shape = RoundedCornerShape(10.dp))
                                                .size(48.dp)
                                        ) {
                                            Icon(
                                                painterResource(ca.uwaterloo.cs.R.drawable.edit),
                                                tint = Color.White,
                                                contentDescription = null,
                                                modifier = Modifier.size(30.dp))
                                        }
                                    }
                                }
                            }
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
//                item() {
//                    Spacer(Modifier.height(10.dp))
//
//                    Row(
//                        Modifier
//                            .height(IntrinsicSize.Min)
//                            .border(BorderStroke(3.dp, Color.InstagramPurple)),
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.Center
//                    ) {
//                        IconButton(
//                            onClick = {
//                                nav.navigate(HarvestFormDestination())
//                            },
//                            modifier = Modifier
//                                .width(60.dp)
//                                .height(60.dp)
//                        ) {
//                            Icon(
//                                imageVector = Icons.Filled.Add,
//                                contentDescription = "Catalogue",
//                                tint = Color.Green,
//                                modifier = Modifier.fillMaxSize(1.0f)
//                            )
//                        }
//                    }
//                }
            }
        }
    },
        bottomBar = { NavigationBar(nav) },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier
                    .width(80.dp)
                    .height(80.dp),
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
        }
    )
}

private fun editItem(nav: DestinationsNavigator, data: ProductInformation, useTemplate: Boolean) {
    nav.navigate(ProductFormDestination(data, useTemplate))
}

private fun addItem(nav: DestinationsNavigator) {
    nav.navigate(ProductFormDestination(creation = true))
}

@Composable
fun generateMockData(amount: Int = 7, context: Context) {
}


fun createMockProduct(context: Context) {
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
}
