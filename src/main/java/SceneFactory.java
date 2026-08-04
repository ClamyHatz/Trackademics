import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Creates scenes for the whole application.
 *
 * This follows the Week 5 Scene Factory pattern: a static create() method takes
 * a SceneType and the Stage, switches on the type, and returns a Scene. The
 * caller installs it with stage.setScene(...). Because Project 2 requires FXML,
 * each private builder loads its scene from an FXML file instead of building
 * controls in code.
 *
 * After loading an FXML, the factory hands the Stage to the scene's controller
 * if that controller implements StageAware. This lets controllers trigger
 * navigation to other scenes.
 *
 * @author Bay Shahryar
 * @version 0.1.0
 * @since 7/28/26
 */
public class SceneFactory {

    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 600;

    /**
     * Creates the Scene for the requested scene type.
     *
     * @param type  the scene to build
     * @param stage the primary Stage, handed to the controller if it is StageAware
     * @return the built Scene, ready for the caller to install with setScene
     */
    public static Scene create(SceneType type, Stage stage) {
        return switch (type) {
            case LOGIN -> buildLoginScene(stage);
            case REGISTER -> buildRegisterScene(stage);
            case DASHBOARD -> buildDashboardScene(stage);
            case COURSES -> buildCoursesScene(stage);
            case ENROLLMENT -> buildEnrollmentScene(stage);
            case ASSIGNMENTS -> buildAssignmentsScene(stage);
            case GRADES -> buildGradesScene(stage);
            case HOME -> buildHomeScene(stage);
        };
    }

    private static Scene buildLoginScene(Stage stage) {
        return loadFxml(SceneType.LOGIN, stage);
    }

    private static Scene buildRegisterScene(Stage stage) {
        return loadFxml(SceneType.REGISTER, stage);
    }

    private static Scene buildDashboardScene(Stage stage) {
        return loadFxml(SceneType.DASHBOARD, stage);
    }

    private static Scene buildCoursesScene(Stage stage) {
        return loadFxml(SceneType.COURSES, stage);
    }

    private static Scene buildAssignmentsScene(Stage stage) {
        return loadFxml(SceneType.ASSIGNMENTS, stage);
    }

    private static Scene buildEnrollmentScene(Stage stage) {
        return loadFxml(SceneType.ENROLLMENT, stage);
    }

    private static Scene buildGradesScene(Stage stage) {
        return loadFxml(SceneType.GRADES, stage);
    }

    private static Scene buildHomeScene(Stage stage) {
        return loadFxml(SceneType.HOME, stage);
    }

    /**
     * Loads the FXML file for a scene type, hands the Stage to the controller
     * if it is StageAware, and wraps the result in a Scene.
     *
     * @param type  the scene whose FXML should be loaded
     * @param stage the primary Stage, passed to a StageAware controller
     * @return a Scene built from the loaded FXML
     */
    private static Scene loadFxml(SceneType type, Stage stage) {
        String fxmlFile = type.getFxmlFile();
        URL location = SceneFactory.class.getResource(fxmlFile);

        if (location == null) {
            throw new IllegalStateException(
                    "FXML not found for " + type + ": " + fxmlFile
                            + ". Make sure the file sits under src/main/resources/.");
        }

        try {
            FXMLLoader loader = new FXMLLoader(location);
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof StageAware) {
                ((StageAware) controller).setStage(stage);
            }

            return new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load FXML for " + type, e);
        }
    }
}