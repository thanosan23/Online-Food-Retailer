package ca.uwaterloo.cs

import android.Manifest
import android.R
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
        }
        requestCameraPermission()
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
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
        val context = this.baseContext
        val imgRow = Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            //for (img in images) {
            Box(
                Modifier
                    .background(Color.Cyan)
                    .size(40.dp)
                    .aspectRatio(1f)
            )
            {
                if (shouldShowPhoto.value) {
                    Image(
                        painter = rememberImagePainter(photoUri),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Button(onClick = {
                    // images.remove(img)
                }) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Delete image"
                    )
                }
            }
            //}
            Box(
                modifier = Modifier
                    .background(Color.Cyan)
                    .size(40.dp)
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            )
            {
                CameraView(
                    outputDirectory = outputDirectory,
                    executor = cameraExecutor,
                    onImageCaptured = ::handleImageCapture,
                    onError = { Log.e("kilo", "View error:", it) }
                )
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

    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.i("kilo", "Permission previously granted")
                shouldShowCamera.value = true
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
    ) { isGranted ->
        if (isGranted) {
            Log.i("kilo", "Permission granted")
            shouldShowCamera.value = true
        } else {
            Log.i("kilo", "Permission denied")
        }
    }

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)

    private fun handleImageCapture(uri: Uri) {
        Log.i("kilo", "Image captured: $uri")
        photoUri = uri
        shouldShowPhoto.value = true
        shouldShowCamera.value = false
    }

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

    private lateinit var photoUri: Uri
    private var shouldShowPhoto: MutableState<Boolean> = mutableStateOf(false)
}

