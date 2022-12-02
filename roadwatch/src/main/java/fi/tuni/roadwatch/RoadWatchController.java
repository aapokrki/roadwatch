package fi.tuni.roadwatch;

import com.sothawo.mapjfx.Projection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Objects;


/**
 * This class controls the main UX components for the program. It contains the navigator bar
 * and launches new fxml site's when navigator bar's icons are clicked.
 */
public class RoadWatchController {
    public GridPane root;
    public BorderPane mapPane;
    public BorderPane infoPane;
    public MapController mapController;

    public Pane home;
    public HomeController homeController;

    public Pane weather;
    public WeatherController weatherController;

    public Pane combine;
    public CombineController combineController;

    public Pane preferences;
    public PreferencesController preferencesController;

    public Pane road;
    public RoadController roadController;

    private SessionData sessionData;

    @FXML
    private Label siteLabel;
    @FXML
    private Label trafficMessageCount;

    /**
     * Initializes the main window.
     * @param sessionData
     * @param helperFunctions Helps controllers navigate with sessionData.
     * @throws IOException
     */
    public void initialize(SessionData sessionData, HelperFunctions helperFunctions) throws IOException {
        this.sessionData = sessionData;
        sessionData.helperFunctions = helperFunctions;

        helperFunctions.setSessionData(sessionData);
        sessionData.setHelperFunctions(helperFunctions);

        String newLabel = sessionData.trafficMessage.features.size() + " Traffic announcements";
        trafficMessageCount.setText(newLabel);
        loadHome();
    }

    /**
     * Setups map.
     * @throws IOException
     */
    public void loadMap() throws IOException {
        FXMLLoader mapFxmlLoader = new FXMLLoader();
        Parent rootNode = mapFxmlLoader.load(getClass().getResourceAsStream("fxml/mapview.fxml"));

        mapController = mapFxmlLoader.getController();
        final Projection projection = Projection.WEB_MERCATOR;

        // init map controls and set sessiondata
        mapController.setSessionData(sessionData);
        mapController.initMapAndControls(projection);
        sessionData.presetLocations = mapController.getLocationsAsList();

                Pane mapView = (Pane) rootNode;
        mapPane.setCenter(mapView);
    }

    /**
     * Sets home fxml.
     * @throws IOException
     */
    public void loadHome() throws IOException {
        if(homeController == null){
            FXMLLoader homeFxmlLoader = new FXMLLoader();
            Parent rootNode = homeFxmlLoader.load(getClass().getResourceAsStream("fxml/home.fxml"));
            homeController = homeFxmlLoader.getController();
            home = (Pane) rootNode;
        }
        infoPane.setCenter(home);
        siteLabel.setText("HOME");
        StackPane.setAlignment(infoPane, Pos.CENTER_RIGHT);
        mapPane.setVisible(true);
        changeLayout("NORMAL");
    }


    /**
     * Sets weather fxml.
     * @throws IOException
     */
    public void loadWeather() throws IOException {
        if(weatherController == null){
            FXMLLoader weatherFxmlLoader = new FXMLLoader();
            Parent rootNode = weatherFxmlLoader.load(getClass().getResourceAsStream("fxml/weather.fxml"));
            weatherController = weatherFxmlLoader.getController();
            weatherController.initializeController(sessionData);
            sessionData.setHelperFunctions(sessionData.helperFunctions);
            weather = (Pane) rootNode;
        }

        infoPane.setCenter(weather);
        siteLabel.setText("WEATHER");
        changeLayout("NORMAL");
    }

    /**
     * Sets combine fxml.
     * @throws IOException
     * @throws URISyntaxException
     * @throws ParserConfigurationException
     * @throws ParseException
     * @throws InterruptedException
     * @throws SAXException
     */
    public void loadCombine() throws IOException, URISyntaxException, ParserConfigurationException, ParseException, InterruptedException, SAXException {
        if(combineController == null){
            FXMLLoader combineFxmlLoader = new FXMLLoader();
            Parent rootNode = combineFxmlLoader.load(getClass().getResourceAsStream("fxml/combine.fxml"));
            combineController = combineFxmlLoader.getController();
            combineController.initializeController(sessionData);
            combine = (Pane) rootNode;
        }
        infoPane.setCenter(combine);
        siteLabel.setText("COMBINE");
        changeLayout("WIDE");
    }

    /**
     * Sets preference fxml.
     * @throws IOException
     */
    public void loadPreferences() throws IOException {
        if(preferencesController == null){
            FXMLLoader preferencesFxmlLoader = new FXMLLoader();
            Parent rootNode = preferencesFxmlLoader.load(getClass().getResourceAsStream("fxml/preferences.fxml"));
            preferencesController = preferencesFxmlLoader.getController();
            preferencesController.initializeController(sessionData);
            preferences = (Pane) rootNode;
        }
        infoPane.setCenter(preferences);
        siteLabel.setText("PREFERENCES");
        changeLayout("NORMAL");
    }

    /**
     * Sets road data fxml.
     * @throws IOException
     * @throws URISyntaxException
     */
    public void loadRoadData() throws IOException, URISyntaxException {
        if(roadController == null){
            FXMLLoader roadFxmlLoader = new FXMLLoader();
            Parent rootNode = roadFxmlLoader.load(getClass().getResourceAsStream("fxml/roaddata.fxml"));
            roadController = roadFxmlLoader.getController();
            roadController.initializeController(sessionData);
            road = (Pane) rootNode;
        }
        infoPane.setCenter(road);
        siteLabel.setText("ROAD DATA");
        changeLayout("NORMAL");
    }

    /**
     * Adjusts screen view to show map when it's needed.
     * @param viewType normal when map is wanted visible, wide when it is not
     */
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
}