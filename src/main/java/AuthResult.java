/**
 * The outcome of an authentication action (register or login).
 *
 * Carries whether the action succeeded, a message to show the user, and the
 * User involved when there is one (for example the account that just logged in).
 * Controllers use the message to drive an alert and use the user to store the
 * current login.
 *
 * @author Bay Shahryar
 * @version 0.1.0
 * @since 8/8/26
 */
public class AuthResult {

    private final boolean success;
    private final String message;
    private final User user;

    /**
     * Creates an AuthResult.
     *
     * @param success whether the action succeeded
     * @param message a message describing the outcome, shown to the user
     * @param user    the user involved, or null when there is none
     */
    public AuthResult(boolean success, String message, User user) {
        this.success = success;
        this.message = message;
        this.user = user;
    }

    /**
     * Convenience factory for a failure with no user.
     *
     * @param message the reason the action failed
     * @return a failed AuthResult carrying the message
     */
    public static AuthResult failure(String message) {
        return new AuthResult(false, message, null);
    }

    /**
     * Convenience factory for a success carrying a user.
     *
     * @param message a message describing the success
     * @param user    the user the action produced or authenticated
     * @return a successful AuthResult carrying the user
     */
    public static AuthResult success(String message, User user) {
        return new AuthResult(true, message, user);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }
}