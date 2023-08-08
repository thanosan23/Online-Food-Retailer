package ca.uwaterloo.cs.product

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Paint.Align
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import ca.uwaterloo.cs.NavigationBar
import ca.uwaterloo.cs.Singleton
import ca.uwaterloo.cs.ValidFoodNames
import ca.uwaterloo.cs.db.DBClient
import ca.uwaterloo.cs.db.DBManager
import ca.uwaterloo.cs.destinations.simulateTransactionDestination
import ca.uwaterloo.cs.destinations.MainContentDestination
import ca.uwaterloo.cs.form.*
import ca.uwaterloo.cs.harvest.HarvestInformation
import ca.uwaterloo.cs.platform.PlatformState
import ca.uwaterloo.cs.pushpull.sync
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
    useTemplate: Boolean = true,  //worker:false, farmer:true
    creation: Boolean = false
) {
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
                        }
                        .systemBarsPadding(),
                ) {
                    Text(if (data == null) "ADD PRODUCT" else "EDIT PRODUCT")
                    ShowProductForm(navigator,
                        data ?: ProductInformation(),
                        useTemplate,
                        creation)
                }
            },
            bottomBar = { NavigationBar(navigator) }
        )
    }
}

@Composable
fun ShowProductForm(
    nav: DestinationsNavigator,
    data: ProductInformation,
    useTemplate: Boolean,
    creation: Boolean,
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
        if (useTemplate) {
//            Row(verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier
//                    .fillMaxWidth()
//            ) {
//                platformState.platformsUI.PlatformsDropDown()
//
//            }
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
                            validators = listOf(Required()),
                            dropdownList = ValidFoodNames().getValidFoodNames()
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
                            validators = listOf(Required(), IsNumber()),
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
                            dropdownList = listOf("0.50", "1.00", "2.00", "5.00")
                        ),
                        Field(
                            name = "Unit",
                            initValue = formState.getData().getOrDefault(
                                "Unit",
                                if (data.unit == "kg") "kg" else data.unit
                            ),
                            prompt = "Enter Unit",
                            label = "Unit",
                            validators = listOf(Required()),
                            inputType = KeyboardType.Text,
                            dropdownList = listOf("kg", "lbs")
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
                Column() {
                    SendCancelDeleteWidgets(
                        formState = formState,
                        platformState = platformState,
                        data = data,
                        image = image,
                        nav = nav,
                        context = context,
                        useTemplate = useTemplate,
                        creation = creation
                    )
                }
            }
            Button(
                onClick = {
                    nav.navigate(simulateTransactionDestination(data))
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Simulate Transaction")
            }
            Spacer(modifier = Modifier.height(20.dp))
        } else {
//            Form(
//                state = formState,
//                fields = listOf(
//                    Field(
//                        name = "Name",
//                        initValue = formState.getData()
//                            .getOrDefault("Name", if (data.name == "") "" else data.name),
//                        prompt = "Enter product name",
//                        label = "Product Name",
//                        validators = listOf(Required()),
//                        readOnly = true
//                    ),
//                    Field(
//                        name = "Description",
//                        initValue = formState.getData().getOrDefault(
//                            "Description",
//                            if (data.description == "") "" else data.description
//                        ),
//                        prompt = "Enter description",
//                        label = "Product Description",
//                        validators = listOf(Required()),
//                        readOnly = true
//                    ),
//                    Field(
//                        name = "Amount",
//                        initValue = formState.getData().getOrDefault(
//                            "Amount",
//                            if (data.amount == 0L) "0" else data.amount.toString()
//                        ),
//                        prompt = "Enter amount available",
//                        label = "Product Amount",
//                        validators = listOf(Required(), IsNumber()),
//                        inputType = KeyboardType.Number,
//                        formatter = NumberTransformation(),
//                        readOnly = true
//                    ),
//                    Field(
//                        name = "Price",
//                        initValue = formState.getData().getOrDefault(
//                            "Price",
//                            if (data.price == 0) "0.00" else (data.price / 100.0).toString()
//                        ),
//                        prompt = "Enter price",
//                        label = "Product Price",
//                        validators = listOf(Required(), IsNumber(), NonZero()),
//                        inputType = KeyboardType.Number,
//                        readOnly = true
//                    ),
//                )
//            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(20.dp))
                Form(
                    state = formState,
                    fields = listOf(
                        Field(
                            name = "Amount Editor",
                            initValue = "0",
                            prompt = "Enter amount available",
                            label = "Change Amount",
                            validators = listOf(Required(), IsNumber(), NonZero()),
                            inputType = KeyboardType.Number,
                            formatter = NumberTransformation()
                        ),
                        Field(
                                name = "Harvest Description",
                                initValue = "no description",
                                prompt = "Enter Harvest Description",
                                validators = listOf()
                        ),
                        Field(
                            name = "Unit",
                            initValue = formState.getData().getOrDefault(
                                "Unit",
                                if (data?.unit == "kg") "kg" else data!!.unit
                            ),
                            prompt = "Enter Unit",
                            label = "Unit",
                            validators = listOf(Required()),
                            inputType = KeyboardType.Text,
                            dropdownList = listOf("kg", "lbs")
                        )
                    )
                )

                @Composable
                fun numberButton(size: Int) {
                    val button = Button(
                        onClick = {
                            try {
                                formState.fields.first().setValue(
                                    ((formState.fields.first().getValue().toInt())+size).toString()
                                )
                            } catch (e : java.lang.NumberFormatException) {
                                AlertDialog.Builder(context)
                                    .setMessage("An empty value is not a number!").show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
                    ) {
                        Text(size.toString())
                    }
                    return button
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    //.background(Color.Gray),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    numberButton(1)
                    numberButton(10)
                    numberButton(100)
                    numberButton(1000)
                }
            }
            AddOrRemove(
                formState = formState,
                platformState = platformState,
                data = data,
                image = image,
                nav = nav,
                context = context,
                useTemplate = useTemplate
            )

//            Button(
//                onClick = {
//                    nav.navigate(simulateTransactionDestination(data))
//                          },
//                modifier = Modifier.align(Alignment.CenterHorizontally)
//
//            ) {
//                Text(text = "Simulate Transaction")
//            }
//            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun AddOrRemove(
    formState: FormState,
    platformState: PlatformState,
    data: ProductInformation,
    image: String,
    nav: DestinationsNavigator,
    context: Context,
    useTemplate: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

    Row(
        horizontalArrangement = Arrangement.Center,
    ) {
            Button(onClick = {
                if (formState.validate()) {
                    addProductNumber(data, formState.getData(), context, nav);
                    sync(context);
                    nav.navigate(MainContentDestination)
                }
            }, modifier = Modifier.size(Dp(100f), Dp(100f))) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Catalogue",
                    tint = Color.InstagramPurple
                )
            }
    //        Spacer(Modifier.weight(1f))
    //        Button(onClick = {
    //            if (formState.validate()) {
    //                removeProductNumber(data, formState.getData(), context, nav)
    //            }
    //        }, modifier = Modifier.size(Dp(100f), Dp(100f))) {
    //            Icon(
    //                imageVector = Icons.Filled.Remove,
    //                contentDescription = "Catalogue",
    //                tint = Color.InstagramPurple
    //            )
    //        }
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
    context: Context,
    useTemplate: Boolean,
    creation: Boolean
    ) {

    Row {
        Button(onClick = {
            if (formState.validate() && platformState.validate()) {
                val newData1: Map<String, String> =formState.getData() + platformState.getData()
                var actualAmount:Long=0
                if(newData1["platform1"].toBoolean()){
                    actualAmount+=newData1["platform1_amount"]!!.toLong()
                }
                if(newData1["platform2"].toBoolean()){
                    actualAmount+=newData1["platform2_amount"]!!.toLong()
                }
//                if((newData1["platform1"].toBoolean() or newData1["platform2"].toBoolean())==false){
//                    AlertDialog.Builder(context)
//                        .setMessage("Please select at least one platform").show()
//                }
//                else if(newData1["Amount"]!!.toLong()<actualAmount){
//                        AlertDialog.Builder(context)
//                            .setMessage("Please make sure the sum of platform number is smaller than product amount").show()
//                }
//                else{
//                    saveProduct(data, formState.getData() + platformState.getData(), image, context)
//                    nav.navigate(MainContentDestination)
//                }

                saveProduct(data, formState.getData(), image, context)
                sync(context);
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
            deleteProduct(data, context, nav)
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
    data.price = (newData["Price"]!!.toDouble() * 100).toInt()
    data.amount = newData["Amount"]!!.toLong()
    data.unit = newData["Unit"]!!
    data.image = newImage
    data.platform1 = newData["platform1"].toBoolean()
    data.platform2 = newData["platform2"].toBoolean()
    if(data.platform1){
        data.platform1_amount=newData["platform1_amount"]!!.toLong()
        data.platform1_price=newData["platform1_price"]!!.toInt()
    }
    if(data.platform2){
        data.platform2_amount=newData["platform2_amount"]!!.toLong()
        data.platform2_price=newData["platform2_price"]!!.toInt()
    }
    data.exportData("${context.filesDir}/out2")
}

private fun saveProductToDB(
    creation: Boolean,
    data: ProductInformation,
    newData: Map<String, String>,
    newImage: String,
){
    data.name = newData["Name"]!!
    data.description = newData["Description"]!!
    data.price = (newData["Price"]!!.toDouble() * 100).toInt()
    data.amount = newData["Amount"]!!.toLong()
    data.unit = newData["Unit"]!!
    data.image = newImage
    data.platform1 = newData["platform1"].toBoolean()
    data.platform2 = newData["platform2"].toBoolean()
    if(data.platform1){
        data.platform1_amount=newData["platform1_amount"]!!.toLong()
        data.platform1_price=newData["platform1_price"]!!.toInt()
    }
    if(data.platform2){
        data.platform2_amount=newData["platform2_amount"]!!.toLong()
        data.platform2_price=newData["platform2_price"]!!.toInt()
    }
}

private fun deleteProduct(data: ProductInformation, context: Context, nav: DestinationsNavigator) {
    AlertDialog.Builder(context)
        .setTitle("Delete Product")
        .setMessage("Are you sure you want to delete this product? This operation is irreversible.")
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setPositiveButton(
            android.R.string.yes
        ) { _, _ ->
            data.deleteData("${context.filesDir}/out2")
            sync(context);
            nav.navigate(MainContentDestination)
        }
        .setNegativeButton(android.R.string.no, null).show()
}

private fun addProductNumber(
    data: ProductInformation,
    newData: Map<String, String>,
    context: Context,
    nav: DestinationsNavigator
) {
    val harvestRequest = HarvestInformation(
        fromWorker = Singleton.userId,
        product = data,
        amount = newData["Amount Editor"]!!.toInt(),
        harvestDescription = newData["Harvest Description"]!!,
        unit = newData["Unit"]!!
    )
    harvestRequest.exportData("${context.filesDir}/outharvest")
    nav.navigate(MainContentDestination)
}

private fun removeProductNumber(
    data: ProductInformation,
    newData: Map<String, String>,
    context: Context,
    nav: DestinationsNavigator
) {
    AlertDialog.Builder(context)
        .setTitle("Edit Product Number")
        .setMessage("Are you sure to edit this product number? This will send a request to your manager.")
        //.setIcon(android.R.drawable.ic_dialog_alert)
        .setPositiveButton(
            android.R.string.yes
        ) { _, _ ->
            val harvestRequest = HarvestInformation(
                fromWorker = Singleton.userId,
                product = data,
                amount = -newData["Amount Editor"]!!.toInt(),
                harvestDescription = newData["Harvest Description"]!!,
                unit = newData["Unit"]!!
            )
            AlertDialog.Builder(context)
                .setMessage("Request has been sent").show()
            harvestRequest.exportData("${context.filesDir}/outharvest")
            sync(context);
            nav.navigate(MainContentDestination)
        }
        .setNegativeButton(android.R.string.no, null).show()
}