package fi.tuni.roadwatch;

import fi.tuni.roadwatch.CombineController;
import fi.tuni.roadwatch.SessionData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class QuickViewController {
    private SessionData sessionData;
    private BorderPane mapPane;
    private BorderPane infoPane;
    private Label siteLabel;

    @FXML
    private Button combinePageButton;
    @FXML
    public Pane combine;
    // TODO: Tiedot päivittyy tilanteen mukaan.
    @FXML
    private Label conditionLabel;
    @FXML
    private Label maintenanceLabel;
    @FXML
    private Label alertsLabel;

    public CombineController combineController;

    public void setSessionData(SessionData sessionData) {
        this.sessionData = sessionData;
    }

    public void setData(BorderPane mapPane, BorderPane infoPane, Label siteLabel) {
        this.mapPane = mapPane;
        this.infoPane = infoPane;
        this.siteLabel = siteLabel;
    }

    public void loadCombine(ActionEvent event) throws IOException {
        //TODO: toimii mut kai tän voi tehä paremmi i guess
        if(combineController == null){
            FXMLLoader combineFxmlLoader = new FXMLLoader();
            Parent rootNode = null;
            try {
                rootNode = combineFxmlLoader.load(getClass().getResourceAsStream("fxml/combine.fxml"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            combineController = combineFxmlLoader.getController();
            combineController.setSessionData(sessionData);
            combine = (Pane) rootNode;
        }

        mapPane.setVisible(false);
        GridPane.setConstraints(infoPane,0,3);
        GridPane.setColumnSpan(infoPane,3);

        siteLabel.setText("COMBINE");
        infoPane.setCenter(combine);

    }
}
