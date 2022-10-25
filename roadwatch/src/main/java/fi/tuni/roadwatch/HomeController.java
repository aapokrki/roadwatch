package fi.tuni.roadwatch;

import fi.tuni.roadwatch.SessionData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class HomeController {

    private SessionData sessionData;

    @FXML
    private Button gambinaButton;

    @FXML
    private Label homeLabel;

    // Example test of a button click that changes a label and stays that way.
    @FXML
    void onGambinaButtonClick(ActionEvent event) {
        homeLabel.setText("Monke");
    }

    public void setSessionData(SessionData sessionData) {
        this.sessionData = sessionData;
    }
}
