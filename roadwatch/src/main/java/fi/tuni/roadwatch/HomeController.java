package fi.tuni.roadwatch;

import fi.tuni.roadwatch.SessionData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class HomeController {

    private SessionData sessionData;

    public void setSessionData(SessionData sessionData) {
        this.sessionData = sessionData;
    }
}
