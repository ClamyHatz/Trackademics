import java.sql.Connection;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controls the login screen.
 *
 * Reads the username and password, asks AuthService to log in, and on success
 * stores the user in Session and returns to the home page. Failures are shown
 * both in an inline message label and in an alert dialog.
 *
 * @author Bay Shahryar
 * @version 0.1.0
 * @since 8/9/26
 */
public class LoginController implements StageAware {

    private Stage stage;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    /**
     * Called by SceneFactory after login.fxml has been loaded.
     *
     * @param stage the application's primary stage
     */
    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Handles the Log In button: validates and authenticates the input.
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        AuthResult result;
        try {
            Connection connection = DatabaseConnection.getConnection();
            AuthService authService = new AuthService(new UserDao(connection));
            result = authService.login(username, password);
            connection.close();
        } catch (SQLException exception) {
            showAlert(Alert.AlertType.ERROR, "Login Error",
                    "Could not reach the database. Please try again.");
            return;
        }

        if (result.isSuccess()) {
            Session.setCurrentUser(result.getUser());
            showAlert(Alert.AlertType.INFORMATION, "Login", result.getMessage());
            changeScene(SceneType.HOME);
        } else {
            messageLabel.setText(result.getMessage());
            showAlert(Alert.AlertType.WARNING, "Login Failed", result.getMessage());
        }
    }

    /**
     * Opens the registration screen.
     */
    @FXML
    private void openRegister() {
        changeScene(SceneType.REGISTER);
    }

    /**
     * Returns to the home screen.
     */
    @FXML
    private void openHome() {
        changeScene(SceneType.HOME);
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
                    "Stage was not supplied to LoginController.");
        }
        stage.setScene(SceneFactory.create(sceneType, stage));
    }
}