import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Inserts data into the database, for example purposes.
 *
 * @author Lily Keus
 * @version 0.2.0
 * @since 8/5/2026
 */

/*
THIS CLASS IS MENT TO ONLY BE USED ONCE.
EVERY REPEATED USE FILLS THE DATABASE WITH DUPLICATE DATA.
IT HAS ALREADY BEEN USED SO DON'T USE IT.
 */

public class DataMaker {

    private static void addDemoData(
            Connection connection,
            GradeDao gradeDao,
            UserDao userDao,
            ClassDAO classDao,
            AssignmentDao assignmentDao

    ) throws SQLException {

        User teacher1 = new User(0, "exampleTeacher", "password", "TEACHER");
        userDao.insert(teacher1);

        User student1 = new User(0, "exampleStudent", "password", "STUDENT");
        userDao.insert(student1);

        Course course1 = new Course(0, "101", "Programming 101", "Summer 2026", teacher1.getUserId());
        classDao.insert(course1);

        int enrollmentId = insertEnrollment(
                connection,
                course1.getClassId(),
                student1.getUserId()
        );


        Assignment assignment1 = new Assignment(0, course1.getClassId(), "A01, Hello World",
                "Goal: printing 'Hello World'", LocalDate.of(2026, 8, 14),
                10, "ACTIVE"
                );

        assignmentDao.insert(assignment1);

        Grade grade = new Grade(
                enrollmentId,
                assignment1.getAssignmentId(),
                92.5,
                0.20
        );

        gradeDao.insert(grade);

    }

    private static int insertEnrollment(
            Connection connection,
            int classId,
            int studentId

    ) throws SQLException {

        String sql = """
                INSERT INTO enrollments
                    (class_id, student_id, enrolled_on, status)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS
        )) {
            statement.setInt(1, classId);
            statement.setInt(2, studentId);
            statement.setString(3, "2026-08-05");
            statement.setString(4, "active");
            statement.executeUpdate();

            return getGeneratedId(statement, "enrollment");
        }
    }

    private static int getGeneratedId(
            PreparedStatement statement,
            String recordType

    ) throws SQLException {

        try (ResultSet keys = statement.getGeneratedKeys()) {
            if (keys.next()) {
                return keys.getInt(1);
            }
        }

        throw new SQLException(
                "No generated ID was returned for the " + recordType
        );
    }

    public static List<Grade> findAllEnrollments() throws SQLException {
        String sql = "SELECT class_id, student_id, enrolled_on, status "
                + "FROM enrollments "
                + "ORDER BY enrollment_id";

        List<Grade> grades = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {

            while (results.next()) {
                grades.add(makeGrade(result));
            }
        }

        return grades;
    }

    public static void main(String[] args) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // SQLite requires this setting separately for each connection.
            connection.createStatement().execute("PRAGMA foreign_keys = ON");

            GradeDao gradeDao = new GradeDao(connection);
            UserDao userDao = new UserDao(connection);
            ClassDAO classDAO = new ClassDAO();
            AssignmentDao assignmentDao = new AssignmentDao(connection);

            addDemoData(connection, gradeDao, userDao, classDAO, assignmentDao);

            System.out.println(userDao.findAll());
            System.out.println(classDAO.findAll());
            System.out.println(findAllEnrollments());
            System.out.println(assignmentDao.findAll());
            System.out.println(gradeDao.findAll());

        } catch (SQLException exception) {
            System.err.println("Database error:");
            exception.printStackTrace();
        }
    }
}