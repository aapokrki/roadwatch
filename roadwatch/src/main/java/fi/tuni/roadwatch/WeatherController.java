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
import java.time.*;
import java.util.*;


public class WeatherController {

    enum Datatype {
        TEMPERATURE,
        CHARTS
    }
    private Datatype datatype;
    private SessionData sessionData;
    private final LocalDateTime currentDate = LocalDateTime.now();

    @FXML
    private ComboBox<String> comboBox;
    @FXML
    private Label datatypeLabel;
    @FXML
    private  DatePicker startDatePicker;
    @FXML
    private  DatePicker endDatePicker;

    @FXML
    private AnchorPane temperaturePane;
    @FXML
    private Label dateLabel;
    @FXML
    private Label nowLabel;
    @FXML
    private Label tempRightNowLabel;
    @FXML
    private Label tempMaxLabel;
    @FXML
    private Label tempMinLabel;
    @FXML
    private Label tempErrorLabel;
    private Date savedDate;
    @FXML
    private Label maxLabel;
    @FXML
    private Label minLabel;
    @FXML
    private Label avgLabel;
    @FXML
    private DatePicker chooseDay;

    // Chart components
    @FXML
    private AnchorPane chartPane;
    @FXML
    protected LineChart<String, Double> lineChart;
    @FXML
    private Label errorLabel;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private Button windButton;
    private XYChart.Series<String, Double> visibilitySeries;
    private XYChart.Series<String, Double> windSeries;
    @FXML
    private Button visibilityButton;
    @FXML
    private Button saveDataButton;


    public void setSessionData(SessionData sessionData) {
        this.sessionData = sessionData;
        setTemperatureData();
    }

    private void setTemperatureData() {
        datatype = Datatype.TEMPERATURE;
        datatypeLabel.setText(datatype.toString());
        setTemperature();
        chartPane.setVisible(false);
    }

    @FXML
    private void changeDatatype() {
        if(comboBox.getValue().equalsIgnoreCase(Datatype.TEMPERATURE.toString())) {
            setTemperatureData();
        } else {
            datatype = Datatype.CHARTS;
            datatypeLabel.setText(datatype.toString());
            temperaturePane.setVisible(false);
            chartPane.setVisible(true);
        }
    }

    @FXML
    // Calculates visibility data according to start and end date to a linechart
    private void calculateVisibilityData() throws ParseException, ParserConfigurationException, IOException, SAXException, InterruptedException {
        if(datePickerErrorCheck()){
            // Second button press, time to clear data.
            if(visibilityButton.getStyleClass().contains("basicButtonGreen")) {
                visibilityButton.getStyleClass().remove("basicButtonGreen");
                visibilityButton.getStyleClass().add("basicButton");
                lineChart.getData().removeAll(visibilitySeries);

            } else { // Button has not been pressed
                visibilityButton.getStyleClass().removeAll();
                visibilityButton.getStyleClass().add("basicButtonGreen");

                lineChart.setAnimated(false);
                sessionData.createWeatherData(getStartDate(), getEndDate());
                Thread.sleep(1000);

                visibilitySeries = sessionData.createGraphSeries("VISIBILITY");

                if(visibilitySeries.getData().size() != 0){
                    visibilitySeries.setName("Visibility");
                    lineChart.getData().add(visibilitySeries);
                    xAxis.setLabel("Time");
                    yAxis.setLabel("km");
                }
                else{
                    errorLabel.setText("No Data");
                }
            }
        }
    }

    @FXML
    // Calculates wind data according to start and end date to a linechart
    private void calculateWindData() throws ParserConfigurationException, IOException, ParseException, SAXException, InterruptedException {
        if(datePickerErrorCheck()){
            // Second button press, time to clear data.
            if(windButton.getStyleClass().contains("basicButtonGreen")) {
                windButton.getStyleClass().remove("basicButtonGreen");
                windButton.getStyleClass().add("basicButton");
                lineChart.getData().removeAll(windSeries);

            } else { // Button has not been pressed
                windButton.getStyleClass().removeAll();
                windButton.getStyleClass().add("basicButtonGreen");

                lineChart.setAnimated(false);
                errorLabel.setText("");
                sessionData.createWeatherData(getStartDate(), getEndDate());
                Thread.sleep(1000);

                windSeries = sessionData.createGraphSeries("WIND");

                if(windSeries.getData().size() != 0){
                    windSeries.setName("Wind");
                    lineChart.getData().add(windSeries);
                    xAxis.setLabel("Time");
                    yAxis.setLabel("m/s");
                }
                else{
                    errorLabel.setText("No Data");
                }
            }
        }
    }

