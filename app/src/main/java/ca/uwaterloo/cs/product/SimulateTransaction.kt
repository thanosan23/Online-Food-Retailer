package ca.uwaterloo.cs.product

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import ca.uwaterloo.cs.destinations.MainContentDestination
import ca.uwaterloo.cs.form.FormState
import ca.uwaterloo.cs.platform.PlatformState
import ca.uwaterloo.cs.ui.theme.OnlineFoodRetailTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.lang.reflect.Modifier

@Destination
@Composable
fun simulateTransaction(
    //travel to this screen by clicking button
    //function inputs: (product id, selected platforms, current product amount)
    nav: DestinationsNavigator,
    data: ProductInformation
) {
    val formState by remember { mutableStateOf(FormState()) }
    val platformState by remember { mutableStateOf(PlatformState(data)) }
    var reductionAmount by remember { mutableStateOf("") }


    OnlineFoodRetailTheme {
        //select which platform by which to simulate transaction
        platformState.platformsUI.PlatformsDropDown()


        //field to specify reduction amount
        Column(/*Modifier.padding(20.dp)*/) {
            TextField(
                value = reductionAmount,
                onValueChange = { reductionAmount = it },
                label = { Text("Reduction Amount") },
                modifier = androidx.compose.ui.Modifier.align(Alignment.CenterHorizontally),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
        }
        Button(onClick = {
            nav.navigate(MainContentDestination)
        }) {
//            eventually this will call back end to reduce product amount by reduction amount
//            for now, just edit mock data by reducing product amount by reduction amount,
//            then return to product form

            Text(text = "Confirm Simulate Transaction")
        }
    }
}