package ca.uwaterloo.cs

import android.content.Intent
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import ca.uwaterloo.cs.ui.theme.InstagramPurple

sealed class NavItem(
    var title: String,
    var icon: Int,
) {
    // https://www.flaticon.com/free-icons
    object Catalogue: NavItem("Catalogue", R.drawable.catalogue_icon)
    object Logistics: NavItem("Logistics", R.drawable.logistics_icon)
    object History: NavItem("History", R.drawable.history_icon)
    object Profile: NavItem("Profile", R.drawable.profile_icon)
}


@Composable
fun NavigationBar() {
    val items = listOf(
        NavItem.Catalogue,
        NavItem.Logistics,
        NavItem.History,
        NavItem.Profile
    )
    val context = LocalContext.current

    BottomNavigation(
        backgroundColor = Color.InstagramPurple
    ) {
        items.forEach { item ->
            BottomNavigationItem(
                selected = false,
                icon = { Icon(painterResource(id = item.icon,), contentDescription = item.title,
                    modifier= Modifier.width(25.dp).height(25.dp),
                tint = Color.White)
                },
                label = { Text(text = item.title, fontSize = 12.sp, color = Color.White
                ) },
                onClick = {
                    var intent = Intent(context, Profile::class.java)
                    when (item.title) {
                        "Catalogue" -> intent = Intent(context, MainActivity::class.java)
                        "Logistics" -> intent = Intent(context, MainActivity::class.java)
                        "History" -> intent = Intent(context, MainActivity::class.java)
                        "Profile" -> intent = Intent(context, MainActivity::class.java)
                    }
                    startActivity(context, intent, null)
                }
            )
        }
    }
}




