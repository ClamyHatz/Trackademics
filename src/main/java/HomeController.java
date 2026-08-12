import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Controls navigation from the home page.
 *
 * The home page also reflects login state: when someone is logged in it shows
 * who they are and a Log Out button, and when no one is logged in it shows the
 * Log In button instead. Login state is read from Session.
 *
 * @author Bay Shahryar
 * @author Estefan Vicencio
 * @author Lily Keus
 * @version 0.2.0
 * @since 8/10/26
 */
public class HomeController implements StageAware {

    private Stage stage;

    @FXML
    private Label statusLabel;

    @FXML
    private Button loginButton;

    @FXML
    private Button logoutButton;

    /**
     * Called by SceneFactory after home.fxml has been loaded.
     *
     * @param stage the application's primary stage
     */
    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Sets the home page up to match the current login state.
     *
     * JavaFX calls this automatically once the FXML fields are injected.
     */
    @FXML
    private void initialize() {
        refreshLoginState();
    }

    /**
     * Shows the login status and toggles the Log In / Log Out buttons based on
     * whether someone is currently logged in.
     */
    private void refreshLoginState() {
        User currentUser = Session.getCurrentUser();
        boolean loggedIn = currentUser != null;

        if (loggedIn) {
            statusLabel.setText(
                    "Logged in as " + currentUser.getUsername()
                            + " (" + currentUser.getRole() + ")");
        } else {
            statusLabel.setText("Not logged in");
        }

        // A hidden node should also be unmanaged so it doesn't leave a gap.
        loginButton.setVisible(!loggedIn);
        loginButton.setManaged(!loggedIn);
        logoutButton.setVisible(loggedIn);
        logoutButton.setManaged(loggedIn);
    }

    @FXML
    private void openCourses() {
        changeScene(SceneType.COURSES);
    }

    @FXML
    private void openEnrollment() {
        changeScene(SceneType.ENROLLMENT);
    }

    @FXML
    private void openAssignments() {
        changeScene(SceneType.ASSIGNMENTS);
    }

    @FXML
    private void openGrades() {
        changeScene(SceneType.GRADES);
    }

    @FXML
    private void openLogin() {
        changeScene(SceneType.LOGIN);
    }

    /**
     * Logs the current user out and refreshes the home page.
     */
    @FXML
    private void handleLogout() {
        Session.clear();
        refreshLoginState();
    }

    /**
     * Replaces the scene displayed in the primary stage.
     *
     * @param sceneType the scene to display
     */
    private void changeScene(SceneType sceneType) {
        if (stage == null) {
            throw new IllegalStateException(
                    "Stage was not supplied to HomeController.");
        }

        stage.setScene(SceneFactory.create(sceneType, stage));
    }
}