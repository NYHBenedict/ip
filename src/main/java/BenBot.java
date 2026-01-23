import java.util.Scanner;

public class BenBot {
    private static final String LINE = "____________________________________________________________";

    public static class Task {
        private final String description;
        private boolean isDone;

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

        public String DisplayString() {
            return "[" + (isDone ? "X" : " ") + "] " + description;
        }
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

            tasks[taskCount] = new Task(input);
            taskCount++;
            printLine();
            System.out.println(" added: " + input);
            printLine();
        }

        sc.close();
    }


}
