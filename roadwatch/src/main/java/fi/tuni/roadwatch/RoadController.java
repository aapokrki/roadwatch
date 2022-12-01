package fi.tuni.roadwatch;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class RoadController {
    private final ArrayList<Label> RCLabels = new ArrayList<>();


    int timeFrame = 0;
    @FXML
    private Label infoLabel;
    // Road condition labels aka RC labels.
    @FXML
    private Label RCLabel;
    @FXML
    private Label frictionLabel;
    @FXML
    private Label precipitationLabel;
    @FXML
    private Label roadConditionLabel;
    @FXML
    private Label overallConditionLabel;
    @FXML
    private Label alertsLabel;

    @FXML
    public Label loadingLabel;

    @FXML
    public ComboBox<String> timeFrameComboBox;

    @FXML
    public AnchorPane datePickerPane;

    @FXML
    public DatePicker startDatePicker;

    @FXML
    public DatePicker endDatePicker;

    private SessionData sessionData;

    public void setSessionData(SessionData sessionData) {
        this.sessionData = sessionData;
        RCLabels.add(RCLabel);
        RCLabels.add(frictionLabel);
        RCLabels.add(precipitationLabel);
        RCLabels.add(roadConditionLabel);
        RCLabels.add(overallConditionLabel);
        timeFrameComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void onOverallConditionClick() {
        infoLabel.setText(overallConditionLabel.getText());
        changeRCColors(overallConditionLabel);
        System.out.println(sessionData.roadData.overallCondition);

    }

    @FXML
    private void onPrecipitationClick() {
        infoLabel.setText(precipitationLabel.getText());
        changeRCColors(precipitationLabel);
        System.out.println(sessionData.roadData.precipicationCondition);
    }

    @FXML
    private void onRoadConditionClick() {
        infoLabel.setText(roadConditionLabel.getText());
        changeRCColors(roadConditionLabel);
        System.out.println(sessionData.roadData.roadCondition);

    }

    @FXML
    private void onFrictionClick() {
        infoLabel.setText(frictionLabel.getText());
        changeRCColors(frictionLabel);
        System.out.println(sessionData.roadData.frictionCondition);
    }

    @FXML
    private void onAlertsClick() {
        infoLabel.setText(alertsLabel.getText());
        changeColorsA();
    }

    private void changeRCColors(Label blueLabel) {
        alertsLabel.getStyleClass().removeAll("basicHeadingBlue");
        alertsLabel.getStyleClass().add("basicHeadingGrey");

        RCLabel.getStyleClass().removeAll("basicHeadingGrey");
        RCLabel.getStyleClass().add("basicHeadingWhite");

        for(var l : RCLabels) {
            l.getStyleClass().removeAll("basicTextGrey");
            l.getStyleClass().removeAll("basicTextBlue");
            if(l.getText().equals(blueLabel.getText())) {
                l.getStyleClass().add("basicTextBlue");
            } else {
                l.getStyleClass().add("basicTextWhite");
            }
        }
    }

    private void changeColorsA() {
        RCLabel.getStyleClass().removeAll("basicHeadingWhite");
        RCLabel.getStyleClass().add("basicHeadingGrey");

        for(var l : RCLabels) {
            l.getStyleClass().removeAll("basicTextWhite");
            l.getStyleClass().removeAll("basicTextBlue");
            l.getStyleClass().add("basicTextGrey");
        }
        alertsLabel.getStyleClass().removeAll("basicHeadingGrey");
        alertsLabel.getStyleClass().add("basicHeadingBlue");
    }

    @FXML
    private void onCalculateClick() throws IOException, URISyntaxException, InterruptedException {

        sessionData.createRoadData();
        sessionData.roadData.setForecastConditions(timeFrame);
        alertsLabel.setText(sessionData.roadData.trafficMessageAmount + " ALERTS");

        //MAINTENANCE
        sessionData.createMaintenance("",startDatePicker.getValue(),endDatePicker.getValue());
    }

    @FXML
    public void changeTimeFrame() {
        String str = timeFrameComboBox.getValue();
        if(Objects.equals(str, "CURRENT")){
            timeFrame = 0;
        }else{
            timeFrame = Character.getNumericValue(str.charAt(0));
        }
    }
    private Date getStartDate(){
        LocalDate startLocalDate = startDatePicker.getValue();
        if(startLocalDate == null){
            System.err.println("Dates cant be null");
            return null;
        }
        Instant instant = Instant.from(startLocalDate.atStartOfDay(ZoneId.systemDefault()));
        Date startDate = Date.from(instant);

        return sessionData.helperFunctions.trimToStart(startDate,0);
    }

    private Date getEndDate(){
        LocalDate endLocalDate = endDatePicker.getValue();
        if(endLocalDate == null){
            System.err.println("Dates cant be null");
            return null;
        }
        Instant instant2 = Instant.from(endLocalDate.atStartOfDay(ZoneId.systemDefault()));
        Date endDate = Date.from(instant2);

        return  sessionData.helperFunctions.trimToEnd(endDate,0);
    }
}
