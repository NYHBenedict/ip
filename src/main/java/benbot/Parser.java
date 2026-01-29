package benbot;

/**
 * Parses user input into commands that can be executed by the chatbot.
 */
public class Parser {
    /**
     * Parses the user input into a command keyword and arguments.
     *
     * @param input Raw user input string.
     * @return Parsed command.
     */
    public Command parse(String input) {
        String trimmed = input.trim();
        if (trimmed.isEmpty()) return new Command("", "");
        String[] parts = trimmed.split(" ", 2);
        String keyword = parts[0];
        String rest = parts.length == 2 ? parts[1].trim() : "";
        return new Command(keyword, rest);
    }
}
