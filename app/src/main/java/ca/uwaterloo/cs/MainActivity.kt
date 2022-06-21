package ca.uwaterloo.cs

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.uwaterloo.cs.ui.theme.OnlineFoodRetailTheme
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import ca.uwaterloo.cs.ui.theme.InstagramOrange
import ca.uwaterloo.cs.ui.theme.InstagramPurple
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import java.io.File
import kotlin.concurrent.thread

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      OnlineFoodRetailTheme {
        MainContent()
      }
    }
  }
}

@Composable
fun StoryAvatar(imageUrl:String){
  Box(
    modifier = Modifier
      .padding(end = 8.dp)
      .border(
        width = 2.dp,
        brush = Brush.verticalGradient(listOf(Color.InstagramOrange, Color.InstagramPurple)),
        shape = CircleShape
      )
      .padding(6.dp)
      .size(60.dp)
      .clip(CircleShape)
      .background(Color.LightGray)
  ){
    AsyncImage(model = imageUrl,
      contentDescription = null,
      contentScale = ContentScale.Crop)
  }
}

@Composable
fun MainContent() {
  Box(
    Modifier
      .background(MaterialTheme.colors.background),
  ) {
   val clickedDoggo = remember {mutableStateOf<String?>(null)}
    TableScreen()
  }
}

@Composable
fun InstagramPart(){
  Column {
    Text (
      modifier = Modifier.padding(16.dp),
      text = "Instagram",
      fontSize = 20.sp,
      fontWeight = FontWeight.Medium
    )
    Row (
      Modifier
        .horizontalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 16.dp)
    ){
      val doggos = remember {mutableStateOf(emptyList<String>())}
      LaunchedEffect(Unit){
        thread {
          val presenter = InstagramHomePresenter()
          doggos.value = presenter.fetchDogImages()
        }
      }

      for (doggo in doggos.value){
        StoryAvatar(
          imageUrl = doggo
        )
      }
    }
  }
}

@Composable
fun RowScope.TableCell(
  text: String,
  weight: FontWeight
) {
  Text(
    text = text,
    Modifier
      .border(1.dp, Color.Black)
      .padding(8.dp),
    fontFamily = FontFamily.SansSerif,
    fontWeight = weight
    )
}

@Composable
fun TableScreen() {
  // Just a fake data... a Pair of Int and String
  val tableData = (1..25).mapIndexed { index, item ->
    index to "Item $index"
  }
  // Each cell of a column must have the same weight.
  val column1Weight = .3f // 30%
  val column2Weight = .7f // 70%
  // The LazyColumn will be our table. Notice the use of the weights below
  LazyColumn(
    Modifier
      .padding(50.dp)
      .background(Color.White),
  horizontalAlignment = Alignment.CenterHorizontally) {
    // Here is the header
    item {
      Row(Modifier.background(Color.Gray)) {
        TableCell(text = "Column 1", weight = FontWeight.Light)
        TableCell(text = "Column 2", weight = FontWeight.Light)
      }
    }
    // Here are all the lines of your table.
    items(tableData) {
      val (id, text) = it

      Divider()
      Row(
        Modifier.padding(20.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Image(
          painter = painterResource(id = R.drawable.ic_pumpkin),
          contentDescription = null,
          modifier = Modifier.width(100.dp).height(100.dp)
        )
        Spacer(Modifier.width(30.dp))
        Text("Alfred Sisley",
        )
      }
    }
    }
  }

