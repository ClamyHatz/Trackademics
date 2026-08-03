
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

    /**
     * Constructs a grade record with an existing grade identifier.
     *
     * @param gradeId the unique identifier for the grade record
     * @param enrollmentID the identifier of the related enrollment
     * @param assignmentID the identifier of the related assignment
     * @param grade the numeric grade earned
     * @param weight the weight of the grade in an overall calculation
     */
    public Grade(int gradeId, int enrollmentID, int assignmentID, double grade, double weight) {
        this.gradeId = gradeId;
        this.enrollmentID = enrollmentID;
        this.assignmentID = assignmentID;
        this.grade = grade;
        this.weight = weight;
    }

    /**
     * Constructs a new grade record without assigning a grade identifier.
     * This constructor is useful when the identifier will be generated later,
     * such as when the record is inserted into a database.
     *
     * @param enrollmentID the identifier of the related enrollment
     * @param assignmentID the identifier of the related assignment
     * @param grade the numeric grade earned
     * @param weight the weight of the grade in an overall calculation
     */
    public Grade(int enrollmentID, int assignmentID, double grade, double weight) {
        this.enrollmentID = enrollmentID;
        this.assignmentID = assignmentID;
        this.grade = grade;
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Grade grade1 = (Grade) o;
        return getGradeId() == grade1.getGradeId() &&
                getEnrollmentID() == grade1.getEnrollmentID() &&
                getAssignmentID() == grade1.getAssignmentID() &&
                getGrade() == grade1.getGrade() &&
                getWeight() == grade1.getWeight();
    }

    @Override
    public int hashCode() {
        int result = getGradeId();
        result = 31 * result + getEnrollmentID();
        result = 31 * result + getAssignmentID();
        result = 31 * result + Double.hashCode(getGrade());
        result = 31 * result + Double.hashCode(getWeight());
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

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }



}
