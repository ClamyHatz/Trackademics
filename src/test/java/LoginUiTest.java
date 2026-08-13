import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TestFX UI test for the login flow.
 *
 * Launches the app on the home scene, navigates to login, signs in with a known
 * account, and confirms the app transitions back to home with the user logged
 * in. A uniquely named test user is created up front so the test does not depend
 * on the seeded data being present.
 *
 * @author Bay Shahryar
 * @version 0.1.0
 * @since 8/10/26
 */
public class LoginUiTest extends ApplicationTest {

    private String testUsername;
    private static final String TEST_PASSWORD = "testpass123";

    /**
     * Starts the application on the home scene, the same entry point as Main.
     *
     * @param stage the stage supplied by the TestFX runtime
     */
    @Override
    public void start(Stage stage) {
        stage.setScene(SceneFactory.create(SceneType.HOME, stage));
        stage.show();
    }

    /**
     * Creates a unique test user in the real database before each test, and
     * clears any leftover session so the app starts logged out.
     */
    @BeforeEach
    public void createTestUser() throws SQLException {
        Session.clear();
        testUsername = "uitest_" + System.currentTimeMillis();
        try (Connection connection = DatabaseConnection.getConnection()) {
            UserDao userDao = new UserDao(connection);
            userDao.insert(new User(0, testUsername, TEST_PASSWORD, "STUDENT"));
        }
    }

    /**
     * Clears the session after each test so it does not leak into the next.
     */
    @AfterEach
    public void clearSession() {
        Session.clear();
    }

    @Test
    public void loginWithValidCredentialsReachesHome() {
        // From home, open the login screen.
        clickOn("Log In");

        // Fill in the login form with the test account.
        clickOn("#usernameField").write(testUsername);
        clickOn("#passwordField").write(TEST_PASSWORD);

        // Submit using the login button's fx:id so it is not confused with the
        // "Log In" button on the home screen.
        clickOn("#loginButton");

        // A success alert appears; dismiss it by pressing Enter on the default
        // OK button.
        push(KeyCode.ENTER);

        // After a successful login the user is stored in the session.
        User loggedIn = Session.getCurrentUser();
        assertNotNull(loggedIn, "A user should be logged in after valid login");
        assertTrue(testUsername.equals(loggedIn.getUsername()),
                "The logged-in user should match the account we signed in with");
    }
}