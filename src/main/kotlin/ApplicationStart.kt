import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.VBox
import javafx.stage.Screen
import javafx.stage.Stage

// MVC with coupled View and Controller (a more typical method than MVC1)
// A simple MVC example inspired by Joseph Mack, http://www.austintek.com/mvc/
// This version uses MVC: two views coordinated with the observer pattern, but no separate controller.

enum class Screens{
    Catalogue, ProductForm
}

class ApplicationStart : Application() {

    override fun start(stage: Stage) {
        val initialScreen = Screens.Catalogue
        val catalogueScreen = CatalogueScreen()
        stage.scene = Scene(catalogueScreen)
        stage.show()
    }
}