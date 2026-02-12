package benbot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * UI that captures output to a StringBuilder for use by the GUI.
 * Use getAndClearOutput() to read and reset the accumulated message.
 */
public class GuiUi implements UiOutput {
    private static final String LINE = "____________________________________________________________";
    private final StringBuilder output = new StringBuilder();

    public void clear() {
        output.setLength(0);
    }

    public String getAndClearOutput() {
        String result = output.toString().trim();
        clear();
        return result.isEmpty() ? "(no output)" : result;
    }

    public void showLine() {
        output.append(LINE).append("\n");
    }

    public void showGreeting() {
        showLine();
        output.append(" What's good! I'm BenBot!\n");
        output.append(" What can I do for you?\n");
        showLine();
        output.append("\n");
    }

    public void showGoodbye() {
        showLine();
        output.append(" Cya soon!\n");
        showLine();
    }

    public void showError(String message) {
        showLine();
        output.append(" ").append(message).append("\n");
        showLine();
    }

    public void showTasks(TaskList taskList) {
        showLine();
        output.append(" Here are the tasks in your list:\n");
        for (int i = 0; i < taskList.size(); i++) {
            output.append(" ").append(i + 1).append(".").append(taskList.get(i).displayString()).append("\n");
        }
        showLine();
    }

    public void showMarked(Task task) {
        showLine();
        output.append(" Nice! I've marked this task as done:\n");
        output.append(" ").append(task.displayString()).append("\n");
        showLine();
    }

    public void showUnmarked(Task task) {
        showLine();
        output.append(" OK, I've marked this task as not done yet:\n");
        output.append(" ").append(task.displayString()).append("\n");
        showLine();
    }

    public void showAdded(Task task, int count) {
        showLine();
        output.append(" Got it. I've added this task:\n");
        output.append(" ").append(task.displayString()).append("\n");
        output.append(" Now you have ").append(count).append(" tasks in the list.\n");
        showLine();
    }

    public void showDeleted(Task task, int count) {
        showLine();
        output.append(" Noted. I've removed this task:\n");
        output.append("   ").append(task.displayString()).append("\n");
        output.append(" Now you have ").append(count).append(" tasks in the list.\n");
        showLine();
    }

    public void showFindResults(List<Task> matches) {
        showLine();
        output.append(" Here are the matching tasks in your list:\n");
        for (int i = 0; i < matches.size(); i++) {
            output.append(" ").append(i + 1).append(".").append(matches.get(i).displayString()).append("\n");
        }
        showLine();
    }

    public void showLoadingError() {
        showError("I couldn't load your saved tasks, starting with an empty list.");
    }

    @Override
    public void showHelp(String message) {
        showLine();
        output.append(" ").append(message.replace("\n", "\n ")).append("\n");
        showLine();
    }

    @Override
    public void showFreeTimes(List<LocalDate> freeDays, int hoursRequested) {
        showLine();
        if (freeDays.isEmpty()) {
            output.append(" No free days found in the next 60 days (each day has a deadline).\n");
        } else if (hoursRequested > 0 && freeDays.size() == 1) {
            String dateStr = freeDays.get(0).format(DateTimeFormatter.ofPattern("MMM dd yyyy"));
            output.append(" Nearest day with a ").append(hoursRequested).append("-hour free slot: ").append(dateStr).append("\n");
        } else {
            output.append(" Here are the next free days (no deadlines on these days):\n");
            for (int i = 0; i < freeDays.size(); i++) {
                output.append(" ").append(i + 1).append(". ")
                        .append(freeDays.get(i).format(DateTimeFormatter.ofPattern("MMM dd yyyy"))).append("\n");
            }
        }
        showLine();
    }
}
