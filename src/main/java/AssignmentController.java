import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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
  private TableColumn<Assignment, String> classColumn;

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

    classColumn.setCellValueFactory(
        cellData ->
            new ReadOnlyStringWrapper(
                getClassName(
                    cellData.getValue().getClassId())));

    User currentUser =
        Session.getCurrentUser();

    if (currentUser == null) {
      return;
    }

    boolean teacher =
        "TEACHER".equalsIgnoreCase(
            currentUser.getRole());

    addAssignmentButton.setDisable(!teacher);
    editAssignmentButton.setDisable(!teacher);
    deleteAssignmentButton.setDisable(!teacher);

    loadAssignments();
  }

  /**
   * Opens an empty form for adding an assignment.
   */
  @FXML
  private void openAssignmentForm() {
    AssignmentFormController.setAssignmentToEdit(
        null);

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
   * Loads assignments from the database.
   */
  private void loadAssignments() {
    User currentUser =
        Session.getCurrentUser();

    if (currentUser == null) {
      return;
    }

    if ("TEACHER".equalsIgnoreCase(
        currentUser.getRole())) {

      loadTeacherAssignments(
          currentUser.getUserId());

    } else {
      loadAllAssignments();
    }
  }

  /**
   * Loads all assignments for a student.
   */
  private void loadAllAssignments() {
    try (Connection connection =
        DatabaseConnection.getConnection()) {

      AssignmentDao assignmentDao =
          new AssignmentDao(connection);

      List<Assignment> savedAssignments =
          assignmentDao.findAll();

      ObservableList<Assignment> assignments =
          FXCollections.observableArrayList(
              savedAssignments);

      assignmentTable.setItems(
          assignments);

    } catch (SQLException exception) {
      messageLabel.setText(
          "Could not load assignments.");

      exception.printStackTrace();
    }
  }

  /**
   * Loads assignments that belong to the teacher's classes.
   *
   * @param teacherId the logged-in teacher id
   */
  private void loadTeacherAssignments(
      int teacherId) {

    try {
      ClassDAO classDao =
          new ClassDAO();

      List<Course> courses =
          classDao.findByTeacher(
              teacherId);

      List<Assignment> teacherAssignments =
          new ArrayList<>();

      try (Connection connection =
          DatabaseConnection.getConnection()) {

        AssignmentDao assignmentDao =
            new AssignmentDao(connection);

        for (Course course : courses) {
          List<Assignment> classAssignments =
              assignmentDao.findByClassId(
                  course.getClassId());

          teacherAssignments.addAll(
              classAssignments);
        }
      }

      ObservableList<Assignment> assignments =
          FXCollections.observableArrayList(
              teacherAssignments);

      assignmentTable.setItems(
          assignments);

    } catch (SQLException exception) {
      messageLabel.setText(
          "Could not load assignments.");

      exception.printStackTrace();
    }
  }

  /**
   * Gets the class name for an assignment.
   *
   * @param classId the assignment's class id
   * @return the class code and title
   */
  private String getClassName(
      int classId) {

    try {
      ClassDAO classDao =
          new ClassDAO();

      Course course =
          classDao.findById(
              classId);

      if (course == null) {
        return "Unknown";
      }

      return course.getClassCode()
          + " - "
          + course.getTitle();

    } catch (SQLException exception) {
      exception.printStackTrace();

      return "Unknown";
    }
  }

  /**
   * Returns to the home screen.
   */
  @FXML
  private void goBack() {
    stage.setScene(
        SceneFactory.create(
            SceneType.HOME,
            stage));
  }

  /**
   * Logs the current user out and returns home.
   */
  @FXML
  private void logout() {
    Session.clear();

    Alert alert =
        new Alert(
            Alert.AlertType.INFORMATION);

    alert.setTitle("Logout");
    alert.setHeaderText(null);
    alert.setContentText(
        "SUCCESSFULLY LOGGED OUT");

    alert.showAndWait();

    stage.setScene(
        SceneFactory.create(
            SceneType.HOME,
            stage));
  }
}