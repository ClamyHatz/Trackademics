import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Inserts data into the database, for example purposes.
 *
 * @author Lily Keus
 * @version 0.3.0
 * @since 8/7/2026
 */

/*
THIS CLASS IS MENT TO ONLY BE USED ONCE.
EVERY REPEATED USE FILLS THE DATABASE WITH DUPLICATE DATA.
IT HAS ALREADY BEEN USED SO DON'T USE IT.
 */

public class DataMaker {


    private static void addDemoData(GradeDao gradeDao, UserDao userDao, ClassDAO classDao,
                                    AssignmentDao assignmentDao, EnrollmentDAO enrollmentDao)
            throws SQLException {

        // Teachers
        User teacher1 = new User(0, "Prf. Stewart", "password", "TEACHER");
        userDao.insert(teacher1);

        User teacher2 = new User(0, "Dr. Doctor", "doctor", "TEACHER");
        userDao.insert(teacher2);

        // Students
        User student1 = new User(0, "Bob", "password1", "STUDENT");
        userDao.insert(student1);

        User student2 = new User(0, "Sally", "CoolThing72", "STUDENT");
        userDao.insert(student2);

        User student3 = new User(0, "Charlie", "passwordz", "STUDENT");
        userDao.insert(student3);

        User student4 = new User(0, "Cane", "pazzword", "STUDENT");
        userDao.insert(student4);

        User student5 = new User(0, "Mable", "wordpass", "STUDENT");
        userDao.insert(student5);

        User student6 = new User(0, "Joe", "SomethingIG", "STUDENT");
        userDao.insert(student6);

        // Courses
        Course course1 = new Course(0, "CST101", "Programming 101",
                "Fall 2026", teacher1.getUserId());
        classDao.insert(course1);

        Course course2 = new Course(0, "CST201", "Data Structures",
                "Fall 2026", teacher1.getUserId());
        classDao.insert(course2);

        Course course3 = new Course(0, "CST301", "Databases",
                "Fall 2026", teacher2.getUserId());
        classDao.insert(course3);

        // Enrollments (every student is in every class)
        Enrollment c1Student1 = new Enrollment(course1.getClassId(), student1.getUserId());
        enrollmentDao.insert(c1Student1);
        Enrollment c1Student2 = new Enrollment(course1.getClassId(), student2.getUserId());
        enrollmentDao.insert(c1Student2);
        Enrollment c1Student3 = new Enrollment(course1.getClassId(), student3.getUserId());
        enrollmentDao.insert(c1Student3);
        Enrollment c1Student4 = new Enrollment(course1.getClassId(), student4.getUserId());
        enrollmentDao.insert(c1Student4);
        Enrollment c1Student5 = new Enrollment(course1.getClassId(), student5.getUserId());
        enrollmentDao.insert(c1Student5);
        Enrollment c1Student6 = new Enrollment(course1.getClassId(), student6.getUserId());
        enrollmentDao.insert(c1Student6);

        Enrollment c2Student1 = new Enrollment(course2.getClassId(), student1.getUserId());
        enrollmentDao.insert(c2Student1);
        Enrollment c2Student2 = new Enrollment(course2.getClassId(), student2.getUserId());
        enrollmentDao.insert(c2Student2);
        Enrollment c2Student3 = new Enrollment(course2.getClassId(), student3.getUserId());
        enrollmentDao.insert(c2Student3);
        Enrollment c2Student4 = new Enrollment(course2.getClassId(), student4.getUserId());
        enrollmentDao.insert(c2Student4);
        Enrollment c2Student5 = new Enrollment(course2.getClassId(), student5.getUserId());
        enrollmentDao.insert(c2Student5);
        Enrollment c2Student6 = new Enrollment(course2.getClassId(), student6.getUserId());
        enrollmentDao.insert(c2Student6);

        Enrollment c3Student1 = new Enrollment(course3.getClassId(), student1.getUserId());
        enrollmentDao.insert(c3Student1);
        Enrollment c3Student2 = new Enrollment(course3.getClassId(), student2.getUserId());
        enrollmentDao.insert(c3Student2);
        Enrollment c3Student3 = new Enrollment(course3.getClassId(), student3.getUserId());
        enrollmentDao.insert(c3Student3);
        Enrollment c3Student4 = new Enrollment(course3.getClassId(), student4.getUserId());
        enrollmentDao.insert(c3Student4);
        Enrollment c3Student5 = new Enrollment(course3.getClassId(), student5.getUserId());
        enrollmentDao.insert(c3Student5);
        Enrollment c3Student6 = new Enrollment(course3.getClassId(), student6.getUserId());
        enrollmentDao.insert(c3Student6);

        // Assignments (one assignment for each course)
        Assignment assignment1 = new Assignment(
                0,
                course1.getClassId(),
                "A01 - Hello World",
                "Create a program that prints Hello World.",
                LocalDate.of(2026, 9, 10),
                100,
                "ACTIVE"
        );
        assignmentDao.insert(assignment1);

        Assignment assignment2 = new Assignment(
                0,
                course2.getClassId(),
                "A01 - Array Practice",
                "Practice storing and searching values in arrays.",
                LocalDate.of(2026, 9, 17),
                100,
                "ACTIVE"
        );
        assignmentDao.insert(assignment2);

        Assignment assignment3 = new Assignment(
                0,
                course3.getClassId(),
                "A01 - SQLite Tables",
                "Create and populate tables in SQLite.",
                LocalDate.of(2026, 9, 24),
                100,
                "ACTIVE"
        );
        assignmentDao.insert(assignment3);


        // Grades (each course only has one demo assignment, so its weight is 1.0 (100%))
        // Programming 101 grades
        gradeDao.insert(new Grade(c1Student1.getEnrollmentId(), assignment1.getAssignmentId(), 95, 1.0));
        gradeDao.insert(new Grade(c1Student2.getEnrollmentId(), assignment1.getAssignmentId(), 88, 1.0));
        gradeDao.insert(new Grade(c1Student3.getEnrollmentId(), assignment1.getAssignmentId(), 76, 1.0));
        gradeDao.insert(new Grade(c1Student4.getEnrollmentId(), assignment1.getAssignmentId(), 91, 1.0));
        gradeDao.insert(new Grade(c1Student5.getEnrollmentId(), assignment1.getAssignmentId(), 84, 1.0));
        gradeDao.insert(new Grade(c1Student6.getEnrollmentId(), assignment1.getAssignmentId(), 98, 1.0));

        // Data Structures grades
        gradeDao.insert(new Grade(c2Student1.getEnrollmentId(), assignment2.getAssignmentId(), 89, 1.0));
        gradeDao.insert(new Grade(c2Student2.getEnrollmentId(), assignment2.getAssignmentId(), 93, 1.0));
        gradeDao.insert(new Grade(c2Student3.getEnrollmentId(), assignment2.getAssignmentId(), 81, 1.0));
        gradeDao.insert(new Grade(c2Student4.getEnrollmentId(), assignment2.getAssignmentId(), 87, 1.0));
        gradeDao.insert(new Grade(c2Student5.getEnrollmentId(), assignment2.getAssignmentId(), 78, 1.0));
        gradeDao.insert(new Grade(c2Student6.getEnrollmentId(), assignment2.getAssignmentId(), 96, 1.0));

        // Databases grades
        gradeDao.insert(new Grade(c3Student1.getEnrollmentId(), assignment3.getAssignmentId(), 92, 1.0));
        gradeDao.insert(new Grade(c3Student2.getEnrollmentId(), assignment3.getAssignmentId(), 85, 1.0));
        gradeDao.insert(new Grade(c3Student3.getEnrollmentId(), assignment3.getAssignmentId(), 90, 1.0));
        gradeDao.insert(new Grade(c3Student4.getEnrollmentId(), assignment3.getAssignmentId(), 74, 1.0));
        gradeDao.insert(new Grade(c3Student5.getEnrollmentId(), assignment3.getAssignmentId(), 88, 1.0));
        gradeDao.insert(new Grade(c3Student6.getEnrollmentId(), assignment3.getAssignmentId(), 97, 1.0));
    }

    public static void main(String[] args) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.createStatement().execute("PRAGMA foreign_keys = ON");

            GradeDao gradeDao = new GradeDao(connection);
            UserDao userDao = new UserDao(connection);
            ClassDAO classDAO = new ClassDAO();
            AssignmentDao assignmentDao = new AssignmentDao(connection);
            EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

            //The line below is commented so it cannot be run if accidentally clicked
            //addDemoData(gradeDao, userDao, classDAO, assignmentDao, enrollmentDAO);

            System.out.println("Users:");
            System.out.println(userDao.findAll());

            System.out.println("Courses:");
            System.out.println(classDAO.findAll());

            System.out.println("Enrollments:");
            System.out.println(enrollmentDAO.findAll());

            System.out.println("Assignments:");
            System.out.println(assignmentDao.findAll());

            System.out.println("Grades:");
            System.out.println(gradeDao.findAll());

        } catch (SQLException exception) {
            System.err.println("Database error:");
            exception.printStackTrace();
        }
    }
}