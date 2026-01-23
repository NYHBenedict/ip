import java.util.Scanner;

public class BenBot {
    private static final String LINE = "____________________________________________________________";

    private static void printGreeting() {
        printLine();
        System.out.println("""
                 What's good! I'm BenBot\s
                 What can I do for you today?\s
                """);
        printLine();
    }

    private static void printLine() {
        System.out.println(LINE);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        String[] tasks = new String[100];
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
                        System.out.println(" " + (i + 1) + ". " + tasks[i]);
                    }
                }
                printLine();
                continue;
            }

            if (taskCount < 100) {
                tasks[taskCount] = input;
                taskCount++;
                printLine();
                System.out.println(" added: " + input);
                printLine();
            } else {
                printLine();
                System.out.println(" Too many tasks! ");
                printLine();
            }
        }

        sc.close();
    }


}
