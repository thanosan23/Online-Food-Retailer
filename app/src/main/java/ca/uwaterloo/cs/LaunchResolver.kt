package ca.uwaterloo.cs
import androidx.compose.runtime.Composable
import ca.uwaterloo.cs.destinations.LoginDestination
import ca.uwaterloo.cs.destinations.MainContentDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination(start=true)
@Composable
fun newstart(
    nav: DestinationsNavigator
){
    if (Singleton.isNewUser){
        nav.navigate(LoginDestination)
    }
    else{
//        nav.navigate(LoginDestination)
        nav.navigate(MainContentDestination)
    }
}