
/**
 * Holds grade information.
 *
 * @author Lily Keus
 * @version 0.1.0
 * @since 8/2/2026
 */

public class Grade {
    private int gradeId; // PK
    private int enrollmentID; // FK
    private int assignmentID; // FK
    private double grade;
    private double weight;

    public Grade(int gradeId, int enrollmentID, int assignmentID, int grade, int weight) {
        this.gradeId = gradeId;
        this.enrollmentID = enrollmentID;
        this.assignmentID = assignmentID;
        this.grade = grade;
        this.weight = weight;
    }

    public Grade(int enrollmentID, int assignmentID, int grade, int weight) {
        this.enrollmentID = enrollmentID;
        this.assignmentID = assignmentID;
        this.grade = grade;
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Grade grade1 = (Grade) o;
        return getGradeId() == grade1.getGradeId() && getEnrollmentID() == grade1.getEnrollmentID() && getAssignmentID() == grade1.getAssignmentID() && getGrade() == grade1.getGrade() && getWeight() == grade1.getWeight();
    }

    @Override
    public int hashCode() {
        int result = getGradeId();
        result = 31 * result + getEnrollmentID();
        result = 31 * result + getAssignmentID();
        result = 31 * result + getGrade();
        result = 31 * result + getWeight();
        return result;
    }

    @Override
    public String toString() {
        return "Grade{" +
                "gradeId=" + gradeId +
                ", enrollmentID=" + enrollmentID +
                ", assignmentID=" + assignmentID +
                ", grade=" + grade +
                ", weight=" + weight +
                '}';
    }

    public int getGradeId() {
        return gradeId;
    }

    public void setGradeId(int gradeId) {
        this.gradeId = gradeId;
    }

    public int getEnrollmentID() {
        return enrollmentID;
    }

    public void setEnrollmentID(int enrollmentID) {
        this.enrollmentID = enrollmentID;
    }

    public int getAssignmentID() {
        return assignmentID;
    }

    public void setAssignmentID(int assignmentID) {
        this.assignmentID = assignmentID;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }



}
