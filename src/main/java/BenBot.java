import java.util.Scanner;

public class BenBot {
    private static final String LINE = "____________________________________________________________";

    public static class Task {
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

        public String displayString() {
            return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description;
        }
    }

    public static class Todo extends Task {
        public Todo(String description) {
            super(description);
        }

        @Override
        protected String getTypeIcon() {
            return "T";
        }
    }

    public static class Deadline extends Task {
        private final String by;

        public Deadline(String description, String by) {
            super(description);
            this.by = by;
        }

        @Override
        protected String getTypeIcon() {
            return "D";
        }

        @Override
        public String displayString() {
            return "[" + getTypeIcon() + "][" + getStatusIcon() + "] "
                    + description + " (by: " + by + ")";
        }
    }

    public static class Event extends Task {
        private final String from;
        private final String to;

        public Event(String description, String from, String to) {
            super(description);
            this.from = from;
            this.to = to;
        }

        @Override
        protected String getTypeIcon() {
            return "E";
        }

        @Override
        public String displayString() {
            return "[" + getTypeIcon() + "][" + getStatusIcon() + "] "
                    + description + " (from: " + from + " to: " + to + ")";
        }
    }

    public static class BenBotException extends Exception {
        public BenBotException(String message) {
            super(message);
        }
    }

    private static void printAddMessage(Task task, int count) {
        printLine();
        System.out.println(" Got it. I've added this task:");
        System.out.println(" " + task.displayString());
        System.out.println(" Now you have " + count + " tasks in the list.");
        printLine();
    }

    private static int parseIndex(String input, String cmd, int taskCount) throws BenBotException {
        String rest = input.substring(cmd.length()).trim();
        if (rest.isEmpty()) {
            throw new BenBotException("Please specify a task number. Example: " + cmd + " 2");
        }

        int number;
        try {
            number = Integer.parseInt(rest);
        } catch (NumberFormatException e) {
            throw new BenBotException("Task number must be a number. Example: " + cmd + " 2");
        }

        int idx = number - 1;
        if (idx < 0 || idx >= taskCount) {
            throw new BenBotException("Task number out of range. Use list to see valid numbers.");
        }
        return idx;
    }

    private static void printGreeting() {
        printLine();
        System.out.println("""
                 What's good! I'm BenBot\s
                 What can I do for you today?
                """);
        printLine();
    }

    private static void printLine() {
        System.out.println(LINE);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        Task[] tasks = new Task[100];
        int taskCount = 0;

        printGreeting();

        while (sc.hasNextLine()) {
            String input = sc.nextLine().trim();

            try {
                if (input.equals("bye")) {
                    printLine();
                    System.out.println(" Bye. Hope to see you again soon!");
                    printLine();
                    break;
                }

                if (input.equals("list")) {
                    printLine();
                    System.out.println(" Here are the tasks in your list:");
                    for (int i = 0; i < taskCount; i++) {
                        System.out.println(" " + (i + 1) + "." + tasks[i].displayString());
                    }
                    printLine();
                    continue;
                }

                if (input.startsWith("mark")) {
                    int idx = parseIndex(input, "mark", taskCount);
                    tasks[idx].markDone();
                    printLine();
                    System.out.println(" Nice! I've marked this task as done:");
                    System.out.println(" " + tasks[idx].displayString());
                    printLine();
                    continue;
                }

                if (input.startsWith("unmark")) {
                    int idx = parseIndex(input, "unmark", taskCount);
                    tasks[idx].markNotDone();
                    printLine();
                    System.out.println(" OK, I've marked this task as not done yet:");
                    System.out.println(" " + tasks[idx].displayString());
                    printLine();
                    continue;
                }

                if (input.startsWith("todo")) {
                    String desc = input.length() > 4 ? input.substring(4).trim() : "";
                    if (desc.isEmpty()) {
                        throw new BenBotException("Todo description cannot be empty. Try: todo read book");
                    }
                    tasks[taskCount++] = new Todo(desc);
                    printAddMessage(tasks[taskCount - 1], taskCount);
                    continue;
                }

                if (input.startsWith("deadline")) {
                    String rest = input.length() > 8 ? input.substring(8).trim() : "";
                    if (rest.isEmpty()) {
                        throw new BenBotException("Deadline description cannot be empty. Try: deadline return book" +
                                " /by Sunday");
                    }
                    String[] parts = rest.split(" /by ", 2);
                    if (parts.length < 2 || parts[1].trim().isEmpty()) {
                        throw new BenBotException("Deadline needs a /by part. Example: deadline return book" +
                                " /by Sunday");
                    }
                    tasks[taskCount++] = new Deadline(parts[0].trim(), parts[1].trim());
                    printAddMessage(tasks[taskCount - 1], taskCount);
                    continue;
                }

                if (input.startsWith("event")) {
                    String rest = input.length() > 5 ? input.substring(5).trim() : "";
                    if (rest.isEmpty()) {
                        throw new BenBotException("Event description cannot be empty. Try: event meeting" +
                                " /from 2pm /to 4pm");
                    }

                    String[] fromSplit = rest.split(" /from ", 2);
                    if (fromSplit.length < 2 || fromSplit[1].trim().isEmpty()) {
                        throw new BenBotException("Event needs a /from part. Example: event meeting /from 2pm /to 4pm");
                    }

                    String desc = fromSplit[0].trim();
                    String[] toSplit = fromSplit[1].split(" /to ", 2);
                    if (toSplit.length < 2 || toSplit[1].trim().isEmpty()) {
                        throw new BenBotException("Event needs a /to part. Example: event meeting /from 2pm /to 4pm");
                    }

                    String from = toSplit[0].trim();
                    String to = toSplit[1].trim();

                    tasks[taskCount++] = new Event(desc, from, to);
                    printAddMessage(tasks[taskCount - 1], taskCount);
                    continue;
                }

                if (input.startsWith("delete")) {
                    int idx = parseIndex(input, "delete", taskCount); // 0-based index

                    Task removed = tasks[idx];

                    // shift left to fill the gap
                    for (int i = idx; i < taskCount - 1; i++) {
                        tasks[i] = tasks[i + 1];
                    }
                    tasks[taskCount - 1] = null;
                    taskCount--;

                    printLine();
                    System.out.println(" Noted. I've removed this task:");
                    System.out.println("   " + removed.displayString());
                    System.out.println(" Now you have " + taskCount + " tasks in the list.");
                    printLine();
                    continue;
                }

                // Unknown command (use of AI)
                throw new BenBotException("I don't understand that command. Try: todo / deadline /" +
                        " event / list / mark / unmark / bye");

            } catch (BenBotException e) {
                printLine();
                System.out.println(" " + e.getMessage());
                printLine();
            } catch (Exception e) {
                // Safety net: prevents program crash (use of AI)
                printLine();
                System.out.println(" Something went wrong. Please try again.");
                printLine();
            }

        }

        sc.close();
    }


}
