package benbot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Handles loading tasks from and saving tasks to the local file system.
 */
public class Storage {
    /** Delimiter used between fields in the storage file. */
    private static final String STORAGE_DELIMITER = " \\| ";
    /** Value stored when a task is marked done. */
    private static final String DONE_MARKER = "1";
    /** Storage type code for Todo. */
    private static final String TYPE_TODO = "T";
    /** Storage type code for Deadline. */
    private static final String TYPE_DEADLINE = "D";
    /** Storage type code for Event. */
    private static final String TYPE_EVENT = "E";

    private final String filePath;

    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads tasks from the storage file.
     *
     * @return A list of tasks loaded from disk.
     * @throws IOException If an error occurs while reading the file.
     */
    public ArrayList<Task> load() throws IOException {
        ensureFileExists();

        ArrayList<Task> loaded = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                Task t = parseLine(line);
                if (t != null) loaded.add(t);
            }
        }
        return loaded;
    }

    public void save(Task[] tasks, int taskCount) throws IOException {
        assert tasks != null : "Task array must not be null";
        assert taskCount >= 0 && taskCount <= tasks.length : "taskCount must be in range [0, tasks.length]";
        ensureFileExists();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (int i = 0; i < taskCount; i++) {
                assert tasks[i] != null : "Task at index " + i + " must not be null";
                bw.write(encode(tasks[i]));
                bw.newLine();
            }
        }
    }

    private void ensureFileExists() throws IOException {
        File f = new File(filePath);
        File parent = f.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        if (!f.exists()) {
            f.createNewFile();
        }
    }

    private String encode(Task t) {
        assert t != null : "Task to encode must not be null";
        String doneFlag = t.isDone() ? DONE_MARKER : "0";

        if (t instanceof Deadline dead) {
            return TYPE_DEADLINE + " | " + doneFlag + " | " + dead.getDescription() + " | " + dead.getBy();
        }
        if (t instanceof Event event) {
            return TYPE_EVENT + " | " + doneFlag + " | " + event.getDescription()
                    + " | " + event.getFrom() + " | " + event.getTo();
        }
        return TYPE_TODO + " | " + doneFlag + " | " + t.getDescription();
    }

    private Task parseLine(String line) {
        String[] parts = line.split(STORAGE_DELIMITER);
        if (parts.length < 3) {
            return null;
        }

        String type = parts[0].trim();
        boolean isDone = parts[1].trim().equals(DONE_MARKER);
        String desc = parts[2].trim();

        Task t;
        switch (type) {
        case TYPE_TODO:
            t = new Todo(desc);
            break;
        case TYPE_DEADLINE:
            assert parts.length >= 4 : "Deadline line must have at least 4 pipe-separated parts";
            LocalDate by = LocalDate.parse(parts[3].trim());
            t = new Deadline(desc, by);
            break;
        case TYPE_EVENT:
            if (parts.length < 5) {
                return null;
            }
            t = new Event(desc, parts[3].trim(), parts[4].trim());
            break;
        default:
            return null;
        }

        if (isDone) {
            t.markDone();
        }
        return t;
    }
}