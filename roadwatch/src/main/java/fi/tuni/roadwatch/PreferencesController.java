package fi.tuni.roadwatch;

import fi.tuni.roadwatch.SessionData;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.util.Objects;

public class PreferencesController {
    private String locationPreference;
    private String weatherPreference;
    private String roadDataPreference;

    @FXML
    private Label locationLabel;
    @FXML
    private ComboBox<String> weatherComboBox;
    @FXML
    private ComboBox<String> conditionTypeComboBox;
    private SessionData sessionData;
    public void setSessionData(SessionData sessionData) {
        this.sessionData = sessionData;
    }

    public void changeWeatherType() {
        weatherPreference = weatherComboBox.getValue();
    }
    public void changeConditionType() {
        roadDataPreference = conditionTypeComboBox.getValue();
    }
}
