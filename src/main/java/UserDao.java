import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles reading and writing User records in the database.
 *
 * The DAO is given a Connection to work with, so the real app can pass a SQLite
 * connection and tests can pass an in-memory H2 connection. All SQL uses
 * PreparedStatement so user input is never concatenated into a query.
 *
 * @author Bay Shahryar
 * @version 0.1.0
 * @since 8/3/26
 */
public class UserDao {

    private final Connection connection;

    /**
     * Creates a UserDao that runs its queries on the given connection.
     *
     * @param connection an open database connection
     */
    public UserDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * Inserts a new user and returns it with its generated id set.
     *
     * @param user the user to insert (its userId is ignored and replaced)
     * @return the same user with its new database id set
     * @throws SQLException if the insert fails (for example a duplicate username)
     */
    public User insert(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement statement =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getRole());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setUserId(keys.getInt(1));
                }
            }
        }
        return user;
    }

    /**
     * Finds a user by id.
     *
     * @param userId the id to look up
     * @return the matching User, or null if none exists
     * @throws SQLException if the query fails
     */
    public User findById(int userId) throws SQLException {
        String sql = "SELECT user_id, username, password, role FROM users WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return readRow(rs);
                }
            }
        }
        return null;
    }

    /**
     * Finds a user by username. This is the lookup login uses.
     *
     * @param username the username to look up
     * @return the matching User, or null if none exists
     * @throws SQLException if the query fails
     */
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT user_id, username, password, role FROM users WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return readRow(rs);
                }
            }
        }
        return null;
    }

    /**
     * Returns every user in the table.
     *
     * @return a list of all users, empty if there are none
     * @throws SQLException if the query fails
     */
    public List<User> findAll() throws SQLException {
        String sql = "SELECT user_id, username, password, role FROM users";
        List<User> users = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                users.add(readRow(rs));
            }
        }
        return users;
    }

    /**
     * Updates an existing user's username, password, and role by its id.
     *
     * @param user the user carrying the id to update and the new values
     * @return true if a row was updated, false if no user had that id
     * @throws SQLException if the update fails
     */
    public boolean update(User user) throws SQLException {
        String sql = "UPDATE users SET username = ?, password = ?, role = ? WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getRole());
            statement.setInt(4, user.getUserId());
            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a user by id.
     *
     * @param userId the id of the user to delete
     * @return true if a row was deleted, false if no user had that id
     * @throws SQLException if the delete fails
     */
    public boolean delete(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Builds a User from the current row of a ResultSet.
     *
     * @param rs a ResultSet positioned on a user row
     * @return a User made from that row
     * @throws SQLException if a column cannot be read
     */
    private User readRow(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("role"));
    }
}