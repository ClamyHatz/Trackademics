
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Opens the database connection and sets up the tables.
 *
 * All the DAOs call getConnection() from here, so everything ends up in the
 * same database file. The schema only runs the first time. Since it uses
 * CREATE TABLE IF NOT EXISTS, running it again is harmless.
 *
 * @author Ayoung Choi
 * @version 0.1.0
 * @since 7/31/26
 *
 */
public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:trackademics.db";
    private static boolean schemaLoaded = false;

    /**
     * Opens a connection to the database, creating the tables on first use.
     *
     * @return an open Connection the caller is responsible for closing
     * @throws SQLException if the database cannot be opened
     */
    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(URL);
        if (!schemaLoaded) {
            applySchema(connection);
            schemaLoaded = true;
        }
        return connection;
    }


    /**
     * Runs schema.sql against the given connection.
     *
     *  Comments are stripped before splitting, otherwise the leftover comments at
     *  the end of the file get sent to the database as if they were a statement.
     *
     * @param connection an open connection to apply the schema to
     * @throws SQLException if the schema cannot be applied
     */
    private static void applySchema(Connection connection) throws SQLException {
        String sql = readSchema().replaceAll("--[^\n]*", "");
        try (Statement statement = connection.createStatement()) {
            for (String command : sql.split(";")) {
                if (!command.isBlank()) {
                    statement.execute(command);
                }
            }
        }
    }

    /**
     * Reads schema.sql from the resources folder.
     *
     * @return the contents of schema.sql
     */
    private static String readSchema() {
        try (InputStream in = DatabaseConnection.class.getResourceAsStream("/schema.sql")) {
            if (in == null) {
                throw new IllegalStateException("schema.sql not found in resources");
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to read schema.sql", e);
        }
    }
}