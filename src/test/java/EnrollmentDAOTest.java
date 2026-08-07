import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for EnrollmentDAO.
 *
 * Enrollments point at a class and a user, so each test needs those rows to
 * exist first. BeforeEach makes a teacher, a student and a class to hang the
 * enrollments off of, and AfterEach clears everything out so a failed assert
 * doesn't leave rows behind.
 *
 * @author Ayoung Choi
 * @version 0.1.0
 * @since 8/6/26
 */

public class EnrollmentDAOTest {

    private final EnrollmentDAO dao = new EnrollmentDAO();
    private final ClassDAO classDao = new ClassDAO();

    private int teacherId;
    private int studentId;
    private int classId;
    private Integer extraClassId;

    @BeforeEach
    void createTestRows() throws SQLException {
        teacherId = insertUser("test_teacher", "TEACHER");
        studentId = insertUser("test_student", "STUDENT");

        Course course = new Course("TEST101", "Test Course", "Summer 2026", teacherId);
        classDao.insert(course);
        classId = course.getClassId();
    }

    @AfterEach
    void removeTestRows() throws SQLException {
        if (extraClassId != null) {
            for (Enrollment enrollment : dao.findByClass(extraClassId)) {
                dao.delete(enrollment.getEnrollmentId());
            }
            classDao.delete(extraClassId);
            extraClassId = null;
        }
        for (Enrollment enrollment : dao.findByClass(classId)) {
            dao.delete(enrollment.getEnrollmentId());
        }
        classDao.delete(classId);
        deleteUser(teacherId);
        deleteUser(studentId);
    }

    @Test
    void insertThenFindByIdGivesBackTheSameEnrollment() throws SQLException {
        Enrollment enrollment = new Enrollment(classId, studentId);
        dao.insert(enrollment);

        Enrollment found = dao.findById(enrollment.getEnrollmentId());

        assertNotNull(found);
        assertEquals(classId, found.getClassId());
        assertEquals(studentId, found.getStudentId());
        assertEquals(enrollment.getEnrolledOn(), found.getEnrolledOn());
        assertTrue(found.isActive());
    }

    @Test
    void dropMarksTheEnrollmentDroppedWithoutRemovingIt() throws SQLException {
        Enrollment enrollment = new Enrollment(classId, studentId);
        dao.insert(enrollment);

        dao.drop(enrollment.getEnrollmentId());
        Enrollment found = dao.findById(enrollment.getEnrollmentId());

        assertNotNull(found);
        assertEquals(Enrollment.Status.DROPPED, found.getStatus());    }

    @Test
    void findByClassOnlyReturnsThatClass() throws SQLException {
        Course other = new Course("TEST102", "Other Course", "Summer 2026", teacherId);
        classDao.insert(other);
        extraClassId = other.getClassId();

        Enrollment mine = new Enrollment(classId, studentId);
        Enrollment theirs = new Enrollment(extraClassId, studentId);
        dao.insert(mine);
        dao.insert(theirs);

        List<Enrollment> found = dao.findByClass(classId);

        assertEquals(1, found.size());
        assertEquals(mine.getEnrollmentId(), found.get(0).getEnrollmentId());
    }

    @Test
    void enrollingTheSameStudentTwiceFails() throws SQLException {
        dao.insert(new Enrollment(classId, studentId));

        assertThrows(SQLException.class,
                () -> dao.insert(new Enrollment(classId, studentId)));
    }

    @Test
    void deleteRemovesTheEnrollment() throws SQLException {
        Enrollment enrollment = new Enrollment(classId, studentId);
        dao.insert(enrollment);

        dao.delete(enrollment.getEnrollmentId());

        assertNull(dao.findById(enrollment.getEnrollmentId()));
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