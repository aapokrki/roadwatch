package fi.tuni.roadwatch;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * This class combines the UX elements of weather and road data. The user can select a time frame
 * and compare the data side by side.
 */
public class CombineController {
    private final LocalDateTime currentDate = LocalDateTime.now();
    private SessionData sessionData;
    String taskType = "ALL";
    int timeFrame = 0;
    String conditionType = "OVERALL";

    @FXML
    public ComboBox<String> timeFrameComboBox;
    @FXML
    public PieChart conditionChart;
    @FXML
    public PieChart maintenanceChart;
    @FXML
    public AnchorPane datePickerPane;
    @FXML
    public DatePicker startDatePicker;
    @FXML
    public DatePicker endDatePicker;
    @FXML
    public AnchorPane maintenanceInputPane;
    @FXML
    public ComboBox<String> maintenanceTaskCombobox;
    @FXML
    public AnchorPane conditionInputPane;
    @FXML
    public ComboBox<String> conditionTypeComboBox;
    @FXML
    public Button conditionModeButton;
    @FXML
    public Button maintenanceModeButton;
    @FXML
    private Label dateErrorLabel;

    // WEATHER CHART COMPONENTS
    private XYChart.Series<String, Double> visibilitySeries;
    private XYChart.Series<String, Double> windSeries;
    private XYChart.Series<String, Double> temperatureSeries;

    @FXML
    protected LineChart<String, Double> lineChart;
    @FXML
    private Label chartErrorLabel;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private Button temperatureButton;
    @FXML
    private Button windButton;
    @FXML
    private Button visibilityButton;

    @FXML
    public ComboBox<String> dataTypeCombobox;

    // Mutual actions.
    /**
     * Initializes all components and data.
     * @param sessionData
     * @throws IOException
     * @throws URISyntaxException
     * @throws ParserConfigurationException
     * @throws ParseException
     * @throws InterruptedException
     * @throws SAXException
     */
    public void initializeController(SessionData sessionData) throws IOException, URISyntaxException, ParserConfigurationException, ParseException, InterruptedException, SAXException {
        this.sessionData = sessionData;
        ObservableList<String> taskTypesObservable= FXCollections.observableArrayList(sessionData.taskTypes);
        taskTypesObservable.add(0,"ALL");
        maintenanceTaskCombobox.setItems(taskTypesObservable);

        timeFrameComboBox.getSelectionModel().selectFirst();
        timeFrameComboBox.setValue("CURRENT");

        conditionTypeComboBox.getSelectionModel().selectFirst();
        conditionTypeComboBox.setValue("OVERALL");

        maintenanceTaskCombobox.getSelectionModel().selectFirst();
        maintenanceTaskCombobox.setValue("ALL");

        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now());

        //ROAD CONDITION INIT
        sessionData.createRoadData();
        changeTimeFrame();

        //INITIALIZE TO START WITH CONDITION SHOWING
        conditionChart.setVisible(false);
        conditionInputPane.setVisible(false);


        dataTypeCombobox.setItems(FXCollections.observableArrayList(sessionData.getDataClassTypesAsList()));

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
        //ROAD CONDITION
        sessionData.createRoadData();
        sessionData.roadData.setForecastConditions(timeFrame);
        changeTimeFrame();

