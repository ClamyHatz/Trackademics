import java.time.LocalDate;

/**
 * @description: Checks assignment information before it is saved.
 * @author Estefan Vicencio
 * @version 0.1.0
 * @since 8/2/2026
 */
public class AssignmentService {

  /**
   * Checks if the assignment information is valid.
   */
  public String checkAssignment(
      int classId,
      String title,
      LocalDate dueDate,
      double pointsPossible) {

    if (classId <= 0) {
      return "The class ID must be greater than zero.";
    }

    if (title == null || title.isBlank()) {
      return "A title is required.";
    }

    if (dueDate == null) {
      return "A due date is required.";
    }

    if (pointsPossible <= 0) {
      return "Points must be greater than zero.";
    }

    return "";
  }
}