package benbot;

/**
 * Result of processing one user command in GUI mode.
 */
public class GetResponseResult {
    private final String message;
    private final boolean shouldExit;

    public GetResponseResult(String message, boolean shouldExit) {
        this.message = message;
        this.shouldExit = shouldExit;
    }

    /**
     * Returns the response text to show in the GUI.
     *
     * @return The bot's reply message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns whether the user said "bye" and the app should close.
     *
     * @return true if the user requested exit.
     */
    public boolean shouldExit() {
        return shouldExit;
    }
}
