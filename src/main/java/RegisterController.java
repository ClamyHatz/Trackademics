import java.sql.Connection;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controls the registration screen.
 *
 * Reads the username, password, and confirm password, then asks AuthService to
 * register the account. New accounts are always students; teacher accounts are
 * seeded, so no one can register themselves as a teacher. On success it shows an
 * alert and returns to the login screen. Failures show an inline message and an
 * alert.
 *
 * @author Bay Shahryar
 * @version 0.3.0
 * @since 8/9/26
 */
public class RegisterController implements StageAware {

    /** New registrations are always students. */
    private static final String DEFAULT_ROLE = "STUDENT";

    private Stage stage;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label messageLabel;

    /**
     * Called by SceneFactory after register.fxml has been loaded.
     *
     * @param stage the application's primary stage
     */
    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Handles the Register button: validates and creates the account.
     */
    @FXML
    private void handleRegister() {
        messageLabel.setText("");

        String username = usernameField.getText().strip();
        String password = passwordField.getText().strip();
        String confirmPassword = confirmPasswordField.getText().strip();

        AuthResult result;
        try (Connection connection = DatabaseConnection.getConnection()) {
            AuthService authService = new AuthService(new UserDao(connection));
            result = authService.register(username, password, confirmPassword, DEFAULT_ROLE);
        } catch (SQLException exception) {
            showAlert(Alert.AlertType.ERROR, "Registration Error",
                    "Could not reach the database. Please try again.");
            return;
        }

        if (result.isSuccess()) {
            showAlert(Alert.AlertType.INFORMATION, "Registration", result.getMessage());
            changeScene(SceneType.LOGIN);
        } else {
            messageLabel.setText(result.getMessage());
            showAlert(Alert.AlertType.WARNING, "Registration Failed", result.getMessage());
        }
    }

    /**
     * Returns to the login screen.
     */
    @FXML
    private void openLogin() {
        changeScene(SceneType.LOGIN);
    }

    /**
     * Shows a dialog to the user.
     *
     * @param type    the kind of alert (information, warning, error)
     * @param title   the dialog title
     * @param message the message to display
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Replaces the scene shown in the primary stage.
     *
     * @param sceneType the scene to display
     */
    private void changeScene(SceneType sceneType) {
        if (stage == null) {
            throw new IllegalStateException(
                    "Stage was not supplied to RegisterController.");
        }
        stage.setScene(SceneFactory.create(sceneType, stage));
    }
}