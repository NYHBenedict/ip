package benbot;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * A dialog box with an ImageView and a Label for the speaker's text.
 */
public class DialogBox extends HBox {

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private static final String USER_BUBBLE_STYLE =
            "-fx-background-color: #DCF8C6;"
            + "-fx-background-radius: 12 12 0 12;"
            + "-fx-padding: 8 12;"
            + "-fx-font-size: 13px;"
            + "-fx-text-fill: black;";

    private static final String BOT_BUBBLE_STYLE =
            "-fx-background-color: #FFFFFF;"
            + "-fx-background-radius: 12 12 12 0;"
            + "-fx-padding: 8 12;"
            + "-fx-font-size: 13px;"
            + "-fx-text-fill: black;";

    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        displayPicture.setImage(img);
        displayPicture.setVisible(false);
        displayPicture.setManaged(false);
        setSpacing(0);
    }

    /**
     * Flips the dialog so the image is on the left and text on the right (for bot).
     */
    private void flip() {
        ObservableList<javafx.scene.Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
    }

    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.dialog.setStyle(USER_BUBBLE_STYLE);
        db.setAlignment(Pos.TOP_RIGHT);
        return db;
    }

    public static DialogBox getDukeDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.dialog.setStyle(BOT_BUBBLE_STYLE);
        db.flip();
        return db;
    }
}
