package fi.tuni.roadwatch;

import com.sothawo.mapjfx.Coordinate;
import com.sothawo.mapjfx.Projection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
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
    public BorderPane infoPane;
    public MapController mapController;
    private SessionData sessionData;

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

    public void setSessionData(RoadWatchController roadWatchController){
        this.sessionData= new SessionData(roadWatchController, mapController);
        mapController.setSessionData(sessionData);

    }

    public void loadMap() throws IOException {
        FXMLLoader mapFxmlLoader = new FXMLLoader();
        Parent rootNode = mapFxmlLoader.load(getClass().getResourceAsStream("fxml/mapview.fxml"));

        mapController = mapFxmlLoader.getController();
        final Projection projection = Projection.WEB_MERCATOR;
//        final Projection projection = getParameters().getUnnamed().contains("wgs84")
//                ? Projection.WGS_84 : Projection.WEB_MERCATOR;
        mapController.initMapAndControls(projection);
        Pane mapView = (Pane) rootNode;

        mapPane.setCenter(mapView);
    }

    // Actions
    public void loadHome(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/home.fxml")));
        Pane home = (Pane) root;
        infoPane.setCenter(home);

//        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
//        scene = new Scene(root);
//        stage.setScene(scene);
//        stage.show();
    }


    public void loadWeather(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/weather.fxml")));
        Pane weather = (Pane) root;
        infoPane.setCenter(weather);


//        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
//        scene = new Scene(root);
//        stage.setScene(scene);
//        stage.show();
    }

    public void loadCombine(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/combine.fxml")));
        Pane combine = (Pane) root;
        infoPane.setCenter(combine);

        Label label = (Label) combine.lookup("#combineLabel");
        label.setText(sessionData.currentCoordinates.toString());

//        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
//        scene = new Scene(root);
//        stage.setScene(scene);
//        stage.show();
    }

    public void loadPreferences(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/preferences.fxml")));
        Pane preferences = (Pane) root;
        infoPane.setCenter(preferences);
//        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
//        scene = new Scene(root);
//        stage.setScene(scene);
//        stage.show();
    }

    public void loadRoadData(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/roaddata.fxml")));
        Pane roadData = (Pane) root;
        infoPane.setCenter(roadData);
//        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
//        scene = new Scene(root);
//        stage.setScene(scene);
//        stage.show();
    }
}