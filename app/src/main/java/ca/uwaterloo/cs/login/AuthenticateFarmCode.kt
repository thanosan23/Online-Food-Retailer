package ca.uwaterloo.cs.login

import android.content.Context
import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import ca.uwaterloo.cs.Listener
import ca.uwaterloo.cs.db.DBManager
import ca.uwaterloo.cs.destinations.SignupAsWorkerDestination
import ca.uwaterloo.cs.ui.theme.OnlineFoodRetailTheme
import ca.uwaterloo.cs.ui.theme.Shapes
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun authenticateFarmerCode(
    navigator: DestinationsNavigator
){
    val dbManager = DBManager(null)
    OnlineFoodRetailTheme {
        val rawId = LocalContext.current.resources.getIdentifier("wheat", "raw", LocalContext.current.packageName)
        val video = "android.resource://$LocalContext.current.packageName/$rawId"
        val videoUri = Uri.parse(video)
        val context = LocalContext.current
        val exoPlayer = remember { context.buildExoPlayer(videoUri) }

        DisposableEffect(
            AndroidView(
                factory = { it.buildPlayerView(exoPlayer) },
                modifier = Modifier.fillMaxSize()
            )
        ) {
            onDispose {
                exoPlayer.release()
            }
        }
        ProvideWindowInsets {
            Column(
                modifier = Modifier
                    .fillMaxSize()
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
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    shape = Shapes.small,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    )
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

                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 8.dp)

                ) {
                    Text(text = "Sign up")
                }
            }
        }
    }
}

private fun Context.buildExoPlayer(uri: Uri) =
    ExoPlayer.Builder(this).build().apply {
        setMediaItem(MediaItem.fromUri(uri))
        repeatMode = Player.REPEAT_MODE_ALL
        playWhenReady = true
        prepare()
    }

private fun Context.buildPlayerView(exoPlayer: ExoPlayer) =
    StyledPlayerView(this).apply {
        player = exoPlayer
        layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        useController = false
        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
    }