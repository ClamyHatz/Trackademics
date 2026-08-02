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
 * @author Estefan Vicencio
 * @version 0.1.0
 * @since 7/31/26
 */
public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:trackademics.db";
    private static boolean schemaLoaded = false;

    /**
     * Opens a connection to the database and creates the tables if needed.
     *
     * @return an open database connection
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
     * Runs each command from schema.sql.
     *
     * @param connection the open database connection
     * @throws SQLException if a command cannot run
     */
    private static void applySchema(Connection connection)
        throws SQLException {

        String sql = readSchema();

        StringBuilder sqlWithoutComments =
            new StringBuilder();

        for (String line : sql.split("\n")) {
            if (!line.trim().startsWith("--")) {
                sqlWithoutComments
                    .append(line)
                    .append("\n");
            }
        }

        for (String command :
            sqlWithoutComments.toString().split(";")) {

            String cleanCommand = command.trim();

            if (!cleanCommand.isEmpty()) {
                Statement statement =
                    connection.createStatement();

                statement.executeUpdate(cleanCommand);
                statement.close();
            }
        }
    }

    /**
     * Reads schema.sql from the resources folder.
     *
     * @return the text inside schema.sql
     */
    private static String readSchema() {
        try (InputStream input =
            DatabaseConnection.class
                .getResourceAsStream(
                    "/schema.sql")) {

            if (input == null) {
                throw new IllegalStateException(
                    "schema.sql not found in resources");
            }

            return new String(
                input.readAllBytes(),
                StandardCharsets.UTF_8);

        } catch (Exception exception) {
            throw new IllegalStateException(
                "Failed to read schema.sql",
                exception);
        }
    }
}