package fi.tuni.roadwatch;

import javafx.fxml.FXML;

import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;


public class WeatherController {

    enum Datatype {
        TEMPERATURE,
        WIND,
        VISIBILITY
    }
    private final Integer timeline = 0;
    private Datatype datatype;
    private SessionData sessionData;
    private LocalDateTime currentDate = LocalDateTime.now();

    @FXML
    private ComboBox<String> comboBox;
    @FXML
    private Label datatypeLabel;
    @FXML
    private  DatePicker startDatePicker;
    @FXML
    private  DatePicker endDatePicker;

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


    // ONNIN TESTI LABELIT TEE NÄISTÄ HIENOI RONJA
    private Date savedDate;

    @FXML
    private Label maxlabel;
    @FXML
    private Label minlabel;
    @FXML
    private Label avglabel;

    @FXML
    private DatePicker chooseDay;

    @FXML
    private Label tempRightNowLabel;
    @FXML
    private Label tempMaxLabel;
    @FXML
    private Label tempMinLabel;


    // Wind components
    @FXML
    private AnchorPane windPane;
    @FXML
    private Label windLabel;
    @FXML
    protected LineChart<String, Double> windChart;
    @FXML
    private CategoryAxis xAxisWind;
    @FXML
    private NumberAxis yAxisWind;
    @FXML
    private Label errorLabel;

    // Visibility components
    @FXML
    private AnchorPane visibilityPane;
    @FXML
    private Label  visibilityLabel ;
    @FXML
    private LineChart<String, Double> visibilityChart;
    @FXML
    private CategoryAxis xAxisVisibility;
    @FXML
    private NumberAxis yAxisVisibility;

    public void setSessionData(SessionData sessionData) {
        this.sessionData = sessionData;
        datatype = Datatype.TEMPERATURE;
        // TODO: enemmän enum tyyppien hyödynnystä?
        visibilityPane.setVisible(false);
        windPane.setVisible(false);
    }

    @FXML
    private void changeDatatype() {
        if(comboBox.getValue().equalsIgnoreCase(Datatype.TEMPERATURE.toString())) {
            datatype = Datatype.TEMPERATURE;
            datatypeLabel.setText(datatype.toString());
            setTemperature();
            visibilityPane.setVisible(false);
            windPane.setVisible(false);
            startDatePicker.setVisible(false);
            endDatePicker.setVisible(false);
        }
        else if(comboBox.getValue().equalsIgnoreCase(Datatype.WIND.toString())) {
            datatype = Datatype.WIND;
            datatypeLabel.setText(datatype.toString());
            temperaturePane.setVisible(false);
            visibilityPane.setVisible(false);
            windPane.setVisible(true);
            windChart.setVisible(true);
            startDatePicker.setVisible(true);
            endDatePicker.setVisible(true);
        } else {
            datatype = Datatype.VISIBILITY;
            datatypeLabel.setText(datatype.toString());
            temperaturePane.setVisible(false);
            visibilityPane.setVisible(true);
            windPane.setVisible(false);
            visibilityChart.setVisible(true);
            startDatePicker.setVisible(true);
            endDatePicker.setVisible(true);
        }
    }

