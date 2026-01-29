package benbot;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class Storage {
    private final String filePath;

    public Storage(String filePath) {
        this.filePath = filePath;
    }

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
        ensureFileExists();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (int i = 0; i < taskCount; i++) {
                bw.write(encode(tasks[i]));
                bw.newLine();
            }
        }
    }

    private void ensureFileExists() throws IOException {
        File f = new File(filePath);
        File parent = f.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();
        if (!f.exists()) f.createNewFile();
    }

    private String encode(Task t) {
        String done = t.isDone() ? "1" : "0";

        if (t instanceof Deadline) {
            Deadline d = (Deadline) t;
            return "D | " + done + " | " + d.getDescription() + " | " + d.getBy();
        }
        if (t instanceof Event) {
            Event e = (Event) t;
            return "E | " + done + " | " + e.getDescription() + " | " + e.getFrom() + " | " + e.getTo();
        }
        // default benbot.Todo
        return "T | " + done + " | " + t.getDescription();
    }

    private Task parseLine(String line) {
        String[] parts = line.split(" \\| ");
        if (parts.length < 3) return null;

        String type = parts[0].trim();
        boolean done = parts[1].trim().equals("1");
        String desc = parts[2].trim();

        Task t;
        switch (type) {
            case "T":
                t = new Todo(desc);
                break;
            case "D":
                LocalDate by = LocalDate.parse(parts[3].trim());
                t = new Deadline(desc, by);
                break;
            case "E":
                if (parts.length < 5) return null;
                t = new Event(desc, parts[3].trim(), parts[4].trim());
                break;
            default:
                return null;
        }

        if (done) t.markDone();
        return t;
    }
}