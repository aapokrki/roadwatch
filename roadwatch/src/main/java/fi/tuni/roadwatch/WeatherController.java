package fi.tuni.roadwatch;

import fi.tuni.roadwatch.SessionData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.controlsfx.control.action.Action;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


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

    private Label weatherLabel;


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

    //Onnin testejä
    @FXML
    private Button getweatherdatabutton;
    private ArrayList<WeatherData> wantedWeatherData = new ArrayList<>();
    @FXML
    private Label Windlabel;
    @FXML
    private Label  VisibilityLabel ;
    @FXML
    private Label   temperaturelabel;
    @FXML
    private Label  coordinatesLabel ;
    @FXML
    private Label  dateandTimeLabel ;


    // Changes date String in to string 8601Format to use in urlstring
    public Date timeAndDateAsDate(String datestring) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(datestring);
    }


    //Test to check if apiread works and gets data to weather controller
    @FXML
    private void calculateData(ActionEvent actionEvent) throws ParserConfigurationException, IOException, ParseException, SAXException {
        Date datestarttime = timeAndDateAsDate("2022-11-01T15:40:10Z");
        wantedWeatherData = sessionData.createWeatherData(datestarttime);
        WeatherData wantedData = null;

        // Saves the most current lates information to the date closest to current date
        Date wanted = sessionData.getClosestDate();
        for(WeatherData wd : wantedWeatherData){
            if (wd.getDate().toString().equals(wanted.toString())){
                wantedData = wd;
            }
        }

        assert wantedData != null;
        //Windlabel.setText(String.valueOf(wantedData.getWind()));
        Windlabel.setText(String.valueOf(wantedData.getWind()));
        temperaturelabel.setText(String.valueOf(wantedData.getTemperature()));
        coordinatesLabel.setText(wantedData.getCoordinates());
        dateandTimeLabel.setText(wantedData.getDate().toString());
        VisibilityLabel.setText(String.valueOf(wantedData.getCloudiness())) ;


    }


}
