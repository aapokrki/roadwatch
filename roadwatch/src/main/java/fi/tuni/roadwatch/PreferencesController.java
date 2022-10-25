package fi.tuni.roadwatch;

import fi.tuni.roadwatch.SessionData;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class PreferencesController {

    @FXML
    private Label preferencesLabel;
    private SessionData sessionData;
    public void setSessionData(SessionData sessionData) {
        this.sessionData = sessionData;
    }
}
