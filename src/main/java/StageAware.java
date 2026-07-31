import javafx.stage.Stage;

/**
 * Lets the SceneFactory hand the shared Stage to a controller after loading
 * its FXML, so the controller can trigger navigation to other scenes.
 *
 * Controllers that need to switch scenes implement this. The factory calls
 * setStage right after loading the FXML, so the controller has the Stage ready.
 *
 * @author Bay Shahryar
 * @version 0.1.0
 * @since 7/28/26
 */
public interface StageAware {
    void setStage(Stage stage);
}