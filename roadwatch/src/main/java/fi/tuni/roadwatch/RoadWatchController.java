package fi.tuni.roadwatch;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class RoadWatchController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}