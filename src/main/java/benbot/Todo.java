package benbot;

/**
 * A task with no date or time (e.g. "read book").
 */
public class Todo extends Task {
    /**
     * Creates a todo with the given description.
     *
     * @param description What the task is about.
     */
    public Todo(String description) {
        super(description);
    }
}