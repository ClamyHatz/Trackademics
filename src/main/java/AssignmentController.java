import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
  private Button addAssignmentButton;

  @FXML
  private Button editAssignmentButton;

  @FXML
  private Button deleteAssignmentButton;

  @FXML
  private Label messageLabel;

  /**
   * Gives the controller the main window.
   */
  @Override
  public void setStage(Stage stage) {
    this.stage = stage;
    checkUserRole();
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
   * Checks whether the logged-in user can change assignments.
   */
  private void checkUserRole() {
    User currentUser =
        Session.getCurrentUser();

    boolean canEdit =
        currentUser != null
            && "TEACHER".equals(
            currentUser.getRole());

    addAssignmentButton.setDisable(!canEdit);
    editAssignmentButton.setDisable(!canEdit);
    deleteAssignmentButton.setDisable(!canEdit);
  }

  /**
   * Opens an empty form for adding an assignment.
   */
  @FXML
  private void openAssignmentForm() {
    User currentUser =
        Session.getCurrentUser();

    if (currentUser == null
        || !"TEACHER".equals(
        currentUser.getRole())) {

      messageLabel.setText(
          "Only teachers can add assignments.");
      return;
    }

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

    if (!canManageAssignment(
        selectedAssignment)) {

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

    if (!canManageAssignment(
        selectedAssignment)) {

      return;
    }

    try (Connection connection =
        DatabaseConnection.getConnection()) {

      AssignmentDao assignmentDao =
          new AssignmentDao(connection);

      assignmentDao.delete(
          selectedAssignment.getAssignmentId());

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
   * Checks whether the logged-in teacher owns the
   * class that the assignment belongs to.
   */
  private boolean canManageAssignment(
      Assignment assignment) {

    User currentUser =
        Session.getCurrentUser();

    if (currentUser == null
        || !"TEACHER".equals(
        currentUser.getRole())) {

      messageLabel.setText(
          "Only teachers can change assignments.");
      return false;
    }

    try {
      ClassDAO classDao =
          new ClassDAO();

      Course course =
          classDao.findById(
              assignment.getClassId());

      if (course == null) {
        messageLabel.setText(
            "The class could not be found.");
        return false;
      }

      if (course.getTeacherId()
          != currentUser.getUserId()) {

        messageLabel.setText(
            "You can only manage assignments for your own classes.");
        return false;
      }

      return true;

    } catch (SQLException exception) {
      messageLabel.setText(
          "Could not check the class.");

      exception.printStackTrace();
      return false;
    }
  }

  /**
   * Returns to the home screen.
   */
  @FXML
  private void goHome() {
    stage.setScene(
        SceneFactory.create(
            SceneType.HOME,
            stage));
  }

  /**
   * Loads assignments from the database.
   */
  private void loadAssignments() {
    try (Connection connection =
        DatabaseConnection.getConnection()) {

      AssignmentDao assignmentDao =
          new AssignmentDao(connection);

      List<Assignment> savedAssignments =
          assignmentDao.findAll();

      ObservableList<Assignment> assignments =
          FXCollections.observableArrayList(
              savedAssignments);

      assignmentTable.setItems(assignments);

    } catch (SQLException exception) {
      System.out.println(
          "Could not load assignments.");

      exception.printStackTrace();
    }
  }
}