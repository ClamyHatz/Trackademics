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

public class GradesController implements StageAware {

    public enum ViewMode {
        STUDENT("My grades"),
        TEACHER_BY_STUDENT("Students"),
        TEACHER_BY_ASSIGNMENT("Assignments");

        private final String label;

        ViewMode(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    @FXML private Label statusLabel;
    @FXML private Label selectionPromptLabel;
    @FXML private Label tableTitleLabel;
    @FXML private Label classSelectionPromptLabel;
    @FXML private Label viewByComboBoxLabel;
    @FXML private Label overallGradeLabel;
    @FXML private Label topGradesLabel;

    @FXML private ComboBox<ViewMode> viewByComboBox;
    @FXML private ComboBox<String> selectionComboBox;
    @FXML private ComboBox<String> userTypeComboBox;
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
    private boolean teacher = true;
    private int currentStudentId = 4;

    @FXML
    private void initialize() {
        assignmentColumn.setCellValueFactory(new PropertyValueFactory<>("assignmentName"));
        studentColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("scoreText"));
        weightColumn.setCellValueFactory(
                new PropertyValueFactory<>("coursePercentText")
        );

        gradesTable.setItems(shownRows);
        gradesTable.setEditable(true);

        scoreColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        scoreColumn.setOnEditCommit(event -> saveScore(
                event.getRowValue(),
                event.getNewValue()
        ));

        viewByComboBox.setItems(FXCollections.observableArrayList(
                ViewMode.TEACHER_BY_STUDENT,
                ViewMode.TEACHER_BY_ASSIGNMENT
        ));
        viewByComboBox.getSelectionModel().selectFirst();
        userTypeComboBox.setItems(FXCollections.observableArrayList(
                "Teacher",
                "Student"
        ));
        userTypeComboBox.getSelectionModel().selectFirst();

        userTypeComboBox.setValue("Teacher");
        viewByComboBox.setValue(ViewMode.TEACHER_BY_STUDENT);

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

        try (Connection connection = DatabaseConnection.getConnection()) {
            GradeDao gradeDao = new GradeDao(connection);
            AssignmentDao assignmentDao = new AssignmentDao(connection);
            EnrollmentDAO enrollmentDao = new EnrollmentDAO();
            UserDao userDao = new UserDao(connection);
            ClassDAO classDao = new ClassDAO();

            List<Assignment> assignments = assignmentDao.findAll();
            List<Enrollment> enrollments = enrollmentDao.findAll();
            List<Grade> grades = gradeDao.findAll();

            Map<Integer, Double> classPointTotals = new HashMap<>();

            for (Assignment assignment : assignments) {
                int classId = assignment.getClassId();

                double currentTotal = classPointTotals.getOrDefault(classId, 0.0);

                classPointTotals.put(
                        classId,
                        currentTotal + assignment.getPointsPossible()
                );
            }

            for (Assignment assignment : assignments) {

                Course course = classDao.findById(assignment.getClassId());

                // Find every student enrolled in this assignment's class
                for (Enrollment enrollment : enrollments) {

                    if (enrollment.getClassId() != assignment.getClassId()) {
                        continue;
                    }

                    User student = userDao.findById(enrollment.getStudentId());

                    // Grade may not exist yet
                    Grade grade = null;

                    for (Grade existingGrade : grades) {
                        if (existingGrade.getAssignmentId() == assignment.getAssignmentId()
                                && existingGrade.getEnrollmentId() == enrollment.getEnrollmentId()) {

                            grade = existingGrade;
                            break;
                        }
                    }

                    double totalClassPoints =
                            classPointTotals.getOrDefault(assignment.getClassId(), 0.0);

                    double coursePercent = 0.0;

                    if (totalClassPoints > 0) {
                        coursePercent =
                                assignment.getPointsPossible()
                                        / totalClassPoints
                                        * 100.0;
                    }

                    GradeRow row = new GradeRow(
                            grade,
                            assignment.getAssignmentId(),
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
            statusLabel.setText("Grades refreshed");

        } catch (SQLException exception) {
            statusLabel.setText("Could not load grades");
            exception.printStackTrace();
        }
    }

    @FXML
    private void handleViewByChanged() {
        changeView();
        fillSelectionBox();
        showSelectedRows();
    }

    @FXML
    private void handleSelectionChanged() {
        showSelectedRows();
    }

    private void changeView() {
        ViewMode mode = getSelectedMode();
        boolean assignmentView = mode == ViewMode.TEACHER_BY_ASSIGNMENT;
        boolean studentView = mode == ViewMode.STUDENT;

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
        classSelectionPromptLabel.setVisible(studentView);
        classSelectionPromptLabel.setManaged(studentView);
        classSelectionComboBox.setVisible(studentView);
        classSelectionComboBox.setManaged(studentView);
        weightColumn.setVisible(studentView);
        overallGradeLabel.setVisible(studentView);
        overallGradeLabel.setManaged(studentView);

        if (assignmentView) {
            selectionPromptLabel.setText("Assignment");
            tableTitleLabel.setText("Grades by assignment");
            topGradesLabel.setText("Grades");
        } else if (studentView) {
            tableTitleLabel.setText("My grades");
            try (Connection connection = DatabaseConnection.getConnection()) {
                UserDao userDao = new UserDao(connection);
                topGradesLabel.setText("Grades For: " + userDao.findById(currentStudentId).getUsername());
            } catch (SQLException exception) {
                statusLabel.setText("Could not load name");
                topGradesLabel.setText("Grades For: null");
                exception.printStackTrace();
            }
        } else {
            selectionPromptLabel.setText("Student");
            tableTitleLabel.setText("Grades by student");
            topGradesLabel.setText("Grades");
        }

        gradesTable.setEditable(teacher);
        scoreColumn.setEditable(teacher);
    }

    private void fillSelectionBox() {
        String oldSelection = selectionComboBox.getValue();
        List<String> choices = new ArrayList<>();
        List<String> classChoices = new ArrayList<>();
        ViewMode mode = getSelectedMode();

        for (GradeRow row : allRows) {
            String choice = null;
            String classChoice = null;

            if (mode == ViewMode.TEACHER_BY_STUDENT) {
                choice = row.getStudentName();
            } else if (mode == ViewMode.TEACHER_BY_ASSIGNMENT) {
                choice = row.getAssignmentName();
            } else if (mode == ViewMode.STUDENT) {
                classChoice = row.getCourseName();
            }

            if (choice != null && !choices.contains(choice)) {
                choices.add(choice);
            }
            if (classChoice != null && !classChoices.contains(classChoice)) {
                classChoices.add(classChoice);
            }
        }

        selectionComboBox.setItems(FXCollections.observableArrayList(choices));
        classSelectionComboBox.setItems(FXCollections.observableArrayList(classChoices));

        if (oldSelection != null && choices.contains(oldSelection)) {
            selectionComboBox.setValue(oldSelection);
        } else if (!choices.isEmpty()) {
            selectionComboBox.getSelectionModel().selectFirst();
        } else if (!classChoices.isEmpty()) {
            classSelectionComboBox.getSelectionModel().selectFirst();
        }
    }

    private void showSelectedRows() {
        shownRows.clear();

        ViewMode mode = getSelectedMode();
        String selected = selectionComboBox.getValue();
        String selectedClass = classSelectionComboBox.getValue();

        for (GradeRow row : allRows) {
            boolean show = false;

            if (mode == ViewMode.STUDENT) {
                show = row.getStudentId() == currentStudentId
                        && (selectedClass == null
                        || row.getCourseName().equals(selectedClass));

            } else if (mode == ViewMode.TEACHER_BY_STUDENT) {
                show = selected == null
                        || row.getStudentName().equals(selected);

            } else if (mode == ViewMode.TEACHER_BY_ASSIGNMENT) {
                show = selected == null
                        || row.getAssignmentName().equals(selected);
            }

            if (show) {
                shownRows.add(row);
            }
        }

        gradesTable.refresh();
        updateOverallGrade();
    }

    private void saveScore(GradeRow row, String text) {
        if (!teacher) {
            gradesTable.refresh();
            return;
        }

        Double score = readScore(text);

        if (score == null || score < 0 || score > row.getPointsPossible()) {
            statusLabel.setText(
                    "Enter a score from 0 to " + row.getPointsPossible()
            );

            gradesTable.refresh();
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            GradeDao gradeDao = new GradeDao(connection);

            if (row.getGrade() == null) {

                Grade grade = new Grade(
                        row.getEnrollmentId(),
                        row.getAssignmentId(),
                        score,
                        row.getCoursePercent() / 100.0
                );

                gradeDao.insert(grade);

            } else {

                row.getGrade().setGrade(score);

                gradeDao.update(row.getGrade());
            }

            statusLabel.setText("Score saved");
            refreshView();

        } catch (SQLException exception) {
            statusLabel.setText("Could not save score");
            exception.printStackTrace();
            gradesTable.refresh();
        }
    }

    private void updateOverallGrade() {
        if (getSelectedMode() != ViewMode.STUDENT) {
            overallGradeLabel.setText("");
            return;
        }

        double pointsEarned = 0.0;
        double pointsPossible = 0.0;

        for (GradeRow row : shownRows) {

            // Ignore assignments that have not been graded yet
            if (row.getGrade() == null) {
                continue;
            }

            pointsEarned += row.getGrade().getGrade();
            pointsPossible += row.getPointsPossible();
        }

        if (pointsPossible == 0.0) {
            overallGradeLabel.setText("Overall Grade: —");
            return;
        }

        double overallPercent =
                pointsEarned / pointsPossible * 100.0;

        overallGradeLabel.setText(
                String.format(
                        "Overall Grade: %.2f%%  (%.1f / %.1f)",
                        overallPercent,
                        pointsEarned,
                        pointsPossible
                )
        );
    }

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
            return null;
        }
    }

    private ViewMode getSelectedMode() {
        ViewMode mode = viewByComboBox.getValue();

        if (mode == null) {
            return ViewMode.TEACHER_BY_STUDENT;
        }

        return mode;
    }

    private String getSelectedUserMode() {
        String mode = userTypeComboBox.getValue();

        if (mode == null) {
            return "Teacher";
        }

        return mode;
    }

    public void handleViewByUserType() {
        String mode = getSelectedUserMode();

        if ("Teacher".equals(mode)) {
            viewByComboBox.setItems(FXCollections.observableArrayList(
                    ViewMode.TEACHER_BY_STUDENT,
                    ViewMode.TEACHER_BY_ASSIGNMENT
            ));
            this.teacher = true;
        } else {
            viewByComboBox.setItems(FXCollections.observableArrayList(
                    ViewMode.STUDENT
            ));
            this.teacher = false;
        }

        viewByComboBox.getSelectionModel().selectFirst();
        changeView();
        refreshView();
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void goHome() {
        stage.setScene(SceneFactory.create(SceneType.HOME, stage));
    }

    /**
     * One row shown in the table. It combines information found through the DAOs.
     */
    public static class GradeRow {
        private final Grade grade;
        private final int assignmentId;
        private final String assignmentName;
        private final LocalDate dueDate;
        private final double pointsPossible;
        private final int studentId;
        private final String studentName;
        private final String courseName;
        private final int enrollmentId;
        private final double coursePercent;

        public GradeRow(
                Grade grade,
                int assignmentId,
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

        public String getScoreText() {
            if (grade == null) {
                return "— / " + pointsPossible;
            }

            return grade.getGrade() + " / " + pointsPossible;
        }

        public double getCoursePercent() {
            return coursePercent;
        }

        public String getCoursePercentText() {
            return String.format("%.2f%%", coursePercent);
        }
    }
}