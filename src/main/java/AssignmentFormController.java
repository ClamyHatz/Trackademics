import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

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

  private static int selectedClassId;

  private Stage stage;

  @FXML
  private Label formTitleLabel;

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
   * Stores the class selected on the assignment screen.
   *
   * @param classId the selected class id
   */
  public static void setSelectedClassId(
      int classId) {

    selectedClassId = classId;
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
            "ACTIVE"));

    statusComboBox.setValue(
        "ACTIVE");

    if (assignmentToEdit != null) {
      loadAssignment();

    } else {
      formTitleLabel.setText(
          "Create Assignment");

      saveButton.setText(
          "Create Assignment");
    }
  }

  /**
   * Checks the form and saves the assignment.
   */
  @FXML
  private void saveAssignment() {
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

    int classId =
        selectedClassId;

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

    if (!canManageClass(classId)) {
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
      selectedClassId = 0;

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
   * Checks whether the logged-in teacher owns
   * the selected class.
   */
  private boolean canManageClass(
      int classId) {

    User currentUser =
        Session.getCurrentUser();

    if (currentUser == null
        || !"TEACHER".equalsIgnoreCase(
        currentUser.getRole())) {

      messageLabel.setText(
          "Only teachers can save assignments.");
      return false;
    }

    try {
      ClassDAO classDao =
          new ClassDAO();

      Course course =
          classDao.findById(
              classId);

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
   * Returns to the assignment table.
   */
  @FXML
  private void goBack() {
    assignmentToEdit = null;
    selectedClassId = 0;

    stage.setScene(
        SceneFactory.create(
            SceneType.ASSIGNMENTS,
            stage));
  }

  /**
   * Returns to the home screen.
   */
  @FXML
  private void goHome() {
    assignmentToEdit = null;
    selectedClassId = 0;

    stage.setScene(
        SceneFactory.create(
            SceneType.HOME,
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

    selectedClassId =
        assignmentToEdit.getClassId();

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