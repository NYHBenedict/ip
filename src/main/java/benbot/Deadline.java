package benbot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Deadline extends Task {
    private final LocalDate by;

    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    @Override
    public String displayString() {
        DateTimeFormatter outFmt = DateTimeFormatter.ofPattern("MMM dd yyyy");
        return "[D][" + getStatusIcon() + "] "
                + description + " (by: " + by.format(outFmt) + ")";
    }

    public LocalDate getBy() {
        return by;
    }
}