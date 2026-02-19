package benbot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void find_keywordInDescription_returnsMatchingTasks() throws Exception {
        TaskList list = new TaskList();
        list.add(new Todo("read book"));
        list.add(new Todo("buy groceries"));
        list.add(new Todo("read newspaper"));

        var matches = list.find("read");

        assertEquals(2, matches.size());
        assertTrue(matches.get(0).getDescription().contains("read"));
        assertTrue(matches.get(1).getDescription().contains("read"));
    }

    @Test
    void find_caseInsensitive_matchesRegardlessOfCase() throws Exception {
        TaskList list = new TaskList();
        list.add(new Todo("Read Book"));

        var matches = list.find("read");

        assertEquals(1, matches.size());
        assertEquals("Read Book", matches.get(0).getDescription());
    }

    @Test
    void find_noMatch_returnsEmptyList() throws Exception {
        TaskList list = new TaskList();
        list.add(new Todo("read book"));

        var matches = list.find("xyz");

        assertTrue(matches.isEmpty());
    }
}
