package ca.uwaterloo.cs.Profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.uwaterloo.cs.R
import ca.uwaterloo.cs.destinations.ProfileContentDestination
import ca.uwaterloo.cs.ui.theme.Divider
import ca.uwaterloo.cs.ui.theme.OnlineFoodRetailTheme
import ca.uwaterloo.cs.ui.theme.TextPrimary
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination()
@Composable
fun AccountSettingListScreen(nav: DestinationsNavigator) {
    Box(
        Modifier
            .background(Color.White)
            .fillMaxSize()
    ) {
        Column(
            Modifier
                .background(Color.White)
                .fillMaxWidth()
        ) {
            TopBar(nav)
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )
            ProfileListItem(R.drawable.settings, "UserName", nav)
            Divider(startIndent = 56.dp, color = Color.Divider, thickness = 0.8f.dp)
            ProfileListItem(R.drawable.offline, "Password", nav)
        }
    }
}

@Composable
fun TopBar(nav: DestinationsNavigator){
    Box(
        Modifier
            .background(Color.White)
            .fillMaxWidth()
    ) {
        Row(
            Modifier
                .height(48.dp)
        ) {
            IconButton(
                onClick = {
                    nav.navigate(ProfileContentDestination) }) {
                Icon(
                    painterResource(R.drawable.ic_back),
                    null,
                    Modifier
                        .align(Alignment.CenterVertically)
                        .size(36.dp)
                        .padding(8.dp),
                    tint = Color.Black
                )
            }
        }
        Text("Account Setting",
            Modifier.align(Alignment.Center),
            color = Color.TextPrimary)
    }
}

/*
@Preview(showBackground = true)
@Composable
fun AccountSettingListPreview() {
    OnlineFoodRetailTheme {
        AccountSettingListScreen()
    }
}*/
