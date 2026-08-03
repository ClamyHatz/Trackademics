import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Application entry point for Trackademics.
 *
 * Main keeps things small: it sets the window title, then asks the SceneFactory
 * to build the starting scene and installs it on the Stage. The app opens on
 * Login because that is the common entry for returning users, and every other
 * slice depends on knowing the logged-in user.
 *
 * @author Bay Shahryar
 * @version 0.1.0
 * @since 7/28/26
 */
public class Main extends Application {

    private static final String TITLE = "Trackademics";

    /**
     * Launches the JavaFX application.
     *
     * @param args command line arguments (unused)
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Builds the starting scene through the factory and shows it.
     *
     * @param stage the primary Stage supplied by JavaFX
     */
    @Override
    public void start(Stage stage) {
        stage.setTitle(TITLE);
        stage.setScene(SceneFactory.create(SceneType.LOGIN, stage));
        stage.show();
    }
}
