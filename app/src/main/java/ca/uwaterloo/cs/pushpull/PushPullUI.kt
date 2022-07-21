package ca.uwaterloo.cs.pushpull

import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import ca.uwaterloo.cs.NavigationBar
import ca.uwaterloo.cs.Singleton
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
    val pushPullFarmer = PushPullFarmer(context)
    val pushWorker = PushWorker(context)
    val pullWorker = PullWorker(context)
    Button(
        onClick = {
            Thread {
                if (Singleton.isFarmer) {
                    pushPullFarmer.harvestResolver()
                    pushPullFarmer.productResolver()
                    Thread.sleep(5000)
                } else {
                    pullWorker.run()
                    Thread.sleep(10000)
                    pushWorker.run()
                }
            }.start()
        },
    ) {
        Text(text = "Update")
    }
}