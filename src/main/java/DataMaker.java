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

    private static void addDemoData(GradeDao gradeDao, UserDao userDao, ClassDAO classDao,
            AssignmentDao assignmentDao, EnrollmentDAO enrollmentDao) throws SQLException {

        User teacher1 = new User(0, "exampleTeacher", "password", "TEACHER");
        userDao.insert(teacher1);

        User student1 = new User(0, "exampleStudent", "password", "STUDENT");
        userDao.insert(student1);

        Course course1 = new Course(0, "101", "Programming 101", "Summer 2026", teacher1.getUserId());
        classDao.insert(course1);

        Enrollment enrollment = new Enrollment(course1.getClassId(), student1.getUserId());
        enrollmentDao.insert(enrollment);

        Assignment assignment1 = new Assignment(0, course1.getClassId(), "A01, Hello World",
                "Goal: printing 'Hello World'", LocalDate.of(2026, 8, 14),
                10, "ACTIVE"
                );
        assignmentDao.insert(assignment1);

        Grade grade = new Grade(enrollment.getEnrollmentId(), assignment1.getAssignmentId(), 92.5, 0.20);
        gradeDao.insert(grade);

    }

    public static void main(String[] args) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // SQLite requires this setting separately for each connection.
            connection.createStatement().execute("PRAGMA foreign_keys = ON");

            GradeDao gradeDao = new GradeDao(connection);
            UserDao userDao = new UserDao(connection);
            ClassDAO classDAO = new ClassDAO();
            AssignmentDao assignmentDao = new AssignmentDao(connection);
            EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

            addDemoData(gradeDao, userDao, classDAO, assignmentDao, enrollmentDAO);

            System.out.println(userDao.findAll());
            System.out.println(classDAO.findAll());
            System.out.println(enrollmentDAO.findAll());
            System.out.println(assignmentDao.findAll());
            System.out.println(gradeDao.findAll());

        } catch (SQLException exception) {
            System.err.println("Database error:");
            exception.printStackTrace();
        }
    }
}