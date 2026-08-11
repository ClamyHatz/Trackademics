import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the database work for grades.
 *
 * @author Lily Keus
 * @version 0.2.0
 * @since 8/10/2026
 */

public class GradeDao {
    private final Connection connection;

    /**
     * Gives the DAO a database connection.
     *
     * @param connection the database connection used by this DAO
     */
    public GradeDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * Adds a grade to the database and assigns the generated ID to the Grade object.
     *
     * @param grade the grade to add to the database
     * @throws SQLException if a database error occurs or no generated ID is returned
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
     *
     * @param gradeId the ID of the grade to find
     * @return the matching Grade object, or null if no grade is found
     * @throws SQLException if a database error occurs
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
     *
     * @return a list containing all grades in the database
     * @throws SQLException if a database error occurs
     */
    public List<Grade> findAll() throws SQLException {
        String sql = "SELECT grade_id, enrollment_id, assignment_id, grade, weight "
                + "FROM grades "
                + "ORDER BY grade_id";

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
     * Gets every grade associated with a specific enrollment.
     *
     * @param enrollmentId the enrollment ID used to find grades
     * @return a list of grades associated with the enrollment
     * @throws SQLException if a database error occurs
     */
    public List<Grade> findByEnrollmentId(int enrollmentId) throws SQLException {

        String sql = "SELECT grade_id, enrollment_id, assignment_id, grade, weight "
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
     * Gets every grade associated with a specific assignment.
     *
     * @param assignmentId the assignment ID used to find grades
     * @return a list of grades associated with the assignment
     * @throws SQLException if a database error occurs
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
     * Updates an existing grade in the database.
     *
     * @param grade the grade containing the updated values
     * @throws SQLException if a database error occurs or the grade does not exist
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
     *
     * @param gradeId the ID of the grade to delete
     * @throws SQLException if a database error occurs or the grade does not exist
     */
    public void delete(int gradeId) throws SQLException {
        String sql = "DELETE FROM grades "
                + "WHERE grade_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, gradeId);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No grade found with ID " + gradeId);
            }
        }
    }

    /**
     * Creates a Grade object from the current row of a result set.
     *
     * @param result the result set containing grade data
     * @return a Grade object created from the current result-set row
     * @throws SQLException if an error occurs while reading the result set
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
