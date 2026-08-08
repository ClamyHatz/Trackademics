/**
 * One class offering: a course code and title being taught in a term.
 *
 * This mirrors a row in the classes table. It's named Course because Class is
 * already taken in Java.
 *
 * The DAO builds these when it reads from the database
 * and pulls the values back out when it writes.
 *
 * @author Ayoung Choi
 * @version 0.1.0
 * @since 7/31/26
 */

public class Course {

    private int classId;
    private String classCode;
    private String title;
    private String term;
    private int teacherId;

    /**
     * Creates a course that is already in the database.
     *
     * @param classId    the database id
     * @param classCode  the course code, for example CST 338
     * @param title      the course title
     * @param term       the term it runs in
     * @param teacherId  the id of the teacher who teaches it
     */
    public Course(int classId, String classCode, String title, String term, int teacherId) {
        this.classId = classId;
        this.classCode = classCode;
        this.title = title;
        this.term = term;
        this.teacherId = teacherId;
    }

    /**
     * Creates a new course that has not been saved yet, so it has no id.
     *
     * @param classCode  the course code, for example CST 338
     * @param title      the course title
     * @param term       the term it runs in
     * @param teacherId  the id of the teacher who teaches it
     */
    public Course(String classCode, String title, String term, int teacherId) {
        this(0, classCode, title, term, teacherId);
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    @Override
    public String toString() {
        return classCode + " " + title + " (" + term + ")";
    }
}