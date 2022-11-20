package fi.tuni.roadwatch;

import com.sothawo.mapjfx.Projection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.controlsfx.control.spreadsheet.Grid;

import java.io.IOException;
import java.util.Objects;


public class RoadWatchController {
    // Components
    private Rectangle2D screen;
    private Stage stage;
    private Scene scene;
    public GridPane root;
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
    @FXML
    private Label trafficMessageCount;

    public void setScreenBounds(Rectangle2D screenBounds) {
        this.screen = screenBounds;
    }

    public void setScreenSize() {
//        root.minWidth(screen.getMinX());
//        root.minHeight(screen.getMinY());
//        root.maxWidth(screen.getMaxX());
//        root.maxHeight(screen.getMaxY());
//        root.prefWidth(screen.getWidth());
//        root.prefHeight(screen.getHeight());
    }

    public void setSessionData(SessionData sessionData){
        this.sessionData = sessionData;
    }

    public void loadMap() throws IOException {
        FXMLLoader mapFxmlLoader = new FXMLLoader();
        Parent rootNode = mapFxmlLoader.load(getClass().getResourceAsStream("fxml/mapview.fxml"));

        mapController = mapFxmlLoader.getController();
        final Projection projection = Projection.WEB_MERCATOR;

        // init map controls and set sessiondata
        mapController.setSessionData(sessionData);
        mapController.initMapAndControls(projection);

        Pane mapView = (Pane) rootNode;

        mapPane.setCenter(mapView);
    }

    // Actions
    public void loadHome() throws IOException {

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


    public void loadWeather() throws IOException {

        if(weatherController == null){
            FXMLLoader weatherFxmlLoader = new FXMLLoader();
            Parent rootNode = weatherFxmlLoader.load(getClass().getResourceAsStream("fxml/weather.fxml"));
            weatherController = weatherFxmlLoader.getController();
            weatherController.setSessionData(sessionData);
            weather = (Pane) rootNode;
        }

        infoPane.setCenter(weather);
        siteLabel.setText("WEATHER");
        changeLayout("NORMAL");

    }

    public void loadQuickView() throws IOException {
        if(quickViewController == null){
            FXMLLoader quickViewFxmlLoader = new FXMLLoader();
            Parent rootNode = quickViewFxmlLoader.load(getClass().getResourceAsStream("fxml/quickview.fxml"));
            quickViewController = quickViewFxmlLoader.getController();
            quickViewController.setSessionData(sessionData);
            quickViewController.setData(mapPane, infoPane, siteLabel);
            quickView = (Pane) rootNode;
        }
        infoPane.setCenter(quickView);
        siteLabel.setText("QUICK VIEW");
        changeLayout("NORMAL");

    }

    public void loadPreferences() throws IOException {
        if(preferencesController == null){
            FXMLLoader preferencesFxmlLoader = new FXMLLoader();
            Parent rootNode = preferencesFxmlLoader.load(getClass().getResourceAsStream("fxml/preferences.fxml"));
            preferencesController = preferencesFxmlLoader.getController();
            preferencesController.setSessionData(sessionData);
            preferences = (Pane) rootNode;
        }
        infoPane.setCenter(preferences);
        siteLabel.setText("PREFERENCES");
        changeLayout("WIDE");

    }

    public void loadRoadData() throws IOException {

        if(roadController == null){
            FXMLLoader roadFxmlLoader = new FXMLLoader();
            Parent rootNode = roadFxmlLoader.load(getClass().getResourceAsStream("fxml/roaddata.fxml"));
            roadController = roadFxmlLoader.getController();
            roadController.setSessionData(sessionData);
            road = (Pane) rootNode;
        }
        infoPane.setCenter(road);
        siteLabel.setText("ROAD DATA");
        changeLayout("WIDE");

    }

    void changeLayout(String viewType){

        if(Objects.equals(viewType, "NORMAL")){
            mapPane.setVisible(true);
            GridPane.setConstraints(infoPane,1,3);
            GridPane.setColumnSpan(infoPane,2);

        }

        if(Objects.equals(viewType, "WIDE")){
            mapPane.setVisible(false);
            GridPane.setConstraints(infoPane,0,3);
            GridPane.setColumnSpan(infoPane,3);
        }
    }

    void onButtonClick(Button selected, String oldIcon, String newIcon, Button b2, Button b3, Button b4,
                       String ni2, String ni3, String ni4) {
        selected.getStyleClass().removeAll(oldIcon);
        selected.getStyleClass().add(newIcon);
        b2.getStyleClass().add(ni2);
        b3.getStyleClass().add(ni3);
        b4.getStyleClass().add(ni4);
    }
}