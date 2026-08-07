import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @FXML private ComboBox<ViewMode> viewByComboBox;
    @FXML private ComboBox<String> selectionComboBox;

    @FXML private TableView<GradeRow> gradesTable;
    @FXML private TableColumn<GradeRow, String> assignmentColumn;
    @FXML private TableColumn<GradeRow, String> studentColumn;
    @FXML private TableColumn<GradeRow, Number> studentIdColumn;
    @FXML private TableColumn<GradeRow, LocalDate> dueDateColumn;
    @FXML private TableColumn<GradeRow, String> scoreColumn;
    @FXML private TableColumn<GradeRow, Number> weightColumn;

    private final ObservableList<GradeRow> allRows = FXCollections.observableArrayList();
    private final ObservableList<GradeRow> shownRows = FXCollections.observableArrayList();

    private Stage stage;
    private boolean teacher = true;
    private int currentStudentId = -1;

    @FXML
    private void initialize() {
        assignmentColumn.setCellValueFactory(new PropertyValueFactory<>("assignmentName"));
        studentColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("scoreText"));
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weightPercent"));

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
            EnrollmentDao enrollmentDao = new EnrollmentDao(connection);
            UserDao userDao = new UserDao(connection);

            List<Grade> grades = gradeDao.findAll();

            for (Grade grade : grades) {
                Assignment assignment = assignmentDao.findById(grade.getAssignmentId());
                Enrollment enrollment = enrollmentDao.findById(grade.getEnrollmentId());

                User student = userDao.findById(enrollment.getStudentId());

                GradeRow row = new GradeRow(
                        grade,
                        assignment.getAssignmentId(),
                        assignment.getTitle(),
                        assignment.getDueDate(),
                        assignment.getPointsPossible(),
                        student.getUserId(),
                        student.getUsername()
                );
                allRows.add(row);
            }
            fillSelectionBox();
            showSelectedRows();
            statusLabel.setText("Grades refreshed");

        } catch (SQLException exception) {
            statusLabel.setText("Could not load grades");
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
        studentIdColumn.setVisible(assignmentView);
        assignmentColumn.setVisible(!assignmentView);
        dueDateColumn.setVisible(!assignmentView);

        selectionPromptLabel.setVisible(!studentView);
        selectionPromptLabel.setManaged(!studentView);
        selectionComboBox.setVisible(!studentView);
        selectionComboBox.setManaged(!studentView);

        if (assignmentView) {
            selectionPromptLabel.setText("Assignment");
            tableTitleLabel.setText("Grades by assignment");
        } else if (studentView) {
            tableTitleLabel.setText("My grades");
        } else {
            selectionPromptLabel.setText("Student");
            tableTitleLabel.setText("Grades by student");
        }

        gradesTable.setEditable(teacher);
        scoreColumn.setEditable(teacher);
    }

    private void fillSelectionBox() {
        String oldSelection = selectionComboBox.getValue();
        List<String> choices = new ArrayList<>();
        ViewMode mode = getSelectedMode();

        for (GradeRow row : allRows) {
            String choice = null;

            if (mode == ViewMode.TEACHER_BY_STUDENT) {
                choice = row.getStudentName();
            } else if (mode == ViewMode.TEACHER_BY_ASSIGNMENT) {
                choice = row.getAssignmentName();
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
    }

    private void showSelectedRows() {
        shownRows.clear();

        ViewMode mode = getSelectedMode();
        String selected = selectionComboBox.getValue();

        for (GradeRow row : allRows) {
            boolean show = false;

            if (mode == ViewMode.STUDENT) {
                show = row.getStudentId() == currentStudentId;
            } else if (mode == ViewMode.TEACHER_BY_STUDENT) {
                show = selected == null || row.getStudentName().equals(selected);
            } else if (mode == ViewMode.TEACHER_BY_ASSIGNMENT) {
                show = selected == null || row.getAssignmentName().equals(selected);
            }

            if (show) {
                shownRows.add(row);
            }
        }

        gradesTable.refresh();
    }

    private void saveScore(GradeRow row, String text) {
        if (!teacher) {
            gradesTable.refresh();
            return;
        }

        Double score = readScore(text);

        if (score == null || score < 0 || score > row.getPointsPossible()) {
            statusLabel.setText("Enter a score from 0 to " + row.getPointsPossible());
            gradesTable.refresh();
            return;
        }

        row.getGrade().setGrade(score);

        try (Connection connection = DatabaseConnection.getConnection()) {
            GradeDao gradeDao = new GradeDao(connection);
            gradeDao.update(row.getGrade());
            statusLabel.setText("Score saved");
            refreshView();
        } catch (SQLException exception) {
            statusLabel.setText("Could not save score");
            exception.printStackTrace();
            gradesTable.refresh();
        }
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

    public void configureUser(boolean teacher, int currentStudentId) {
        this.teacher = teacher;
        this.currentStudentId = currentStudentId;

        if (teacher) {
            viewByComboBox.setItems(FXCollections.observableArrayList(
                    ViewMode.TEACHER_BY_STUDENT,
                    ViewMode.TEACHER_BY_ASSIGNMENT
            ));
        } else {
            viewByComboBox.setItems(FXCollections.observableArrayList(
                    ViewMode.STUDENT
            ));
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

        public GradeRow(
                Grade grade,
                int assignmentId,
                String assignmentName,
                LocalDate dueDate,
                double pointsPossible,
                int studentId,
                String studentName
        ) {
            this.grade = grade;
            this.assignmentId = assignmentId;
            this.assignmentName = assignmentName;
            this.dueDate = dueDate;
            this.pointsPossible = pointsPossible;
            this.studentId = studentId;
            this.studentName = studentName;
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
            return grade.getGrade() + " / " + pointsPossible;
        }

        public double getWeightPercent() {
            return grade.getWeight() * 100.0;
        }
    }
}