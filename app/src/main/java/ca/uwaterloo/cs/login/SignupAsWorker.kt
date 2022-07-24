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
import androidx.compose.ui.graphics.Color
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
import ca.uwaterloo.cs.Singleton
import ca.uwaterloo.cs.bemodels.SignUpWorker
import ca.uwaterloo.cs.db.DBManager
import ca.uwaterloo.cs.destinations.MainContentDestination
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
fun SignupAsWorker(
    navigator: DestinationsNavigator,
    farmerUserId: String,
) {
    println("did the navigation do any damage? $farmerUserId")
    OnlineFoodRetailTheme {
        val focusManager = LocalFocusManager.current
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
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.background)
                    //.wrapContentSize(align = Alignment.Center)
                    .padding(20.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            focusManager.clearFocus()
                        })
                    }
            )  {
                Text(
                    text = "Sign up as a worker",
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                )
                Spacer(modifier = Modifier.height(21.dp))
                var username by remember {mutableStateOf("")}
                var firstName by remember { mutableStateOf("")}
                var familyName by remember { mutableStateOf("")}

                var usernameErrorFound by remember {mutableStateOf(false)}

                TextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    isError = usernameErrorFound,
                    singleLine = true,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    shape = Shapes.small,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                TextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
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
                TextField(
                    value = familyName,
                    onValueChange = { familyName = it },
                    label = { Text("Family Name") },
                    singleLine = true,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
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
                        val signUpWorker = SignUpWorker(
                            Singleton.userId,
                            firstName,
                            familyName,
                            farmerUserId
                        )
                        DBManager(null).storeSignUpWorker(signUpWorker)
                        navigator.navigate(MainContentDestination)
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(text = "Sign up")
                }
            }
        }
    }
}

fun verifySignupAsWorker(username: String, farmID: String): Boolean {
    return true
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