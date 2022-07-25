package ca.uwaterloo.cs.pushpull

import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getSystemService
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

fun checkConnection(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (connectivityManager != null) {
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true
            }
        }
    }
    return false
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
                val connected = checkConnection(context)
                if (connected) {
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
                } else {
                    val builder = android.app.AlertDialog.Builder(context)
                    builder.setMessage("No network connection")
                        .setCancelable(true)
                        .setPositiveButton("OK", DialogInterface.OnClickListener {
                            dialog, id -> dialog.cancel()
                        })
                    val alert = builder.create()
                    alert.setTitle("Warning")
                    alert.show()
                }
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