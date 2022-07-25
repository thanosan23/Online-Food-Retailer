package ca.uwaterloo.cs.Profile

import androidx.compose.foundation.layout.R
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.uwaterloo.cs.NavigationBar
import ca.uwaterloo.cs.Singleton
import ca.uwaterloo.cs.bemodels.UserProfile
import ca.uwaterloo.cs.bemodels.readUserProfileFromFiles
import ca.uwaterloo.cs.db.DBManager
import ca.uwaterloo.cs.destinations.Destination
import ca.uwaterloo.cs.destinations.AccountSettingListScreenDestination
import ca.uwaterloo.cs.ui.theme.*
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


/*class User(
    val email: String,
    val name: String,
    @DrawableRes val avatar: Int
) {
    companion object {
        val Me: User = User("Test", "Test", ca.uwaterloo.cs.R.drawable.ic_pumpkin)
    }
}*/

@Composable
fun ProfileTopBar(userProfile:UserProfile) {
    val firstName = userProfile.firstName
    val familyName = userProfile.familyName
    val name = "$firstName $familyName"
    val email = userProfile.email

    Row(
        Modifier
            .background(Color.White)
            .fillMaxWidth()
            .height(224.dp)
    ) {
        Image(
            painterResource(id = ca.uwaterloo.cs.R.drawable.anonymous), contentDescription = "Profile picture",
            Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 24.dp)
                .clip(RoundedCornerShape(6.dp))
                .size(120.dp)
        )
        Column(
            Modifier
                .weight(1f)
                .padding(start = 20.dp)
        ) {
            Text(
                name,
                Modifier.padding(top = 64.dp),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color.Companion.TextPrimary,
            )
            Text(
                "Email：${email}",
                Modifier.padding(top = 16.dp),
                fontSize = 14.sp,
                color = androidx.compose.ui.graphics.Color.Companion.TextPrimary
            )
        }
        /*Icon(
            painterResource(ca.uwaterloo.cs.R.drawable.ic_arrow_more), contentDescription = "Details about Profile",
            Modifier
                .align(Alignment.CenterVertically)
                .padding(end = 16.dp)
                .size(16.dp),
            tint = androidx.compose.ui.graphics.Color.Companion.TextPrimary
        )*/
    }
}


@Composable
fun ProfileListItem(
    @DrawableRes icon: Int,
    title: String,
    nav: DestinationsNavigator,
    badge: @Composable (() -> Unit)? = null,
    endBadge: @Composable (() -> Unit)? = null
) {
    Row(
        Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painterResource(icon), "title", Modifier
                .padding(12.dp, 8.dp, 8.dp, 8.dp)
                .size(36.dp)
                .padding(8.dp)
        )
        Text(
            title,
            fontSize = 17.sp,
            color = androidx.compose.ui.graphics.Color.Companion.TextPrimary
        )
        badge?.invoke()
        Spacer(Modifier.weight(1f))
        endBadge?.invoke()
        IconButton(
            onClick = {
                nav.navigate(AccountSettingListScreenDestination) }) {
            Icon(
                painterResource(ca.uwaterloo.cs.R.drawable.ic_arrow_more), contentDescription = "More",
                Modifier
                    .padding(0.dp, 0.dp, 12.dp, 0.dp)
                    .size(16.dp),
                tint = androidx.compose.ui.graphics.Color.Companion.TextPrimary
            )
        }
        /*Icon(
            painterResource(ca.uwaterloo.cs.R.drawable.ic_arrow_more), contentDescription = "More",
            Modifier
                .padding(0.dp, 0.dp, 12.dp, 0.dp)
                .size(16.dp),
            tint = androidx.compose.ui.graphics.Color.Companion.TextPrimary
        )*/
    }
}

@Composable
fun ProfileList(nav: DestinationsNavigator) {
    val userProfile = readUserProfileFromFiles(LocalContext.current)

    Box(
        Modifier
            .background(Color.BG)
            .fillMaxSize()
    ) {
        Column(
            Modifier
                .background(Color.White)
                .fillMaxWidth()
        ) {
            ProfileTopBar(userProfile!!)
            Spacer(
                Modifier
                    .background(Color.Spacer)
                    .fillMaxWidth()
                    .height(18.dp)
            )
            /*ProfileListItem(ca.uwaterloo.cs.R.drawable.settings, "Account Setting",nav)
            Divider(startIndent = 56.dp, color = Color.Divider, thickness = 0.8f.dp)
            ProfileListItem(ca.uwaterloo.cs.R.drawable.offline, "Offline Mode",nav)
            Divider(startIndent = 56.dp, color = Color.Divider, thickness = 0.8f.dp)
            ProfileListItem(ca.uwaterloo.cs.R.drawable.swap, "Language", nav)
            Divider(startIndent = 56.dp, color = Color.Divider, thickness = 0.8f.dp)*/
            ProfileListItem(ca.uwaterloo.cs.R.drawable.logout, "Log out", nav)
        }
    }
}

@Composable
@com.ramcosta.composedestinations.annotation.Destination
fun ProfileContent(nav: DestinationsNavigator) {
    val useTemplate: Boolean = true //farmer:true,worker:false
    Scaffold(
        content = { ProfileList(nav) },
        bottomBar = { NavigationBar(nav) })
}

/*@Preview(showBackground = true)
@Composable
fun ProfileListPreview() {
    OnlineFoodRetailTheme {
        ProfileList()
    }
}*/
