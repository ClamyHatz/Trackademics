import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * @description: Controls the assignment form.
 * @author Estefan Vicencio
 * @version 0.1.0
 * @since 8/2/2026
 */
public class AssignmentFormController implements StageAware {

  private static Assignment assignmentToEdit;

  private final List<Course> teacherCourses =
      new ArrayList<>();

  private Stage stage;

  @FXML
  private Label formTitleLabel;

  @FXML
  private ComboBox<String> classComboBox;

  @FXML
  private TextField titleField;

  @FXML
  private TextArea descriptionArea;

  @FXML
  private DatePicker dueDatePicker;

  @FXML
  private TextField pointsField;

  @FXML
  private ComboBox<String> statusComboBox;

  @FXML
  private Button saveButton;

  @FXML
  private Label messageLabel;

  /**
   * Stores the assignment selected for editing.
   *
   * @param assignment the selected assignment
   */
  public static void setAssignmentToEdit(
      Assignment assignment) {

    assignmentToEdit = assignment;
  }

  /**
   * Gives the controller access to the application stage.
   *
   * @param stage the application stage
   */
  @Override
  public void setStage(Stage stage) {
    this.stage = stage;
  }

  /**
   * Sets up the form when the scene opens.
   */
  @FXML
  public void initialize() {
    statusComboBox.setItems(
        FXCollections.observableArrayList(
            "ACTIVE",
            "INACTIVE"));

    statusComboBox.setValue(
        "ACTIVE");

    loadTeacherClasses();

    if (assignmentToEdit != null) {
      loadAssignment();
    }
  }

  /**
   * Loads the logged-in teacher's classes.
   */
  private void loadTeacherClasses() {
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

      List<String> classNames =
          new ArrayList<>();

      for (Course course : teacherCourses) {
        classNames.add(
            course.getClassCode()
                + " - "
                + course.getTitle());
      }

      classComboBox.setItems(
          FXCollections.observableArrayList(
              classNames));

      if (!teacherCourses.isEmpty()) {
        classComboBox.getSelectionModel()
            .selectFirst();
      }

    } catch (SQLException exception) {
      messageLabel.setText(
          "Could not load your classes.");

      exception.printStackTrace();
    }
  }

  /**
   * Returns the selected class.
   *
   * @return the selected course, or null
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
   * Checks the form and saves the assignment.
   */
  @FXML
  private void saveAssignment() {
    Course selectedCourse =
        getSelectedCourse();

    if (selectedCourse == null) {
      messageLabel.setText(
          "Select a class.");
      return;
    }

    int classId =
        selectedCourse.getClassId();

    double pointsPossible;

    try {
      pointsPossible =
          Double.parseDouble(
              pointsField.getText());

    } catch (NumberFormatException exception) {
      messageLabel.setText(
          "Points must be a number.");
      return;
    }

    String title =
        titleField.getText();

    LocalDate dueDate =
        dueDatePicker.getValue();

    AssignmentService service =
        new AssignmentService();

    String validationMessage =
        service.checkAssignment(
            classId,
            title,
            dueDate,
            pointsPossible);

    if (!validationMessage.isEmpty()) {
      messageLabel.setText(
          validationMessage);
      return;
    }

    String description =
        descriptionArea.getText();

    String status =
        statusComboBox.getValue();

    if (status == null) {
      status = "ACTIVE";
    }

    try (Connection connection =
        DatabaseConnection.getConnection()) {

      AssignmentDao assignmentDao =
          new AssignmentDao(connection);

      if (assignmentToEdit == null) {
        Assignment assignment =
            new Assignment(
                classId,
                title,
                description,
                dueDate,
                pointsPossible,
                status);

        assignmentDao.insert(
            assignment);

      } else {
        assignmentToEdit.setClassId(
            classId);

        assignmentToEdit.setTitle(
            title);

        assignmentToEdit.setDescription(
            description);

        assignmentToEdit.setDueDate(
            dueDate);

        assignmentToEdit.setPointsPossible(
            pointsPossible);

        assignmentToEdit.setStatus(
            status);

        assignmentDao.update(
            assignmentToEdit);
      }

      assignmentToEdit = null;

      stage.setScene(
          SceneFactory.create(
              SceneType.ASSIGNMENTS,
              stage));

    } catch (SQLException exception) {
      messageLabel.setText(
          "Could not save the assignment.");

      exception.printStackTrace();
    }
  }

  /**
   * Returns to the assignment table.
   */
  @FXML
  private void goBack() {
    assignmentToEdit = null;

    stage.setScene(
        SceneFactory.create(
            SceneType.ASSIGNMENTS,
            stage));
  }

  /**
   * Places the selected assignment information into the form.
   */
  private void loadAssignment() {
    formTitleLabel.setText(
        "Edit Assignment");

    saveButton.setText(
        "Update Assignment");

    for (int index = 0;
        index < teacherCourses.size();
        index++) {

      Course course =
          teacherCourses.get(index);

      if (course.getClassId()
          == assignmentToEdit.getClassId()) {

        classComboBox
            .getSelectionModel()
            .select(index);

        break;
      }
    }

    titleField.setText(
        assignmentToEdit.getTitle());

    descriptionArea.setText(
        assignmentToEdit.getDescription());

    dueDatePicker.setValue(
        assignmentToEdit.getDueDate());

    pointsField.setText(
        Double.toString(
            assignmentToEdit.getPointsPossible()));

    statusComboBox.setValue(
        assignmentToEdit.getStatus());
  }
}