public class Task {
    protected final String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public void markDone() {
        isDone = true;
    }

    public void markNotDone() {
        isDone = false;
    }

    protected String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    protected String getTypeIcon() {
        return "T"; // base/unknown
    }

    public String getDescription() { return description; }

    public boolean isDone() { return isDone; }

    public String displayString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description;
    }
}

