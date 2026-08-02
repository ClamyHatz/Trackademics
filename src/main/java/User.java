/**
 * One user account. Matches a single row in the users table.
 *
 * A user is either a STUDENT or a TEACHER, set by the role field. Other slices
 * refer to a user by userId.
 *
 * @author Bay Shahryar
 * @version 0.1.0
 * @since 7/31/26
 */
public class User {

    private int userId;
    private String username;
    private String password;
    private String role;

    /**
     * Creates a user with all fields set.
     *
     * @param userId   the database id (0 if not saved yet)
     * @param username the login name
     * @param password the account password
     * @param role     STUDENT or TEACHER
     */
    public User(int userId, String username, String password, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}