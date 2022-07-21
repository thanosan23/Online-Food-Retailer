package ca.uwaterloo.cs.simulateTransaction

import androidx.annotation.IntegerRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import ca.uwaterloo.cs.destinations.ProductFormDestination
import ca.uwaterloo.cs.form.FormState
import ca.uwaterloo.cs.platform.PlatformState
import ca.uwaterloo.cs.product.ProductInformation
import ca.uwaterloo.cs.ui.theme.OnlineFoodRetailTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.lang.NumberFormatException

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
    var reductionAmountIsError by remember { mutableStateOf(false) }


    OnlineFoodRetailTheme {


        //field to specify reduction amount
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(MaterialTheme.colors.background)
                .padding(20.dp)
        ) {
            Text(
                text = "Simulate Transaction",
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.height(20.dp))

            //select which platform by which to simulate transaction
            platformState.platformsUI.PlatformsDropDown()
            Spacer(modifier = Modifier.height(20.dp))

            TextField(
                value = reductionAmount,
                onValueChange = { reductionAmount = it },
                label = { Text("Reduction Amount") },
                isError = reductionAmountIsError,
                modifier = androidx.compose.ui.Modifier.align(Alignment.CenterHorizontally),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )

            Button(
                onClick = {
                    reductionAmountIsError = updateProductAfterTransactionIsError(data, reductionAmount)
                    if (!reductionAmountIsError) {
                        nav.navigate(ProductFormDestination(data))
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Confirm Simulate Transaction")
            }
        }

    }
}

private fun updateProductAfterTransactionIsError(data: ProductInformation, reductionAmountInput: String): Boolean
{
    var reductionAmount = 0
    try {
        reductionAmount = reductionAmountInput.toInt()
    }
    catch (e: NumberFormatException) {
        return true
    }
    if (data.platform1) {
        data.platform1_amount -= reductionAmount
    }
    if (data.platform2) {
        data.platform2_amount -= reductionAmount
    }
    data.amount -= reductionAmount
    return false
}