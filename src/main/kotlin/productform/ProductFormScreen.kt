package productform

import javafx.geometry.Insets
import javafx.scene.control.*
import javafx.scene.layout.GridPane

class ProductFormScreen(model: ProductFormModel?) : GridPane(){
    init
    {
        this.vgap = 5.0
        this.hgap = 5.0
        this.padding = Insets(5.0, 15.0, 5.0, 15.0)
        val nameText = Label("Name: ")
        val nameInput = TextField(model?.name)
        nameInput.promptText = "Enter product name"

        val imgText = Label("Image: ")
        val imgInput = TextField(model?.img ?: "TESTING FORM. TO BE REWORKED")
        imgInput.promptText = "TESTING FORM. TO BE REWORKED"

        this.add(nameText, 0, 0)
        this.add(nameInput, 1, 0)
        this.add(imgText, 0, 1)
        this.add(imgInput, 1, 1)
    }
}