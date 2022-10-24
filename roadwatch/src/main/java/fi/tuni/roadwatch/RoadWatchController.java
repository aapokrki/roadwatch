package fi.tuni.roadwatch;

import com.sothawo.mapjfx.Projection;
import fi.tuni.roadwatch.controllers.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;


/** TODO: Pitää uudelleen miettiä fxml:ät ja controllerit
 *  TODO: Joko ikkunalla yksi fxml jonka tietoja sit muutellaan ja sille oma controller joka muuttelee
 *  TODO: Tai Useampi fxml eri näkymille ja niille kaikille controllerit tai näin mutta jpku hassu kikka ilman kontrollereita
 */


public class RoadWatchController {
    // Components
    private Stage stage;
    private Scene scene;
    public VBox root;
    public StackPane contentPane;
    public BorderPane mapPane;
    public BorderPane infoPane;
    public MapController mapController;

    public Pane home;
    public HomeController homeController;

    public Pane weather;
    public WeatherController weatherController;

    public Pane quickView;
    public QuickViewController quickViewController;

    public Pane preferences;
    public PreferencesController preferencesController;

    public Pane road;
    public RoadController roadController;

    private SessionData sessionData;

    //MAINWINDOW
    @FXML
    private Label logo;
    @FXML
    private ButtonBar buttonBar;
    @FXML
    private Button homeButton;
    @FXML
    private Button weatherButton;
    @FXML
    private Button quickViewButton;
    @FXML
    private Button preferencesButton;
    @FXML
    private Button roadDataButton;
    @FXML
    private Label siteLabel;


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

        if(homeController == null){
            FXMLLoader homeFxmlLoader = new FXMLLoader();
            Parent rootNode = homeFxmlLoader.load(getClass().getResourceAsStream("fxml/home.fxml"));
            homeController = homeFxmlLoader.getController();
            homeController.setSessionData(sessionData);
            home = (Pane) rootNode;
        }

        infoPane.setCenter(home);
        siteLabel.setText("HOME");
        StackPane.setAlignment(infoPane, Pos.CENTER_RIGHT);
        mapPane.setVisible(true);
    }


    public void loadWeather(ActionEvent event) throws IOException {

        if(weatherController == null){
            FXMLLoader weatherFxmlLoader = new FXMLLoader();
            Parent rootNode = weatherFxmlLoader.load(getClass().getResourceAsStream("fxml/weather.fxml"));
            weatherController = weatherFxmlLoader.getController();
            weatherController.setSessionData(sessionData);
            weather = (Pane) rootNode;
        }

        infoPane.setCenter(weather);
        siteLabel.setText("WEATHER");
        StackPane.setAlignment(infoPane, Pos.CENTER_RIGHT);
        mapPane.setVisible(true);

    }

    public void loadQuickView(ActionEvent event) throws IOException {
        if(quickViewController == null){
            FXMLLoader quickViewFxmlLoader = new FXMLLoader();
            Parent rootNode = quickViewFxmlLoader.load(getClass().getResourceAsStream("fxml/quickview.fxml"));
            quickViewController = quickViewFxmlLoader.getController();
            quickViewController.setSessionData(sessionData);
            quickViewController.setData(mapPane, infoPane, siteLabel);
            quickView = (Pane) rootNode;
        }

        // Test output of setting coordinates to a view
        if(sessionData.currentCoordinates != null){
            quickViewController.setCoordinates();
        }
        infoPane.setCenter(quickView);
        siteLabel.setText("QUICK VIEW");
    }

    public void loadPreferences(ActionEvent event) throws IOException {
        if(preferencesController == null){
            FXMLLoader preferencesFxmlLoader = new FXMLLoader();
            Parent rootNode = preferencesFxmlLoader.load(getClass().getResourceAsStream("fxml/preferences.fxml"));
            preferencesController = preferencesFxmlLoader.getController();
            preferencesController.setSessionData(sessionData);
            preferences = (Pane) rootNode;
        }
        mapPane.setVisible(false);
        infoPane.setCenter(preferences);
        StackPane.setAlignment(infoPane, Pos.CENTER);
        siteLabel.setText("PREFERENCES");
    }

    public void loadRoadData(ActionEvent event) throws IOException {

        if(roadController == null){
            FXMLLoader roadFxmlLoader = new FXMLLoader();
            Parent rootNode = roadFxmlLoader.load(getClass().getResourceAsStream("fxml/roaddata.fxml"));
            roadController = roadFxmlLoader.getController();
            roadController.setSessionData(sessionData);
            road = (Pane) rootNode;
        }
        mapPane.setVisible(false);
        infoPane.setCenter(road);
        StackPane.setAlignment(infoPane, Pos.CENTER);
        siteLabel.setText("ROAD DATA");
    }

    void onButtonClick(ActionEvent event, Button btn, String oldIcon, String newIcon) {
        btn.setOnAction((ActionEvent e) -> {
            btn.getStyleClass().removeAll(oldIcon);
            btn.getStyleClass().add(newIcon);
        });
    }
}