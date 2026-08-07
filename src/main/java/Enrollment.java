import java.time.LocalDate;

/**
 * One student in one class.
 *
 * Matches a row of the enrollments table. Status is either active or
 * dropped since dropping keeps the row around instead of deleting it.
 *
 * @author Ayoung Choi
 * @version 0.2.0
 * @since 8/6/26
 */
public class Enrollment {

    /** Whether the student is still in the class or has dropped it. */
    public enum Status {
        ACTIVE("active"),
        DROPPED("dropped");

        private final String value;

        Status(String value) {
            this.value = value;
        }

        /**
         * The string that goes in the status column.
         *
         * @return the database value for this status
         */
        public String getValue() {
            return value;
        }

        /**
         * Turns a status column value back into a Status.
         *
         * @param value the string from the database
         * @return the matching status
         * @throws IllegalArgumentException if the value isn't one we know
         */
        public static Status fromValue(String value) {
            for (Status status : values()) {
                if (status.value.equals(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown enrollment status: " + value);
        }
    }

    private int enrollmentId;
    private int classId;
    private int studentId;
    private LocalDate enrolledOn;
    private Status status;

    /**
     * Builds an enrollment that already has an id from the database.
     *
     * @param enrollmentId the id of the row
     * @param classId the class being enrolled in
     * @param studentId the student enrolling
     * @param enrolledOn the date they enrolled
     * @param status active or dropped
     */
    public Enrollment(int enrollmentId, int classId, int studentId,
                      LocalDate enrolledOn, Status status) {
        this.enrollmentId = enrollmentId;
        this.classId = classId;
        this.studentId = studentId;
        this.enrolledOn = enrolledOn;
        this.status = status;
    }

    /**
     * Builds a new enrollment before it's saved. Starts active and dated
     * today, and insert fills in the id.
     *
     * @param classId the class being enrolled in
     * @param studentId the student enrolling
     */
    public Enrollment(int classId, int studentId) {
        this(0, classId, studentId, LocalDate.now(), Status.ACTIVE);
    }

    /**
     * Checks if the student is still in the class.
     *
     * @return true if the status is active
     */
    public boolean isActive() {
        return status == Status.ACTIVE;
    }



    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public LocalDate getEnrolledOn() {
        return enrolledOn;
    }

    public void setEnrolledOn(LocalDate enrolledOn) {
        this.enrolledOn = enrolledOn;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}