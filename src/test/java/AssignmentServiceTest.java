import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * @description: Tests the assignment validation rules.
 * @author Estefan Vicencio
 * @version 0.1.0
 * @since 8/2/2026
 */
public class AssignmentServiceTest {

  /**
   * Tests assignment information that is valid.
   */
  @Test
  public void validAssignmentTest() {
    AssignmentService service = new AssignmentService();

    String message =
        service.checkAssignment(
            1,
            "Java Homework",
            LocalDate.of(2026, 8, 10),
            100.0);

    assertEquals("", message);
  }

  /**
   * Tests a class ID of zero.
   */
  @Test
  public void invalidClassIdTest() {
    AssignmentService service = new AssignmentService();

    String message =
        service.checkAssignment(
            0,
            "Java Homework",
            LocalDate.of(2026, 8, 10),
            100.0);

    assertEquals(
        "The class ID must be greater than zero.",
        message);
  }

  /**
   * Tests an assignment without a title.
   */
  @Test
  public void missingTitleTest() {
    AssignmentService service = new AssignmentService();

    String message =
        service.checkAssignment(
            1,
            "",
            LocalDate.of(2026, 8, 10),
            100.0);

    assertEquals(
        "A title is required.",
        message);
  }

  /**
   * Tests an assignment without a due date.
   */
  @Test
  public void missingDueDateTest() {
    AssignmentService service = new AssignmentService();

    String message =
        service.checkAssignment(
            1,
            "Java Homework",
            null,
            100.0);

    assertEquals(
        "A due date is required.",
        message);
  }

  /**
   * Tests an assignment with zero possible points.
   */
  @Test
  public void zeroPointsTest() {
    AssignmentService service = new AssignmentService();

    String message =
        service.checkAssignment(
            1,
            "Java Homework",
            LocalDate.of(2026, 8, 10),
            0.0);

    assertEquals(
        "Points must be greater than zero.",
        message);
  }
}