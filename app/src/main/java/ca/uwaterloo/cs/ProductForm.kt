package ca.uwaterloo.cs

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import ca.uwaterloo.cs.destinations.MainContentDestination
import ca.uwaterloo.cs.form.*
import ca.uwaterloo.cs.ui.theme.OnlineFoodRetailTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.io.File
import java.text.SimpleDateFormat


@Destination
@Composable
fun ProductForm(navigator: DestinationsNavigator, data: ProductInformation?) {
    OnlineFoodRetailTheme {
        Scaffold(
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.background)
                        .padding(20.dp),
                ) {
                    Text(if (data == null) "ADD PRODUCT" else "EDIT PRODUCT")
                    ShowProductForm(navigator, data ?: ProductInformation())
                }
            },
            bottomBar = { NavigationBar() }
        )
    }
}

@Composable
fun ShowProductForm(nav: DestinationsNavigator, data: ProductInformation) {
    val state by remember { mutableStateOf(FormState()) }
    val images = ArrayList(data.images)
    val context = LocalContext.current

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
                saveProduct(data, state.getData(), images, context)
                nav.navigate(MainContentDestination)
            }
        }) {
            Text("Submit")
        }
        Button(onClick = {
            nav.navigate(MainContentDestination)
        }) {
            Text("Cancel")
        }
        Button(onClick = {
            deleteProduct(data, context)
            nav.navigate(MainContentDestination)
        }) {
            Text("Delete")
        }
    }
}

@Composable
fun FormImages(images: ArrayList<String>) {
    var shouldShowPhoto by remember { mutableStateOf(images.isNotEmpty()) }
    println("SHOULD SHOW: $shouldShowPhoto")
    println(images.getOrNull(0))
    var imageUri by remember {
        mutableStateOf(images.getOrNull(0)?.toUri())
    }
    val context = LocalContext.current
    var fileUri: Uri? = null
    var isCameraSelected = false

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imageUri = fileUri!!
            images.add(fileUri.toString())
            println("Images")
            for (img in images) {
                println(img)
            }
            println("end")
            shouldShowPhoto = true
        }
    }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        if (uri != null) {
            images.add(uri.toString())
            println("Images")
            for (img in images) {
                println(img)
            }
            println("end")
            shouldShowPhoto = true
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            if (isCameraSelected) {
                fileUri = createImageFile(context)
                cameraLauncher.launch(fileUri)
            } else {
                galleryLauncher.launch("image/*")
            }
        } else {
            Toast.makeText(context, "Permission Denied!", Toast.LENGTH_SHORT).show()
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .height(200.dp)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (imageUri != null) {
                Column(
                    Modifier
                        .background(Color.Cyan) // TODO: delete
                        .width(200.dp)
                        .height(200.dp)
                        .aspectRatio(1f)
                )
                {
                    Box(
                        modifier = Modifier
                            // .background(Color.InstagramPurple) // TODO: REMOVE DEBUG BACKGROUNDS
                            .width(200.dp)
                            .height(200.dp)
                            .aspectRatio(1f),
                        contentAlignment = Alignment.Center
                    )
                    {
                        imageUri?.let {
                            val btm = if (Build.VERSION.SDK_INT < 28) {
                                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                            } else {
                                val source = ImageDecoder.createSource(context.contentResolver, it)
                                ImageDecoder.decodeBitmap(source)
                            }
                            Image(
                                bitmap = btm.asImageBitmap(),
                                contentDescription = "Image",
                                alignment = Alignment.TopCenter,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.45f)
                                    .padding(top = 10.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                        /*
                        Image(
                            painter = rememberImagePainter(images[0].toUri()),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize(1f)
                        )

                         */
                    }
                }
            }
            // TODO: REQUEST CAMERA PERMISSION
            Box(
                modifier = Modifier
                    // .background(Color.InstagramPurple) // TODO: REMOVE DEBUG BACKGROUNDS
                    .width(200.dp)
                    .height(200.dp)
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            )
            {
                IconButton(
                    modifier = Modifier.fillMaxSize(1f),
                    onClick = {
                        val options =
                            arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
                        val builder = AlertDialog.Builder(context)
                        builder.setTitle("Add Photo")
                        builder.setItems(options) { dialog, item ->
                            if (options[item] == "Take Photo") {
                                when (PackageManager.PERMISSION_GRANTED) {
                                    ContextCompat.checkSelfPermission(
                                        context, Manifest.permission.CAMERA
                                    ) -> {
                                        fileUri = createImageFile(context)
                                        cameraLauncher.launch(fileUri)
                                    }
                                    else -> {
                                        isCameraSelected = true
                                        permissionLauncher.launch(Manifest.permission.CAMERA)
                                    }
                                }
                            } else if (options[item] == "Choose from Gallery") {
                                when (PackageManager.PERMISSION_GRANTED) {
                                    ContextCompat.checkSelfPermission(
                                        context, Manifest.permission.READ_EXTERNAL_STORAGE
                                    ) -> {
                                        galleryLauncher.launch("image/*")
                                    }
                                    else -> {
                                        isCameraSelected = false
                                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                                    }
                                }
                            } else if (options[item] == "Cancel") {
                                dialog.dismiss()
                            }
                        }
                        builder.show()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.PhotoCamera,
                        contentDescription = "Catalogue",
                        tint = Color.Black,
                        modifier = Modifier.fillMaxSize(0.65f)
                    )
                }
            }
        }
        Button(
            enabled = shouldShowPhoto,
            onClick = {
                images.clear()
                imageUri = null
                shouldShowPhoto = false
            },
        ) {
            Text("Change Image")
        }
    }
}

private fun createImageFile(context: Context): Uri {
    val timeStamp = SimpleDateFormat.getDateTimeInstance()
    val file = File(context.filesDir, "JPEG_test.jpg")
    return FileProvider.getUriForFile(
        context,
        context.applicationContext.packageName + ".provider",
        file
    );
}

private fun saveProduct(
    data: ProductInformation,
    newData: Map<String, String>,
    newImages: ArrayList<String>,
    context: Context
) {
    println("NEW IMAGES")
    for (str in newImages) {
        println(str)
    }
    println("END")
    data.name = newData["Name"]!!
    data.description = newData["Description"]!!
    data.price = (newData["Price"]!!.toDouble() * 100).toInt()
    data.amount = newData["Amount"]!!.toLong()
    data.images.clear()
    data.images.addAll(newImages)
    data.exportData(context)
}

private fun deleteProduct(data: ProductInformation, context: Context) {
    AlertDialog.Builder(context)
        .setTitle("Delete Product")
        .setMessage("Are you sure you want to delete this product? This operation is irreversible.")
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setPositiveButton(
            android.R.string.yes
        ) { _, _ ->
            data.deleteData(context)
        }
        .setNegativeButton(android.R.string.no, null).show()
}
