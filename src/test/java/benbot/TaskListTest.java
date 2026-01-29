package benbot;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskListTest {

    @Test
    void delete_middle_shiftsLeftAndDecreasesSize() throws Exception {
        TaskList list = new TaskList();
        list.add(new Todo("a"));
        list.add(new Todo("b"));
        list.add(new Todo("c"));

        Task removed = list.delete(1); // delete "b"

        assertEquals("b", removed.getDescription());
        assertEquals(2, list.size());
        assertEquals("a", list.get(0).getDescription());
        assertEquals("c", list.get(1).getDescription()); // shifted into index 1
    }

    @Test
    void delete_last_reducesSizeAndKeepsOrder() throws Exception {
        TaskList list = new TaskList();
        list.add(new Todo("a"));
        list.add(new Todo("b"));

        list.delete(1);

        assertEquals(1, list.size());
        assertEquals("a", list.get(0).getDescription());
    }
}
