package ca.uwaterloo.cs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ca.uwaterloo.cs.form.*
import ca.uwaterloo.cs.ui.theme.OnlineFoodRetailTheme

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
        }
    }

    @Composable
    fun FormImages(images: ArrayList<String>) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            for (img in images) {
                Box(
                    Modifier
                        .background(Color.Cyan)
                        .aspectRatio(1f)
                )
                {
                    Text(img)
                }
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
}
