package fi.tuni.roadwatch;

import fi.tuni.roadwatch.SessionData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

public class WeatherController {
    private Integer timeline = 0;

    @FXML
    private ComboBox<String> comboBox;
    @FXML
    private MenuItem timeline2;
    @FXML
    private MenuItem timeline4;
    @FXML
    private MenuItem timeline6;
    @FXML
    private MenuItem timeline12;
    @FXML
    private Label label2;
    @FXML
    private Label label4;
    @FXML
    private Label label6;
    @FXML
    private Label label12;

    private SessionData sessionData;
    public void setSessionData(SessionData sessionData) {
        this.sessionData = sessionData;
    }


     @FXML
     void test() {
        timeline = Integer.parseInt(comboBox.getValue().replace("H", ""));
        if(timeline == 2) {
            changeTimeColors(label2, label4, label6, label12);
        }
        else if (timeline == 4) {
            changeTimeColors(label4, label2, label6, label12);
        }
        else if (timeline == 6) {
            changeTimeColors(label6, label2, label4, label12);
        } else {
            changeTimeColors(label12, label2, label4, label6);
        }
     }

    void changeTimeColors(Label selected, Label l2, Label l3, Label l4) {
        selected.getStyleClass().add("basicHeadingGreen");
        l2.getStyleClass().removeAll("basicHeadingGreen");
        l2.getStyleClass().add("basicHeading");
        l3.getStyleClass().removeAll("basicHeadingGreen");
        l3.getStyleClass().add("basicHeading");
        l4.getStyleClass().removeAll("basicHeadingGreen");
        l4.getStyleClass().add("basicHeading");
    }
}
