import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import org.junit.jupiter.api.AfterEach;
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

  @Override
  public void start(Stage stage) {
    this.stage = stage;

    User teacher =
        new User(
            1,
            "testTeacher",
            "password",
            "TEACHER");

    Session.setCurrentUser(teacher);

    stage.setScene(
        SceneFactory.create(
            SceneType.ASSIGNMENTS,
            stage));

    stage.show();
  }

  @AfterEach
  public void clearSession() {
    Session.clear();
  }

  @Test
  public void addAssignmentOpensFormTest() {
    Scene assignmentScene =
        stage.getScene();

    Button addButton =
        lookup("#addAssignmentButton")
            .queryAs(Button.class);

    interact(addButton::fire);

    assertNotSame(
        assignmentScene,
        stage.getScene());

    Button saveButton =
        lookup("#saveButton")
            .queryAs(Button.class);

    assertNotNull(saveButton);
  }
}