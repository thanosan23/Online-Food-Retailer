import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.layout.VBox

internal class View1(
    private val model: Model
    ) : VBox(), IView {

    // good idea to start button off in unknown state
    private val button = Button("?")

    // When notified by the model that things have changed,
    // update to display the new value
    override fun updateView() {
        println("View1: updateView")
        // just set the button name to the counter
        button.text = model.counterValue.toString()
    }

    init {
        // setup the view (i.e. group+widget)
        this.alignment = Pos.CENTER
        this.minHeight = 100.0
        button.setMinSize(75.0, 25.0)
        button.setMaxSize(100.0, 50.0)

        // the previous controller code will just be handled here
        // we don't need always need a separate controller class!
        button.setOnMouseClicked {
            println("Controller: changing Model (actionPerformed)")
            model.incrementCounter()
        }

        // add button widget to the pane
        children.add(button)

        // register with the model when we're ready to start receiving data
        model.addView(this)
    }
}