package ca.uwaterloo.cs.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.uwaterloo.cs.Listener
import ca.uwaterloo.cs.bemodels.SignUpFarmer
import ca.uwaterloo.cs.db.DBManager
import ca.uwaterloo.cs.destinations.LoginDestination
import ca.uwaterloo.cs.destinations.SignupAsWorkerDestination
import ca.uwaterloo.cs.form.FormState
import ca.uwaterloo.cs.ui.theme.OnlineFoodRetailTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun authenticateFarmerCode(
    navigator: DestinationsNavigator
){
    val dbManager = DBManager(null)
    OnlineFoodRetailTheme {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(MaterialTheme.colors.background)
                .wrapContentSize(align = Alignment.Center)
                .padding(20.dp)
        )  {
            Text(
                text = "enter your farm code",
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.height(21.dp))

            var farmCode by remember { mutableStateOf("") }
            var isError by remember { mutableStateOf(false)}
            var label by remember { mutableStateOf("farm code")}

            TextField(
                value = farmCode,
                onValueChange = { farmCode = it },
                label = { Text(label) },
                isError = isError,
                singleLine = true,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(2.dp))

            Button(
                onClick = {
                    class ListenerImpl() : Listener<String?>() {
                        override fun activate(input: String?) {
                            if (input == null){
                                isError = true
                                label = "this is invalid"
                            }
                            else{
                                println("this is the true $input")
                                navigator.navigate(SignupAsWorkerDestination(input))
                            }
                        }
                    }
                    val authListener = ListenerImpl()
                    dbManager.authenticateFarmCode(farmCode, authListener)
                },

                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Sign up")
            }
        }
    }
}
