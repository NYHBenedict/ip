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

        public String DisplayString() {
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
        public String DisplayString() {
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
        public String DisplayString() {
            return "[" + getTypeIcon() + "][" + getStatusIcon() + "] "
                    + description + " (from: " + from + " to: " + to + ")";
        }
    }

    private static void printAddMessage(Task task, int count) {
        printLine();
        System.out.println(" Got it. I've added this task:");
        System.out.println(" " + task.DisplayString());
        System.out.println(" Now you have " + count + " tasks in the list.");
        printLine();
    }

    private static int parseTaskNumber(String s) {
        return Integer.parseInt(s.trim());
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

            if (input.equals("bye")) {
                printLine();
                System.out.println(" Cya soon! :)");
                printLine();
                break;
            }

            if (input.equals("list")) {
                printLine();
                if (taskCount == 0) {
                    System.out.println("no tasks yet!");
                } else {
                    for (int i = 0; i < taskCount; i++) {
                        System.out.println(" " + (i + 1) + ". " + tasks[i].DisplayString());
                    }
                }
                printLine();
                continue;
            }

            if (input.startsWith("mark ")) {
                int index = parseTaskNumber(input.substring(5)) - 1;
                if (index >= 0 && index < taskCount) {
                    tasks[index].markDone();
                    System.out.println(" Awesome! I've marked this as done: ");
                    System.out.println(" " + tasks[index].DisplayString());
                    printLine();
                }
                continue;
            }

            if (input.startsWith("unmark ")) {
                int index = parseTaskNumber(input.substring(7)) - 1;
                if (index >= 0 && index < taskCount) {
                    tasks[index].markNotDone();
                    System.out.println(" Alroght! I've marked this as not done yet: ");
                    System.out.println(" " + tasks[index].DisplayString());
                    printLine();
                }
                continue;
            }

            if (input.startsWith("todo ")) {
                String desc = input.substring(5).trim();
                tasks[taskCount++] = new Todo(desc);
                printAddMessage(tasks[taskCount - 1], taskCount);
                continue;
            }

            if (input.startsWith("deadline ")) {
                String rest = input.substring(9).trim();
                String[] parts = rest.split(" /by ", 2);
                String desc = parts[0].trim();
                String by = (parts.length == 2) ? parts[1].trim() : "";
                tasks[taskCount++] = new Deadline(desc, by);
                printAddMessage(tasks[taskCount - 1], taskCount);
                continue;
            }

            if (input.startsWith("event ")) {
                String rest = input.substring(6).trim();

                String[] fromSplit = rest.split(" /from ", 2);
                String desc = fromSplit[0].trim();

                String from = "";
                String to = "";

                if (fromSplit.length == 2) {
                    String[] toSplit = fromSplit[1].split(" /to ", 2);
                    from = toSplit[0].trim();
                    if (toSplit.length == 2) {
                        to = toSplit[1].trim();
                    }
                }

                tasks[taskCount++] = new Event(desc, from, to);
                printAddMessage(tasks[taskCount - 1], taskCount);
                continue;
            }

            tasks[taskCount++] = new Todo(input);
            printAddMessage(tasks[taskCount - 1], taskCount);

        }

        sc.close();
    }


}
