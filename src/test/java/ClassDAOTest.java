import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests the ClassDAO methods.
 *
 * These hit the real database file, so each test deletes what it made when
 * it's done. Test course codes start with TEST so they don't get mixed up
 * with real ones.
 *
 * @author Ayoung Choi
 * @version 0.1.0
 * @since 8/2/26
 */
public class ClassDAOTest {

    private final ClassDAO dao = new ClassDAO();

    @Test
    public void insertThenFindByIdGivesBackTheSameCourse() throws SQLException {
        Course course = new Course("TEST 101", "Insert Test", "Fall 2026", 1);
        dao.insert(course);
        Course found = dao.findById(course.getClassId());
        assertNotNull(found);
        assertEquals("TEST 101", found.getClassCode());
        assertEquals("Insert Test", found.getTitle());
        dao.delete(course.getClassId());
    }


    @Test
    public void updateChangesTheTitle() throws SQLException {
        Course course = new Course("TEST 102", "Before", "Fall 2026", 1);
        dao.insert(course);
        course.setTitle("After");
        dao.update(course);
        assertEquals("After", dao.findById(course.getClassId()).getTitle());
        dao.delete(course.getClassId());
    }


    @Test
    public void deleteRemovesTheCourse() throws SQLException {
        Course course = new Course("TEST 103", "Delete Test", "Fall 2026", 1);
        dao.insert(course);
        int id = course.getClassId();
        dao.delete(id);
        assertNull(dao.findById(id));
    }
}