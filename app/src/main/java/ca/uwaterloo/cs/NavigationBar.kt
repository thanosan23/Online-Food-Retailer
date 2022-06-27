package ca.uwaterloo.cs

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
    
    BottomNavigation(
        backgroundColor = Color.InstagramPurple
    ) {
        items.forEach { item ->
            BottomNavigationItem(
                selected = false,
                icon = { Icon(painterResource(id = item.icon), contentDescription = item.title) },
                //label = { Text(text = item.title) },
                onClick = {
                    /*TODO*/
                }
            )
        }
    }
}

