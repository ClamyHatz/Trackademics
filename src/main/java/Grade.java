
/**
 * Holds grade information.
 *
 * @author Lily Keus
 * @version 0.2.0
 * @since 8/2/2026
 */

public class Grade {
    private int gradeId; // PK
    private int enrollmentId; // FK
    private int assignmentId; // FK
    private double grade;
    private double weight;

    /**
     * Constructs a grade record with an existing grade identifier.
     *
     * @param gradeId the unique identifier for the grade record
     * @param enrollmentId the identifier of the related enrollment
     * @param assignmentId the identifier of the related assignment
     * @param grade the numeric grade earned
     * @param weight the weight of the grade in an overall calculation
     */
    public Grade(int gradeId, int enrollmentId, int assignmentId, double grade, double weight) {
        this.gradeId = gradeId;
        this.enrollmentId = enrollmentId;
        this.assignmentId = assignmentId;
        this.grade = grade;
        this.weight = weight;
    }

    /**
     * Constructs a new grade record without assigning a grade identifier.
     * This constructor is useful when the identifier will be generated later,
     * such as when the record is inserted into a database.
     *
     * @param enrollmentId the identifier of the related enrollment
     * @param assignmentId the identifier of the related assignment
     * @param grade the numeric grade earned
     * @param weight the weight of the grade in an overall calculation
     */
    public Grade(int enrollmentId, int assignmentId, double grade, double weight) {
        this(0, enrollmentId, assignmentId, grade, weight);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Grade other = (Grade) o;

        return gradeId == other.gradeId
                && enrollmentId == other.enrollmentId
                && assignmentId == other.assignmentId
                && Double.compare(grade, other.grade) == 0
                && Double.compare(weight, other.weight) == 0;
    }

    @Override
    public int hashCode() {
        int result = getGradeId();
        result = 31 * result + getEnrollmentId();
        result = 31 * result + getAssignmentId();
        result = 31 * result + Double.hashCode(getGrade());
        result = 31 * result + Double.hashCode(getWeight());
        return result;
    }

    @Override
    public String toString() {
        return "Grade{" +
                "gradeId=" + gradeId +
                ", enrollmentId=" + enrollmentId +
                ", assignmentId=" + assignmentId +
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

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public int getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(int assignmentId) {
        this.assignmentId = assignmentId;
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
