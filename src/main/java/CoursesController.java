import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * Runs the courses scene.
 *
 * The table is backed by an ObservableList, so adding or removing a course
 * from that list is enough to redraw the table. Every write goes to ClassDAO
 * first and the list only changes if the database call worked.
 *
 * @author Ayoung Choi
 * @version 0.1.0
 * @since 8/6/26
 */
public class CoursesController implements StageAware {

    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course, String> codeColumn;
    @FXML private TableColumn<Course, String> titleColumn;
    @FXML private TableColumn<Course, String> termColumn;
    @FXML private TableColumn<Course, Number> teacherColumn;

    @FXML private TextField codeField;
    @FXML private TextField titleField;
    @FXML private TextField termField;
    @FXML private TextField teacherField;
    @FXML private Label messageLabel;

    private final ClassDAO dao = new ClassDAO();
    private final ObservableList<Course> courses = FXCollections.observableArrayList();

    private Stage stage;

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Wires the columns to Course fields, hands the table its list, and
     * loads what's already in the database. JavaFX calls this once the
     * FXML is loaded.
     */
    @FXML
    public void initialize() {
        codeColumn.setCellValueFactory(
                row -> new SimpleStringProperty(row.getValue().getClassCode()));
        titleColumn.setCellValueFactory(
                row -> new SimpleStringProperty(row.getValue().getTitle()));
        termColumn.setCellValueFactory(
                row -> new SimpleStringProperty(row.getValue().getTerm()));
        teacherColumn.setCellValueFactory(
                row -> new SimpleIntegerProperty(row.getValue().getTeacherId()));

        courseTable.setItems(courses);

        courseTable.getSelectionModel().selectedItemProperty().addListener(
                (list, oldCourse, newCourse) -> fillForm(newCourse));

        loadCourses();
    }

    /**
     * Reads what the form has and saves it as a new course.
     */
    @FXML
    private void handleAdd() {
        Course course = readForm(0);
        if (course == null) {
            return;
        }

        try {
            dao.insert(course);
            courses.add(course);
            clearForm();
            messageLabel.setText("Added " + course.getClassCode() + ".");
        } catch (SQLException e) {
            messageLabel.setText("Couldn't add that course: " + e.getMessage());
        }
    }

    /**
     * Applies the form to whichever course is selected.
     */
    @FXML
    private void handleUpdate() {
        Course selected = courseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            messageLabel.setText("Pick a course to update first.");
            return;
        }

        Course edited = readForm(selected.getClassId());
        if (edited == null) {
            return;
        }

        try {
            dao.update(edited);
            courses.set(courses.indexOf(selected), edited);
            messageLabel.setText("Updated " + edited.getClassCode() + ".");
        } catch (SQLException e) {
            messageLabel.setText("Couldn't update that course: " + e.getMessage());
        }
    }

    /**
     * Deletes whichever course is selected.
     */
    @FXML
    private void handleDelete() {
        Course selected = courseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            messageLabel.setText("Pick a course to delete first.");
            return;
        }

        try {
            dao.delete(selected.getClassId());
            courses.remove(selected);
            clearForm();
            messageLabel.setText("Deleted " + selected.getClassCode() + ".");
        } catch (SQLException e) {
            messageLabel.setText("Couldn't delete that course: " + e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        courseTable.getSelectionModel().clearSelection();
        clearForm();
        messageLabel.setText("");
    }

    @FXML
    private void handleBack() {
        stage.setScene(SceneFactory.create(SceneType.HOME, stage));
    }

    /**
     * Pulls every course out of the database into the list the table watches.
     */
    private void loadCourses() {
        try {
            courses.setAll(dao.findAll());
        } catch (SQLException e) {
            messageLabel.setText("Couldn't load courses: " + e.getMessage());
        }
    }

    /**
     * Builds a Course out of the form, or returns null and says what's wrong.
     *
     * @param classId the id to give the course, 0 for a new one
     * @return the course, or null if the form isn't filled in properly
     */
    private Course readForm(int classId) {
        String code = codeField.getText().trim();
        String title = titleField.getText().trim();
        String term = termField.getText().trim();
        String teacher = teacherField.getText().trim();

        if (code.isEmpty() || title.isEmpty() || term.isEmpty() || teacher.isEmpty()) {
            messageLabel.setText("Every field needs something in it.");
            return null;
        }

        int teacherId;
        try {
            teacherId = Integer.parseInt(teacher);
        } catch (NumberFormatException e) {
            messageLabel.setText("Teacher ID has to be a number.");
            return null;
        }

        return new Course(classId, code, title, term, teacherId);
    }

    private void fillForm(Course course) {
        if (course == null) {
            return;
        }
        codeField.setText(course.getClassCode());
        titleField.setText(course.getTitle());
        termField.setText(course.getTerm());
        teacherField.setText(String.valueOf(course.getTeacherId()));
    }

    private void clearForm() {
        codeField.clear();
        titleField.clear();
        termField.clear();
        teacherField.clear();
    }
}