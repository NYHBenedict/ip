package benbot;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the main GUI window.
 */
public class MainWindow extends AnchorPane {

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private BenBot benbot;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image benbotImage = new Image(this.getClass().getResourceAsStream("/images/DaDuke.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        // Show greeting on start
        dialogContainer.getChildren().add(
                DialogBox.getDukeDialog("What's good! I'm BenBot!\nWhat can I do for you?", benbotImage));
    }

    /**
     * Injects the BenBot instance.
     */
    public void setBenBot(BenBot b) {
        benbot = b;
    }

    /**
     * Handles user input: sends to BenBot, shows user and bot dialog boxes, clears input.
     * Exits the application when user says bye.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }
        userInput.clear();

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage)
        );

        GetResponseResult result = benbot.getResponse(input);
        dialogContainer.getChildren().add(
                DialogBox.getDukeDialog(result.getMessage(), benbotImage)
        );

        if (result.shouldExit()) {
            Platform.exit();
        }
    }
}
