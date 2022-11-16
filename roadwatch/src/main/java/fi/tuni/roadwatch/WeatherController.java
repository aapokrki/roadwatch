package fi.tuni.roadwatch;

import fi.tuni.roadwatch.SessionData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.chart.LineChart;
import javafx.scene.control.*;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.apache.hc.client5.http.utils.DateUtils;
import org.controlsfx.control.action.Action;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;


public class WeatherController {

    enum Datatype {
        TEMPERATURE,
        WIND,
        VISIBILITY
    }
    private final Integer timeline = 0;
    private Datatype datatype = Datatype.TEMPERATURE;
    private SessionData sessionData;
    private ArrayList<WeatherData> wantedWeatherData = new ArrayList<>();
    private LocalDateTime currentDate = LocalDateTime.now();

    @FXML
    private ComboBox<String> comboBox;
    @FXML
    private Label datatypeLabel;

    // Temperature components
    private TreeMap<Date, Double> temperatureTimeMap = new TreeMap<Date, Double>();
    @FXML
    private AnchorPane temperaturePane;
    @FXML
    private  Label cityLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label nowLabel;
    @FXML
    private Label tomorrowLabel;
    @FXML
    private Label dATomorrowLabel;
    @FXML
    private Button avgTempButton;
    @FXML
    private Button minMaxTempButton;

    // Wind components
    private TreeMap<Date, Double> windTimeMap = new TreeMap<Date, Double>();
    @FXML
    private Label windLabel;
    @FXML
    private LineChart<Integer, Integer> windChart;

    // Visibility components
    private TreeMap<Date, Double> visibilityTimeMap = new TreeMap<Date, Double>();
    @FXML
    private Label  visibilityLabel ;
    @FXML
    private LineChart<Integer, Integer> visibilityChart;

    public void setSessionData(SessionData sessionData) {
        this.sessionData = sessionData;
    }

     @FXML
     private void changeDatatype() {
        if(comboBox.getValue().equalsIgnoreCase(Datatype.TEMPERATURE.toString())) {
            datatype = Datatype.TEMPERATURE;
            datatypeLabel.setText(datatype.toString());
            setTemperature();
        }
        else if(comboBox.getValue().equalsIgnoreCase(Datatype.WIND.toString())) {
            datatype = Datatype.WIND;
            datatypeLabel.setText(datatype.toString());
            temperaturePane.setVisible(false);
        } else {
            datatype = Datatype.VISIBILITY;
            datatypeLabel.setText(datatype.toString());
            temperaturePane.setVisible(false);
        }
     }

    // Changes date String in to string 8601Format to use in urlstring
    public Date timeAndDateAsDate(String datestring) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(datestring);
    }

    //Test to check if apiread works and gets data to weather controller
    @FXML
    private void calculateData() throws ParserConfigurationException, IOException, ParseException, SAXException {
        Date startTime = Date.from(currentDate.atZone(ZoneId.systemDefault()).toInstant());
        var endLDate = LocalDate.parse(startTime.toString()).plusDays(timeline); // TODO: day amount
        Date endTime = Date.from(Date.from(Instant.from(endLDate)).toInstant());
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
        windLabel.setText(wantedData.getWind() + "m/s");
        visibilityLabel.setText(wantedData.getCloudiness() + "km") ;
    }

    // Temperature actions
    @FXML
    private void setTemperature() {
        temperaturePane.setVisible(true);
        dateLabel.setText(currentDate.getDayOfMonth() + "." + currentDate.getMonthValue() + "." + currentDate.getYear());
    }

    @FXML
    private void onNowClick() {
        changeTimeColors(nowLabel, tomorrowLabel, dATomorrowLabel);
    }

    @FXML
    private void onTomorrowClick() {
        changeTimeColors(tomorrowLabel, nowLabel, dATomorrowLabel);
    }

    @FXML
    private void onDATomorrowClick() {
        changeTimeColors(dATomorrowLabel, nowLabel, tomorrowLabel);
    }

    @FXML
    private void changeTimeColors(Label selected, Label l2, Label l3) {
        selected.getStyleClass().add("basicHeadingGreen");
        l2.getStyleClass().removeAll("basicHeadingGreen");
        l2.getStyleClass().add("basicHeading");
        l3.getStyleClass().removeAll("basicHeadingGreen");
        l3.getStyleClass().add("basicHeading");
    }

    @FXML
    private void onAvgBtnClick() {

    }

    @FXML
    private void onMinMaxBtnClick() {

    }
}
