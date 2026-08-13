import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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

  private final List<Course> teacherCourses =
      new ArrayList<>();

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
  private Label classSelectionLabel;

  @FXML
  private ComboBox<String> classComboBox;

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

    if (!Session.isLoggedIn()) {
      stage.setScene(
          SceneFactory.create(
              SceneType.LOGIN,
              stage));
    }
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

    classSelectionLabel.setVisible(teacher);
    classSelectionLabel.setManaged(teacher);

    classComboBox.setVisible(teacher);
    classComboBox.setManaged(teacher);

    if (teacher) {
      loadTeacherCourses();
    } else {
      loadAllAssignments();
    }
  }

  /**
   * Loads the classes that belong to the logged-in teacher.
   */
  private void loadTeacherCourses() {
    User currentUser =
        Session.getCurrentUser();

    if (currentUser == null) {
      return;
    }

    try {
      ClassDAO classDao =
          new ClassDAO();

      teacherCourses.clear();

      teacherCourses.addAll(
          classDao.findByTeacher(
              currentUser.getUserId()));

      ObservableList<String> courseNames =
          FXCollections.observableArrayList();

      for (Course course : teacherCourses) {
        courseNames.add(
            course.getClassCode()
                + " - "
                + course.getTitle());
      }

      classComboBox.setItems(
          courseNames);

      if (teacherCourses.isEmpty()) {
        messageLabel.setText(
            "You do not have any classes.");

        assignmentTable.setItems(
            FXCollections.observableArrayList());

        addAssignmentButton.setDisable(true);
        editAssignmentButton.setDisable(true);
        deleteAssignmentButton.setDisable(true);

        return;
      }

      classComboBox.getSelectionModel()
          .selectFirst();

      loadSelectedClassAssignments();

    } catch (SQLException exception) {
      messageLabel.setText(
          "Could not load your classes.");

      exception.printStackTrace();
    }
  }

  /**
   * Loads assignments when the teacher changes classes.
   */
  @FXML
  private void changeClass() {
    loadSelectedClassAssignments();
  }

  /**
   * Returns the class currently selected by the teacher.
   *
   * @return the selected course, or null if none is selected
   */
  private Course getSelectedCourse() {
    int selectedIndex =
        classComboBox
            .getSelectionModel()
            .getSelectedIndex();

    if (selectedIndex < 0
        || selectedIndex >= teacherCourses.size()) {

      return null;
    }

    return teacherCourses.get(
        selectedIndex);
  }

  /**
   * Loads assignments for the teacher's selected class.
   */
  private void loadSelectedClassAssignments() {
    Course selectedCourse =
        getSelectedCourse();

    if (selectedCourse == null) {
      return;
    }

    try (Connection connection =
        DatabaseConnection.getConnection()) {

      AssignmentDao assignmentDao =
          new AssignmentDao(connection);

      List<Assignment> savedAssignments =
          assignmentDao.findByClassId(
              selectedCourse.getClassId());

      ObservableList<Assignment> assignments =
          FXCollections.observableArrayList(
              savedAssignments);

      assignmentTable.setItems(
          assignments);

      messageLabel.setText("");

    } catch (SQLException exception) {
      messageLabel.setText(
          "Could not load assignments.");

      exception.printStackTrace();
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
   * Opens an empty form for creating an assignment.
   */
  @FXML
  private void openAssignmentForm() {
    Course selectedCourse =
        getSelectedCourse();

    if (selectedCourse == null) {
      messageLabel.setText(
          "Select a class first.");
      return;
    }

    AssignmentFormController.setAssignmentToEdit(
        null);

    AssignmentFormController.setSelectedClassId(
        selectedCourse.getClassId());

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

    AssignmentFormController.setSelectedClassId(
        selectedAssignment.getClassId());

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

    } catch (SQLException exception) {
      messageLabel.setText(
          "Could not delete the assignment.");

      exception.printStackTrace();
      return;
    }

    loadSelectedClassAssignments();

    messageLabel.setText(
        "Assignment deleted.");
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
   * Logs the current user out and returns to the home screen.
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