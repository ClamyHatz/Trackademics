/**
 * Holds the currently logged-in user for the whole application.
 *
 * Login sets the current user here after a successful sign-in, and any slice can
 * read it to find out who is logged in and what their role is. A null current
 * user means no one is logged in yet.
 *
 * This is a simple static holder because there is only ever one logged-in user
 * at a time in a desktop app.
 *
 * @author Bay Shahryar
 * @version 0.1.0
 * @since 8/9/26
 */
public class Session {

    private static User currentUser;

    /** Private constructor: this class is only used through its static methods. */
    private Session() {
    }

    /**
     * Sets the user who is now logged in.
     *
     * @param user the authenticated user
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Returns the user who is currently logged in.
     *
     * @return the current user, or null if no one is logged in
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Reports whether someone is logged in.
     *
     * @return true if a user is logged in, false otherwise
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Clears the current user, for logging out.
     */
    public static void clear() {
        currentUser = null;
    }
}