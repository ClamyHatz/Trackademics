import java.sql.SQLException;

/**
 * Holds the account rules for registration and login.
 *
 * AuthService is given a UserDao, so the real app wires it to a SQLite
 * connection and tests wire it to an in-memory H2 connection. Every method
 * returns an AuthResult carrying a success flag, a message to show the user,
 * and the user when there is one.
 *
 * Login deliberately returns the same generic message whether the username is
 * unknown or the password is wrong, so the app never reveals which usernames
 * exist.
 *
 * @author Bay Shahryar
 * @version 0.1.0
 * @since 8/8/26
 */
public class AuthService {

    /** The only roles a new account may have. */
    private static final String ROLE_STUDENT = "STUDENT";
    private static final String ROLE_TEACHER = "TEACHER";

    /** Shown for any login failure so we don't reveal which usernames exist. */
    private static final String GENERIC_LOGIN_ERROR = "Invalid username or password.";

    private final UserDao userDao;

    /**
     * Creates an AuthService backed by the given UserDao.
     *
     * @param userDao the DAO used to read and write user records
     */
    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Registers a new account after checking the input.
     *
     * Checks run in order and stop at the first problem: blank username, blank
     * password, blank confirm, missing role, mismatched passwords, then a
     * duplicate username. Username and password are stripped of surrounding
     * whitespace so " bay " and "bay" are treated as the same. On success the
     * new user is inserted and returned.
     *
     * @param username        the requested username
     * @param password        the chosen password
     * @param confirmPassword the repeated password, which must match
     * @param role            the account role, STUDENT or TEACHER
     * @return a successful AuthResult with the new user, or a failure with a reason
     */
    public AuthResult register(String username, String password,
                               String confirmPassword, String role) {
        if (isBlank(username)) {
            return AuthResult.failure("Please enter a username.");
        }
        username = username.strip();
        if (isBlank(password)) {
            return AuthResult.failure("Please enter a password.");
        }
        password = password.strip();
        if (isBlank(confirmPassword)) {
            return AuthResult.failure("Please confirm your password.");
        }
        confirmPassword = confirmPassword.strip();
        if (isBlank(role)) {
            return AuthResult.failure("Please choose a role.");
        }
        if (!role.equals(ROLE_STUDENT) && !role.equals(ROLE_TEACHER)) {
            return AuthResult.failure("Role must be STUDENT or TEACHER.");
        }
        if (!password.equals(confirmPassword)) {
            return AuthResult.failure("Passwords do not match.");
        }

        try {
            if (userDao.findByUsername(username) != null) {
                return AuthResult.failure("That username is already taken.");
            }
            User created = userDao.insert(new User(0, username, password, role));
            return AuthResult.success("Account created. You can now log in.", created);
        } catch (SQLException exception) {
            // A duplicate can still slip through between the check and the insert,
            // because the username column is UNIQUE. Treat that as taken; treat
            // anything else as a generic database problem.
            if (isDuplicateUsername(exception)) {
                return AuthResult.failure("That username is already taken.");
            }
            return AuthResult.failure("Something went wrong. Please try again.");
        }
    }

    /**
     * Logs a user in after checking the input.
     *
     * Blank fields return a specific prompt. Username and password are stripped
     * of surrounding whitespace to match how accounts are stored. Any real
     * failure (unknown username or wrong password) returns the same generic
     * message so the app does not reveal which usernames exist.
     *
     * @param username the entered username
     * @param password the entered password
     * @return a successful AuthResult with the user, or a generic failure
     */
    public AuthResult login(String username, String password) {
        if (isBlank(username)) {
            return AuthResult.failure("Please enter a username.");
        }
        username = username.strip();
        if (isBlank(password)) {
            return AuthResult.failure("Please enter a password.");
        }
        password = password.strip();

        try {
            User user = userDao.findByUsername(username);
            if (user == null || !user.getPassword().equals(password)) {
                return AuthResult.failure(GENERIC_LOGIN_ERROR);
            }
            return AuthResult.success("Welcome, " + user.getUsername() + ".", user);
        } catch (SQLException exception) {
            return AuthResult.failure("Something went wrong. Please try again.");
        }
    }

    /**
     * Reports whether a string is null or only whitespace.
     *
     * @param value the string to check
     * @return true if the value is null, empty, or only whitespace
     */
    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    /**
     * Reports whether a SQLException looks like a duplicate username violation.
     *
     * @param exception the exception thrown by the insert
     * @return true if the message mentions a uniqueness or constraint problem
     */
    private boolean isDuplicateUsername(SQLException exception) {
        String message = exception.getMessage();
        if (message == null) {
            return false;
        }
        String lower = message.toLowerCase();
        return lower.contains("unique") || lower.contains("constraint");
    }
}