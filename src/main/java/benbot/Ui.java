package benbot;

import java.util.Scanner;

public class Ui {
    private static final String LINE = "____________________________________________________________";
    private final Scanner sc = new Scanner(System.in);

    public void showLine() {
        System.out.println(LINE);
    }

    public void showGreeting() {
        showLine();
        System.out.println(" What's good! I'm benbot.BenBot!");
        System.out.println(" What can I do for you?");
        showLine();
        System.out.println();
    }

    public void showGoodbye() {
        showLine();
        System.out.println(" Cya soon!");
        showLine();
    }

    public String readCommand() {
        return sc.hasNextLine() ? sc.nextLine().trim() : null;
    }

    public void showError(String message) {
        showLine();
        System.out.println(" " + message);
        showLine();
    }

    public void showTasks(TaskList taskList) {
        showLine();
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < taskList.size(); i++) {
            System.out.println(" " + (i + 1) + "." + taskList.get(i).displayString());
        }
        showLine();
    }

    public void showMarked(Task task) {
        showLine();
        System.out.println(" Nice! I've marked this task as done:");
        System.out.println(" " + task.displayString());
        showLine();
    }

    public void showUnmarked(Task task) {
        showLine();
        System.out.println(" OK, I've marked this task as not done yet:");
        System.out.println(" " + task.displayString());
        showLine();
    }

    public void showAdded(Task task, int count) {
        showLine();
        System.out.println(" Got it. I've added this task:");
        System.out.println(" " + task.displayString());
        System.out.println(" Now you have " + count + " tasks in the list.");
        showLine();
    }

    public void showDeleted(Task task, int count) {
        showLine();
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + task.displayString());
        System.out.println(" Now you have " + count + " tasks in the list.");
        showLine();
    }

    public void showLoadingError() {
        showError("I couldn't load your saved tasks, starting with an empty list.");
    }
}
