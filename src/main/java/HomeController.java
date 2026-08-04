import javafx.fxml.FXML;
import javafx.stage.Stage;

/**
 * Controls navigation from the home page.
 */
public class HomeController implements StageAware {

    private Stage stage;

    /**
     * Called by SceneFactory after home.fxml has been loaded.
     *
     * @param stage the application's primary stage
     */
    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
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