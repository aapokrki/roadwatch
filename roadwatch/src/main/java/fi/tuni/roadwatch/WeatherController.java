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

/**
 * This class controls weather UX data, that includes temperature, wind and visibility
 * data. Temperature can be shown from three days, today, tomorrow and the day after
 * tomorrow. The program show minimum and maximum temperature of those days.
 * Wind and visibility data are shown in the form of a graph. The user can see
 * data of all the weather types from the past as well.
 */
public class WeatherController {

    enum Datatype {
        TEMPERATURE,
        CHARTS
    }
    private Datatype datatype;
    private SessionData sessionData;
    private final LocalDateTime currentDate = LocalDateTime.now();
    private Date savedDate;

    // Mutual components.
    @FXML
    private Label datatypeLabel;
    @FXML
    private ComboBox<String> comboBox;

    // Temperature components
    @FXML
    private AnchorPane temperaturePane;
    @FXML
    private Label dateLabel;
    @FXML
    private Label todayLabel;
    @FXML
    private Label tempRightNowLabel;
    @FXML
    private Label tempMaxLabel;
    @FXML
    private Label tempMinLabel;
    @FXML
    private Label tempErrorLabel;
    @FXML
    private DatePicker tempDatePicker;
    @FXML
    private Label avgLabel;
    @FXML
    private Label maxLabel;
    @FXML
    private Label minLabel;
    @FXML
    private Label dateErrorLabel;

    // Chart components
    private XYChart.Series<String, Double> visibilitySeries;
    private XYChart.Series<String, Double> windSeries;
    private XYChart.Series<String, Double> temperatureSeries;
    @FXML
    private AnchorPane chartPane;
    @FXML
    protected LineChart<String, Double> lineChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private Label chartErrorLabel;
    @FXML
    private Button windButton;
    @FXML
    private Button visibilityButton;
    @FXML
    private  DatePicker startDatePicker;
    @FXML
    private  DatePicker endDatePicker;
    @FXML
    private Label dataSavedLabel;