    // Changes date String in to string 8601Format to use in urlstring
    public Date timeAndDateAsDate(String datestring) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(datestring);
    }

    @FXML
    private void calculateVisibilityData() throws ParseException, ParserConfigurationException, IOException, SAXException {
        if(sessionData.currentCoordinates == null) {
            errorLabel.setText("Choose coordinates!");
        } else {
            visibilityChart.setAnimated(false);
            visibilityChart.getData().clear();
            // Gets the date right now and adds a few seconds to get forecast from API
            // Also getting the date and the end of day
            Date startTime = timeAndDateAsDate("2022-11-03T00:40:10Z");
            Date endTime = timeAndDateAsDate("2022-11-04T20:15:10Z");
            sessionData.createWeatherData(startTime, endTime);
            XYChart.Series<String, Double> visibilitySeries = sessionData.createGraphSeries("VISIBILITY");

            if(visibilitySeries != null){
                visibilitySeries.setName("Visibility");
                visibilityChart.getData().add(visibilitySeries);
                windChart.setVisible(false);
                xAxisVisibility.setLabel("Time");
                yAxisVisibility.setLabel("km");
            }
        }
    }

    @FXML
    private void calculateWindData() throws ParserConfigurationException, IOException, ParseException, SAXException {
        windChart.setVisible(true);
        if(sessionData.currentCoordinates == null) {
            errorLabel.setText("Choose coordinates!");
        } else {
            windChart.setAnimated(false);
            windChart.getData().clear();
            // Gets the date right now and adds a few seconds to get forecast from API
            // Also getting the date and the end of day
            errorLabel.setText("");
            Calendar cal = Calendar.getInstance();
            long timeInSecs = cal.getTimeInMillis();
            Date startTime = new Date(timeInSecs + (10*60*10));
            Date endTime = timeAndDateAsDate(LocalDate.now().atTime(23, 59, 59) + "Z");

            // Creates weather data according to new start and end time to sessionData
            sessionData.createWeatherData(startTime, endTime);

            XYChart.Series<String, Double> windSeries = sessionData.createGraphSeries("WIND");
            if(windSeries != null){
                windSeries.setName("Wind");

                windChart.getData().add(windSeries);
                visibilityChart.setVisible(false);
                xAxisWind.setLabel("Time");
                yAxisWind.setLabel("m/s");
            }
            else{
                errorLabel.setText("No Data");
            }

        }
    }

    @FXML
    private void saveDate() throws ParseException {
        LocalDate localDate = chooseDay.getValue();
        Instant instant = Instant.from(localDate.atStartOfDay(ZoneOffset.UTC));
        savedDate = Date.from(instant);

    }

    // Temperature actions
    @FXML
    private void setTemperature() {
        temperaturePane.setVisible(true);
        dateLabel.setText(currentDate.getDayOfMonth() + "." + currentDate.getMonthValue() + "." + currentDate.getYear());
    }

    // Changes temperature labels according to which day you want to see
    private void changeTempLabels(String nowOrNot){
        double min = sessionData.WantedWeatherData.get(0).getTemperature();
        double max = sessionData.WantedWeatherData.get(0).getTemperature();
        tempRightNowLabel.setVisible(false);

        //Sets min max and now labels according to newest weather information
        for(WeatherData wd : sessionData.WantedWeatherData){
            if(wd.getTemperature() <= min){
                min = wd.getTemperature();
            }
            if(wd.getTemperature() >= max){
                max = wd.getTemperature();
            }
            if(Objects.equals(wd.getDate(), sessionData.getClosestDate())){
                if(nowOrNot.equals("now")){
                    tempRightNowLabel.setVisible(true);
                    tempRightNowLabel.setText(String.valueOf(wd.getTemperature()));
                }

            }
        }
        tempMinLabel.setText(String.valueOf(min));
        tempMaxLabel.setText(String.valueOf(max));
    }

    // Helper function to set the time of day to 00:00:00, also can add days to date
    private Date trimToStart(Date date, int Days){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, Days);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);

        return cal.getTime();
    }

    // Helper function to set the time of day to 23:59:59, also can add days to date
    private Date trimToEnd(Date date, int Days){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, Days);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE,59);
        cal.set(Calendar.SECOND,59);

        return cal.getTime();
    }

    @FXML
    private void onNowClick() throws ParseException, ParserConfigurationException, IOException, SAXException {
        changeTimeColors(nowLabel, tomorrowLabel, dATomorrowLabel);

        // Gets the date right now and adds a few seconds to get forecast from API
        // Also getting the date and the end of day
        Calendar cal = Calendar.getInstance();
        long timeInSecs = cal.getTimeInMillis();
        Date startTime = new Date(timeInSecs + (10*60*10));
        Date endTime = timeAndDateAsDate(LocalDate.now().atTime(23, 59, 59) + "Z");

        // Creates weather data according to new start and end time to sessionData
        sessionData.createWeatherData(startTime, endTime);

        changeTempLabels("now");
    }

    @FXML
    private void onTomorrowClick() throws ParseException, ParserConfigurationException, IOException, SAXException {
        changeTimeColors(tomorrowLabel, nowLabel, dATomorrowLabel);

        // Sets the date to the next day to hours 00 - 24
        Date now = Calendar.getInstance().getTime();
        Date startTime = trimToStart(now,1);
        Date endTime = trimToEnd(now, 1);

        // Creates weather data according to new start and end time to sessionData
        sessionData.createWeatherData(startTime, endTime);
        changeTempLabels("not");

    }

    @FXML
    private void onDATomorrowClick() throws ParserConfigurationException, IOException, ParseException, SAXException {
        changeTimeColors(dATomorrowLabel, nowLabel, tomorrowLabel);

        // Sets the date to the next day to hours 00 - 24
        Date now = Calendar.getInstance().getTime();
        Date startTime = trimToStart(now,2);
        Date endTime = trimToEnd(now, 2);

        // Creates weather data according to new start and end time
        sessionData.createWeatherData(startTime, endTime);
        changeTempLabels("not");
    }

    @FXML
    private void changeTimeColors(Label selected, Label l2, Label l3) {
        selected.getStyleClass().add("basicHeadingGreen");
        l2.getStyleClass().removeAll("basicHeadingGreen");
        l2.getStyleClass().add("basicHeading");
        l3.getStyleClass().removeAll("basicHeadingGreen");
        l3.getStyleClass().add("basicHeading");
    }


    // Counts the average temperature of a certain day in certain month and year
    // at certain location
    @FXML
    private void onAvgBtnClick() throws ParseException, ParserConfigurationException, IOException, SAXException {
        Date startTime = savedDate;
        Date endTime = trimToEnd(savedDate,0);

        sessionData.createAvgMinMax(startTime, endTime);

        avglabel.setText(sessionData.getAVG_value());
    }

    // Counts the min and max temperature of a certain day in certain month and year
    // at certain location
    @FXML
    private void onMinMaxBtnClick() throws ParseException, ParserConfigurationException, IOException, SAXException {
        Date startTime = savedDate;
        Date endTime = trimToEnd(savedDate,0);

        sessionData.createAvgMinMax(startTime, endTime);

        minlabel.setText(String.valueOf(sessionData.getMIN_value()));
        maxlabel.setText(String.valueOf(sessionData.getMAX_value()));
    }
}