        //MAINTENANCE
        onApplyMaintenanceClick();
    }

    /**
     * Updates timeframe according to the user's selection.
     */
    @FXML
    private void changeTimeFrame() {
        String str = timeFrameComboBox.getValue();
        if(Objects.equals(str, "CURRENT")){
            timeFrame = 0;
        }else{
            String subs = str.substring(0,str.length()-1);
            timeFrame = Integer.parseInt(subs);
        }
        sessionData.roadData.setForecastConditions(timeFrame);
        changeConditionType();
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

    // Road data components.
    /**
     * Changes maintenance task type according to selected value.
     */
    @FXML
    private void changeTaskType() {
        this.taskType = maintenanceTaskCombobox.getValue();
    }

    /**
     * Creates maintenance chart according to selected timeframe.
     * @throws IOException
     * @throws URISyntaxException
     */
    @FXML
    private void onApplyMaintenanceClick() throws IOException, URISyntaxException, ParseException, ParserConfigurationException, InterruptedException, SAXException {
        if(Objects.equals(taskType, "ALL")){
            sessionData.createMaintenance("",startDatePicker.getValue(),endDatePicker.getValue());
        }else{
            sessionData.createMaintenance(taskType,startDatePicker.getValue(),endDatePicker.getValue());
        }
        maintenanceChart.setData(sessionData.createMaintenanceChart());

        if(maintenanceChart.getData().size() == 0){
            maintenanceChart.setTitle("NO DATA");
        }else{
            maintenanceChart.setTitle(taskType + " TASKS AVERAGE");
        }

        // Reapply weathercharts
        if(windButton.getStyleClass().contains("basicButtonGreen")){
            calculateWindData(true);
        }
        if(visibilityButton.getStyleClass().contains("basicButtonGreen")){
            calculateVisibilityData(true);
        }
        if(temperatureButton.getStyleClass().contains("basicButtonGreen")){
            calculateTemperatureData(true);
        }
    }

    /**
     * Sets condition components visible and hides maintenance components.
     */
    @FXML
    private void onConditionModeClicked() {
        maintenanceChart.setVisible(false);
        conditionChart.setVisible(true);

        conditionInputPane.setVisible(true);
        maintenanceInputPane.setVisible(false);
    }

    /**
     * Sets maintenance components visible and hides condition components.
     */
    @FXML
    private void onMaintenanceModeClicked() {
        maintenanceChart.setVisible(true);
        conditionChart.setVisible(false);

        maintenanceInputPane.setVisible(true);
        conditionInputPane.setVisible(false);
    }

    /**
     * Changes road condition type in condition chart.
     */
    @FXML
    private void changeConditionType() {
        this.conditionType = conditionTypeComboBox.getValue();
        if(Objects.equals(conditionType, "OVERALL")){
            conditionChart.setData(sessionData.createRoadConditionChart(sessionData.roadData.overallCondition));

        }
        if(Objects.equals(conditionType, "FRICTION")){
            conditionChart.setData(sessionData.createRoadConditionChart(sessionData.roadData.frictionCondition));

        }
        if(Objects.equals(conditionType, "SLIPPERINESS")){
            conditionChart.setData(sessionData.createRoadConditionChart(sessionData.roadData.roadCondition));

        }
        if(Objects.equals(conditionType, "PRECIPICATION")){
            conditionChart.setData(sessionData.createRoadConditionChart(sessionData.roadData.precipicationCondition));

        }
        if(timeFrame == 0 && !Objects.equals(conditionType, "OVERALL")){
            conditionChart.setTitle("NO DATA");

        }else{
            conditionChart.setTitle(conditionType + " CONDITION IN AREA");
        }
    }

    // Weather components.
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
     *  Calculates wind data according to start and end date to a linechart
     */
    @FXML
    private void calculateWindData(boolean show) throws ParserConfigurationException, IOException, ParseException, SAXException, InterruptedException {
        if(datePickerErrorCheck()){
            chartErrorLabel.setText("");
            // Second button press, time to clear data.
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
            // Second button press, time to clear data.
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
    /**
     * Checks if temperature button has been clicked already or not and changes its appearance and actions according to it.
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws ParseException
     * @throws InterruptedException
     * @throws SAXException
     */
    @FXML
    private void onTemperatureButtonClicked() throws ParserConfigurationException, IOException, ParseException, InterruptedException, SAXException {
        if(temperatureButton.getStyleClass().contains("basicButtonGreen")){
            temperatureButton.getStyleClass().remove("basicButtonGreen");
            temperatureButton.getStyleClass().add("basicButton");
            calculateTemperatureData(false);
        }else{
            temperatureButton.getStyleClass().removeAll();
            temperatureButton.getStyleClass().add("basicButtonGreen");
            calculateTemperatureData(true);

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
                sessionData.createWeatherData(getStartDate(), getEndDate());
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
     * Saves data to json/xml
     * @throws IOException
     */
    public void onSaveButtonClick() throws IOException {
        String dataToSave = dataTypeCombobox.getValue();
        sessionData.writeDataToFile(dataTypeCombobox.getValue(), SessionData.DataClassType.valueOf(dataTypeCombobox.getValue()));
    }

    /**
     * Loads Data from json/xml
     * @throws URISyntaxException
     * @throws IOException
     */
    public void onLoadButtonClick() throws URISyntaxException, IOException {
        Stage fileChooserStage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.json", ".xml"));

        File selectedFile = fileChooser.showOpenDialog(fileChooserStage);
        if (selectedFile != null) {
            SessionData.DataClassType temp = SessionData.DataClassType.valueOf(dataTypeCombobox.getValue());
            sessionData.readDataFromFile(selectedFile, temp);

            maintenanceChart.setData(sessionData.createMaintenanceChart());

            if(maintenanceChart.getData().size() == 0){
                maintenanceChart.setTitle("NO DATA");
            }else{
                maintenanceChart.setTitle(taskType + " TASKS AVERAGE");
            }
        }
    }
}
