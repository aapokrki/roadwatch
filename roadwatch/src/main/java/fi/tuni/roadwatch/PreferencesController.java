package fi.tuni.roadwatch;

import fi.tuni.roadwatch.SessionData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.util.Objects;

public class PreferencesController {
    private String locationPreference;
    private String weatherPreference;
    private String roadDataPreference;
    private String maintenancePreference;

    // TODO: Aapo linkkaa karttaan tai sit poistan.
    @FXML
    private Label locationLabel;
    @FXML
    private ComboBox<String> weatherComboBox;
    @FXML
    private ComboBox<String> conditionTypeComboBox;
    @FXML
    public ComboBox<String> maintenanceTaskCombobox;
    private SessionData sessionData;

    public void setSessionData(SessionData sessionData) {
        this.sessionData = sessionData;

        ObservableList<String> taskTypesObservable= FXCollections.observableArrayList(sessionData.taskTypes);
        taskTypesObservable.add(0,"ALL");
        maintenanceTaskCombobox.setItems(taskTypesObservable);
    }

    public void setPreferences() {
        weatherPreference = weatherComboBox.getValue();
        roadDataPreference = conditionTypeComboBox.getValue();
        maintenancePreference = maintenanceTaskCombobox.getValue();
    }
}
