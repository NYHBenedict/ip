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

    public String getMessage() {
        return message;
    }

    public boolean shouldExit() {
        return shouldExit;
    }
}
