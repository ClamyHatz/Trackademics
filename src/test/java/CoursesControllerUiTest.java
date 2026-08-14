import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * TestFX test for the courses scene.
 *
 * Fills in the form, clicks Add, and checks the row shows up in the table.
 * That covers the whole path: the controller reads the form, ClassDAO writes
 * the row, and the ObservableList behind the table picks it up on its own.
 *
 * The teacher and the session get set up inside start(), because TestFX calls
 * that before BeforeEach and the controller checks the session as soon as the
 * FXML loads.
 *
 * @author Ayoung Choi
 * @version 0.2.0
 * @since 8/11/26
 */
public class CoursesControllerUiTest extends ApplicationTest {

    private static final String TEST_CODE = "TESTUI 101";

    private final ClassDAO dao = new ClassDAO();

    private int teacherId;

    @Override
    public void start(Stage stage) {
        try {
            teacherId = insertUser("testui_teacher", "TEACHER");
        } catch (SQLException e) {
            throw new IllegalStateException("Couldn't set up the test teacher", e);
        }

        Session.setCurrentUser(new User(teacherId, "testui_teacher", "test", "TEACHER"));

        Scene scene = SceneFactory.create(SceneType.COURSES, stage);
        stage.setScene(scene);
        stage.show();
    }

    @AfterEach
    public void removeTestRows() throws SQLException {
        for (Course course : dao.findAll()) {
            if (TEST_CODE.equals(course.getClassCode())) {
                dao.delete(course.getClassId());
            }
        }
        deleteUser(teacherId);
        Session.clear();
    }

    @Test
    public void addingACourseFromTheFormPutsItInTheTable() throws SQLException {
        int before = dao.findAll().size();

        clickOn("#codeField").write(TEST_CODE);
        clickOn("#titleField").write("UI Test Course");
        clickOn("#termField").write("Fall 2026");
        clickOn("#teacherField").write(String.valueOf(teacherId));
        clickOn("#addButton");

        List<Course> after = dao.findAll();
        assertEquals(before + 1, after.size());

        Course saved = after.stream()
                .filter(course -> TEST_CODE.equals(course.getClassCode()))
                .findFirst()
                .orElse(null);

        assertNotNull(saved);
        assertEquals("UI Test Course", saved.getTitle());
        assertEquals("Fall 2026", saved.getTerm());
        assertEquals(teacherId, saved.getTeacherId());
    }

    /**
     * Puts a user straight into the table, since Slice 1's DAO isn't
     * something this test should depend on.
     */
    private int insertUser(String username, String role) throws SQLException {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, username);
            statement.setString(2, "test");
            statement.setString(3, role);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }

    private void deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            statement.executeUpdate();
        }
    }
}