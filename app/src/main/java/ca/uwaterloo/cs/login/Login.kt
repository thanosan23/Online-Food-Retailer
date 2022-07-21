package ca.uwaterloo.cs.login

import android.content.Context
import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import ca.uwaterloo.cs.destinations.MainContentDestination
import ca.uwaterloo.cs.destinations.SignupAsManagerDestination
import ca.uwaterloo.cs.destinations.SignupAsWorkerDestination
import ca.uwaterloo.cs.destinations.authenticateFarmerCodeDestination
import ca.uwaterloo.cs.ui.theme.OnlineFoodRetailTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
import com.google.android.exoplayer2.ui.StyledPlayerView
import java.io.File

@Destination
@Composable
fun Login(
    navigator: DestinationsNavigator
) {
    OnlineFoodRetailTheme {
        var video_url = "https://cdn.videvo.net/videvo_files/video/free/2020-05/large_watermarked/3d_ocean_1590675653_preview.mp4"
        val mediaItem = MediaItem.fromUri(video_url)
        /*val rawId = resources.getIdentifier("clouds", "raw", packageName)
        val video = "android.resource://$packageName/$rawId"
        val videoUri = Uri.parse(rawId)*/
        //val focusManager = LocalFocusManager.current
        val context = LocalContext.current
        val exoPlayer = remember { context.buildExoPlayer(mediaItem) }

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
                    //.fillMaxWidth()
                    //.background(MaterialTheme.colors.background)
                    //.wrapContentSize(align = Alignment.Center)
                    .padding(20.dp)
                    /*.pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            focusManager.clearFocus()
                        })
                    }*/
            ) {
                //Spacer(modifier = Modifier.height(100.dp))
                Text(
                    text = "Online Food Retailer",
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                )
                Spacer(modifier = Modifier.height(21.dp))

                Button(
                    onClick = {
                        navigator.navigate(SignupAsManagerDestination)
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(text = "Sign up as Manager")
                }
                Button(
                    onClick = {
                        navigator.navigate(authenticateFarmerCodeDestination)
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(text = "Sign up as Worker")
                }
            }
        }
    }
}

private fun Context.buildExoPlayer(mediaItem: MediaItem) =
    ExoPlayer.Builder(this).build().apply {
        setMediaItem(mediaItem)
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
        resizeMode = RESIZE_MODE_ZOOM
    }