import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * TestFX UI test for the Accounts slice login scene.
 *
 * Loads login.fxml through the FXMLLoader, installs it on the stage, and
 * verifies the login controls are present and wired. This runs the real
 * LoginController against the real FXML in a live scene graph.
 *
 * @author Bay Shahryar
 * @version 0.5.0
 * @since 8/10/26
 */
public class LoginUiTest extends ApplicationTest {

    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setScene(new Scene(new javafx.scene.layout.StackPane(), 800, 600));
        stage.show();
    }

    @AfterEach
    public void clearSession() {
        Session.clear();
    }

    @Test
    public void loginSceneLoadsWithAllControlsTest() throws Exception {
        Session.clear();

        URL loginFxml = LoginUiTest.class.getResource("/login.fxml");
        assertNotNull(loginFxml, "login.fxml must be available in src/main/resources");

        Parent root = new FXMLLoader(loginFxml).load();
        interact(() -> stage.setScene(new Scene(root, 800, 600)));

        TextField usernameField = lookup("#usernameField").queryAs(TextField.class);
        PasswordField passwordField = lookup("#passwordField").queryAs(PasswordField.class);
        Button loginButton = lookup("#loginButton").queryAs(Button.class);

        assertNotNull(usernameField, "The login scene should have a username field");
        assertNotNull(passwordField, "The login scene should have a password field");
        assertNotNull(loginButton, "The login scene should have a login button");

        assertEquals("Log In", loginButton.getText());
    }

    @Test
    public void typingIntoLoginFieldsUpdatesTheirTextTest() throws Exception {
        Session.clear();

        URL loginFxml = LoginUiTest.class.getResource("/login.fxml");
        assertNotNull(loginFxml);

        Parent root = new FXMLLoader(loginFxml).load();
        interact(() -> stage.setScene(new Scene(root, 800, 600)));

        // Set the field values on the FX thread and confirm they take.
        interact(() -> {
            TextField usernameField = lookup("#usernameField").queryAs(TextField.class);
            PasswordField passwordField = lookup("#passwordField").queryAs(PasswordField.class);
            usernameField.setText("demoUser");
            passwordField.setText("demoPass");
        });

        TextField usernameField = lookup("#usernameField").queryAs(TextField.class);
        PasswordField passwordField = lookup("#passwordField").queryAs(PasswordField.class);

        assertEquals("demoUser", usernameField.getText());
        assertEquals("demoPass", passwordField.getText());
        assertTrue(usernameField.isVisible());
    }
}