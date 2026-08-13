import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

/**
 * Controls the grades.fxml scene
 *
 * @author Lily Keus
 * @version 0.1.0
 * @since 8/10/2026
 */

public class GradesController implements StageAware {

    public enum ViewMode {
        STUDENT("My grades"),
        TEACHER_BY_STUDENT("Students"),
        TEACHER_BY_ASSIGNMENT("Assignments"),
        LOGGED_OUT("Logged Out");

        private final String label;

        /**
         * Creates a view mode with the specified display label.
         *
         * @param label the text displayed for this view mode
         */
        ViewMode(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    @FXML private Label selectionPromptLabel;
    @FXML private Label tableTitleLabel;
    @FXML private Label classSelectionPromptLabel;
    @FXML private Label viewByComboBoxLabel;
    @FXML private Label overallGradeLabel;
    @FXML private Label topGradesLabel;
    @FXML private Label loggedOutLabel;
    @FXML private Label statusLabel;

    @FXML private ComboBox<ViewMode> viewByComboBox;
    @FXML private ComboBox<String> selectionComboBox;
    @FXML private ComboBox<String> classSelectionComboBox;

    @FXML private TableView<GradeRow> gradesTable;
    @FXML private TableColumn<GradeRow, String> assignmentColumn;
    @FXML private TableColumn<GradeRow, String> studentColumn;
    @FXML private TableColumn<GradeRow, LocalDate> dueDateColumn;
    @FXML private TableColumn<GradeRow, String> scoreColumn;
    @FXML private TableColumn<GradeRow, String> weightColumn;

    private final ObservableList<GradeRow> allRows = FXCollections.observableArrayList();
    private final ObservableList<GradeRow> shownRows = FXCollections.observableArrayList();

    private Stage stage;
    private boolean teacher;
    private int currentStudentId;
    private User currentUser;
    private boolean updatingControls = false;

    /**
     * Initializes the grades screen and configures the table, user permissions,
     * view options, and displayed grade data.
     */
    @FXML
    private void initialize() {
        currentUser = Session.getCurrentUser();

        assignmentColumn.setCellValueFactory(new PropertyValueFactory<>("assignmentName"));
        studentColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("scoreText"));
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("coursePercentText"));

        gradesTable.setItems(shownRows);
        gradesTable.setEditable(true);

        scoreColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        scoreColumn.setOnEditCommit(event -> saveScore(event.getRowValue(),
                event.getNewValue()));

        if (currentUser == null) {
            teacher = false;
            changeView();
            return;
        }

        currentStudentId = currentUser.getUserId();
        teacher = "TEACHER".equalsIgnoreCase(currentUser.getRole());

        if (teacher) {
            viewByComboBox.setItems(FXCollections.observableArrayList(ViewMode.TEACHER_BY_STUDENT,
                    ViewMode.TEACHER_BY_ASSIGNMENT));
            viewByComboBox.getSelectionModel().selectFirst();
        }

        changeView();
        refreshView();
    }

