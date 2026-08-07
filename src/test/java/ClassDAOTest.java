import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests the ClassDAO methods.
 *
 * These hit the real database file, so BeforeEach makes a teacher for the
 * classes to point at and AfterEach clears everything out. Cleanup lives in
 * AfterEach rather than at the end of each test so a failed assert doesn't
 * leave rows behind. Test course codes start with TEST so they don't get
 * mixed up with real ones.
 *
 * @author Ayoung Choi
 * @version 0.2.0
 * @since 8/2/26
 */
public class ClassDAOTest {

    private final ClassDAO dao = new ClassDAO();

    private int teacherId;
    private Integer createdClassId;

    @BeforeEach
    public void createTeacher() throws SQLException {
        teacherId = insertUser("test_teacher", "TEACHER");
    }

    @AfterEach
    public void removeTestRows() throws SQLException {
        if (createdClassId != null) {
            dao.delete(createdClassId);
            createdClassId = null;
        }
        deleteUser(teacherId);
    }

    @Test
    public void insertThenFindByIdGivesBackTheSameCourse() throws SQLException {
        Course course = new Course("TEST 101", "Insert Test", "Fall 2026", teacherId);
        dao.insert(course);
        createdClassId = course.getClassId();

        Course found = dao.findById(course.getClassId());

        assertNotNull(found);
        assertEquals("TEST 101", found.getClassCode());
        assertEquals("Insert Test", found.getTitle());
    }

    @Test
    public void updateChangesTheTitle() throws SQLException {
        Course course = new Course("TEST 102", "Before", "Fall 2026", teacherId);
        dao.insert(course);
        createdClassId = course.getClassId();

        course.setTitle("After");
        dao.update(course);

        assertEquals("After", dao.findById(course.getClassId()).getTitle());
    }

    @Test
    public void deleteRemovesTheCourse() throws SQLException {
        Course course = new Course("TEST 103", "Delete Test", "Fall 2026", teacherId);
        dao.insert(course);
        int id = course.getClassId();

        dao.delete(id);

        assertNull(dao.findById(id));
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