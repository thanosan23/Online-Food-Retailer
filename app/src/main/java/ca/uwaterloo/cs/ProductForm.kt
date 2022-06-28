package ca.uwaterloo.cs

import android.R
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import ca.uwaterloo.cs.form.*
import ca.uwaterloo.cs.platform.PlatformState
import ca.uwaterloo.cs.ui.theme.InstagramPurple
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
        val focusManager = LocalFocusManager.current
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(MaterialTheme.colors.background)
                .padding(20.dp)
                .pointerInput(Unit){detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })}
            ,
        ) {
            Text(if (data == null) "ADD PRODUCT" else "EDIT PRODUCT")
            ShowProductForm(data ?: ProductInformation())
        }
    }

    @Composable
    fun ShowProductForm(data: ProductInformation) {
        val formState by remember { mutableStateOf(FormState()) }
        val platformState by remember {mutableStateOf(PlatformState())}
        val images = ArrayList<String>(data.images)
        Spacer(Modifier.height(20.dp))
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
            ,

        ) {
            platformState.platformsUI.PlatformsDropDown()
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(20.dp))
                Form(
                    state = formState,
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
                SendCancelDeleteWidgets(
                    formState = formState,
                    platformState = platformState,
                    data = data,
                    images = images
                )
            }
        }
    }

    @Composable
    fun SendCancelDeleteWidgets(formState: FormState, platformState: PlatformState, data: ProductInformation, images: ArrayList<String>){
        Row() {
            Button(onClick = {
                if (formState.validate() && platformState.validate()) {
                    saveProduct(data, formState.getData() + platformState.getData(), images)
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.Send,
                    contentDescription = "Catalogue",
                    tint = Color.InstagramPurple
                )
            }
            Button(onClick = {
                super.finish()
            }) {
                Icon(
                    imageVector = Icons.Filled.Cancel,
                    contentDescription = "Catalogue",
                    tint = Color.InstagramPurple
                )
            }
            Button(onClick = {
                deleteProduct(data)
            }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Catalogue",
                    tint = Color.InstagramPurple
                )
            }
        }
        Spacer(Modifier.height(30.dp))
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
                Icon(
                    imageVector = Icons.Filled.ChangeCircle,
                    contentDescription = "Catalogue",
                    tint = Color.InstagramPurple
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

