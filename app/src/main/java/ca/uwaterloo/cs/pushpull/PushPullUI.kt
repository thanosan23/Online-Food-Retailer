package ca.uwaterloo.cs.pushpull

import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import ca.uwaterloo.cs.NavigationBar
import ca.uwaterloo.cs.Singleton
import ca.uwaterloo.cs.destinations.SignupAsManagerDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun PushPullUI(
    navigator: DestinationsNavigator
){
    Scaffold(
        content = {UI(navigator)},
        bottomBar = { NavigationBar(navigator)}
    )
}

@Composable
fun UI(navigator: DestinationsNavigator){
    val context = LocalContext.current
    val pushFarmer = PushFarmer(context)
    val pullFarmer = PullFarmer(context)
    val pushWorker = PushWorker(context)
    val pullWorker = PullWorker(context)
    Button(
        onClick = {
            if (Singleton.isFarmer){
                pushFarmer.run()
                Thread.sleep(3000)
                pullFarmer.run()
            }
            else{
                pullWorker.run()
                Thread.sleep(3000)
                pushWorker.run()
            }
        },
    ) {
        Text(text = "Update")
    }
}