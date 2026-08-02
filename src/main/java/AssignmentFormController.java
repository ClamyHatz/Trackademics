import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * @description: Controls the form for adding an assignment.
 * @author Estefan Vicencio
 * @version 0.1.0
 * @since 8/2/2026
 */
public class AssignmentFormController {

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
  private Label messageLabel;

  /**
   * Sets up the status choices.
   */
  @FXML
  public void initialize() {
    statusBox.getItems().add("Not Started");
    statusBox.getItems().add("In Progress");
    statusBox.getItems().add("Completed");

    statusBox.setValue("Not Started");
  }

  /**
   * Saves a new assignment.
   */
  @FXML
  private void saveAssignment() {
    String title = titleField.getText();
    String description = descriptionField.getText();
    LocalDate dueDate = dueDatePicker.getValue();
    String status = statusBox.getValue();

    if (title.isBlank()) {
      messageLabel.setText("A title is required.");
      return;
    }

    if (dueDate == null) {
      messageLabel.setText("A due date is required.");
      return;
    }

    try {
      int classId =
          Integer.parseInt(classIdField.getText());

      double points =
          Double.parseDouble(pointsField.getText());

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

      Assignment assignment =
          new Assignment(
              classId,
              title,
              description,
              dueDate,
              points,
              status);

      Connection connection =
          DatabaseConnection.getConnection();

      AssignmentDao assignmentDao =
          new AssignmentDao(connection);

      assignmentDao.insert(assignment);
      connection.close();

      messageLabel.setText(
          "Assignment saved.");

      clearForm();

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