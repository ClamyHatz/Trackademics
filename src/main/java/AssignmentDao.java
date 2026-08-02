import java.sql.Connection;

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
}