package fi.tuni.javafxproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class HelloController {

    @FXML
    private AnchorPane dataPane;

    @FXML
    private Label logoLabel;

    @FXML
    private Label logoLabel1;

    @FXML
    private Label logoLabel11;

    @FXML
    private Label logoLabel12;

    @FXML
    private BorderPane mapPane;

    @FXML
    private AnchorPane roadPane;

    @FXML
    private AnchorPane weatherPane;

    @FXML
    void onHomeBtnClicked(ActionEvent event) {
        roadPane.setDisable(false);
        roadPane.setVisible(true);
        weatherPane.setPrefHeight(200);

    }

    @FXML
    void onWeatherBtnClicked(ActionEvent event) {
        roadPane.setDisable(true);
        roadPane.setVisible(false);
        weatherPane.setPrefHeight(450);
    }

    @FXML
    void onMapBtnClicked(ActionEvent event) {
        System.out.println("Map");
        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("map-view.fxml");
        mapPane.setCenter(view);
    }

    void initializeMap(Pane view){
        mapPane.setCenter(view);
    }

    public void onLahtiSelect(ActionEvent actionEvent) {
    }
}
