import java.time.LocalDate;

/**
 * Holds the information for an assignment.
 *
 * @author Estefan Vicencio
 * @version 0.1.0
 * @since 8/2/2026
 */
public class Assignment {

  private int assignmentId;
  private int classId;
  private String title;
  private String description;
  private LocalDate dueDate;
  private double pointsPossible;
  private String status;

  /**
   * Makes an assignment that is already in the database.
   */
  public Assignment(
      int assignmentId,
      int classId,
      String title,
      String description,
      LocalDate dueDate,
      double pointsPossible,
      String status) {

    this.assignmentId = assignmentId;
    this.classId = classId;
    this.title = title;
    this.description = description;
    this.dueDate = dueDate;
    this.pointsPossible = pointsPossible;
    this.status = status;
  }

  /**
   * Makes a new assignment before it is saved.
   */
  public Assignment(
      int classId,
      String title,
      String description,
      LocalDate dueDate,
      double pointsPossible,
      String status) {

    this.classId = classId;
    this.title = title;
    this.description = description;
    this.dueDate = dueDate;
    this.pointsPossible = pointsPossible;
    this.status = status;
  }
}