package ca.uwaterloo.cs.platform

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ca.uwaterloo.cs.ProductInformation
import ca.uwaterloo.cs.R

class PlatformsUI(data: ProductInformation) {
    var platform1CheckBoxState: Boolean by mutableStateOf(data.platform1)
    var platform2CheckBoxState: Boolean by mutableStateOf(data.platform2)

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PlatformsDropDown(){
        Column(
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.width(10.dp))
                Image(
                    painter = painterResource(id = R.drawable.socleo),
                    contentDescription = null,
                    modifier = Modifier
                        .width(120.dp)
                        .height(100.dp)
                        .border(BorderStroke(1.dp, Color.Black))
                )
                Checkbox(
                    checked = platform1CheckBoxState,
                    onCheckedChange = { platform1CheckBoxState = it }
                )
            }

            Spacer(Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.width(5.dp))

                Image(
                    painter = painterResource(id = R.drawable.loblaws),
                    contentDescription = null,
                    modifier = Modifier
                        .width(130.dp)
                        .height(120.dp)
                        .border(BorderStroke(5.dp, Color.White))
                )
                Checkbox(
                    checked = platform2CheckBoxState,
                    onCheckedChange = {platform2CheckBoxState = it}
                )
            }
        }


    }
}