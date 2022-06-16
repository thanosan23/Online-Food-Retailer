import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.VBox
import javafx.stage.Stage

// MVC with coupled View and Controller (a more typical method than MVC1)
// A simple MVC example inspired by Joseph Mack, http://www.austintek.com/mvc/
// This version uses MVC: two views coordinated with the observer pattern, but no separate controller.
class MVC2 : Application() {

    override fun start(stage: Stage) {
        // window name
        stage.title = this.javaClass.name

        // create and initialize the Model to hold our counter
        val model = Model()

        // create each view, and tell them about the model
        // the views will register themselves with the model
        val view1 = View1(model)
        val view2 = View2(model)
        val grid = VBox()
        grid.children.add(view1) // top-view
        grid.children.add(view2) // bottom-view

        // Add grid to a scene (and the scene to the stage)
        val scene = Scene(grid, 200.0, 200.0)
        stage.scene = scene
        stage.show()
    }
}