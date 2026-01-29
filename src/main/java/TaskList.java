public class TaskList {
    private final Task[] tasks = new Task[100];
    private int size = 0;

    public TaskList() {}

    public TaskList(java.util.List<Task> loaded) {
        for (Task t : loaded) {
            tasks[size++] = t;
        }
    }

    public int size() { return size; }
    public Task get(int index) { return tasks[index]; }

    public Task add(Task t) {
        tasks[size++] = t;
        return t;
    }

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

    public Task[] rawArray() { return tasks; }   // for Storage.save
}
