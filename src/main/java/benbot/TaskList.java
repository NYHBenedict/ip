package benbot;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a list of tasks and provides operations to manage them.
 */
public class TaskList {
    /** Maximum number of tasks the list can hold. */
    private static final int MAX_CAPACITY = 100;

    private final Task[] tasks = new Task[MAX_CAPACITY];
    private int size = 0;

    public TaskList() {
    }

    public TaskList(java.util.List<Task> loaded) {
        for (Task t : loaded) {
            tasks[size++] = t;
        }
    }

    public int size() {
        return size;
    }

    public Task get(int index) {
        assert index >= 0 && index < size : "Index must be in range [0, size)";
        return tasks[index];
    }

    /**
     * Adds a task to the task list.
     *
     * @param t Task to be added.
     * @return The added task.
     */
    public Task add(Task t) {
        assert t != null : "Task to add must not be null";
        assert size < tasks.length : "Task list capacity must not be exceeded";
        tasks[size++] = t;
        return t;
    }

    /**
     * Deletes the task at the specified index.
     *
     * @param index Index of the task to delete.
     * @return The removed task.
     */
    public Task delete(int index) {
        assert index >= 0 && index < size : "Index must be in range [0, size)";
        Task removed = tasks[index];
        for (int i = index; i < size - 1; i++) {
            tasks[i] = tasks[i + 1];
        }
        tasks[size - 1] = null;
        size--;
        return removed;
    }

    public Task mark(int index) {
        assert index >= 0 && index < size : "Index must be in range [0, size)";
        tasks[index].markDone();
        return tasks[index];
    }

    public Task unmark(int index) {
        assert index >= 0 && index < size : "Index must be in range [0, size)";
        tasks[index].markNotDone();
        return tasks[index];
    }

    /**
     * Returns the backing array of tasks for storage serialization.
     * Callers must only access indices in range [0, size()).
     *
     * @return The internal task array.
     */
    public Task[] rawArray() {
        return tasks;
    }

    /**
     * Finds tasks whose description contains the keyword (case-insensitive).
     *
     * @param keyword The search term.
     * @return List of matching tasks (may be empty).
     */
    public List<Task> find(String keyword) {
        List<Task> matches = new ArrayList<>();
        for (int i = 0; i < size(); i++) {
            Task t = get(i);
            if (t.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                matches.add(t);
            }
        }
        return matches;
    }
}
