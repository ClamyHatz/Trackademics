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
}