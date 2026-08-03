import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the database work for grades.
 *
 * @author Lily Keus
 * @version 0.1.0
 * @since 8/3/2026
 */

public class GradeDao {
    private final Connection connection;

    /**
     * Gives the DAO a database connection.
     */
    public GradeDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * Adds a grade to the database.
     */
    public void insert(Grade grade) throws SQLException {
        String sql = "INSERT INTO grades "
                + "(enrollment_id, assignment_id, grade, weight) "
                + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, grade.getEnrollmentId());
            statement.setInt(2, grade.getAssignmentId());
            statement.setDouble(3, grade.getGrade());
            statement.setDouble(4, grade.getWeight());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    grade.setGradeId(keys.getInt(1));
                } else {
                    throw new SQLException("Insert succeeded, but no grade ID was returned.");
                }
            }
        }
    }

    /**
     * Finds a grade by its ID.
     */
    public Grade findById(int gradeId) throws SQLException {
        String sql = "SELECT grade_id, enrollment_id, assignment_id, grade, weight "
                + "FROM grades "
                + "WHERE grade_id = ?";

        Grade grade = null;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, gradeId);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    int enrollmentId = result.getInt("enrollment_id");
                    int assignmentId = result.getInt("assignment_id");
                    double gradeValue = result.getDouble("grade");
                    double weight = result.getDouble("weight");

                    grade = new Grade(gradeId, enrollmentId, assignmentId, gradeValue, weight);
                }
            }
        }

        return grade;
    }

    /**
     * Gets every grade from the database.
     */
    public List<Grade> findAll() throws SQLException {
        String sql = "SELECT grade_id, enrollment_id, assignment_id, grade, weight "
                + "FROM grades "
                + "ORDER BY grade_id = ?";

        List<Grade> grades = new ArrayList<>();

        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {

            while (result.next()) {
                grades.add(makeGrade(result));
            }
        }

        return grades;
    }

    /**
     * Gets every grade for one enrollment.
     */
    public List<Grade> findByEnrollmentId(int enrollmentId) throws SQLException {

        String sql = "SELECT grade_id, enrollment_id, assignment_id, grade, weight"
                + "FROM grades "
                + "WHERE enrollment_id = ? "
                + "ORDER BY grade_id";

        List<Grade> grades = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, enrollmentId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    Grade grade = makeGrade(result);
                    grades.add(grade);
                }
            }
        }

        return grades;
    }

    /**
     * Gets every grade for one assignment.
     */
    public List<Grade> findByAssignmentId(int assignmentId) throws SQLException {

        String sql = "SELECT grade_id, enrollment_id, assignment_id, grade, weight "
                + "FROM grades "
                + "WHERE assignment_id = ? "
                + "ORDER BY grade_id";

        List<Grade> grades = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, assignmentId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    Grade grade = makeGrade(result);
                    grades.add(grade);
                }
            }
        }

        return grades;
    }

    /**
     * Updates a grade in the database.
     */
    public void update(Grade grade) throws SQLException {
        String sql = "UPDATE grades SET "
                + "enrollment_id = ?, "
                + "assignment_id = ?, "
                + "grade = ?, "
                + "weight = ? "
                + "WHERE grade_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, grade.getEnrollmentId());
            statement.setInt(2, grade.getAssignmentId());
            statement.setDouble(3, grade.getGrade());
            statement.setDouble(4, grade.getWeight());
            statement.setInt(5, grade.getGradeId());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException(
                        "No grade found with ID " + grade.getGradeId()
                );
            }
        }
    }

    /**
     * Deletes a grade from the database.
     */
    public void delete(int gradeId) throws SQLException {
        String sql = "DELETE FROM grades "
                + "WHERE grade_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, gradeId);
            statement.executeUpdate();
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No grade found with ID " + gradeId);
            }
        }
    }

    /**
     * Creates a Grade object from the current result-set row.
     */
    private Grade makeGrade(ResultSet result) throws SQLException {
        int gradeId = result.getInt("grade_id");
        int enrollmentId = result.getInt("enrollment_id");
        int assignmentId = result.getInt("assignment_id");
        double gradeValue = result.getDouble("grade");
        double weight = result.getDouble("weight");

        return new Grade(gradeId, enrollmentId, assignmentId, gradeValue, weight);
    }
}