    /**
     * Reloads everything from SQLite.
     * The user presses Refresh when they want to see database changes.
     */
    @FXML
    private void refreshView() {
        allRows.clear();
        statusLabel.setText("");

        if (getSelectedMode() == ViewMode.LOGGED_OUT) {
            shownRows.clear();
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            GradeDao gradeDao = new GradeDao(connection);
            AssignmentDao assignmentDao = new AssignmentDao(connection);
            EnrollmentDAO enrollmentDao = new EnrollmentDAO();
            UserDao userDao = new UserDao(connection);
            ClassDAO classDao = new ClassDAO();

            List<Assignment> assignments = assignmentDao.findAll();
            List<Enrollment> enrollments = enrollmentDao.findAll();
            List<Grade> grades = gradeDao.findAll();

            Map<Integer, Course> allCourses = new HashMap<>();
            for (Course course : classDao.findAll()) {
                allCourses.put(course.getClassId(), course);
            }

            List<Assignment> authorizedAssignments = new ArrayList<>();
            Map<Integer, Course> authorizedCourses = new HashMap<>();

            for (Assignment assignment : assignments) {
                Course course = allCourses.get(assignment.getClassId());

                if (course == null || !canAccessCourse(course, enrollments)) {
                    continue;
                }

                authorizedAssignments.add(assignment);
                authorizedCourses.put(course.getClassId(), course);
            }

            Map<Integer, Double> classPointTotals = new HashMap<>();

            for (Assignment assignment : authorizedAssignments) {
                int classId = assignment.getClassId();

                double currentTotal = classPointTotals.getOrDefault(classId, 0.0);

                classPointTotals.put(classId, currentTotal + assignment.getPointsPossible());
            }

            for (Assignment assignment : authorizedAssignments) {
                Course course = authorizedCourses.get(assignment.getClassId());

                for (Enrollment enrollment : enrollments) {
                    if (!enrollment.isActive() || enrollment.getClassId() != assignment.getClassId()) {
                        continue;
                    }

                    if (!teacher && enrollment.getStudentId() != currentStudentId) {
                        continue;
                    }

                    User student = userDao.findById(enrollment.getStudentId());

                    if (student == null) {
                        continue;
                    }

                    Grade grade = null;

                    for (Grade existingGrade : grades) {
                        if (existingGrade.getAssignmentId() == assignment.getAssignmentId()
                                && existingGrade.getEnrollmentId() == enrollment.getEnrollmentId()) {

                            grade = existingGrade;
                            break;
                        }
                    }

                    double totalClassPoints = classPointTotals.getOrDefault(assignment.getClassId(), 0.0);

                    double coursePercent = 0.0;

                    if (totalClassPoints > 0) {
                        coursePercent = assignment.getPointsPossible() / totalClassPoints * 100.0;
                    }

                    GradeRow row = new GradeRow(
                            grade,
                            assignment.getAssignmentId(),
                            assignment.getClassId(),
                            assignment.getTitle(),
                            assignment.getDueDate(),
                            assignment.getPointsPossible(),
                            student.getUserId(),
                            student.getUsername(),
                            course.getTitle(),
                            enrollment.getEnrollmentId(),
                            coursePercent
                    );

                    allRows.add(row);
                }
            }

            fillSelectionBox();
            showSelectedRows();

        } catch (SQLException exception) {
            exception.printStackTrace();
            statusLabel.setText("Error refreshing view: " +  exception.getMessage());
        }
    }

    /**
     * Handles a change to the selected teacher view mode and updates
     * the available selections and displayed grade rows.
     */
    @FXML
    private void handleViewByChanged() {
        if (updatingControls) {
            return;
        }
        changeView();
        fillSelectionBox();
        showSelectedRows();
    }

    /**
     * Handles a change to the selected student or assignment.
     */
    @FXML
    private void handleSelectionChanged() {
        if (updatingControls) {
            return;
        }
        showSelectedRows();
    }

    /**
     * Handles a change to the selected class and updates the available
     * students or assignments.
     */
    @FXML
    private void handleClassSelectionChanged() {
        if (updatingControls) {
            return;
        }

        ViewMode mode = getSelectedMode();

        if (mode == ViewMode.TEACHER_BY_STUDENT || mode == ViewMode.TEACHER_BY_ASSIGNMENT) {
            fillSelectionBox();
        }
        showSelectedRows();
    }

