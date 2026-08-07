import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all the database work for the enrollments table.
 *
 * Each method grabs its own connection from DatabaseConnection
 * and closes it when it's done. Dropping a class is an update rather
 * than a delete so the row sticks around.
 *
 * @author Ayoung Choi
 * @version 0.2.0
 * @since 8/6/26
 *
 */

public class EnrollmentDAO {

    /**
     * Saves a new enrollment and fills in the id the database generated.
     *
     * @param enrollment the enrollment to save
     * @throws SQLException if the insert fails, including when the student
     *         is already enrolled in that class
     */
    public void insert(Enrollment enrollment) throws SQLException {
        String sql = "INSERT INTO enrollments (class_id, student_id, enrolled_on, status) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, enrollment.getClassId());
            statement.setInt(2, enrollment.getStudentId());
            statement.setString(3, enrollment.getEnrolledOn().toString());
            statement.setString(4, enrollment.getStatus().getValue());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    enrollment.setEnrollmentId(keys.getInt(1));
                }
            }
        }
    }

    /**
     * Finds one enrollment by its id.
     *
     * @param enrollmentId the id to look for
     * @return the enrollment, or null if there isn't one with that id
     * @throws SQLException if the query fails
     */
    public Enrollment findById(int enrollmentId) throws SQLException {
        String sql = "SELECT * FROM enrollments WHERE enrollment_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, enrollmentId);

            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return buildEnrollment(results);
                }
                return null;
            }
        }
    }

    /**
     * Gets every enrollment in the table.
     *
     * @return a list of all enrollments, empty if there aren't any
     * @throws SQLException if the query fails
     */
    public List<Enrollment> findAll() throws SQLException {
        String sql = "SELECT * FROM enrollments";
        List<Enrollment> enrollments = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {

            while (results.next()) {
                enrollments.add(buildEnrollment(results));
            }
        }
        return enrollments;
    }

    /**
     * Gets everyone enrolled in one class, dropped students included.
     *
     * @param classId the class to look up
     * @return that class's enrollments, empty if it has none
     * @throws SQLException if the query fails
     */
    public List<Enrollment> findByClass(int classId) throws SQLException {
        String sql = "SELECT * FROM enrollments WHERE class_id = ?";
        List<Enrollment> enrollments = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, classId);

            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    enrollments.add(buildEnrollment(results));
                }
            }
        }
        return enrollments;
    }

    /**
     * Gets every class one student is in, dropped ones included.
     *
     * @param studentId the student's user id
     * @return that student's enrollments, empty if they have none
     * @throws SQLException if the query fails
     */
    public List<Enrollment> findByStudent(int studentId) throws SQLException {
        String sql = "SELECT * FROM enrollments WHERE student_id = ?";
        List<Enrollment> enrollments = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, studentId);

            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    enrollments.add(buildEnrollment(results));
                }
            }
        }
        return enrollments;
    }

    /**
     * Marks an enrollment dropped instead of deleting it, so the record of
     * the student having been in the class survives.
     *
     * @param enrollmentId the id of the enrollment to drop
     * @throws SQLException if the update fails
     */
    public void drop(int enrollmentId) throws SQLException {
        String sql = "UPDATE enrollments SET status = ? WHERE enrollment_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, Enrollment.Status.DROPPED.getValue());
            statement.setInt(2, enrollmentId);
            statement.executeUpdate();
        }
    }

    /**
     * Updates an existing enrollment.
     *
     * @param enrollment the enrollment to update, with its id already set
     * @throws SQLException if the update fails
     */
    public void update(Enrollment enrollment) throws SQLException {
        String sql = "UPDATE enrollments SET class_id = ?, student_id = ?, "
                + "enrolled_on = ?, status = ? WHERE enrollment_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, enrollment.getClassId());
            statement.setInt(2, enrollment.getStudentId());
            statement.setString(3, enrollment.getEnrolledOn().toString());
            statement.setString(4, enrollment.getStatus().getValue());
            statement.setInt(5, enrollment.getEnrollmentId());
            statement.executeUpdate();
        }
    }

    /**
     * Deletes an enrollment for real. Dropping a class should use drop()
     * instead; this is here for cleanup.
     *
     * @param enrollmentId the id of the enrollment to delete
     * @throws SQLException if the delete fails
     */
    public void delete(int enrollmentId) throws SQLException {
        String sql = "DELETE FROM enrollments WHERE enrollment_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, enrollmentId);
            statement.executeUpdate();
        }
    }

    /**
     * Turns the current row of a result set into an Enrollment.
     *
     * @param results a result set sitting on a row of the enrollments table
     * @return the enrollment built from that row
     * @throws SQLException if a column can't be read
     */
    private Enrollment buildEnrollment(ResultSet results) throws SQLException {
        return new Enrollment(
                results.getInt("enrollment_id"),
                results.getInt("class_id"),
                results.getInt("student_id"),
                LocalDate.parse(results.getString("enrolled_on")),
                Enrollment.Status.fromValue(results.getString("status")));
    }
}