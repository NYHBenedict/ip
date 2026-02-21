package benbot;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Optional;

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
            assert cmd != null : "Parser should always return a non-null Command";

            switch (cmd.keyword) {
            case "todo":
                handleTodo(cmd);
                break;
            case "deadline":
                handleDeadline(cmd);
                break;
            case "event":
                handleEvent(cmd);
                break;
            case "bye":
                ui.showGoodbye();
                return true;
            case "list":
                ui.showTasks(tasks);
                break;
            case "mark":
                handleMark(cmd);
                break;
            case "unmark":
                handleUnmark(cmd);
                break;
            case "delete":
                handleDelete(cmd);
                break;
            case "find":
                handleFind(cmd);
                break;
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

    /** Adds a task, persists to storage, and shows the added message. */
    private void addTaskAndRespond(Task t) throws IOException {
        tasks.add(t);
        storage.save(tasks.rawArray(), tasks.size());
        ui.showAdded(t, tasks.size());
    }

    private void handleTodo(Command cmd) throws BenBotException, IOException {
        if (cmd.rest.isEmpty()) {
            throw new BenBotException("Todo description cannot be empty. Try: todo read book");
        }
        addTaskAndRespond(new Todo(cmd.rest));
    }

    private void handleDeadline(Command cmd) throws BenBotException, IOException {
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
        addTaskAndRespond(new Deadline(parts[0].trim(), byDate));
    }

    private void handleEvent(Command cmd) throws BenBotException, IOException {
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
        validateEventTimes(from, to);
        addTaskAndRespond(new Event(desc, from, to));
    }

    /**
     * If both from and to can be parsed as date or date-time, ensures end is after start.
     * Rejects when from is after to (or equal). Supports yyyy-MM-dd, yyyy-MM-dd HH:mm, dd-MM-yyyy, dd/MM/yyyy.
     * Free-form text (e.g. "Mon 2pm") is not validated.
     */
    private void validateEventTimes(String from, String to) throws BenBotException {
        Optional<LocalDateTime> fromDt = parseEventDateTime(from);
        Optional<LocalDateTime> toDt = parseEventDateTime(to);
        if (fromDt.isPresent() && toDt.isPresent()) {
            if (!toDt.get().isAfter(fromDt.get())) {
                throw new BenBotException("Event end time must be after start time. (from: " + from + " to: " + to + ")");
            }
        }
    }

    private static final DateTimeFormatter EVENT_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    /** e.g. 2025-1-1 4 pm (space before am/pm), case-insensitive */
    private static final DateTimeFormatter EVENT_DATE_TIME_12H_SPACE = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("yyyy-M-d h a")
            .toFormatter();
    /** e.g. 2025-1-1 4pm (no space before am/pm), case-insensitive */
    private static final DateTimeFormatter EVENT_DATE_TIME_12H_NO_SPACE = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("yyyy-M-d ha")
            .toFormatter();
    private static final DateTimeFormatter EVENT_DATE_ISO = DateTimeFormatter.ISO_LOCAL_DATE;
    /** e.g. 2025-1-1 (dashed, single-digit month/day ok) */
    private static final DateTimeFormatter EVENT_DATE_ISO_FLEX = DateTimeFormatter.ofPattern("yyyy-M-d");
    /** Space-separated: yyyy MM dd or yyyy M d (e.g. 2025 02 21 or 2025 2 1). */
    private static final DateTimeFormatter EVENT_DATE_SPACES = DateTimeFormatter.ofPattern("yyyy M d");
    private static final DateTimeFormatter EVENT_DATE_DMY_DASH = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter EVENT_DATE_DMY_SLASH = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Optional<LocalDateTime> parseEventDateTime(String s) {
        if (s == null || s.isEmpty()) return Optional.empty();
        String trimmed = s.trim();
        try {
            return Optional.of(LocalDateTime.parse(trimmed, EVENT_DATE_TIME));
        } catch (DateTimeParseException e) {
            // ignore
        }
        try {
            return Optional.of(LocalDateTime.parse(trimmed, EVENT_DATE_TIME_12H_SPACE));
        } catch (DateTimeParseException e) {
            // ignore
        }
        try {
            return Optional.of(LocalDateTime.parse(trimmed, EVENT_DATE_TIME_12H_NO_SPACE));
        } catch (DateTimeParseException e) {
            // ignore
        }
        for (DateTimeFormatter dateFmt : new DateTimeFormatter[]{
                EVENT_DATE_ISO, EVENT_DATE_ISO_FLEX, EVENT_DATE_SPACES, EVENT_DATE_DMY_DASH, EVENT_DATE_DMY_SLASH}) {
            try {
                return Optional.of(LocalDate.parse(trimmed, dateFmt).atStartOfDay());
            } catch (DateTimeParseException e) {
                // try next
            }
        }
        return Optional.empty();
    }

    private void handleMark(Command cmd) throws BenBotException, IOException {
        int idx = parseIndex(cmd.rest);
        Task t = tasks.mark(idx);
        storage.save(tasks.rawArray(), tasks.size());
        ui.showMarked(t);
    }

    private void handleUnmark(Command cmd) throws BenBotException, IOException {
        int idx = parseIndex(cmd.rest);
        Task t = tasks.unmark(idx);
        storage.save(tasks.rawArray(), tasks.size());
        ui.showUnmarked(t);
    }

    private void handleDelete(Command cmd) throws BenBotException, IOException {
        int idx = parseIndex(cmd.rest);
        Task removed = tasks.delete(idx);
        storage.save(tasks.rawArray(), tasks.size());
        ui.showDeleted(removed, tasks.size());
    }

    private void handleFind(Command cmd) throws BenBotException {
        if (cmd.rest.isEmpty()) {
            throw new BenBotException("Find needs a keyword. Example: find book");
        }
        var matches = tasks.find(cmd.rest);
        ui.showFindResults(matches);
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
        assert idx >= 0 && idx < tasks.size() : "Index must be in valid range after parseIndex";
        return idx;
    }

    public static void main(String[] args) {
        new BenBot(DEFAULT_FILE_PATH).run();
    }
}
