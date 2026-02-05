package benbot;

import java.util.List;

/**
 * Output contract for BenBot: show messages to the user.
 * Implemented by Ui (console) and GuiUi (GUI).
 */
public interface UiOutput {
    void showLine();
    void showGreeting();
    void showGoodbye();
    void showError(String message);
    void showTasks(TaskList taskList);
    void showMarked(Task task);
    void showUnmarked(Task task);
    void showAdded(Task task, int count);
    void showDeleted(Task task, int count);
    void showFindResults(List<Task> matches);
    void showLoadingError();
    void showHelp(String message);
}