    /**
     * Updates the controls and table columns based on the current view mode.
     */
    private void changeView() {
        ViewMode mode = getSelectedMode();
        statusLabel.setText("");

        boolean assignmentView = mode == ViewMode.TEACHER_BY_ASSIGNMENT;
        boolean studentView = mode == ViewMode.STUDENT;
        boolean loggedOut = mode == ViewMode.LOGGED_OUT;

        if (loggedOut) { // Hides everything if the user is logged out
            studentColumn.setVisible(false);
            assignmentColumn.setVisible(false);
            dueDateColumn.setVisible(false);
            weightColumn.setVisible(false);
            selectionPromptLabel.setVisible(false);
            selectionPromptLabel.setManaged(false);
            selectionComboBox.setVisible(false);
            selectionComboBox.setManaged(false);
            viewByComboBoxLabel.setVisible(false);
            viewByComboBoxLabel.setManaged(false);
            viewByComboBox.setVisible(false);
            viewByComboBox.setManaged(false);
            classSelectionPromptLabel.setVisible(false);
            classSelectionPromptLabel.setManaged(false);
            classSelectionComboBox.setVisible(false);
            classSelectionComboBox.setManaged(false);
            overallGradeLabel.setVisible(false);
            overallGradeLabel.setManaged(false);
            gradesTable.setVisible(false);
            gradesTable.setManaged(false);
            tableTitleLabel.setVisible(false);
            tableTitleLabel.setManaged(false);
            topGradesLabel.setVisible(true);
            topGradesLabel.setManaged(true);
            loggedOutLabel.setVisible(true);
            loggedOutLabel.setManaged(true);
            topGradesLabel.setText("No Grades Shown");

            return;
        }
        // Hides / shows different things depending on view type
        gradesTable.setVisible(true);
        gradesTable.setManaged(true);

        tableTitleLabel.setVisible(true);
        tableTitleLabel.setManaged(true);

        topGradesLabel.setVisible(true);
        topGradesLabel.setManaged(true);

        loggedOutLabel.setVisible(false);
        loggedOutLabel.setManaged(false);

        studentColumn.setVisible(assignmentView);
        assignmentColumn.setVisible(!assignmentView);
        dueDateColumn.setVisible(!assignmentView);

        selectionPromptLabel.setVisible(!studentView);
        selectionPromptLabel.setManaged(!studentView);
        selectionComboBox.setVisible(!studentView);
        selectionComboBox.setManaged(!studentView);

        viewByComboBoxLabel.setVisible(!studentView);
        viewByComboBoxLabel.setManaged(!studentView);
        viewByComboBox.setVisible(!studentView);
        viewByComboBox.setManaged(!studentView);

        boolean showClassSelection = studentView || mode == ViewMode.TEACHER_BY_STUDENT
                        || mode == ViewMode.TEACHER_BY_ASSIGNMENT;

        classSelectionPromptLabel.setVisible(showClassSelection);
        classSelectionPromptLabel.setManaged(showClassSelection);
        classSelectionComboBox.setVisible(showClassSelection);
        classSelectionComboBox.setManaged(showClassSelection);

        classSelectionPromptLabel.setText("Class");

        weightColumn.setVisible(studentView);

        overallGradeLabel.setVisible(studentView);
        overallGradeLabel.setManaged(studentView);

        if (assignmentView) {
            selectionPromptLabel.setText("Assignment");
            tableTitleLabel.setText("Grades by assignment");
            topGradesLabel.setText("Grades of " + viewByComboBox.getValue());

        } else if (studentView) {
            tableTitleLabel.setText("My grades");

            try (Connection connection = DatabaseConnection.getConnection()) {
                UserDao userDao = new UserDao(connection);

                topGradesLabel.setText("Grades For: " + userDao.findById(currentStudentId).getUsername());

            } catch (SQLException exception) {
                topGradesLabel.setText("Grades For: null");
                exception.printStackTrace();
                statusLabel.setText("Error Finding Student: " +  exception.getMessage());
            }

        } else {
            selectionPromptLabel.setText("Student");
            tableTitleLabel.setText("Grades by student");
            topGradesLabel.setText("Grades of Students");
        }

        gradesTable.setEditable(teacher);
        scoreColumn.setEditable(teacher);
    }

    /**
     * Fills the class and selection combo boxes with choices available
     * for the current user and view mode.
     */
    private void fillSelectionBox() {
        updatingControls = true;

        try {
            String oldSelection = selectionComboBox.getValue();
            String oldClassSelection = classSelectionComboBox.getValue();

            List<String> choices = new ArrayList<>();
            List<String> classChoices = new ArrayList<>();
            ViewMode mode = getSelectedMode();

            if (mode == ViewMode.STUDENT || mode == ViewMode.TEACHER_BY_STUDENT
                    || mode == ViewMode.TEACHER_BY_ASSIGNMENT) {

                for (GradeRow row : allRows) {
                    String courseName = row.getCourseName();

                    if (!classChoices.contains(courseName)) {
                        classChoices.add(courseName);
                    }
                }

                classSelectionComboBox.setItems(FXCollections.observableArrayList(classChoices));

                if (oldClassSelection != null && classChoices.contains(oldClassSelection)) {
                    classSelectionComboBox.setValue(oldClassSelection);

                } else if (!classChoices.isEmpty()) {
                    classSelectionComboBox.getSelectionModel().selectFirst();
                }
            } else {
                classSelectionComboBox.getItems().clear();
            }

            String selectedClass = classSelectionComboBox.getValue();

            for (GradeRow row : allRows) {
                String choice = null;

                if (mode == ViewMode.TEACHER_BY_STUDENT) {
                    if (row.getCourseName().equals(selectedClass)) {
                        choice = row.getStudentName();
                    }

                } else if (mode == ViewMode.TEACHER_BY_ASSIGNMENT) {
                    if (row.getCourseName().equals(selectedClass)) {
                        choice = row.getAssignmentName();
                    }
                }

                if (choice != null && !choices.contains(choice)) {
                    choices.add(choice);
                }
            }

            selectionComboBox.setItems(FXCollections.observableArrayList(choices));

            if (oldSelection != null && choices.contains(oldSelection)) {
                selectionComboBox.setValue(oldSelection);

            } else if (!choices.isEmpty()) {
                selectionComboBox.getSelectionModel().selectFirst();
            }

        } finally {
            updatingControls = false;
        }
    }

