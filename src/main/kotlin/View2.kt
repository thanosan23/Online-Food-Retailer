import javafx.event.EventHandler
import javafx.scene.control.TextArea
import javafx.scene.layout.VBox

internal class View2(
    private val model: Model
    ) : VBox(), IView {

    private val text = TextArea("")

    // When notified by the model that things have changed,
    // update to display the new value
    override fun updateView() {
        println("View2: updateView")

        // display an 'X' for each counter value
        val s = StringBuilder()
        repeat (model.counterValue) { s.append("X") }
        text.text = s.toString()
    }

    init {
        // set label properties
        text.isWrapText = true
        text.isEditable = false

        // the previous controller code will just be handled here
        // we don't need always need a separate controller class!
        text.setOnMouseClicked {
            println("Controller: changing Model (actionPerformed)")
            model.incrementCounter()
        }

        // add label widget to the pane
        children.add(text)

        // register with the model when we're ready to start receiving data
        model.addView(this)
    }
}