package ca.uwaterloo.cs

import android.R
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import ca.uwaterloo.cs.form.*
import ca.uwaterloo.cs.ui.theme.OnlineFoodRetailTheme
import coil.compose.rememberImagePainter
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class ProductForm : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data = intent.getSerializableExtra("EXTRA_DATA") as? ProductInformation
        println("RECEIVED:")
        println(data)
        println("END")

        setContent {
            OnlineFoodRetailTheme {
//                Column(
//                    modifier = Modifier
//                        .fillMaxHeight()
//                        .fillMaxWidth()
//                        .background(MaterialTheme.colors.background)
//                        .padding(20.dp),
//                ) {
//                    Text(if (data == null) "ADD PRODUCT" else "EDIT PRODUCT")
//                    ShowProductForm(data ?: ProductInformation())
//                }
                Scaffold(
                    content = { FullProductForm(data ?: ProductInformation() ) },
                    bottomBar = { NavigationBar() }
                )
            }
        }
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    @Composable
    fun FullProductForm(data: ProductInformation) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(MaterialTheme.colors.background)
                .padding(20.dp),
        ) {
            Text(if (data == null) "ADD PRODUCT" else "EDIT PRODUCT")
            ShowProductForm(data ?: ProductInformation())
        }
    }

    @Composable
    fun ShowProductForm(data: ProductInformation) {
        val state by remember { mutableStateOf(FormState()) }
        val images = ArrayList<String>(data.images)

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
                    saveProduct(data, state.getData(), images)
                }
            }) {
                Text("Submit")
            }
            Button(onClick = {
                super.finish()
            }) {
                Text("Cancel")
            }
            Button(onClick = {
                deleteProduct(data)
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
                        CameraView(
                            outputDirectory = outputDirectory,
                            executor = cameraExecutor,
                            onImageCaptured = { uri ->
                                images.add(uri.toString())
                                shouldShowPhoto = true
                                shouldShowCamera = false
                            },
                            onError = { Log.e("kilo", "View error:", it) }
                        )
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
        newImages: ArrayList<String>
    ) {
        data.name = newData["Name"]!!
        data.description = newData["Description"]!!
        data.price = (newData["Price"]!!.toDouble() * 100).toInt()
        data.amount = newData["Amount"]!!.toLong()
        data.images.clear()
        data.images.addAll(newImages)
        data.exportData(this.baseContext)
        this.finish()
    }

    private fun deleteProduct(data: ProductInformation) {
        AlertDialog.Builder(this)
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete this product? This operation is irreversible.")
            .setIcon(R.drawable.ic_dialog_alert)
            .setPositiveButton(
                R.string.yes
            ) { _, _ ->
                data.deleteData(this.baseContext)
                this.finish()
            }
            .setNegativeButton(R.string.no, null).show()
    }

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, "OnlineFoodRetailer").apply { mkdirs() }
        }

        return if ((mediaDir != null) && mediaDir.exists()) mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}

