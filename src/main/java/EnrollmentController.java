import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Runs the enrollment scene.
 *
 * Picking a class on the left loads its roster on the right. Both lists are
 * ObservableLists, so enrolling or dropping someone only has to change the
 * list and the table follows. Every request is checked here first, so the
 * unique constraint on (class_id, student_id) stays a last resort rather
 * than the thing the user sees.
 *
 * @author Ayoung Choi
 * @version 0.1.1
 * @since 8/8/26
 */
public class EnrollmentController implements StageAware {

    @FXML private ListView<Course> classList;
    @FXML private Label rosterLabel;
    @FXML private TableView<Enrollment> rosterTable;
    @FXML private TableColumn<Enrollment, Number> studentColumn;
    @FXML private TableColumn<Enrollment, String> enrolledOnColumn;
    @FXML private TableColumn<Enrollment, String> statusColumn;
    @FXML private TextField studentField;
    @FXML private Label messageLabel;

    private final ClassDAO classDao = new ClassDAO();
    private final EnrollmentDAO enrollmentDao = new EnrollmentDAO();

    private final ObservableList<Course> courses = FXCollections.observableArrayList();
    private final ObservableList<Enrollment> roster = FXCollections.observableArrayList();

    private Stage stage;

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Wires the columns to Enrollment fields, hands both lists to their
     * controls, and loads the classes. JavaFX calls this once the FXML
     * is loaded.
     */
    @FXML
    public void initialize() {
        studentColumn.setCellValueFactory(
                row -> new SimpleIntegerProperty(row.getValue().getStudentId()));
        enrolledOnColumn.setCellValueFactory(
                row -> new SimpleStringProperty(row.getValue().getEnrolledOn().toString()));
        statusColumn.setCellValueFactory(
                row -> new SimpleStringProperty(row.getValue().getStatus().getValue()));

        classList.setItems(courses);
        rosterTable.setItems(roster);

        classList.getSelectionModel().selectedItemProperty().addListener(
                (list, oldCourse, newCourse) -> loadRoster(newCourse));

        loadCourses();
    }

    /**
     * Enrolls the student in the box into whichever class is selected.
     */
    @FXML
    private void handleEnroll() {
        Course course = classList.getSelectionModel().getSelectedItem();
        if (course == null) {
            messageLabel.setText("Pick a class first.");
            return;
        }

        int studentId;
        try {
            studentId = Integer.parseInt(studentField.getText().trim());
        } catch (NumberFormatException e) {
            messageLabel.setText("Student ID has to be a number.");
            return;
        }

        try {
            String problem = whyNotEnroll(studentId);
            if (problem != null) {
                messageLabel.setText(problem);
                return;
            }

            Enrollment enrollment = new Enrollment(course.getClassId(), studentId);
            enrollmentDao.insert(enrollment);
            roster.add(enrollment);
            studentField.clear();
            messageLabel.setText("Enrolled student " + studentId + ".");
        } catch (SQLException e) {
            messageLabel.setText("Couldn't enroll that student: " + e.getMessage());
        }
    }

    /**
     * Drops whoever is selected on the roster. The row stays put and its
     * status changes, so the record of them having been in the class lives on.
     */
    @FXML
    private void handleDrop() {
        Enrollment selected = rosterTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            messageLabel.setText("Pick someone on the roster first.");
            return;
        }
        if (!selected.isActive()) {
            messageLabel.setText("That student has already dropped this class.");
            return;
        }

        try {
            enrollmentDao.drop(selected.getEnrollmentId());
            selected.setStatus(Enrollment.Status.DROPPED);
            rosterTable.refresh();
            messageLabel.setText("Dropped student " + selected.getStudentId() + ".");
        } catch (SQLException e) {
            messageLabel.setText("Couldn't drop that student: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        stage.setScene(SceneFactory.create(SceneType.HOME, stage));
    }

    /**
     * Says what's wrong with an enroll request, if anything. The duplicate
     * check reads the roster list, which already holds every enrollment for
     * the selected class.
     *
     * @param studentId the student joining
     * @return null if the request is fine, otherwise what's wrong with it
     * @throws SQLException if a lookup fails
     */
    private String whyNotEnroll(int studentId) throws SQLException {
        if (studentId <= 0) {
            return "The student ID must be greater than zero.";
        }

        User student = findUser(studentId);
        if (student == null) {
            return "There's no user with ID " + studentId + ".";
        }
        if (!"STUDENT".equals(student.getRole())) {
            return "User " + studentId + " isn't a student.";
        }

        for (Enrollment enrollment : roster) {
            if (enrollment.getStudentId() == studentId) {
                return enrollment.isActive()
                        ? "That student is already in this class."
                        : "That student dropped this class and can't re-enroll.";
            }
        }

        return null;
    }

    /**
     * Looks up a user. UserDao takes a connection rather than opening its
     * own, so this hands it one and closes it afterwards.
     */
    private User findUser(int userId) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            return new UserDao(connection).findById(userId);
        }
    }

    private void loadCourses() {
        try {
            courses.setAll(classDao.findAll());
        } catch (SQLException e) {
            messageLabel.setText("Couldn't load classes: " + e.getMessage());
        }
    }

    private void loadRoster(Course course) {
        if (course == null) {
            roster.clear();
            rosterLabel.setText("Roster");
            return;
        }

        try {
            roster.setAll(enrollmentDao.findByClass(course.getClassId()));
            rosterLabel.setText("Roster for " + course.getClassCode());
        } catch (SQLException e) {
            messageLabel.setText("Couldn't load the roster: " + e.getMessage());
        }
    }
}