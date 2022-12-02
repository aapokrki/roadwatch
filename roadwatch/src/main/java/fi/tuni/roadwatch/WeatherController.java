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
import java.net.URISyntaxException;
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
    @FXML
    private Label dateErrorLabel;

    // Chart components
    @FXML
    private AnchorPane chartPane;
    @FXML
    protected LineChart<String, Double> lineChart;
    @FXML
    private Label chartErrorLabel;
    @FXML
    private Label dataSavedLabel;
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


    /**
     * Sets sessionData to the current state
     * @param sessionData
     */
    public void initializeController(SessionData sessionData) {
        this.sessionData = sessionData;
        setTemperatureView();
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now());
        chooseDay.setValue(LocalDate.now().minusDays(1));
        saveDate();
    }


    /**
     * Sets the temperature fxml right
     */
    private void setTemperatureView() {
        datatype = Datatype.TEMPERATURE;
        datatypeLabel.setText(datatype.toString());
        setTemperature();
        chartPane.setVisible(false);
    }

    private void setChartView(){
        datatype = Datatype.CHARTS;
        datatypeLabel.setText(datatype.toString());
        temperaturePane.setVisible(false);
        chartPane.setVisible(true);

    }

    @FXML
    /**
     * Changes data type according to combobox value
     */
    private void changeDatatype() {
        if(comboBox.getValue().equalsIgnoreCase(Datatype.TEMPERATURE.toString())) {
            setTemperatureView();
        } else {
            setChartView();
        }
    }

    @FXML
    private void onWindButtonClicked() throws ParserConfigurationException, IOException, ParseException, InterruptedException, SAXException {
        if(windButton.getStyleClass().contains("basicButtonGreen")){
            windButton.getStyleClass().remove("basicButtonGreen");
            windButton.getStyleClass().add("basicButton");
            calculateWindData(false);
        }else{
            windButton.getStyleClass().removeAll();
            windButton.getStyleClass().add("basicButtonGreen");
            calculateWindData(true);

        }
    }
    @FXML
    /**
     *  Calculates wind data according to start and end date to a linechart
     */
    private void calculateWindData(boolean show) throws ParserConfigurationException, IOException, ParseException, SAXException, InterruptedException {
        if(datePickerErrorCheck()){
            chartErrorLabel.setText("");
            // Button is already pressed, time to clear data.
            if(!show) {
                lineChart.getData().removeAll(windSeries);

            } else { // Button has not been pressed
                lineChart.getData().removeAll(windSeries);

                lineChart.setAnimated(false);
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
                    chartErrorLabel.setText("No Data");
                }
            }
        }
    }

    @FXML
    private void onVisibilityButtonClicked() throws ParserConfigurationException, IOException, ParseException, InterruptedException, SAXException {
        if(visibilityButton.getStyleClass().contains("basicButtonGreen")){
            visibilityButton.getStyleClass().remove("basicButtonGreen");
            visibilityButton.getStyleClass().add("basicButton");
            calculateVisibilityData(false);
        }else{
            visibilityButton.getStyleClass().removeAll();
            visibilityButton.getStyleClass().add("basicButtonGreen");
            calculateVisibilityData(true);

        }
    }
    @FXML
    /**
     * Calculates visibility data according to start and end date to a linechart
     */
    private void calculateVisibilityData(boolean show) throws ParseException, ParserConfigurationException, IOException, SAXException, InterruptedException {
        if(datePickerErrorCheck()){
            chartErrorLabel.setText("");
            // Button is already pressed, time to clear data.
            if(!show) {
                lineChart.getData().removeAll(visibilitySeries);

            } else { // Button has not been pressed
                lineChart.getData().removeAll(visibilitySeries);

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
                    chartErrorLabel.setText("No Data");
                }
            }
        }
    }

    @FXML
    private void onUpdateClick() throws IOException, URISyntaxException, InterruptedException, ParseException, ParserConfigurationException, SAXException {

        sessionData.createRoadData();
        // Reapply weathercharts
        if(windButton.getStyleClass().contains("basicButtonGreen")){
            calculateWindData(true);
        }
        if(visibilityButton.getStyleClass().contains("basicButtonGreen")){
            calculateVisibilityData(true);
        }

    }

    /**
     * Gets the start date of datepicker
     * @return Date object trimmed to start
     */
    private Date getStartDate(){
        LocalDate startLocalDate = startDatePicker.getValue();
        if(startLocalDate == null){
            chartErrorLabel.setText("Dates cant be null");
            return null;
        }
        Instant instant = Instant.from(startLocalDate.atStartOfDay(ZoneId.systemDefault()));
        Date startDate = Date.from(instant);

        return sessionData.helperFunctions.trimToStart(startDate,0);
    }

    /**
     * Gets the end date of datepicker
     * @return Date object trimmed to end
     */
    private Date getEndDate(){
        LocalDate endLocalDate = endDatePicker.getValue();
        if(endLocalDate == null){
            chartErrorLabel.setText("Dates cant be null");
            return null;
        }
        Instant instant2 = Instant.from(endLocalDate.atStartOfDay(ZoneId.systemDefault()));
        Date endDate = Date.from(instant2);

        return  sessionData.helperFunctions.trimToEnd(endDate,0);
    }

    /**
     * saves date from datepicker to variable
     * @throws ParseException
     */
    @FXML
    private void saveDate() {
        LocalDate localDate = chooseDay.getValue();
        Instant instant = Instant.from(localDate.atStartOfDay(ZoneOffset.UTC));
        savedDate = Date.from(instant);
    }

    /**
     * Temperature actions
     */
    @FXML
    private void setTemperature() {
        temperaturePane.setVisible(true);
        dateLabel.setText(currentDate.getDayOfMonth() + "." + currentDate.getMonthValue() + "." + currentDate.getYear());
    }

    /**
     * Changes temperature labels according to which day you want to see
     * @param now boolean
     */
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
            if(Objects.equals(wd.getDate(), sessionData.helperFunctions.getClosestDate())){
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
    /**
     * Functionality for today button. Gets the min max weathers of that day.
     */
    private void onNowClick() throws ParseException, ParserConfigurationException, IOException, SAXException {
        //changeTimeColors(todayLabel, tomorrowLabel, dATomorrowLabel);
        nowLabel.setVisible(true);
        if(sessionData.helperFunctions.coordinateCheck()){
            tempErrorLabel.setText("Choose coordinates, remember to add on map!");
        }
        else{
        // Gets the date right now and adds a few seconds to get forecast from API
        // Also getting the date and the end of day
            tempErrorLabel.setText("");
            Calendar cal = Calendar.getInstance();
            long timeInSecs = cal.getTimeInMillis();
            Date startTime = new Date(timeInSecs + (10*60*10));
            Date endTime = sessionData.helperFunctions.timeAndDateAsDate(LocalDate.now().atTime(23, 59, 59) + "Z");

            // Creates weather data according to new start and end time to sessionData
            sessionData.createWeatherData(startTime, endTime);
            changeTempLabels(true);
        }
    }

    @FXML
    /**
     * Functionality for tomorrow button. Gets the min max weathers of that day.
     */
    private void onTomorrowClick() throws ParseException, ParserConfigurationException, IOException, SAXException {
        //changeTimeColors(tomorrowLabel, todayLabel, dATomorrowLabel);
        nowLabel.setVisible(false);
        if(sessionData.helperFunctions.coordinateCheck()){
            tempErrorLabel.setText("Choose coordinates, remember to add on map!");
        }
        else {
            // Sets the date to the next day to hours 00 - 24
            Date now = Calendar.getInstance().getTime();
            Date startTime = sessionData.helperFunctions.trimToStart(now, 1);
            Date endTime = sessionData.helperFunctions.trimToEnd(now, 1);

            // Creates weather data according to new start and end time to sessionData
            sessionData.createWeatherData(startTime, endTime);
            changeTempLabels(false);
        }
    }

    @FXML
    /**
     * Functionality for day after tomorrow button. Gets the min max weathers of that day.
     */
    private void onDATomorrowClick() throws ParserConfigurationException, IOException, ParseException, SAXException {
        //changeTimeColors(dATomorrowLabel, todayLabel, tomorrowLabel);
        nowLabel.setVisible(false);
        if(sessionData.helperFunctions.coordinateCheck()){
            tempErrorLabel.setText("Choose coordinates, remember to add on map!");
        }
        else {
            // Sets the date to the next day to hours 00 - 24
            Date now = Calendar.getInstance().getTime();
            Date startTime = sessionData.helperFunctions.trimToStart(now, 2);
            Date endTime = sessionData.helperFunctions.trimToEnd(now, 2);

            // Creates weather data according to new start and end time
            sessionData.createWeatherData(startTime, endTime);
            changeTempLabels(false);
        }
    }

    /**
     * Changes time colors according to click
     * @param selected wanted time
     * @param l2
     * @param l3
     */
    @FXML
    private void changeTimeColors(Label selected, Label l2, Label l3) {
        selected.getStyleClass().add("basicHeadingGreen");
        l2.getStyleClass().removeAll("basicHeadingGreen");
        l2.getStyleClass().add("basicHeading");
        l3.getStyleClass().removeAll("basicHeadingGreen");
        l3.getStyleClass().add("basicHeading");
    }


    /**
     * Error checker for datePickers
     * @return boolean true or false
     */
    private boolean datePickerErrorCheck(){
        dateErrorLabel.setText("");
        if(startDatePicker == null || endDatePicker == null){
            chartErrorLabel.setText("Date picker can't be null");
            return false;
        }
        else if(sessionData.helperFunctions.coordinateCheck()) {
            dateErrorLabel.setText("Choose coordinates, remember to add on map!");
            return false;
        }
        else if (getStartDate() == null || getEndDate() == null){
            chartErrorLabel.setText("Date picker can't be null");
            return false;
        }
        else if(Objects.requireNonNull(getStartDate()).after(getEndDate())){
            chartErrorLabel.setText("Start date can't be after end date");
            return false;
        }
        else if(getStartDate().before(sessionData.helperFunctions.convertToDateViaInstant(currentDate.toLocalDate())) &&
                Objects.requireNonNull(getEndDate()).after(sessionData.helperFunctions.convertToDateViaInstant(currentDate.toLocalDate()))){
            chartErrorLabel.setText("Can't get data from both past and future");
            return false;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(getStartDate());
        c.add(Calendar.DATE,7);
        if(c.getTime().compareTo(getEndDate()) <= 0){
            chartErrorLabel.setText("Maximum time length 1 week");
            return false;
        }
        return true;
    }

    /**
     * Error checker for parameters to the minmax or average calculations
     * @param flag
     * @return boolean true or false
     */
    private boolean avgMinMaxErrorCheck(boolean flag){
        System.out.println(sessionData.wantedWeatherAVGMinMax);
        dateErrorLabel.setText("");
        if(savedDate == null){
            dateErrorLabel.setText("Choose a date");
            return false;
        }
        else if(sessionData.helperFunctions.coordinateCheck()) {
            dateErrorLabel.setText("Choose coordinates, remember to add on map!");
            return false;
        }

        if(flag){
            if(savedDate.after(sessionData.helperFunctions.convertToDateViaInstant(LocalDate.from(currentDate)))){
                dateErrorLabel.setText("Can't count average or min-max of future");
                return false;
            }
        }
        else{
            if(savedDate.after(sessionData.helperFunctions.convertToDateViaInstant(LocalDate.from(currentDate)))){
                dateErrorLabel.setText("Can't save date from the future");
                return false;
            }
        }

        return true;
    }

    /**
     * Counts the average temperature of a certain day in certain month and year
     * at certain location
     * @throws ParseException
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    @FXML
    private void onAvgBtnClick() throws ParseException, ParserConfigurationException, IOException, SAXException {
        if(avgMinMaxErrorCheck(true)){
            Date startTime = savedDate;
            Date endTime = sessionData.helperFunctions.trimToEnd(savedDate, 0);

            // If avgminmax is empty, api call failed, needs bigger area
            if(!sessionData.createAvgMinMax(startTime, endTime)){
                dateErrorLabel.setText("Area must be larger to calculate average");
                avgLabel.setText("");
            }else{
                avgLabel.setText("Avg: " + sessionData.helperFunctions.getAVG_value() + "°");
            }
        }
    }

    /**
     * Counts the min and max temperature of a certain day in certain month and year
     * at certain location
     * @throws ParseException
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    @FXML
    private void onMinMaxBtnClick() throws ParseException, ParserConfigurationException, IOException, SAXException {
        if(avgMinMaxErrorCheck(true)){
            Date startTime = savedDate;
            Date endTime = sessionData.helperFunctions.trimToEnd(savedDate, 0);

            // If created avgminMax is empty, api call failed, needs bigger area
            if(!sessionData.createAvgMinMax(startTime, endTime)){
                dateErrorLabel.setText("Area must be larger to calculate min-max");
                minLabel.setText("");
                maxLabel.setText("");
            }else{
                minLabel.setText("Min: " + sessionData.helperFunctions.getMIN_value()  + "°");
                maxLabel.setText("Max: " + sessionData.helperFunctions.getMAX_value()  + "°");
            }
        }
    }

    /**
     * Saves weatherData to map on button click to access later
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws ParseException
     * @throws SAXException
     */
    @FXML
    private void saveWeatherData() throws ParserConfigurationException, IOException, ParseException, SAXException {
        dataSavedLabel.setText("");
        if(avgMinMaxErrorCheck(false)){
            Date startTime = savedDate;
            Date endTime = sessionData.helperFunctions.trimToEnd(savedDate, 0);
            sessionData.createWeatherData(startTime, endTime);
            sessionData.saveWeatherData(savedDate);

            dataSavedLabel.setText("Data saved successfully");
        }
    }
}
