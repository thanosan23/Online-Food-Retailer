import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.stage.Stage
import productform.ProductFormScreen

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
        val productFormScreen = ProductFormScreen(null)

        val borderPane = BorderPane()
        val catalogueButton = Button("Catalogue")
        catalogueButton.setOnAction {
            borderPane.center = catalogueScreen
        }
        val productFormButton = Button("Product Form")
        productFormButton.setOnAction {
            borderPane.center = productFormScreen
        }
        val screenSelect = HBox(catalogueButton, productFormButton) // temp fix to test screens

        borderPane.top = screenSelect
        borderPane.center = catalogueScreen

        stage.scene = Scene(borderPane)
        stage.show()
    }
}