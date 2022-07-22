package ca.uwaterloo.cs.pushpull

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
            content = { toSynchUI() },
            bottomBar = { NavigationBar(navigator) }
        )
}

@Composable
fun toSynchUI(){
    val context = LocalContext.current
    val pushPullFarmer = PushPullFarmer(context)
    val pushWorker = PushWorker(context)
    val pullWorker = PullWorker(context)
    val synchInProgress = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!synchInProgress.value and !Singleton.syncInProgress) {
        Button(
            onClick = {
                Singleton.syncInProgress = true
                synchInProgress.value = true
                Thread {
                    if (Singleton.isFarmer) {
                        pushPullFarmer.harvestResolver()
                        pushPullFarmer.productResolver()
                    } else {
                        pullWorker.run()
                        pushWorker.run()
                    }
                    Thread.sleep(5000)
                    Singleton.syncInProgress = false
                    synchInProgress.value = false
                }.start()
            },
        ){
            Text(text = "Sync")
        }
        }
        else{
            progressBar()
        }
    }
}

@Composable
fun progressBar(){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(100.dp),
            color = Color.Green,
            strokeWidth = 10.dp
        )
    }
}