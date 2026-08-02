import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    ResultSet keys =
        statement.getGeneratedKeys();

    if (keys.next()) {
      assignment.setAssignmentId(
          keys.getInt(1));
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

  /**
   * Gets every assignment from the database.
   */
  public List<Assignment> findAll()
      throws SQLException {

    String sql =
        "SELECT * FROM assignments "
            + "ORDER BY due_date";

    Statement statement =
        connection.createStatement();

    ResultSet result =
        statement.executeQuery(sql);

    List<Assignment> assignments =
        new ArrayList<>();

    while (result.next()) {
      int assignmentId =
          result.getInt("assignment_id");

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

      Assignment assignment =
          new Assignment(
              assignmentId,
              classId,
              title,
              description,
              dueDate,
              pointsPossible,
              status);

      assignments.add(assignment);
    }

    result.close();
    statement.close();

    return assignments;
  }

  /**
   * Gets every assignment for one class.
   */
  public List<Assignment> findByClassId(int classId)
      throws SQLException {

    String sql =
        "SELECT * FROM assignments "
            + "WHERE class_id = ? "
            + "ORDER BY due_date";

    PreparedStatement statement =
        connection.prepareStatement(sql);

    statement.setInt(1, classId);

    ResultSet result =
        statement.executeQuery();

    List<Assignment> assignments =
        new ArrayList<>();

    while (result.next()) {
      int assignmentId =
          result.getInt("assignment_id");

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

      Assignment assignment =
          new Assignment(
              assignmentId,
              classId,
              title,
              description,
              dueDate,
              pointsPossible,
              status);

      assignments.add(assignment);
    }

    result.close();
    statement.close();

    return assignments;
  }

  /**
   * Updates an assignment in the database.
   */
  public void update(Assignment assignment)
      throws SQLException {

    String sql =
        "UPDATE assignments SET "
            + "class_id = ?, "
            + "title = ?, "
            + "description = ?, "
            + "due_date = ?, "
            + "points_possible = ?, "
            + "status = ? "
            + "WHERE assignment_id = ?";

    PreparedStatement statement =
        connection.prepareStatement(sql);

    statement.setInt(
        1,
        assignment.getClassId());

    statement.setString(
        2,
        assignment.getTitle());

    statement.setString(
        3,
        assignment.getDescription());

    statement.setString(
        4,
        assignment.getDueDate().toString());

    statement.setDouble(
        5,
        assignment.getPointsPossible());

    statement.setString(
        6,
        assignment.getStatus());

    statement.setInt(
        7,
        assignment.getAssignmentId());

    statement.executeUpdate();
    statement.close();
  }

  /**
   * Deletes an assignment from the database.
   */
  public void delete(int assignmentId)
      throws SQLException {

    String sql =
        "DELETE FROM assignments "
            + "WHERE assignment_id = ?";

    PreparedStatement statement =
        connection.prepareStatement(sql);

    statement.setInt(1, assignmentId);

    statement.executeUpdate();
    statement.close();
  }
}