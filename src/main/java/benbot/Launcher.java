package benbot;

import javafx.application.Application;

/**
 * Launcher for BenBot GUI (works around JavaFX classpath issues with Gradle).
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
