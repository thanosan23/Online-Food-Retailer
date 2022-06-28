package ca.uwaterloo.cs

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ca.uwaterloo.cs.ui.theme.InstagramPurple
import ca.uwaterloo.cs.ui.theme.OnlineFoodRetailTheme
import java.io.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OnlineFoodRetailTheme {
                MainContent()
            }
        }
    }


//    @Composable
//    fun MainContent() {
//        Column(
//            Modifier
//                .background(MaterialTheme.colors.background)
//                .padding(20.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            TableScreen()
//        }
//    }


    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    fun MainContent() {
        Scaffold(
            content = { TableScreen() },
            bottomBar = { NavigationBar() }
        )
    }


    @Composable
    fun TableScreen() {
        CenterAlignedTopAppBar(
            title = { Text("Catalogue", color = Color.White) },
            navigationIcon = {
                IconButton(onClick = {
                    addItem()
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
        generateMockData()
        val tableData = readData()
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
                        .clickable { editItem(it.second) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.apple_fruit),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth()
                    )
                }
            }
        }
    }


    private fun editItem(data: ProductInformation) {
        val intent = Intent(this, ProductForm::class.java).apply {
            putExtra("EXTRA_DATA", data)
        }
        startActivity(intent)
    }

    private fun addItem() {
        val intent = Intent(this, ProductForm::class.java)
        startActivity(intent)
    }

    private fun generateMockData(amount: Int = 7) {
        val context = this.baseContext
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
                10 * value + 1L
                // arrayListOf(decoded, decoded)
            ).exportData(context)
        }
    }

    private fun readData(): List<Pair<Int, ProductInformation>> {
        // TODO: platform compatibility
        // TODO: load from platform
        val context = this.baseContext
        val dir = File("${context.filesDir}/out")
        if (!dir.exists()) {
            return emptyList()
        }
        val list = ArrayList<Pair<Int, ProductInformation>>()
        for (saveFile in dir.list()) {
            val fileIS = FileInputStream("${context.filesDir}/out/" + saveFile)
            val inStream = ObjectInputStream(fileIS)
            val productInformation = inStream.readObject() as ProductInformation
            list.add(Pair(productInformation.id, productInformation))
            inStream.close()
            fileIS.close()
        }
        return list
    }

    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.i("kilo", "Permission previously granted")
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            ) -> Log.i("kilo", "Show camera permissions dialog")

            else -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }
}
