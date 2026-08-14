import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for EnrollmentDAO.
 *
 * Enrollments point at a class and a user, so each test needs those rows to
 * exist first. BeforeEach makes a teacher, two students and two classes to
 * hang the enrollments off of, and AfterEach clears everything out so a failed
 * assert doesn't leave rows behind. The names carry a random suffix so a run
 * that dies partway through doesn't collide with the next one.
 *
 * @author Ayoung Choi
 * @version 0.2.0
 * @since 8/6/26
 */

public class EnrollmentDAOTest {

    private final EnrollmentDAO dao = new EnrollmentDAO();
    private final ClassDAO classDao = new ClassDAO();

    private int teacherId;
    private int studentId;
    private int otherStudentId;
    private int classId;
    private int otherClassId;

    @BeforeEach
    void createTestRows() throws SQLException {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 6);

        teacherId = insertUser("test_teacher_" + suffix, "TEACHER");
        studentId = insertUser("test_student_" + suffix, "STUDENT");
        otherStudentId = insertUser("test_student2_" + suffix, "STUDENT");

        Course course = new Course("TEST" + suffix, "Test Course", "Summer 2026", teacherId);
        classDao.insert(course);
        classId = course.getClassId();

        Course otherCourse = new Course("OTHER" + suffix, "Other Course", "Summer 2026", teacherId);
        classDao.insert(otherCourse);
        otherClassId = otherCourse.getClassId();
    }

    @AfterEach
    void removeTestRows() throws SQLException {
        for (Enrollment enrollment : dao.findByClass(classId)) {
            dao.delete(enrollment.getEnrollmentId());
        }
        for (Enrollment enrollment : dao.findByClass(otherClassId)) {
            dao.delete(enrollment.getEnrollmentId());
        }
        classDao.delete(classId);
        classDao.delete(otherClassId);
        deleteUser(teacherId);
        deleteUser(studentId);
        deleteUser(otherStudentId);
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
    void findAllGrowsByOneWhenAnEnrollmentIsAdded() throws SQLException {
        int before = dao.findAll().size();

        Enrollment enrollment = new Enrollment(classId, studentId);
        dao.insert(enrollment);

        List<Enrollment> after = dao.findAll();

        assertEquals(before + 1, after.size());
        assertEquals(1, countById(after, enrollment.getEnrollmentId()));
    }

    @Test
    void findByStudentOnlyReturnsThatStudentAndKeepsDroppedOnes() throws SQLException {
        Enrollment first = new Enrollment(classId, studentId);
        Enrollment second = new Enrollment(otherClassId, studentId);
        Enrollment someoneElse = new Enrollment(classId, otherStudentId);
        dao.insert(first);
        dao.insert(second);
        dao.insert(someoneElse);

        dao.drop(second.getEnrollmentId());

        List<Enrollment> found = dao.findByStudent(studentId);

        assertEquals(2, found.size());
        assertEquals(1, countById(found, first.getEnrollmentId()));
        assertEquals(1, countById(found, second.getEnrollmentId()));
        assertEquals(0, countById(found, someoneElse.getEnrollmentId()));
    }

    @Test
    void updateChangesThePersistedStatusAndDate() throws SQLException {
        Enrollment enrollment = new Enrollment(classId, studentId);
        dao.insert(enrollment);

        LocalDate backdated = enrollment.getEnrolledOn().minusDays(7);
        enrollment.setEnrolledOn(backdated);
        enrollment.setStatus(Enrollment.Status.DROPPED);
        dao.update(enrollment);

        Enrollment found = dao.findById(enrollment.getEnrollmentId());

        assertNotNull(found);
        assertEquals(backdated, found.getEnrolledOn());
        assertEquals(Enrollment.Status.DROPPED, found.getStatus());
        assertEquals(classId, found.getClassId());
        assertEquals(studentId, found.getStudentId());
    }

    @Test
    void dropMarksTheEnrollmentDroppedWithoutRemovingIt() throws SQLException {
        Enrollment enrollment = new Enrollment(classId, studentId);
        dao.insert(enrollment);

        dao.drop(enrollment.getEnrollmentId());
        Enrollment found = dao.findById(enrollment.getEnrollmentId());

        assertNotNull(found);
        assertEquals(Enrollment.Status.DROPPED, found.getStatus());
    }

    @Test
    void findByClassOnlyReturnsThatClass() throws SQLException {
        Enrollment mine = new Enrollment(classId, studentId);
        Enrollment theirs = new Enrollment(otherClassId, studentId);
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
     * How many times an enrollment id shows up in a list. Returns int rather
     * than the long that count() gives back, so assertEquals picks the right
     * overload.
     */
    private int countById(List<Enrollment> enrollments, int enrollmentId) {
        return (int) enrollments.stream()
                .filter(enrollment -> enrollment.getEnrollmentId() == enrollmentId)
                .count();
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