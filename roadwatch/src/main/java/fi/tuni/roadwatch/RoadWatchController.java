package fi.tuni.roadwatch;

import com.sothawo.mapjfx.Projection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class RoadWatchController {
    // Components
    private Stage stage;
    private Scene scene;
    public BorderPane mapPane;

    @FXML
    private Button homeButton;
    @FXML
    private Button weatherButton;
    @FXML
    private Button combineButton;
    @FXML
    private Button preferencesButton;
    @FXML
    private Button roadDataButton;

    @FXML
    public void initialize(){


    }
    // Actions
    public void loadHome(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/roadwatch.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void loadMap() throws IOException {
        String mapFxmlFile = "fxml/mapview.fxml";
        FXMLLoader mapFxmlLoader = new FXMLLoader();
        Parent rootNode = mapFxmlLoader.load(getClass().getResourceAsStream(mapFxmlFile));

        final MapController mapController = mapFxmlLoader.getController();
        final Projection projection = Projection.WGS_84;
//        final Projection projection = getParameters().getUnnamed().contains("wgs84")
//                ? Projection.WGS_84 : Projection.WEB_MERCATOR;
        mapController.initMapAndControls(projection);
        Pane mapView = (Pane) rootNode;

        mapPane.setCenter(mapView);
    }

    public void loadWeather(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/weather.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void loadCombine(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/combine.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void loadPreferences(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/preferences.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void loadRoadData(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/roaddata.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}