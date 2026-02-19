package benbot;

/**
 * Exception thrown when the user's input is invalid or a command cannot be executed.
 * The message is shown to the user (e.g. in console or GUI).
 */
public class BenBotException extends Exception {
    /**
     * Creates an exception with a user-facing message.
     *
     * @param message Message to display to the user.
     */
    public BenBotException(String message) {
        super(message);
    }
}
