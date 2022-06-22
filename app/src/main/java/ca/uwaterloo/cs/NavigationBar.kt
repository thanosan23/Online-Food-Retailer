package ca.uwaterloo.cs

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource

sealed class NavItem(
    var title: String,
    var icon: Int,
) {
    object Catalogue: NavItem("Catalogue", R.drawable.ic_pumpkin)
    object Logistics: NavItem("Logistics", R.drawable.ic_pumpkin)
    object History: NavItem("History", R.drawable.ic_pumpkin)
    object Profile: NavItem("Profile", R.drawable.ic_pumpkin)
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
        //backgroundColor = Color.Blue
    ) {
        items.forEach { item ->
            BottomNavigationItem(
                selected = false,
                icon = { Icon(painterResource(id = item.icon), contentDescription = item.title) },
                label = { Text(text = item.title) },
                onClick = {
                    /*TODO*/
                }
            )
        }
    }
}