    /**
     * Filters the available grade rows according to the current class,
     * student, assignment, and view selections.
     */
    private void showSelectedRows() {
        shownRows.clear();

        ViewMode mode = getSelectedMode();
        String selected = selectionComboBox.getValue();
        String selectedClass = classSelectionComboBox.getValue();

        for (GradeRow row : allRows) {
            boolean show = false;

            if (mode == ViewMode.STUDENT) {
                show = row.getStudentId() == currentStudentId && (selectedClass == null
                        || row.getCourseName().equals(selectedClass));

            } else if (mode == ViewMode.TEACHER_BY_STUDENT) {
                boolean correctClass = selectedClass == null || row.getCourseName().equals(selectedClass);
                boolean correctStudent = selected == null || row.getStudentName().equals(selected);

                show = correctClass && correctStudent;

            } else if (mode == ViewMode.TEACHER_BY_ASSIGNMENT) {
                boolean correctClass = selectedClass == null || row.getCourseName().equals(selectedClass);
                boolean correctAssignment = selected == null || row.getAssignmentName().equals(selected);

                show = correctClass && correctAssignment;
            }

            if (show) {
                shownRows.add(row);
            }
        }

        gradesTable.refresh();
        updateOverallGrade();
    }

    /**
     * Checks whether the logged-in user is allowed to access a course.
     *
     * @param course the course being checked
     * @param enrollments the list of enrollments used to check student access
     * @return true if the current user can access the course; false otherwise
     */
    private boolean canAccessCourse(Course course, List<Enrollment> enrollments) {
        if (currentUser == null || course == null) {
            return false;
        }

        if (teacher) {
            return course.getTeacherId() == currentUser.getUserId();
        }

        for (Enrollment enrollment : enrollments) {
            if (enrollment.isActive()
                    && enrollment.getStudentId() == currentStudentId
                    && enrollment.getClassId() == course.getClassId()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks whether the current teacher is allowed to edit a grade row.
     *
     * @param row the grade row being checked
     * @return true if the current teacher owns the course associated with the row;
     *         false otherwise
     * @throws SQLException if a database error occurs while retrieving the course
     */
    private boolean canEditGradeRow(GradeRow row) throws SQLException {
        if (!teacher || currentUser == null || row == null) {
            return false;
        }

        ClassDAO classDao = new ClassDAO();
        Course course = classDao.findById(row.getClassId());

        return course != null && course.getTeacherId() == currentUser.getUserId();
    }

    /**
     * Validates and saves an edited grade score to the database.
     *
     * @param row the grade row being edited
     * @param text the new score entered by the teacher
     */
    private void saveScore(GradeRow row, String text) {
        if (!teacher) {
            gradesTable.refresh();
            return;
        }

        try {
            if (!canEditGradeRow(row)) {
                gradesTable.refresh();
                return;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            gradesTable.refresh();
            statusLabel.setText("Error Saving Score: " +  exception.getMessage());
            return;
        }

        Double score = readScore(text);

        if (score == null || score < 0 || score > row.getPointsPossible()) {
            statusLabel.setText("Entered an invalid value for score.");
            gradesTable.refresh();
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            GradeDao gradeDao = new GradeDao(connection);

            if (row.getGrade() == null) {
                Grade grade = new Grade(row.getEnrollmentId(), row.getAssignmentId(), score,
                        row.getCoursePercent() / 100.0);
                gradeDao.insert(grade);

            } else {
                row.getGrade().setGrade(score);
                gradeDao.update(row.getGrade());
            }

            refreshView();

        } catch (SQLException exception) {
            exception.printStackTrace();
            gradesTable.refresh();
            statusLabel.setText("Error Saving Score: " +  exception.getMessage());
        }
    }

    /**
     * Calculates and displays the student's overall grade using the
     * currently displayed grade rows.
     */
    private void updateOverallGrade() {
        if (getSelectedMode() != ViewMode.STUDENT) {
            overallGradeLabel.setText("");
            return;
        }

        double pointsEarned = 0.0;
        double pointsPossible = 0.0;

        for (GradeRow row : shownRows) {

            if (row.getGrade() != null) {
                pointsEarned += row.getGrade().getGrade();
                pointsPossible += row.getPointsPossible();
            }
        }

        if (pointsPossible == 0.0) {
            overallGradeLabel.setText("Overall Grade: —");
            return;
        }

        double overallPercent = pointsEarned / pointsPossible * 100.0;

        overallGradeLabel.setText(
                String.format("Overall Grade: %.2f%%  (%.1f / %.1f)", overallPercent, pointsEarned, pointsPossible)
        );
    }

    /**
     * Converts score text into a numeric score.
     * If the text contains a slash, only the value before the slash is used.
     *
     * @param text the score text to parse
     * @return the numeric score, or null if the text is empty or invalid
     */
    private Double readScore(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }

        String number = text.trim();

        if (number.contains("/")) {
            number = number.substring(0, number.indexOf('/')).trim();
        }

        try {
            return Double.parseDouble(number);
        } catch (NumberFormatException exception) {
            statusLabel.setText("Error Reading Score: " +  exception.getMessage());
            return null;
        }
    }

    /**
     * Determines the view mode that should be used for the current user.
     *
     * @return the appropriate view mode for the logged-in user, or
     *         LOGGED_OUT if no user is logged in
     */
    private ViewMode getSelectedMode() {
        if (Session.isLoggedIn()) {
            if (!teacher) {
                return ViewMode.STUDENT;
            }

            ViewMode mode = viewByComboBox.getValue();

            if (mode == null) {
                return ViewMode.TEACHER_BY_STUDENT;
            }

            return mode;
        } else {
            return ViewMode.LOGGED_OUT;
        }
    }

    /**
     * Sets the JavaFX stage used by this controller.
     *
     * @param stage the stage associated with this controller
     */
    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Returns the user to the home screen.
     */
    @FXML
    private void goHome() {
        stage.setScene(SceneFactory.create(SceneType.HOME, stage));
    }

    /**
     * Represents one row displayed in the grades table.
     * Combines grade, assignment, student, enrollment, and course information.
     */
    public static class GradeRow {
        private final Grade grade;
        private final int assignmentId;
        private final int classId;
        private final String assignmentName;
        private final LocalDate dueDate;
        private final double pointsPossible;
        private final int studentId;
        private final String studentName;
        private final String courseName;
        private final int enrollmentId;
        private final double coursePercent;

        /**
         * Creates a grade row containing the information needed by the grades table.
         *
         * @param grade the Grade associated with the row, or null if no grade exists
         * @param assignmentId the ID of the assignment
         * @param classId the ID of the class
         * @param assignmentName the name of the assignment
         * @param dueDate the assignment due date
         * @param pointsPossible the maximum points possible for the assignment
         * @param studentId the ID of the student
         * @param studentName the student's username
         * @param courseName the name of the course
         * @param enrollmentId the ID of the student's enrollment
         * @param coursePercent the percentage of the course represented by the assignment
         */
        public GradeRow(
                Grade grade,
                int assignmentId,
                int classId,
                String assignmentName,
                LocalDate dueDate,
                double pointsPossible,
                int studentId,
                String studentName,
                String courseName,
                int enrollmentId,
                double coursePercent
        ) {
            this.grade = grade;
            this.assignmentId = assignmentId;
            this.classId = classId;
            this.assignmentName = assignmentName;
            this.dueDate = dueDate;
            this.pointsPossible = pointsPossible;
            this.studentId = studentId;
            this.studentName = studentName;
            this.courseName = courseName;
            this.enrollmentId = enrollmentId;
            this.coursePercent = coursePercent;
        }

        public int getEnrollmentId() {
            return enrollmentId;
        }

        public String getCourseName() {
            return courseName;
        }

        public Grade getGrade() {
            return grade;
        }

        public int getAssignmentId() {
            return assignmentId;
        }

        public int getClassId() {
            return classId;
        }

        public String getAssignmentName() {
            return assignmentName;
        }

        public LocalDate getDueDate() {
            return dueDate;
        }

        public double getPointsPossible() {
            return pointsPossible;
        }

        public int getStudentId() {
            return studentId;
        }

        public String getStudentName() {
            return studentName;
        }

        public double getCoursePercent() {
            return coursePercent;
        }

        /**
         * Gets the grade formatted for display in the table.
         *
         * @return the score and points possible as display text
         */
        public String getScoreText() {
            if (grade == null) {
                return "— / " + pointsPossible;
            }

            return grade.getGrade() + " / " + pointsPossible;
        }

        /**
         * Gets the course percentage formatted for display.
         *
         * @return the course percentage followed by a percent sign
         */
        public String getCoursePercentText() {
            return String.format("%.2f%%", coursePercent);
        }

    }
}