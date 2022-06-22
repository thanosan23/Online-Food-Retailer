package ca.uwaterloo.cs

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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


    @Composable
    fun MainContent() {
        Column(
            Modifier
                .background(MaterialTheme.colors.background)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TableScreen()
        }
    }

    @Composable
    fun TableScreen() {
        // Just a fake data... a Pair of Int and String
        val tableData = mockData()
        // Each cell of a column must have the same weight.
        val column1Weight = .3f // 30%
        val column2Weight = .7f // 70%
        // The LazyColumn will be our table. Notice the use of the weights below
        Text("CATALOGUE")
        LazyColumn(
            Modifier
                .padding(20.dp)
                .background(Color.White)
                .border(BorderStroke(3.dp, Color.InstagramPurple))
                .heightIn(0.dp, 640.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Here are all the lines of your table.
            items(tableData, key = { it }) {
                Divider(
                    Modifier
                        .border(BorderStroke(20.dp, Color.InstagramPurple))
                )
                Row(
                    Modifier
                        .padding(0.dp)
                        .height(IntrinsicSize.Min)
                        .clickable { editItem(it.second) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_pumpkin),
                        contentDescription = null,
                        modifier = Modifier
                            .width(100.dp)
                            .height(100.dp)
                    )
                    Spacer(Modifier.width(30.dp))
                    Divider(
                        Modifier
                            .fillMaxHeight()
                            .width(3.dp)
                            .border(BorderStroke(5.dp, Color.InstagramPurple))
                    )
                    Spacer(Modifier.width(30.dp))
                    Text(
                        it.second.name
                    )
                }
            }
        }
        Row {
            Button(onClick = {
                addItem()
            }
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = null
                )
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

    private fun mockData(amount: Int = 7): List<Pair<Int, ProductInformation>> {
        return (1..amount).mapIndexed { index, _ ->
            index to ProductInformation(
                index,
                "apple ${index + 1}",
                "apple ${index + 1} description",
                100 * index + 1,
                10 * index + 1L,
                arrayListOf("img ${index + 1}", "img ${index + 1}", "img ${index + 1}")
            )
        }
    }

    private fun readData(): List<Pair<Int, ProductInformation>> {
        // TODO: platform compatibility
        // TODO: load from platform
        val context = this.baseContext
        println(context.filesDir)
        println("${context.filesDir}/out.txt")
        val dir = File("${context.filesDir}/out")
        if (!dir.exists()) {
            return emptyList()
        }
        val list = ArrayList<Pair<Int, ProductInformation>>()
        for (saveFile in dir.list()) {
            val fileIS = FileInputStream(saveFile)
            val inStream = ObjectInputStream(fileIS)
            val productInformation = inStream.readObject() as ProductInformation
            list.add(Pair(productInformation.id, productInformation))
            inStream.close()
            fileIS.close()
        }
        return list
    }
}
