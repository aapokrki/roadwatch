package fi.tuni.roadwatch;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.ArrayList;

public class RoadController {
    private final ArrayList<Label> RCLabels = new ArrayList<>();
    @FXML
    private Label infoLabel;
    // Road condition labels aka RC labels.
    @FXML
    private Label RCLabel;
    @FXML
    private Label visibilityLabel;
    @FXML
    private Label frictionLabel;
    @FXML
    private Label precipitationLabel;
    @FXML
    private Label winterSlipperinessLabel;
    @FXML
    private Label overallConditionLabel;

    @FXML
    private Label alertsLabel;

    private SessionData sessionData;

    public void setSessionData(SessionData sessionData) {
        this.sessionData = sessionData;
        RCLabels.add(RCLabel);
        RCLabels.add(visibilityLabel);
        RCLabels.add(frictionLabel);
        RCLabels.add(precipitationLabel);
        RCLabels.add(winterSlipperinessLabel);
        RCLabels.add(overallConditionLabel);
    }

    @FXML
    private void onVisibilityClick() {
        infoLabel.setText(visibilityLabel.getText());
        changeRCColors(visibilityLabel);
    }

    @FXML
    private void onFrictionClick() {
        infoLabel.setText(frictionLabel.getText());
        changeRCColors(frictionLabel);
    }

    @FXML
    private void onPrecipitationClick() {
        infoLabel.setText(precipitationLabel.getText());
        changeRCColors(precipitationLabel);
    }

    @FXML
    private void onWinterSlipperinessClick() {
        infoLabel.setText(winterSlipperinessLabel.getText());
        changeRCColors(winterSlipperinessLabel);
    }

    @FXML
    private void onOverallConditionClick() {
        infoLabel.setText(overallConditionLabel.getText());
        changeRCColors(overallConditionLabel);
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
}
