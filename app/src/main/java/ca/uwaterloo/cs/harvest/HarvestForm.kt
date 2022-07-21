package ca.uwaterloo.cs.harvest

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ca.uwaterloo.cs.Singleton
import ca.uwaterloo.cs.destinations.MainContentDestination
import ca.uwaterloo.cs.form.*
import ca.uwaterloo.cs.product.ProductInformation
import ca.uwaterloo.cs.ui.theme.InstagramPurple
import ca.uwaterloo.cs.ui.theme.OnlineFoodRetailTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun HarvestForm(
    nav: DestinationsNavigator,
    data: ProductInformation?
) {
    val formState by remember { mutableStateOf(FormState()) }
    var signToggle by remember { mutableStateOf(true) }
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
                    val amountField = Field(
                        name = "Amount",
                        initValue = formState.getData()
                            .getOrDefault(
                                "Amount",
                                "0"
                            ),
                        prompt = "Enter amount harvested",
                        label = "Harvest Amount",
                        validators = listOf(Required(), IsNumber()),
                        inputType = KeyboardType.Number,
                        formatter = NumberTransformation()
                    )

                    @Composable
                    fun numberButton(size: Int) {
                        val button = Button(
                            onClick = {
                                if (signToggle) {
                                    amountField.setValue(
                                        (amountField.getValue().toInt() + size).toString()
                                    )
                                } else {
                                    amountField.setValue(
                                        (amountField.getValue().toInt() - size).toString()
                                    )
                                }
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = if (signToggle) Color.Green else Color.Red)
                        ) {
                            Text(size.toString())
                        }
                        return button
                    }

                    Column(
                        Modifier
                            .verticalScroll(rememberScrollState())
                            .fillMaxWidth()
                            .padding(20.dp),
                    ) {
                        Form(
                            state = formState,
                            fields = listOf(
                                Field(
                                    name = "Name",
                                    initValue = formState.getData()
                                        .getOrDefault(
                                            "Name",
                                            data?.name ?: ""
                                        ),
                                    prompt = "Enter product name",
                                    label = "Product Name",
                                    validators = listOf(Required()),
                                    readOnly = data != null
                                ),
                                Field(
                                    name = "Description",
                                    initValue = formState.getData().getOrDefault(
                                        "Description",
                                        data?.description ?: ""
                                    ),
                                    prompt = "Enter description",
                                    label = "Product Description",
                                    validators = listOf(Required()),
                                    readOnly = data != null
                                ),
                                amountField
                            )
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Cyan),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Button(
                                onClick = {
                                    signToggle = !signToggle
                                },
                                colors = ButtonDefaults.buttonColors(backgroundColor = if (signToggle) Color.Green else Color.Red)
                            ) {
                                Icon(
                                    imageVector = if (signToggle) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                                    contentDescription = if (signToggle) "Increase stock" else "Decrease stock"
                                )
                            }
                            numberButton(1)
                            numberButton(10)
                            numberButton(100)
                        }
                        SendCancelDeleteWidgets(
                            formState = formState,
                            data = data,
                            nav = nav,
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun SendCancelDeleteWidgets(
    formState: FormState,
    data: ProductInformation?,
    nav: DestinationsNavigator,
) {
    val context = LocalContext.current
    val saveDir = "${context.filesDir}/outharvest"
    Row {
        Button(onClick = {
            if (formState.validate()) {
                if (data != null) {
                    saveHarvestRequestWithProduct(data, formState.getData(), saveDir)
                } else {
                    saveHarvestRequestNoProduct(formState.getData(), "", saveDir)
                }
                nav.navigate(MainContentDestination)
            }
        }) {
            Icon(
                imageVector = Icons.Filled.Save,
                contentDescription = "Save Changes",
                tint = Color.InstagramPurple
            )
        }
        Button(onClick = {
            deleteHarvestRequest(context, nav)
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


private fun saveHarvestRequestWithProduct(
    data: ProductInformation,
    newData: Map<String, String>,
    saveDir: String
) {
    val harvestInformation =
        HarvestInformation(
            fromWorker = "Test Worker",
            product = data,
            amount = newData["Amount"]!!.toInt()
        )
    harvestInformation.exportData(saveDir)
}

private fun saveHarvestRequestNoProduct(
    newData: Map<String, String>,
    image: String,
    saveDir: String
) {
    val harvestInformation =
        HarvestInformation(
            fromWorker = "Test Worker",
            name = newData["Name"]!!,
            description = newData["Description"]!!,
            image = image,
            amount = newData["Amount"]!!.toInt()
        )
    harvestInformation.exportData(saveDir)
}

private fun deleteHarvestRequest(context: Context, nav: DestinationsNavigator) {
    android.app.AlertDialog.Builder(context)
        .setTitle("Cancel changes")
        .setMessage("Are you sure you want to cancel this operation? Any data entered will be lost.")
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setPositiveButton(
            android.R.string.yes
        ) { _, _ ->
            nav.navigate(MainContentDestination)
        }
        .setNegativeButton(android.R.string.no, null).show()
}
