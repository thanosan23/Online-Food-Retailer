import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight


internal class CatalogueScreen() : BorderPane(){
    val vBox = VBox()
    init
    {
        val scrollPane = ScrollPane()
        this.center = scrollPane
        val title = Label("Catalogue")
        //Label for education

        //Label for education
        val label = Label("File Data:")
        val font: Font = Font.font("verdana", FontWeight.BOLD, 12.0)
        label.font = font
        //Creating a table view
        //Creating a table view
        val table: TableView<FileData> = TableView<FileData>()
        this.center = table
        val data: ObservableList<FileData> = FXCollections.observableArrayList<FileData>(
            FileData("file1", "D:\\myFiles\\file1.txt", "25 MB", "12/01/2017"),
            FileData("file2", "D:\\myFiles\\file2.txt", "30 MB", "01/11/2019"),
            FileData("file3", "D:\\myFiles\\file3.txt", "50 MB", "12/04/2017"),
            FileData("file4", "D:\\myFiles\\file4.txt", "75 MB", "25/09/2018")
        )
        //Creating columns
        //Creating columns
        val fileNameCol = TableColumn<String, String>("File Name")
        fileNameCol.cellValueFactory = PropertyValueFactory<String, String>("fileName")
        table.items = data
    }

}

class FileData (val fileName: String, val fileAddress: String, val size: String, val date: String){

}
