package ca.uwaterloo.cs.pushpull

import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ca.uwaterloo.cs.NavigationBar
import ca.uwaterloo.cs.R
import ca.uwaterloo.cs.Singleton
import ca.uwaterloo.cs.db.DBManager
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

fun sync(context: Context) {
    val pushPullFarmer = PushPullFarmer(context)
    val pushWorker = PushWorker(context)
    val pullWorker = PullWorker(context)
    if (!Singleton.syncInProgress) {
        val connected = checkConnection(context)
        if (connected) {
            Singleton.syncInProgress = true
            // syncInProgress.value = true
            Thread {
                if (Singleton.isFarmer) {
                    pushPullFarmer.harvestResolver();
                    pushPullFarmer.resolver();
                } else {
                    pullWorker.run()
                    pushWorker.run()
                }
                Thread.sleep(5000)
                Singleton.syncInProgress = false
                // syncInProgress.value = false
            }.start()
        } else {
            val builder = android.app.AlertDialog.Builder(context)
            builder.setMessage("No network connection")
                .setCancelable(true)
                .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                })
            val alert = builder.create()
            alert.setTitle("Warning")
            alert.show()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun toSynchUI(){
    val context = LocalContext.current
    val pushPullFarmer = PushPullFarmer(context)
    val pushWorker = PushWorker(context)
    val pullWorker = PullWorker(context)
    val syncInProgress = remember { mutableStateOf(false) }

    var platform1CheckBoxState = remember {
        mutableStateOf(false)
    }
    var platform2CheckBoxState = remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!syncInProgress.value and !Singleton.syncInProgress) {
            Button(
                onClick = {
                    val connected = checkConnection(context)
                    if (connected) {
                        Singleton.syncInProgress = true
                        syncInProgress.value = true
                        Thread {
                            if (Singleton.isFarmer) {
                                pushPullFarmer.harvestResolver();
//                                pushPullFarmer.productResolver();
//                                pushPullFarmer.storeResolver();
                                pushPullFarmer.resolver();
                            } else {
                                pullWorker.run()
                                pushWorker.run()
                            }
                            Thread.sleep(5000)
                            Singleton.syncInProgress = false
                            syncInProgress.value = false
                        }.start()
                    } else {
                        val builder = android.app.AlertDialog.Builder(context)
                        builder.setMessage("No network connection")
                            .setCancelable(true)
                            .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id ->
                                dialog.cancel()
                            })
                        val alert = builder.create()
                        alert.setTitle("Warning")
                        alert.show()
                    }
                },
            ) {
                Text(text = "Sync")
            }
            if (Singleton.isFarmer) {
//                Button(onClick = {
//                    val connected = checkConnection(context)
//                    if (connected) {
//                        Singleton.syncInProgress = true
//                        syncInProgress.value = true
//                        Thread {
//                            if (Singleton.isFarmer) {
//                                val dbManager = DBManager(context)
//                                if (platform1CheckBoxState.value) {
//                                    dbManager.syncDFC(Singleton.userId, true)
//                                }
//                                if (platform2CheckBoxState.value) {
//                                    dbManager.syncDFC(Singleton.userId, false)
//                                }
//                                Thread.sleep(5000)
//                                Singleton.syncInProgress = false
//                                syncInProgress.value = false
//                            }
//                        }.start()
//                    } else {
//                        val builder = android.app.AlertDialog.Builder(context)
//                        builder.setMessage("No network connection")
//                            .setCancelable(true)
//                            .setPositiveButton(
//                                "OK",
//                                DialogInterface.OnClickListener { dialog, id ->
//                                    dialog.cancel()
//                                })
//                        val alert = builder.create()
//                        alert.setTitle("Warning")
//                        alert.show()
//                    }
//                }) {
//                    Text(text = "DFC Sync")
//                }
                Row() {
                    Column() {
//                        Image(
//                            painter = painterResource(id = R.drawable.socleo),
//                            contentDescription = null,
//                            modifier = Modifier
//                                .width(120.dp)
//                                .height(100.dp)
//                                .border(BorderStroke(1.dp, Color.Black))
//                        )
//                        androidx.compose.material3.Checkbox(
//                            checked = platform1CheckBoxState.value,
//                            onCheckedChange = { platform1CheckBoxState.value = it }
//                        )
                    }
                    Column() {
//                        Image(
//                            painter = painterResource(id = R.drawable.loblaws),
//                            contentDescription = null,
//                            modifier = Modifier
//                                .width(120.dp)
//                                .height(100.dp)
//                                .border(BorderStroke(1.dp, Color.Black))
//                        )
//                        androidx.compose.material3.Checkbox(
//                            checked = platform2CheckBoxState.value,
//                            onCheckedChange = { platform2CheckBoxState.value = it }
//                        )
                    }
                }
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