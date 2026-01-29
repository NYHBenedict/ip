package benbot;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a list of tasks and provides operations to manage them.
 */
public class TaskList {
    private final Task[] tasks = new Task[100];
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
        return tasks[index];
    }

    /**
     * Adds a task to the task list.
     *
     * @param t Task to be added.
     * @return The added task.
     */
    public Task add(Task t) {
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
        Task removed = tasks[index];
        for (int i = index; i < size - 1; i++) {
            tasks[i] = tasks[i + 1];
        }
        tasks[size - 1] = null;
        size--;
        return removed;
    }

    public Task mark(int index) {
        tasks[index].markDone();
        return tasks[index];
    }

    public Task unmark(int index) {
        tasks[index].markNotDone();
        return tasks[index];
    }

    public Task[] rawArray() {
        return tasks;
    }   // for benbot.Storage.save

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
