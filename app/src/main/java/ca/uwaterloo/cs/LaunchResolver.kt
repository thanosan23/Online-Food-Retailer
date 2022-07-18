package ca.uwaterloo.cs
import androidx.compose.runtime.Composable
import ca.uwaterloo.cs.destinations.LaunchScreenDestination
import ca.uwaterloo.cs.destinations.MainContentDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination(start=true)
@Composable
fun newstart(
    nav: DestinationsNavigator
){
    if (Singleton.isNewUser){
        nav.navigate(LaunchScreenDestination)
    }
    else{
        nav.navigate(MainContentDestination)
    }
}