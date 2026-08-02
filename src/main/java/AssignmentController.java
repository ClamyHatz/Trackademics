import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * @description: Controls the assignment screen.
 * @author Estefan Vicencio
 * @version 0.1.0
 * @since 8/2/2026
 */
public class AssignmentController implements StageAware {

  private Stage stage;

  @FXML
  private TableView<Assignment> assignmentTable;

  @FXML
  private TableColumn<Assignment, String> titleColumn;

  @FXML
  private TableColumn<Assignment, LocalDate> dueDateColumn;

  @FXML
  private TableColumn<Assignment, Double> pointsColumn;

  @FXML
  private TableColumn<Assignment, String> statusColumn;

  /**
   * Gives the controller the main window.
   */
  @Override
  public void setStage(Stage stage) {
    this.stage = stage;
  }

  /**
   * Sets up the assignment table.
   */
  @FXML
  public void initialize() {
    titleColumn.setCellValueFactory(
        new PropertyValueFactory<>("title"));

    dueDateColumn.setCellValueFactory(
        new PropertyValueFactory<>("dueDate"));

    pointsColumn.setCellValueFactory(
        new PropertyValueFactory<>("pointsPossible"));

    statusColumn.setCellValueFactory(
        new PropertyValueFactory<>("status"));

    loadAssignments();
  }

  /**
   * Opens the form for adding an assignment.
   */
  @FXML
  private void openAssignmentForm() {
    stage.setScene(
        SceneFactory.create(
            SceneType.ASSIGNMENT_FORM,
            stage));
  }

  /**
   * Loads assignments from the database.
   */
  private void loadAssignments() {
    try {
      Connection connection =
          DatabaseConnection.getConnection();

      AssignmentDao assignmentDao =
          new AssignmentDao(connection);

      List<Assignment> savedAssignments =
          assignmentDao.findAll();

      ObservableList<Assignment> assignments =
          FXCollections.observableArrayList(
              savedAssignments);

      assignmentTable.setItems(assignments);

      connection.close();

    } catch (SQLException exception) {
      System.out.println(
          "Could not load assignments.");

      exception.printStackTrace();
    }
  }
}