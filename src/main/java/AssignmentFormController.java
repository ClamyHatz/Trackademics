import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * @description: Controls the form for adding or editing an assignment.
 * @author Estefan Vicencio
 * @version 0.1.0
 * @since 8/2/2026
 */
public class AssignmentFormController implements StageAware {

  private static Assignment assignmentToEdit;

  private Stage stage;

  @FXML
  private Label formTitleLabel;

  @FXML
  private TextField classIdField;

  @FXML
  private TextField titleField;

  @FXML
  private TextArea descriptionField;

  @FXML
  private DatePicker dueDatePicker;

  @FXML
  private TextField pointsField;

  @FXML
  private ComboBox<String> statusBox;

  @FXML
  private Button saveButton;

  @FXML
  private Label messageLabel;

  /**
   * Gives the form an assignment to edit.
   */
  public static void setAssignmentToEdit(
      Assignment assignment) {

    assignmentToEdit = assignment;
  }

  /**
   * Gives the controller the main window.
   */
  @Override
  public void setStage(Stage stage) {
    this.stage = stage;
  }

  /**
   * Sets up the form.
   */
  @FXML
  public void initialize() {
    statusBox.getItems().add("Not Started");
    statusBox.getItems().add("In Progress");
    statusBox.getItems().add("Completed");

    statusBox.setValue("Not Started");

    if (assignmentToEdit != null) {
      loadAssignment();
    }
  }

  /**
   * Puts the selected assignment into the form.
   */
  private void loadAssignment() {
    formTitleLabel.setText("Edit Assignment");
    saveButton.setText("Update Assignment");

    classIdField.setText(
        String.valueOf(
            assignmentToEdit.getClassId()));

    titleField.setText(
        assignmentToEdit.getTitle());

    descriptionField.setText(
        assignmentToEdit.getDescription());

    dueDatePicker.setValue(
        assignmentToEdit.getDueDate());

    pointsField.setText(
        String.valueOf(
            assignmentToEdit.getPointsPossible()));

    statusBox.setValue(
        assignmentToEdit.getStatus());
  }

  /**
   * Saves or updates an assignment.
   */
  @FXML
  private void saveAssignment() {
    String title = titleField.getText();
    String description = descriptionField.getText();
    LocalDate dueDate = dueDatePicker.getValue();
    String status = statusBox.getValue();

    if (title.isBlank()) {
      messageLabel.setText(
          "A title is required.");
      return;
    }

    if (dueDate == null) {
      messageLabel.setText(
          "A due date is required.");
      return;
    }

    try {
      int classId =
          Integer.parseInt(
              classIdField.getText());

      double points =
          Double.parseDouble(
              pointsField.getText());

      if (classId <= 0) {
        messageLabel.setText(
            "The class ID must be greater than zero.");
        return;
      }

      if (points <= 0) {
        messageLabel.setText(
            "Points must be greater than zero.");
        return;
      }

      Connection connection =
          DatabaseConnection.getConnection();

      AssignmentDao assignmentDao =
          new AssignmentDao(connection);

      if (assignmentToEdit == null) {
        Assignment assignment =
            new Assignment(
                classId,
                title,
                description,
                dueDate,
                points,
                status);

        assignmentDao.insert(assignment);

        messageLabel.setText(
            "Assignment saved.");

        clearForm();

      } else {
        assignmentToEdit.setClassId(classId);
        assignmentToEdit.setTitle(title);
        assignmentToEdit.setDescription(description);
        assignmentToEdit.setDueDate(dueDate);
        assignmentToEdit.setPointsPossible(points);
        assignmentToEdit.setStatus(status);

        assignmentDao.update(
            assignmentToEdit);

        assignmentToEdit = null;

        stage.setScene(
            SceneFactory.create(
                SceneType.ASSIGNMENTS,
                stage));
      }

      connection.close();

    } catch (NumberFormatException exception) {
      messageLabel.setText(
          "Class ID and points must be numbers.");

    } catch (SQLException exception) {
      messageLabel.setText(
          "Could not save the assignment.");

      exception.printStackTrace();
    }
  }

  /**
   * Goes back to the assignment table.
   */
  @FXML
  private void goBackToAssignments() {
    assignmentToEdit = null;

    stage.setScene(
        SceneFactory.create(
            SceneType.ASSIGNMENTS,
            stage));
  }

  /**
   * Clears the form after saving.
   */
  private void clearForm() {
    classIdField.clear();
    titleField.clear();
    descriptionField.clear();
    dueDatePicker.setValue(null);
    pointsField.clear();
    statusBox.setValue("Not Started");
  }
}