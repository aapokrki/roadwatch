package fi.tuni.roadwatch.controllers;

import fi.tuni.roadwatch.SessionData;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class WeatherController {

    @FXML
    private Label weatherLabel;
    private SessionData sessionData;
    public void setSessionData(SessionData sessionData) {
        this.sessionData = sessionData;
    }
}
