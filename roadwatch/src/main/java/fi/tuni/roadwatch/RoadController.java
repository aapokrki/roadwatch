package fi.tuni.roadwatch;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Objects;

/**
 * This class contains the visualization aspects of all road data. The user can select what data they want to see
 * in the charts.
 */
public class RoadController {
    int timeFrame = 0;
    String conditionType;
    String taskType;
    private SessionData sessionData;

    @FXML
    private Label alertsLabel;
    @FXML
    private Button applyMaintenanceButton;
    @FXML
    private ComboBox<String> timeFrameComboBox;
    @FXML
    private ComboBox<String> conditionTypeComboBox;
    @FXML
    private ComboBox<String> maintenanceTaskCombobox;
    @FXML
    private PieChart conditionChart;
    @FXML
    private PieChart maintenanceChart;
    @FXML
    private AnchorPane datePickerPane;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;

    /**
     * Initializes all roadData data and components.
     * @param sessionData
     */
    public void initializeController(SessionData sessionData) throws IOException, URISyntaxException {
        this.sessionData = sessionData;
        ObservableList<String> taskTypesObservable= FXCollections.observableArrayList(sessionData.taskTypes);
        taskTypesObservable.add(0,"ALL");
        maintenanceTaskCombobox.setItems(taskTypesObservable);

        timeFrameComboBox.getSelectionModel().selectFirst();
        timeFrameComboBox.setValue("CURRENT");

        conditionType = sessionData.conditionPreference;
        conditionTypeComboBox.getSelectionModel().selectFirst();
        conditionTypeComboBox.setValue(conditionType);

        taskType = sessionData.maintenancePreference;
        maintenanceTaskCombobox.getSelectionModel().selectFirst();
        maintenanceTaskCombobox.setValue(taskType);

        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now());

        sessionData.createRoadData();
        changeTimeFrame();
        alertsLabel.setText(sessionData.roadData.trafficMessageAmount + " ALERTS");
    }


    /**
     * Updates data in charts
     */
    @FXML
    private void onUpdateClick() throws IOException, URISyntaxException, InterruptedException {
        //ROAD CONDITION
        sessionData.createRoadData();
        sessionData.roadData.setForecastConditions(timeFrame);
        alertsLabel.setText(sessionData.roadData.trafficMessageAmount + " ALERTS");
        changeTimeFrame();

        //MAINTENANCE
        onApplyMaintenanceClick();
    }

    /**
     * Changes the time frame for Conditionchart
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

    /**
     * Changes maintenance task type according to selected value.
     */
    @FXML
    private void changeTaskType() {
        this.taskType = maintenanceTaskCombobox.getValue();
    }

    /**
     * Creates maintenance chart according to selected timeframe.
     */
    @FXML
    private void onApplyMaintenanceClick() throws IOException, URISyntaxException {
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
    }
}
