package ca.uwaterloo.cs

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.uwaterloo.cs.destinations.LoginDestination
import ca.uwaterloo.cs.destinations.MainContentDestination
import ca.uwaterloo.cs.destinations.SignupDestination
import ca.uwaterloo.cs.form.FormState
import ca.uwaterloo.cs.ui.theme.OnlineFoodRetailTheme
import ca.uwaterloo.cs.ui.theme.InstagramPurple
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination(start = true)
@Composable
fun LaunchScreen(
    navigator: DestinationsNavigator
) {
    val formState by remember { mutableStateOf(FormState()) }
    OnlineFoodRetailTheme {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(color = MaterialTheme.colors.background)
                .wrapContentSize(align = Alignment.Center)
                .padding(20.dp)
        ) {
            Text(
                text = "Online Food Retailer",
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.height(21.dp))
            Button(
                onClick = {navigator.navigate(SignupDestination)},
                modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text(text = "Sign Up")
            }
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = {navigator.navigate(LoginDestination)},
                modifier = Modifier.align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors()
            ) {
                Text(text = "Login")
            }
        }
    }



}