    // Mutual actions.
    /**
     * Initializes all components and data.
     * @param sessionData
     */
    public void initializeController(SessionData sessionData) {
        this.sessionData = sessionData;
        datatype = Datatype.valueOf(sessionData.weatherPreference);

        if(datatype == Datatype.TEMPERATURE) {
            setTemperatureView();
        } else {
            setChartView();
        }

        comboBox.getSelectionModel().selectFirst();
        comboBox.setValue(datatype.toString());
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now());
        tempDatePicker.setValue(LocalDate.now().minusDays(1));
        saveTempDate();
    }

    /**
     * Sets up the temperature components.
     */
    private void setTemperatureView() {
        datatype = Datatype.TEMPERATURE;
        datatypeLabel.setText(datatype.toString());
        setTemperature();
        chartPane.setVisible(false);
    }

    /**
     * Sets up the chart components.
     */
    private void setChartView(){
        datatype = Datatype.CHARTS;
        datatypeLabel.setText(datatype.toString());
        temperaturePane.setVisible(false);
        chartPane.setVisible(true);
    }

    /**
     * Changes data type according to combobox value.
     */
    @FXML
    private void changeDatatype() {
        if(comboBox.getValue().equalsIgnoreCase(Datatype.TEMPERATURE.toString())) {
            setTemperatureView();
        } else {
            setChartView();
        }
    }

    //TODO
    @FXML
    private void preferenceChange(String preference) {
        if(preference.equalsIgnoreCase(Datatype.TEMPERATURE.toString())) {
            setTemperatureView();
        } else {
            setChartView();
        }
    }

    // Temperature actions.
    /**
     * Saves date from datePicker.
     * @throws ParseException
     */
    @FXML
    private void saveTempDate() {
        LocalDate localDate = tempDatePicker.getValue();
        Instant instant = Instant.from(localDate.atStartOfDay(ZoneOffset.UTC));
        savedDate = Date.from(instant);
    }

    /**
     * Sets temperature components.
     */
    @FXML
    private void setTemperature() {
        temperaturePane.setVisible(true);
        dateLabel.setText(currentDate.getDayOfMonth() + "." + currentDate.getMonthValue() + "." + currentDate.getYear());
    }

    /**
     * Changes temperature labels according to which day you want to see.
     * @param now boolean
     */
    @FXML
    private void changeTempLabels(Boolean now){
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

    /**
     * Functionality for today button. Gets the min max and current temperature of that day.
     */
    @FXML
    private void onTodayClick() throws ParseException, ParserConfigurationException, IOException, SAXException {
        todayLabel.setVisible(true);
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

    /**
     * Functionality for tomorrow button. Gets the min max temperature of that day.
     */
    @FXML
    private void onTomorrowClick() throws ParseException, ParserConfigurationException, IOException, SAXException {
        todayLabel.setVisible(false);
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

    /**
     * Functionality for day after tomorrow button. Gets the min max temperature of that day.
     */
    @FXML
    private void onDATomorrowClick() throws ParserConfigurationException, IOException, ParseException, SAXException {
        todayLabel.setVisible(false);
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
     * Error checker for parameters to the minmax or average calculations.
     * @param flag Checks if picked days are from the future or not.
     * @return boolean true or false
     */
    private boolean avgMinMaxErrorCheck(boolean flag){
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
        } else {
            if(savedDate.after(sessionData.helperFunctions.convertToDateViaInstant(LocalDate.from(currentDate)))){
                dateErrorLabel.setText("Can't save date from the future");
                return false;
            }
        }
        return true;
    }

    /**
     * Counts the average temperature of a certain day in certain month and year.
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
     * Counts the min and max temperature of a certain day in certain month and year.
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
     * Saves weatherData to map on button click to access later.
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

    // Chart actions.
    /**
     * Checks if wind button has been clicked already or not and changes its appearance and actions according to it.
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws ParseException
     * @throws InterruptedException
     * @throws SAXException
     */
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

    /**
     *  Calculates wind data according to start and end date to a lineChart.
     */
    @FXML
    private void calculateWindData(boolean show) throws ParserConfigurationException, IOException, ParseException, SAXException, InterruptedException {
        if(datePickerErrorCheck()){
            chartErrorLabel.setText("");
            // Button is already pressed, time to clear data.
            if(!show) {
                lineChart.getData().removeAll(windSeries);

            } else { // Button has not been pressed
                lineChart.getData().removeAll(windSeries);

                lineChart.setAnimated(false);
                sessionData.createWeatherData(getChartStartDate(), getChartEndDate());
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

    /**
     * Checks if visibility button has been clicked already or not and changes its appearance and actions according to it.
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws ParseException
     * @throws InterruptedException
     * @throws SAXException
     */
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

    /**
     * Calculates visibility data according to start and end date to a lineChart.
     */
    @FXML
    private void calculateVisibilityData(boolean show) throws ParseException, ParserConfigurationException, IOException, SAXException, InterruptedException {
        if(datePickerErrorCheck()){
            chartErrorLabel.setText("");
            // Button is already pressed, time to clear data.
            if(!show) {
                lineChart.getData().removeAll(visibilitySeries);

            } else { // Button has not been pressed
                lineChart.getData().removeAll(visibilitySeries);

                lineChart.setAnimated(false);
                sessionData.createWeatherData(getChartStartDate(), getChartEndDate());
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


    /**
     * Calculates temperature data according to start and end date to a lineChart.
     */
    @FXML
    private void calculateTemperatureData(boolean show) throws ParseException, ParserConfigurationException, IOException, SAXException, InterruptedException {
        if(datePickerErrorCheck()){
            chartErrorLabel.setText("");
            // Button is already pressed, time to clear data.
            if(!show) {
                lineChart.getData().removeAll(temperatureSeries);

            } else { // Button has not been pressed
                lineChart.getData().removeAll(temperatureSeries);

                lineChart.setAnimated(false);
                sessionData.createWeatherData(getChartStartDate(), getChartEndDate());
                Thread.sleep(1000);

                temperatureSeries = sessionData.createGraphSeries("TEMPERATURE");

                if(temperatureSeries.getData().size() != 0){
                    temperatureSeries.setName("Temperature");
                    lineChart.getData().add(temperatureSeries);
                    xAxis.setLabel("Time");
                    yAxis.setLabel("°");
                }
                else{
                    chartErrorLabel.setText("No Data");
                }
            }
        }
    }

    /**
     * Creates charts according to new data.
     * @throws IOException
     * @throws URISyntaxException
     * @throws InterruptedException
     * @throws ParseException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
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
     * Gets the start date of chart datePicker.
     * @return Date object trimmed to start
     */
    private Date getChartStartDate(){
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
     * Gets the end date of chart datePicker.
     * @return Date object trimmed to end
     */
    private Date getChartEndDate(){
        LocalDate endLocalDate = endDatePicker.getValue();
        if(endLocalDate == null){
            chartErrorLabel.setText("Dates cant be null");
            return null;
        }
        Instant instant2 = Instant.from(endLocalDate.atStartOfDay(ZoneId.systemDefault()));
        Date endDate = Date.from(instant2);

        return  sessionData.helperFunctions.trimToEnd(endDate,0);
    }

    // Mutual error checker.
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
        else if (getChartStartDate() == null || getChartEndDate() == null){
            chartErrorLabel.setText("Date picker can't be null");
            return false;
        }
        else if(Objects.requireNonNull(getChartStartDate()).after(getChartEndDate())){
            chartErrorLabel.setText("Start date can't be after end date");
            return false;
        }
        else if(getChartStartDate().before(sessionData.helperFunctions.convertToDateViaInstant(currentDate.toLocalDate())) &&
                Objects.requireNonNull(getChartEndDate()).after(sessionData.helperFunctions.convertToDateViaInstant(currentDate.toLocalDate()))){
            chartErrorLabel.setText("Can't get data from both past and future");
            return false;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(getChartStartDate());
        c.add(Calendar.DATE,7);
        if(c.getTime().compareTo(getChartEndDate()) <= 0){
            chartErrorLabel.setText("Maximum time length 1 week");
            return false;
        }
        return true;
    }
}
