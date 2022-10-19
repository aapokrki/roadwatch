package fi.tuni.roadwatch.controllers;

import fi.tuni.roadwatch.SessionData;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class CombineController {

    private SessionData sessionData;

    @FXML
    private Label combineLabel;

    public void setSessionData(SessionData sessionData) {
        this.sessionData = sessionData;
    }

    public void setCoordinates(){
        combineLabel.setText(sessionData.currentCoordinates.toString());
    }
}
