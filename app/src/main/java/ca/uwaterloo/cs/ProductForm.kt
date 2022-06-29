package ca.uwaterloo.cs

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import ca.uwaterloo.cs.destinations.MainContentDestination
import ca.uwaterloo.cs.form.*
import ca.uwaterloo.cs.platform.PlatformState
import ca.uwaterloo.cs.ui.theme.InstagramPurple
import ca.uwaterloo.cs.ui.theme.OnlineFoodRetailTheme
import coil.compose.rememberImagePainter
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Destination
@Composable
fun ProductForm(
    navigator: DestinationsNavigator,
    data: ProductInformation?,
    useTemplate: Boolean = false
) {
    val formState by remember { mutableStateOf(FormState()) }
    OnlineFoodRetailTheme {
        Scaffold(
            content = {
                val focusManager = LocalFocusManager.current
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.background)
                        .padding(20.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = {
                                focusManager.clearFocus()
                            })
                        },
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
fun ShowProductForm(
    nav: DestinationsNavigator,
    data: ProductInformation,
    useTemplate: Boolean,
) {
    val formState by remember { mutableStateOf(FormState()) }
    val platformState by remember { mutableStateOf(PlatformState(data)) }
    var image by remember { mutableStateOf(data.image) }
    val context = LocalContext.current
    var fileUri: Uri? = null
    var isCameraSelected = false
    var isGallerySelected = false

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            image = ""
            image = fileUri!!.toString()
        }
    }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            image = ""
            image = uri.toString()
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                            or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            } catch (e: Exception) {
                println("Exception: " + e.message)
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            if (isCameraSelected) {
                fileUri = createImageFile(context)
                cameraLauncher.launch(fileUri)
            } else if (isGallerySelected) {
                galleryLauncher.launch("image/*")
            }
        } else {
            Toast.makeText(context, "Permission Denied!", Toast.LENGTH_SHORT).show()
        }
    }

    Spacer(Modifier.height(20.dp))
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(20.dp),

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
                        initValue = formState.getData()
                            .getOrDefault("Name", if (data.name == "") "" else data.name),
                        prompt = "Enter product name",
                        label = "Product Name",
                        validators = listOf(Required())
                    ),
                    Field(
                        name = "Description",
                        initValue = formState.getData().getOrDefault(
                            "Description",
                            if (data.description == "") "" else data.description
                        ),
                        prompt = "Enter description",
                        label = "Product Description",
                        validators = listOf(Required())
                    ),
                    Field(
                        name = "Amount",
                        initValue = formState.getData().getOrDefault(
                            "Amount",
                            if (data.amount == 0L) "0" else data.amount.toString()
                        ),
                        prompt = "Enter amount available",
                        label = "Product Amount",
                        validators = listOf(IsNumber(), Required()),
                        inputType = KeyboardType.Number,
                        formatter = NumberTransformation()
                    ),
                    Field(
                        name = "Price",
                        initValue = formState.getData().getOrDefault(
                            "Price",
                            if (data.price == 0) "0.00" else (data.price / 100.0).toString()
                        ),
                        prompt = "Enter price",
                        label = "Product Price",
                        validators = listOf(Required(), IsNumber(), NonZero()),
                        inputType = KeyboardType.Number,
                    ),
                )
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .height(200.dp)
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (image != "") {
                        Column(
                            Modifier
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
                                /* TODO: FIX PERMISSION ISSUE
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

                                 */
                                Image(
                                    painter = rememberImagePainter(image.toUri()),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize(1f)
                                )

                                Button(
                                    enabled = image != "",
                                    onClick = {
                                        image = ""
                                    },
                                    modifier = Modifier.align(Alignment.BottomEnd)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ChangeCircle,
                                        contentDescription = "Delete Image",
                                        tint = Color.InstagramPurple
                                    )
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
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
                                        arrayOf<CharSequence>(
                                            "Take Photo",
                                            "Choose from Gallery",
                                            "Cancel"
                                        )
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
                                                    isGallerySelected = false
                                                    permissionLauncher.launch(Manifest.permission.CAMERA)
                                                }
                                            }
                                        } else if (options[item] == "Choose from Gallery") {
                                            when (PackageManager.PERMISSION_GRANTED) {
                                                ContextCompat.checkSelfPermission(
                                                    context,
                                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                                ) -> {
                                                    galleryLauncher.launch("image/*")
                                                }
                                                else -> {
                                                    isCameraSelected = false
                                                    isGallerySelected = true
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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
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
                    }
                }
            }
            SendCancelDeleteWidgets(
                formState = formState,
                platformState = platformState,
                data = data,
                image = image,
                nav = nav,
                context = context
            )
        }
    }
}

@Composable
fun SendCancelDeleteWidgets(
    formState: FormState,
    platformState: PlatformState,
    data: ProductInformation,
    image: String,
    nav: DestinationsNavigator,
    context: Context
) {
    Row {
        Button(onClick = {
            if (formState.validate() && platformState.validate()) {
                saveProduct(data, formState.getData() + platformState.getData(), image, context)
                nav.navigate(MainContentDestination)
            }
        }) {
            Icon(
                imageVector = Icons.Filled.Save,
                contentDescription = "Catalogue",
                tint = Color.InstagramPurple
            )
        }
        Button(onClick = {
            nav.navigate(MainContentDestination)
        }) {
            Icon(
                imageVector = Icons.Filled.Cancel,
                contentDescription = "Catalogue",
                tint = Color.InstagramPurple
            )
        }
        Button(onClick = {
            deleteProduct(data, context)
            nav.navigate(MainContentDestination)
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

private fun createImageFile(context: Context): Uri {
    val timeStamp = SimpleDateFormat.getDateTimeInstance().format(Date())
    val file = File(context.filesDir, "JPEG_$timeStamp.jpg")
    return FileProvider.getUriForFile(
        context,
        context.applicationContext.packageName + ".provider",
        file
    )
}

private fun saveProduct(
    data: ProductInformation,
    newData: Map<String, String>,
    newImage: String,
    context: Context
) {
    data.name = newData["Name"]!!
    data.description = newData["Description"]!!
    data.price = (newData["Price"]!!.toDouble()).toInt()
    data.amount = newData["Amount"]!!.toLong()
    data.image = newImage
    data.platform1 = newData["platform1"].toBoolean()
    data.platform2 = newData["platform2"].toBoolean()
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