    private Date getStartDate(){
        LocalDate startLocalDate = startDatePicker.getValue();
        if(startLocalDate == null){
            errorLabel.setText("Dates cant be null");
            return null;
        }
        Instant instant = Instant.from(startLocalDate.atStartOfDay(ZoneId.systemDefault()));
        Date startDate = Date.from(instant);

        return sessionData.trimToStart(startDate,0);
    }

    private Date getEndDate(){
        LocalDate endLocalDate = endDatePicker.getValue();
        if(endLocalDate == null){
            errorLabel.setText("Dates cant be null");
            return null;
        }
        Instant instant2 = Instant.from(endLocalDate.atStartOfDay(ZoneId.systemDefault()));
        Date endDate = Date.from(instant2);

        return  sessionData.trimToEnd(endDate,0);
    }

    @FXML
    private void saveDate() {
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
    public void changeTempLabels(Boolean now){
        double min = sessionData.wantedWeatherData.get(0).getTemperature();
        double max = sessionData.wantedWeatherData.get(0).getTemperature();
        tempRightNowLabel.setVisible(false);

        //Sets min max and now labels according to newest weather information
        for(WeatherData wd : sessionData.wantedWeatherData){
            if(wd.getTemperature() <= min){
                min = wd.getTemperature();
            }
            if(wd.getTemperature() >= max){
                max = wd.getTemperature();
            }
            if(Objects.equals(wd.getDate(), sessionData.getClosestDate())){
                if(now){
                    tempRightNowLabel.setVisible(true);
                    tempRightNowLabel.setText(wd.getTemperature() + "°");
                }
            }
        }
        tempMinLabel.setText(min + "°");
        tempMaxLabel.setText(max + "°");
    }


    @FXML
    private void onNowClick() throws ParseException, ParserConfigurationException, IOException, SAXException {
        //changeTimeColors(todayLabel, tomorrowLabel, dATomorrowLabel);
        nowLabel.setVisible(true);
        if(!sessionData.coordinateCheck()){
            tempErrorLabel.setText("Choose coordinates, remember to add on map!");
        }
        else{
        // Gets the date right now and adds a few seconds to get forecast from API
        // Also getting the date and the end of day
            tempErrorLabel.setText("");
            Calendar cal = Calendar.getInstance();
            long timeInSecs = cal.getTimeInMillis();
            Date startTime = new Date(timeInSecs + (10*60*10));
            Date endTime = sessionData.timeAndDateAsDate(LocalDate.now().atTime(23, 59, 59) + "Z");

            // Creates weather data according to new start and end time to sessionData
            sessionData.createWeatherData(startTime, endTime);
            changeTempLabels(true);
        }
    }

    @FXML
    private void onTomorrowClick() throws ParseException, ParserConfigurationException, IOException, SAXException {
        //changeTimeColors(tomorrowLabel, todayLabel, dATomorrowLabel);
        nowLabel.setVisible(false);
        if(!sessionData.coordinateCheck()){
            tempErrorLabel.setText("Choose coordinates, remember to add on map!");
        }
        else {
            // Sets the date to the next day to hours 00 - 24
            Date now = Calendar.getInstance().getTime();
            Date startTime = sessionData.trimToStart(now, 1);
            Date endTime = sessionData.trimToEnd(now, 1);

            // Creates weather data according to new start and end time to sessionData
            sessionData.createWeatherData(startTime, endTime);
            changeTempLabels(false);
        }
    }

    @FXML
    private void onDATomorrowClick() throws ParserConfigurationException, IOException, ParseException, SAXException {
        //changeTimeColors(dATomorrowLabel, todayLabel, tomorrowLabel);
        nowLabel.setVisible(false);
        if(!sessionData.coordinateCheck()){
            tempErrorLabel.setText("Choose coordinates, remember to add on map!");
        }
        else {
            // Sets the date to the next day to hours 00 - 24
            Date now = Calendar.getInstance().getTime();
            Date startTime = sessionData.trimToStart(now, 2);
            Date endTime = sessionData.trimToEnd(now, 2);

            // Creates weather data according to new start and end time
            sessionData.createWeatherData(startTime, endTime);
            changeTempLabels(false);
        }
    }


    private boolean datePickerErrorCheck(){
        errorLabel.setText("");
        if(startDatePicker == null || endDatePicker == null){
            errorLabel.setText("Datepicker cant be null");
            return false;
        }
        else if(!sessionData.coordinateCheck()) {
            errorLabel.setText("Choose coordinates, remember to add on map!");
            return false;
        }
        else if (getStartDate() == null || getEndDate() == null){
            errorLabel.setText("Datepicker cant be null");
            return false;
        }
        else if(Objects.requireNonNull(getStartDate()).after(getEndDate())){
            errorLabel.setText("Start date cant be after end date");
            return false;
        }
        else if(getStartDate().before(sessionData.convertToDateViaInstant(currentDate.toLocalDate())) &&
                Objects.requireNonNull(getEndDate()).after(sessionData.convertToDateViaInstant(currentDate.toLocalDate()))){
            errorLabel.setText("Can't get data from both past and future");
            return false;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(getStartDate());
        c.add(Calendar.DATE,7);
        if(c.getTime().compareTo(getEndDate()) <= 0){
            errorLabel.setText("Maximum time length 1 week");
            return false;
        }
        return true;
    }

    private boolean avgMinMaxErrorCheck(boolean flag){
        System.out.println(sessionData.wantedWeatherAVGMinMax);
        errorLabel.setText("");
        if(savedDate == null){
            errorLabel.setText("Choose a date");
            return false;
        }
        else if(!sessionData.coordinateCheck()) {
            errorLabel.setText("Choose coordinates, remember to add on map!");
            return false;
        }

        if(flag){
            if(savedDate.after(sessionData.convertToDateViaInstant(LocalDate.from(currentDate)))){
                errorLabel.setText("Can't count average or min-max of future");
                return false;
            }
        }
        else{
            if(savedDate.after(sessionData.convertToDateViaInstant(LocalDate.from(currentDate)))){
                errorLabel.setText("Can't save date from the future");
                return false;
            }
        }

        return true;
    }

    // Counts the average temperature of a certain day in certain month and year
    // at certain location
    @FXML
    private void onAvgBtnClick() throws ParseException, ParserConfigurationException, IOException, SAXException {
        if(avgMinMaxErrorCheck(true)){
            Date startTime = savedDate;
            Date endTime = sessionData.trimToEnd(savedDate, 0);

            // If avgminmax is empty, api call failed, needs bigger area
            if(!sessionData.createAvgMinMax(startTime, endTime)){
                errorLabel.setText("Area must be larger to calculate average");
                avgLabel.setText("");
            }else{
                avgLabel.setText("Avg: " + sessionData.getAVG_value() + "°");
            }
        }
    }

    // Counts the min and max temperature of a certain day in certain month and year
    // at certain location
    @FXML
    private void onMinMaxBtnClick() throws ParseException, ParserConfigurationException, IOException, SAXException {
        if(avgMinMaxErrorCheck(true)){
            Date startTime = savedDate;
            Date endTime = sessionData.trimToEnd(savedDate, 0);

            // If created avgminMax is empty, api call failed, needs bigger area
            if(!sessionData.createAvgMinMax(startTime, endTime)){
                errorLabel.setText("Area must be larger to calculate min-max");
                minLabel.setText("");
                maxLabel.setText("");
            }else{
                minLabel.setText("Min: " + sessionData.getMIN_value()  + "°");
                maxLabel.setText("Max: " + sessionData.getMAX_value()  + "°");
            }
        }
    }

    // Saves weatherData to map on button click to access later
    @FXML
    private void saveWeatherData() throws ParserConfigurationException, IOException, ParseException, SAXException {
        if(avgMinMaxErrorCheck(false)){
            Date startTime = savedDate;
            Date endTime = sessionData.trimToEnd(savedDate, 0);
            sessionData.createWeatherData(startTime, endTime);
            sessionData.saveWeatherData(savedDate);
        }

    }
}
