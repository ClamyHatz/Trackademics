import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

/**
 * Handles the database work for assignments.
 *
 * @author Estefan Vicencio
 * @version 0.1.0
 * @since 8/2/2026
 */
public class AssignmentDao {

  private final Connection connection;

  /**
   * Gives the DAO a database connection.
   */
  public AssignmentDao(Connection connection) {
    this.connection = connection;
  }

  /**
   * Adds an assignment to the database.
   */
  public void insert(Assignment assignment) throws SQLException {
    String sql =
        "INSERT INTO assignments "
            + "(class_id, title, description, due_date, "
            + "points_possible, status) "
            + "VALUES (?, ?, ?, ?, ?, ?)";

    PreparedStatement statement =
        connection.prepareStatement(
            sql,
            Statement.RETURN_GENERATED_KEYS);

    statement.setInt(1, assignment.getClassId());
    statement.setString(2, assignment.getTitle());
    statement.setString(3, assignment.getDescription());
    statement.setString(
        4,
        assignment.getDueDate().toString());
    statement.setDouble(
        5,
        assignment.getPointsPossible());
    statement.setString(
        6,
        assignment.getStatus());

    statement.executeUpdate();

    ResultSet keys = statement.getGeneratedKeys();

    if (keys.next()) {
      assignment.setAssignmentId(keys.getInt(1));
    }

    keys.close();
    statement.close();
  }

  /**
   * Finds an assignment by its ID.
   */
  public Assignment findById(int assignmentId)
      throws SQLException {

    String sql =
        "SELECT * FROM assignments "
            + "WHERE assignment_id = ?";

    PreparedStatement statement =
        connection.prepareStatement(sql);

    statement.setInt(1, assignmentId);

    ResultSet result =
        statement.executeQuery();

    Assignment assignment = null;

    if (result.next()) {
      int classId =
          result.getInt("class_id");

      String title =
          result.getString("title");

      String description =
          result.getString("description");

      String dueDateText =
          result.getString("due_date");

      double pointsPossible =
          result.getDouble("points_possible");

      String status =
          result.getString("status");

      LocalDate dueDate =
          LocalDate.parse(dueDateText);

      assignment = new Assignment(
          assignmentId,
          classId,
          title,
          description,
          dueDate,
          pointsPossible,
          status);
    }

    result.close();
    statement.close();

    return assignment;
  }
}