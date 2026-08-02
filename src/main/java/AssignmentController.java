import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
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

  @FXML
  private Label messageLabel;

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
   * Opens an empty form for adding an assignment.
   */
  @FXML
  private void openAssignmentForm() {
    AssignmentFormController.setAssignmentToEdit(null);

    stage.setScene(
        SceneFactory.create(
            SceneType.ASSIGNMENT_FORM,
            stage));
  }

  /**
   * Opens the selected assignment in the edit form.
   */
  @FXML
  private void editAssignment() {
    Assignment selectedAssignment =
        assignmentTable
            .getSelectionModel()
            .getSelectedItem();

    if (selectedAssignment == null) {
      messageLabel.setText(
          "Select an assignment first.");
      return;
    }

    AssignmentFormController.setAssignmentToEdit(
        selectedAssignment);

    stage.setScene(
        SceneFactory.create(
            SceneType.ASSIGNMENT_FORM,
            stage));
  }

  /**
   * Deletes the selected assignment.
   */
  @FXML
  private void deleteAssignment() {
    Assignment selectedAssignment =
        assignmentTable
            .getSelectionModel()
            .getSelectedItem();

    if (selectedAssignment == null) {
      messageLabel.setText(
          "Select an assignment first.");
      return;
    }

    try {
      Connection connection =
          DatabaseConnection.getConnection();

      AssignmentDao assignmentDao =
          new AssignmentDao(connection);

      assignmentDao.delete(
          selectedAssignment.getAssignmentId());

      connection.close();

      loadAssignments();

      messageLabel.setText(
          "Assignment deleted.");

    } catch (SQLException exception) {
      messageLabel.setText(
          "Could not delete the assignment.");

      exception.printStackTrace();
    }
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