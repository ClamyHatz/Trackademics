import java.time.LocalDate;

/**
 * View model used by the shared grades TableView.
 *
 * It combines fields from grades, assignments, enrollments, and users so the
 * FXML can display either a student-centered or assignment-centered view.
 */
public class GradeTableRow {
    private final int gradeId;
    private final int enrollmentId;
    private final int assignmentId;
    private final int studentId;
    private final String studentName;
    private final String assignmentName;
    private final LocalDate dueDate;
    private final LocalDate submittedDate;
    private final double pointsPossible;
    private double score;
    private double weight;

    public GradeTableRow(
            int gradeId,
            int enrollmentId,
            int assignmentId,
            int studentId,
            String studentName,
            String assignmentName,
            LocalDate dueDate,
            LocalDate submittedDate,
            double pointsPossible,
            double score,
            double weight) {
        this.gradeId = gradeId;
        this.enrollmentId = enrollmentId;
        this.assignmentId = assignmentId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.assignmentName = assignmentName;
        this.dueDate = dueDate;
        this.submittedDate = submittedDate;
        this.pointsPossible = pointsPossible;
        this.score = score;
        this.weight = weight;
    }

    public int getGradeId() { return gradeId; }

    public int getEnrollmentId() { return enrollmentId; }

    public int getAssignmentId() { return assignmentId; }

    public int getStudentId() { return studentId; }

    public String getStudentName() { return studentName; }

    public String getAssignmentName() { return assignmentName; }

    public LocalDate getDueDate() { return dueDate; }

    public LocalDate getSubmittedDate() { return submittedDate; }

    public double getPointsPossible() { return pointsPossible; }

    public double getScore() { return score; }

    public double getWeight() { return weight; }

    public void setScore(double score) { this.score = score; }

    public void setWeight(double weight) { this.weight = weight; }

    public double getPercent() {
        return pointsPossible <= 0.0 ? 0.0 : (score / pointsPossible) * 100.0;
    }

    public String getScoreFraction() {
        return formatNumber(score) + " / " + formatNumber(pointsPossible);
    }

    private static String formatNumber(double value) {
        if (value == Math.rint(value)) {
            return Long.toString(Math.round(value));
        }
        return String.format("%.2f", value).replaceAll("0+$", "").replaceAll("\\.$", "");
    }
}
