package fi.tuni.roadwatch;

import fi.tuni.roadwatch.SessionData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.*;

import javafx.scene.control.Label;
import org.controlsfx.control.action.Action;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;


public class WeatherController {
    private Integer timeline = 0;

    @FXML
    private ComboBox<String> comboBox;
    @FXML
    private DatePicker startTimeDP;
    @FXML
    private DatePicker endTimeDP;
    @FXML
    private Label label2;
    @FXML
    private Label label4;
    @FXML
    private Label label6;
    @FXML
    private Label label12;

    private Label weatherLabel;

    private ArrayList<WeatherData> wantedWeatherData = new ArrayList<>();
    private TreeMap<Date, Double> temperature_time_map = new TreeMap<Date, Double>();
    private TreeMap<Date, Double> wind_time_map = new TreeMap<Date, Double>();
    private TreeMap<Date, Double> visibility_time_map = new TreeMap<Date, Double>();

    @FXML
    private Label Windlabel;
    @FXML
    private Label  VisibilityLabel ;

    private SessionData sessionData;

    public void setSessionData(SessionData sessionData) {
        this.sessionData = sessionData;
    }


     @FXML
     private void test() {
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

     @FXML
    private void changeTimeColors(Label selected, Label l2, Label l3, Label l4) {
        selected.getStyleClass().add("basicHeadingGreen");
        l2.getStyleClass().removeAll("basicHeadingGreen");
        l2.getStyleClass().add("basicHeading");
        l3.getStyleClass().removeAll("basicHeadingGreen");
        l3.getStyleClass().add("basicHeading");
        l4.getStyleClass().removeAll("basicHeadingGreen");
        l4.getStyleClass().add("basicHeading");
    }


    // Changes date String in to string 8601Format to use in urlstring
    public Date timeAndDateAsDate(String datestring) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(datestring);
    }

    //Test to check if apiread works and gets data to weather controller
    @FXML
    private void calculateData() throws ParserConfigurationException, IOException, ParseException, SAXException {
        Date startTime = Date.from(Instant.from(startTimeDP.getValue().atStartOfDay(ZoneId.systemDefault())));
        Date endTime = Date.from(Instant.from(endTimeDP.getValue().atStartOfDay(ZoneId.systemDefault())));
                //timeAndDateAsDate("2022-11-01T15:40:10Z");
        wantedWeatherData = sessionData.createWeatherData(startTime, endTime);
        WeatherData wantedData = null;

        /*
        // example making chart series
        Date wanted = sessionData.getClosestDate();
        for(WeatherData wd : wantedWeatherData){
            temperature_time_map.put(wd.getDate(), wd.getTemperature());
            wind_time_map.put(wd.getDate(), wd.getTemperature());
        }
        XYChart.Series temperature_time_series = new XYChart.Series();
        for(Map.Entry<Date, Double> entry : temperature_time_map.entrySet()){
            Date key = entry.getKey();
            Double value = entry.getValue();
            temperature_time_series.getData().add(new XYChart.Data(value, key ));
        }
         */

        // Saves the most current lates information to the date closest to current date
        Date wanted = sessionData.getClosestDate();
        for(WeatherData wd : wantedWeatherData){
            if (wd.getDate().toString().equals(wanted.toString())){
                wantedData = wd;
            }
        }

        assert wantedData != null;
        Windlabel.setText(wantedData.getWind() + "m/s");
        VisibilityLabel.setText(wantedData.getCloudiness() + "km") ;
    }
}
