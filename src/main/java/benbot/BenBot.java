package benbot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class BenBot {
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final Parser parser;

    public BenBot(String filePath) {
        ui = new Ui();
        parser = new Parser();
        storage = new Storage(filePath);

        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (Exception e) {
            ui.showLoadingError();
            loadedTasks = new TaskList();
        }
        tasks = loadedTasks;
    }

    public void run() {
        ui.showGreeting();

        while (true) {
            String input = ui.readCommand();
            if (input == null) break;

            try {
                Command cmd = parser.parse(input);

                switch (cmd.keyword) {
                    case "todo": {
                        if (cmd.rest.isEmpty()) {
                            throw new BenBotException("benbot.Todo description cannot be empty. Try: todo read book");
                        }
                        Task t = tasks.add(new Todo(cmd.rest));
                        storage.save(tasks.rawArray(), tasks.size());
                        ui.showAdded(t, tasks.size());
                        break;
                    }

                    case "deadline": {
                        // expects: deadline <desc> /by yyyy-mm-dd
                        String[] parts = cmd.rest.split(" /by ", 2);
                        if (parts.length < 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
                            throw new BenBotException("benbot.Deadline format: deadline <desc> /by yyyy-mm-dd");
                        }

                        LocalDate byDate;
                        try {
                            byDate = LocalDate.parse(parts[1].trim(), DateTimeFormatter.ISO_LOCAL_DATE);
                        } catch (DateTimeParseException e) {
                            throw new BenBotException("Date must be yyyy-mm-dd. Example: 2019-10-15");
                        }

                        Task t = tasks.add(new Deadline(parts[0].trim(), byDate));
                        storage.save(tasks.rawArray(), tasks.size());
                        ui.showAdded(t, tasks.size());
                        break;
                    }

                    case "event": {
                        // expects: event <desc> /from <from> /to <to>
                        String[] fromSplit = cmd.rest.split(" /from ", 2);
                        if (fromSplit.length < 2 || fromSplit[0].trim().isEmpty() || fromSplit[1].trim().isEmpty()) {
                            throw new BenBotException("benbot.Event format: event <desc> /from <from> /to <to>");
                        }
                        String desc = fromSplit[0].trim();

                        String[] toSplit = fromSplit[1].split(" /to ", 2);
                        if (toSplit.length < 2 || toSplit[0].trim().isEmpty() || toSplit[1].trim().isEmpty()) {
                            throw new BenBotException("benbot.Event format: event <desc> /from <from> /to <to>");
                        }
                        String from = toSplit[0].trim();
                        String to = toSplit[1].trim();

                        Task t = tasks.add(new Event(desc, from, to));
                        storage.save(tasks.rawArray(), tasks.size());
                        ui.showAdded(t, tasks.size());
                        break;
                    }


                    case "bye":
                        ui.showGoodbye();
                        return;

                    case "list":
                        ui.showTasks(tasks);
                        break;

                    case "mark": {
                        int idx = parseIndex(cmd.rest);
                        Task t = tasks.mark(idx);
                        storage.save(tasks.rawArray(), tasks.size());
                        ui.showMarked(t);
                        break;
                    }

                    case "unmark": {
                        int idx = parseIndex(cmd.rest);
                        Task t = tasks.unmark(idx);
                        storage.save(tasks.rawArray(), tasks.size());
                        ui.showUnmarked(t);
                        break;
                    }

                    case "delete": {
                        int idx = parseIndex(cmd.rest);
                        Task removed = tasks.delete(idx);
                        storage.save(tasks.rawArray(), tasks.size());
                        ui.showDeleted(removed, tasks.size());
                        break;
                    }

                    // todo/deadline/event: you’ll keep your existing parsing logic here
                    default:
                        throw new BenBotException("I don't understand that command.");
                }

            } catch (BenBotException e) {
                ui.showError(e.getMessage());
            } catch (Exception e) {
                ui.showError("Something went wrong. Please try again.");
            }
        }
    }

    private int parseIndex(String s) throws BenBotException {
        if (s.isEmpty()) throw new BenBotException("Please provide a task number.");
        int n;
        try { n = Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { throw new BenBotException("benbot.Task number must be a number."); }
        int idx = n - 1;
        if (idx < 0 || idx >= tasks.size()) throw new BenBotException("benbot.Task number out of range.");
        return idx;
    }

    public static void main(String[] args) {
        new BenBot("./data/benbot.txt").run();
    }
}
