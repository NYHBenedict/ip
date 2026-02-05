package benbot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * The main entry point of the BenBot application.
 * Coordinates user interaction, task management, and data storage.
 */
public class BenBot {
    private static final String DEFAULT_FILE_PATH = "./data/benbot.txt";

    private static final String HELP_TEXT =
            "Here are the commands you can use:\n"
            + "\n"
            + "  list\n"
            + "    Show all tasks in your list.\n"
            + "    Usage: list\n"
            + "\n"
            + "  todo <description>\n"
            + "    Add a todo task (no date).\n"
            + "    Usage: todo read book\n"
            + "\n"
            + "  deadline <description> /by <date>\n"
            + "    Add a task with a deadline. Date must be yyyy-mm-dd.\n"
            + "    Usage: deadline return book /by 2025-02-10\n"
            + "\n"
            + "  event <description> /from <from> /to <to>\n"
            + "    Add an event with start and end times.\n"
            + "    Usage: event meeting /from Mon 2pm /to 3pm\n"
            + "\n"
            + "  mark <number>\n"
            + "    Mark a task as done (by list number).\n"
            + "    Usage: mark 1\n"
            + "\n"
            + "  unmark <number>\n"
            + "    Mark a task as not done yet.\n"
            + "    Usage: unmark 1\n"
            + "\n"
            + "  delete <number>\n"
            + "    Remove a task from the list.\n"
            + "    Usage: delete 1\n"
            + "\n"
            + "  find <keyword>\n"
            + "    Find tasks whose description contains the keyword.\n"
            + "    Usage: find book\n"
            + "\n"
            + "  bye\n"
            + "    Exit BenBot.\n"
            + "    Usage: bye\n"
            + "\n"
            + "  help\n"
            + "    Show this help message.\n"
            + "    Usage: help";

    private final Storage storage;
    private final TaskList tasks;
    private final UiOutput ui;
    private final Parser parser;

    /**
     * Creates a new BenBot instance using the given file path for storage (console UI).
     *
     * @param filePath Path to the file used for loading and saving tasks.
     */
    public BenBot(String filePath) {
        this(filePath, new Ui());
    }

    /**
     * Creates a new BenBot instance with the given file path and output (e.g. GuiUi for GUI).
     *
     * @param filePath Path to the file used for loading and saving tasks.
     * @param output   Where to send messages (Ui for console, GuiUi for GUI).
     */
    public BenBot(String filePath, UiOutput output) {
        ui = output;
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

    /**
     * Runs the main command loop of the chatbot (console only).
     * Continuously reads user input and executes commands until exit.
     */
    public void run() {
        ui.showGreeting();
        if (!(ui instanceof Ui)) {
            throw new IllegalStateException("run() is for console only; use getResponse() for GUI.");
        }
        Ui consoleUi = (Ui) ui;

        while (true) {
            String input = consoleUi.readCommand();
            if (input == null) break;
            if (processCommand(input)) return;
        }
    }

    /**
     * Processes one user input and returns the response message for GUI.
     * Only valid when BenBot was constructed with a GuiUi.
     *
     * @param input User command string.
     * @return The response message and whether the user said bye.
     */
    public GetResponseResult getResponse(String input) {
        if (!(ui instanceof GuiUi)) {
            throw new IllegalStateException("getResponse() requires BenBot to be constructed with GuiUi.");
        }
        GuiUi guiUi = (GuiUi) ui;
        guiUi.clear();
        boolean shouldExit = processCommand(input);
        return new GetResponseResult(guiUi.getAndClearOutput(), shouldExit);
    }

    /**
     * Processes one command. Returns true if the user said bye.
     */
    private boolean processCommand(String input) {
        try {
            Command cmd = parser.parse(input);

            switch (cmd.keyword) {
            case "todo": {
                if (cmd.rest.isEmpty()) {
                    throw new BenBotException("Todo description cannot be empty. Try: todo read book");
                }
                Task t = tasks.add(new Todo(cmd.rest));
                storage.save(tasks.rawArray(), tasks.size());
                ui.showAdded(t, tasks.size());
                break;
            }

            case "deadline": {
                String[] parts = cmd.rest.split(" /by ", 2);
                if (parts.length < 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
                    throw new BenBotException("Deadline format: deadline <desc> /by yyyy-mm-dd");
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
                String[] fromSplit = cmd.rest.split(" /from ", 2);
                if (fromSplit.length < 2 || fromSplit[0].trim().isEmpty() || fromSplit[1].trim().isEmpty()) {
                    throw new BenBotException("Event format: event <desc> /from <from> /to <to>");
                }
                String desc = fromSplit[0].trim();

                String[] toSplit = fromSplit[1].split(" /to ", 2);
                if (toSplit.length < 2 || toSplit[0].trim().isEmpty() || toSplit[1].trim().isEmpty()) {
                    throw new BenBotException("Event format: event <desc> /from <from> /to <to>");
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
                return true;

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

            case "find": {
                if (cmd.rest.isEmpty()) {
                    throw new BenBotException("Find needs a keyword. Example: find book");
                }
                var matches = tasks.find(cmd.rest);
                ui.showFindResults(matches);
                break;
            }

            case "help":
                ui.showHelp(HELP_TEXT);
                break;

            default:
                throw new BenBotException("I don't understand that command.");
            }
        } catch (BenBotException e) {
            ui.showError(e.getMessage());
        } catch (Exception e) {
            ui.showError("Something went wrong. Please try again.");
        }
        return false;
    }

    private int parseIndex(String s) throws BenBotException {
        if (s.isEmpty()) throw new BenBotException("Please provide a task number.");
        int n;
        try {
            n = Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            throw new BenBotException("Task number must be a number.");
        }
        int idx = n - 1;
        if (idx < 0 || idx >= tasks.size()) throw new BenBotException("Task number out of range.");
        return idx;
    }

    public static void main(String[] args) {
        new BenBot(DEFAULT_FILE_PATH).run();
    }
}
