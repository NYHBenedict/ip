package benbot;

/**
 * Represents a parsed user command: a keyword (e.g. "todo", "list") and the rest of the input.
 */
public class Command {
    /** The command verb, e.g. "todo", "deadline", "list". */
    public final String keyword;
    /** Everything after the first word, trimmed. */
    public final String rest;

    /**
     * Creates a command with the given keyword and arguments.
     *
     * @param keyword The command verb.
     * @param rest    The remainder of the input (arguments).
     */
    public Command(String keyword, String rest) {
        this.keyword = keyword;
        this.rest = rest;
    }
}
