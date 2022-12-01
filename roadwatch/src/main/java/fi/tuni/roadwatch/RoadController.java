package fi.tuni.roadwatch;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import java.util.ArrayList;
import java.util.Objects;

public class RoadController {
    private final ArrayList<Label> RCLabels = new ArrayList<>();


    int timeFrame = 0;
    String conditionType = "OVERALL";
    String taskType = "ALL";

    @FXML
    public Label alertsLabel;

    @FXML
    private Label infoLabel;
    // Road condition labels aka RC labels.

    @FXML
    public Button applyMaintenanceButton;


    @FXML
    public ComboBox<String> timeFrameComboBox;

    @FXML
    public ComboBox<String> conditionTypeComboBox;

    @FXML
    public ComboBox<String> maintenanceTaskCombobox;

    @FXML
    private PieChart conditionChart;

    @FXML
    private PieChart maintenanceChart;

    @FXML
    public AnchorPane datePickerPane;

    @FXML
    public DatePicker startDatePicker;

    @FXML
    public DatePicker endDatePicker;

    private SessionData sessionData;

    public void initializeController(SessionData sessionData) throws IOException, URISyntaxException {
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

        // INITALIZATION OF DEFAULT VALUES IN ROADWINDOW

        //ROAD CONDITION
        sessionData.createRoadData();
        changeTimeFrame();
        //sessionData.roadData.setForecastConditions(timeFrame);
        alertsLabel.setText(sessionData.roadData.trafficMessageAmount + " ALERTS");

        //MAINTENANCE
        //sessionData.createMaintenance("",startDatePicker.getValue(),endDatePicker.getValue());
        //conditionChart.setData(sessionData.createRoadConditionChart(sessionData.roadData.overallCondition));


    }


    @FXML
    private void onUpdateClick() throws IOException, URISyntaxException, InterruptedException {

        //ROAD CONDITION
        sessionData.createRoadData();
        sessionData.roadData.setForecastConditions(timeFrame);

        alertsLabel.setText(sessionData.roadData.trafficMessageAmount + " ALERTS");

        //MAINTENANCE
        onApplyMaintenanceClick();
    }

    @FXML
    public void changeTimeFrame() {
        String str = timeFrameComboBox.getValue();
        if(Objects.equals(str, "CURRENT")){
            timeFrame = 0;
        }else{
            String subs = str.substring(0,str.length()-1);
            timeFrame = Integer.parseInt(subs);
        }
        sessionData.roadData.setForecastConditions(timeFrame);
        System.out.println(str);
        changeConditionType();
    }

    @FXML
    public void changeConditionType() {
        this.conditionType = conditionTypeComboBox.getValue();
        System.out.println(conditionType);
        // Nää iffit on rumaa koodia don't look ;D
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
    @FXML
    public void changeTaskType() {
        this.taskType = maintenanceTaskCombobox.getValue();
    }

    @FXML
    public void onApplyMaintenanceClick() throws IOException, URISyntaxException {

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
