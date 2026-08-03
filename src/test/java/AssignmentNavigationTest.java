import static org.junit.jupiter.api.Assertions.assertNotSame;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

/**
 * @description: Tests navigation for the assignment screens.
 * @author Estefan Vicencio
 * @version 0.1.0
 * @since 8/2/2026
 */
public class AssignmentNavigationTest extends ApplicationTest {

  private Stage stage;

  /**
   * Opens the assignment screen before the test begins.
   *
   * @param stage the JavaFX test stage
   */
  @Override
  public void start(Stage stage) {
    this.stage = stage;

    stage.setScene(
        SceneFactory.create(
            SceneType.ASSIGNMENTS,
            stage));

    stage.show();
  }

  /**
   * Tests that the Add Assignment button opens the form.
   */
  @Test
  public void addAssignmentOpensFormTest() {
    Scene assignmentScene = stage.getScene();

    Button addButton =
        lookup("#addAssignmentButton")
            .queryAs(Button.class);

    interact(addButton::fire);

    assertNotSame(
        assignmentScene,
        stage.getScene());
  }
}