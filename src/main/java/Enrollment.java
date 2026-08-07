import java.time.LocalDate;

/**
 * One student in one class.
 *
 * Matches a row of the enrollments table. Status is either 'active' or
 * 'dropped' since dropping keeps the row around instead of deleting it.
 *
 * @author Ayoung Choi
 * @version 0.1.0
 * @since 8/6/26
 */
public class Enrollment {

    /** Status for an enrollment the student is still in. */
    public static final String ACTIVE = "active";

    /** Status for one they dropped. */
    public static final String DROPPED = "dropped";

    private int enrollmentId;
    private int classId;
    private int studentId;
    private LocalDate enrolledOn;
    private String status;

    /**
     * Builds an enrollment that already has an id from the database.
     *
     * @param enrollmentId the id of the row
     * @param classId the class being enrolled in
     * @param studentId the student enrolling
     * @param enrolledOn the date they enrolled
     * @param status 'active' or 'dropped'
     */
    public Enrollment(int enrollmentId, int classId, int studentId,
                      LocalDate enrolledOn, String status) {
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
        this(0, classId, studentId, LocalDate.now(), ACTIVE);
    }

    /**
     * Checks if the student is still in the class.
     *
     * @return true if the status is 'active'
     */
    public boolean isActive() {
        return ACTIVE.equals(status);
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}