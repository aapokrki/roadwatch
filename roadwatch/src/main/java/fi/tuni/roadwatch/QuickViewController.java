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
        combinePageButton.setOnAction((ActionEvent e) -> {
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

            // Test output of setting coordinates to a view
            if(sessionData.currentCoordinates != null){
                combineController.setCoordinates();
            }
            mapPane.setVisible(false);
            infoPane.setCenter(combine);
            StackPane.setAlignment(infoPane, Pos.CENTER);
            siteLabel.setText("COMBINE");
        });
    }
}
