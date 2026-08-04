import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all the database work for the classes table.
 *
 * Named ClassDAO after the table, but it passes around Course objects
 * since Class is already taken in Java.
 * Each method grabs its own connection from DatabaseConnection
 * and closes it when it's done.
 *
 * @author Ayoung Choi
 * @version 0.1.0
 * @since 8/2/26
 *
 */

public class ClassDAO {

    /**
     * Saves a new course and fills in the id the database generated.
     *
     * @param course the course to save
     * @throws SQLException if the insert fails
     */
    public void insert(Course course) throws SQLException {
        String sql = "INSERT INTO classes (class_code, title, term, teacher_id) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, course.getClassCode());
            statement.setString(2, course.getTitle());
            statement.setString(3, course.getTerm());
            statement.setInt(4, course.getTeacherId());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    course.setClassId(keys.getInt(1));
                }
            }
        }
    }


    /**
     * Finds one course by its id.
     *
     * @param classId the id to look for
     * @return the course, or null if there isn't one with that id
     * @throws SQLException if the query fails
     */
    public Course findById(int classId) throws SQLException {
        String sql = "SELECT * FROM classes WHERE class_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, classId);

            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return buildCourse(results);
                }
                return null;
            }
        }
    }


    /**
     * Gets every course in the table.
     *
     * @return a list of all courses, empty if there aren't any
     * @throws SQLException if the query fails
     */
    public List<Course> findAll() throws SQLException {
        String sql = "SELECT * FROM classes";
        List<Course> courses = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {

            while (results.next()) {
                courses.add(buildCourse(results));
            }
        }
        return courses;
    }

    /**
     * Gets every course taught by one teacher.
     *
     * @param teacherId the teacher's user id
     * @return that teacher's courses, empty if they have none
     * @throws SQLException if the query fails
     */
    public List<Course> findByTeacher(int teacherId) throws SQLException {
        String sql = "SELECT * FROM classes WHERE teacher_id = ?";
        List<Course> courses = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, teacherId);

            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    courses.add(buildCourse(results));
                }
            }
        }
        return courses;
    }

    /**
     * Updates an existing course.
     *
     * @param course the course to update, with its id already set
     * @throws SQLException if the update fails
     */
    public void update(Course course) throws SQLException {
        String sql = "UPDATE classes SET class_code = ?, title = ?, term = ?, teacher_id = ? "
                + "WHERE class_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, course.getClassCode());
            statement.setString(2, course.getTitle());
            statement.setString(3, course.getTerm());
            statement.setInt(4, course.getTeacherId());
            statement.setInt(5, course.getClassId());
            statement.executeUpdate();
        }
    }

    /**
     * Deletes a course.
     *
     * @param classId the id of the course to delete
     * @throws SQLException if the delete fails
     */
    public void delete(int classId) throws SQLException {
        String sql = "DELETE FROM classes WHERE class_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, classId);
            statement.executeUpdate();
        }
    }

    /**
     * Turns the current row of a result set into a Course.
     *
     * @param results a result set sitting on a row of the classes table
     * @return the course built from that row
     * @throws SQLException if a column can't be read
     */
    private Course buildCourse(ResultSet results) throws SQLException {
        return new Course(
                results.getInt("class_id"),
                results.getString("class_code"),
                results.getString("title"),
                results.getString("term"),
                results.getInt("teacher_id"));
    }
}