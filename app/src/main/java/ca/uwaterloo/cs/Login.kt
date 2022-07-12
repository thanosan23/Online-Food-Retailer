package ca.uwaterloo.cs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.uwaterloo.cs.destinations.MainContentDestination
import ca.uwaterloo.cs.form.Field
import ca.uwaterloo.cs.form.Form
import ca.uwaterloo.cs.form.FormState
import ca.uwaterloo.cs.form.Required
import ca.uwaterloo.cs.ui.theme.OnlineFoodRetailTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination()
@Composable
fun Login(
    navigator: DestinationsNavigator
) {
    val formState by remember { mutableStateOf(FormState()) }
    OnlineFoodRetailTheme {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(MaterialTheme.colors.background)
                .wrapContentSize(align = Alignment.Center)
                .padding(20.dp)
        ) {
            //Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = "Enter your credentials",
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.height(21.dp))
            Form(
                state = formState,
                fields = listOf(
                    Field(
                        name = "Username",
                        initValue = "",
                        prompt = "Enter username",
                        label = "Username",
                        validators = listOf(Required())
                    ),
                    Field(
                        name = "Password",
                        initValue = "",
                        prompt = "Enter password",
                        label = "Password",
                        validators = listOf(Required()),
                        formatter = PasswordVisualTransformation()
                    )
                )
            )
        }
    }
}