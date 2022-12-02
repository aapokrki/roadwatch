package fi.tuni.roadwatch;

import com.sothawo.mapjfx.*;
import com.sothawo.mapjfx.event.MapViewEvent;
import javafx.animation.Transition;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import java.util.*;

public class MapController {

    private SessionData sessionData;

    /** Coordinates to border Finland */
    private static final Coordinate coordNorhWest= new Coordinate(70.30824014125528, 16.352667758380154);
    private static final Coordinate coordNorthEast = new Coordinate(70.291002382638, 35.52837666547143);
    private static final Coordinate coordSouthEast = new Coordinate(59.27583073122375, 31.965916435823015);
    private static final Coordinate coordSouthWest = new Coordinate(59.26658753324523, 16.352667758380154);
    private static final Extent extentFinland = Extent.forCoordinates(coordNorhWest, coordNorthEast, coordSouthEast, coordSouthWest);

    // Preset city locations
    private static final ArrayList<Coordinate> coordTampere = new ArrayList<Coordinate>(
            Arrays.asList(  new Coordinate(61.523, 23.645),
                    new Coordinate(61.523, 23.903),
                    new Coordinate(61.42, 23.903),
                    new Coordinate(61.42, 23.645))
    );
    private static final ArrayList<Coordinate> coordLahti = new ArrayList<Coordinate>(
            Arrays.asList( new Coordinate(60.922, 25.57),
                    new Coordinate(60.922, 25.805),
                    new Coordinate(61.036, 25.805),
                    new Coordinate(61.036, 25.57))
    );
    private static final ArrayList<Coordinate> coordKotka = new ArrayList<Coordinate>(
            Arrays.asList( new Coordinate(60.411, 26.776),
                    new Coordinate(60.411, 27.047),
                    new Coordinate(60.592, 27.047),
                    new Coordinate(60.592, 26.776))
    );
    private static final ArrayList<Coordinate> coordTurku = new ArrayList<Coordinate>(
            Arrays.asList(  new Coordinate(60.3335222, 22.0661599),
                    new Coordinate(60.3335222, 22.4581649),
                    new Coordinate(60.7372962, 22.4581649),
                    new Coordinate(60.7372962, 22.0661599))
    );
    private static final ArrayList<Coordinate> coordHyvinkaa = new ArrayList<Coordinate>(
            Arrays.asList(  new Coordinate(60.5081313, 24.5629319),
                    new Coordinate(60.5081313, 25.1124256),
                    new Coordinate(60.6846302, 25.1124256),
                    new Coordinate(60.6846302, 24.5629319))
    );
    private static final ArrayList<Coordinate> coordPorvoo = new ArrayList<Coordinate>(
            Arrays.asList(  new Coordinate(60.11, 25.3290779),
                    new Coordinate(60.11, 26.2712854),
                    new Coordinate(60.5510037, 26.2712854),
                    new Coordinate(60.5510037, 25.3290779))
    );
    private static final ArrayList<Coordinate> coordHelsinki = new ArrayList<Coordinate>(
            Arrays.asList(  new Coordinate(60.11, 24.552),
                    new Coordinate(60.11, 25.218),
                    new Coordinate(60.345, 25.218),
                    new Coordinate(60.345, 24.552))
    );
    private static final ArrayList<Coordinate> coordJyvaskyla = new ArrayList<Coordinate>(
            Arrays.asList(  new Coordinate(61.8392056, 25.2607866),
                    new Coordinate(61.8392056, 26.051348),
                    new Coordinate(62.428747, 26.051348),
                    new Coordinate(62.428747, 25.2607866))
    );
    private static final ArrayList<Coordinate> coordOulu = new ArrayList<Coordinate>(
            Arrays.asList(  new Coordinate(64.844412, 24.1149326),
                    new Coordinate(64.844412, 26.7715283),
                    new Coordinate(65.5643522, 26.7715283),
                    new Coordinate(65.5643522, 24.1149326))
    );
    private static final ArrayList<Coordinate> coordRovaniemi = new ArrayList<Coordinate>(
            Arrays.asList(  new Coordinate(66.1555137, 24.73658),
                    new Coordinate(66.1555137, 27.3266437),
                    new Coordinate(67.1794991, 27.3266437),
                    new Coordinate(67.1794991, 24.73658))
    );

    /** the markers. */
    private final Marker markerKotka;
    private final Marker markerTampere;
    private final Marker markerClick;
    private final Marker markerLahti;
    private final Marker markerTurku;
    private final Marker markerHyvinkaa;
    private final Marker markerPorvoo;
    private final Marker markerHelsinki;
    private final Marker markerJyvaskyla;
    private final Marker markerOulu;
    private final Marker markerRovaniemi ;

    /** the MapView containing the map */
    @FXML
    private MapView mapView;

    /** Accordion for all the different options */
    @FXML
    private Accordion leftControls;
    @FXML
    private HBox botControls;
    @FXML
    private HBox zoomControls;
    @FXML
    private TitledPane optionsLocations;
    @FXML
    private VBox optionsLocationsVbox;


    // Locationbuttons
    @FXML
    private Button buttonKotka;
    @FXML
    private Button buttonTampere;
    @FXML
    private Button buttonLahti;
    @FXML
    private Button buttonTurku;
    @FXML
    private Button buttonHyvinkaa;
    @FXML
    private Button buttonPorvoo;
    @FXML
    private Button buttonHelsinki;
    @FXML
    public Button buttonJyvaskyla;
    @FXML
    public Button buttonOulu;
    @FXML
    public Button buttonRovaniemi;

    // Checkboxes for markers
    @FXML
    private CheckBox checkKotkaMarker;
    @FXML
    private CheckBox checkTampereMarker;
    @FXML
    private CheckBox checkLahtiMarker;
    @FXML
    public CheckBox checkTurkuMarker;
    @FXML
    public CheckBox checkHyvinkaaMarker;
    @FXML
    public CheckBox checkPorvooMarker;
    @FXML
    public CheckBox checkHelsinkiMarker;
    @FXML
    public CheckBox checkJyvaskylaMarker;
    @FXML
    public CheckBox checkOuluMarker;
    @FXML
    public CheckBox checkRovaniemiMarker;

    /** Check button for click marker */
    @FXML
    private CheckBox checkClickMarker;

    /** Coordinateline for polygon drawing. */
    private CoordinateLine polygonLine;
    /** Check Button for polygon drawing mode. */
    @FXML
    private CheckBox checkDrawPolygon;

    @FXML
    private Button buttonZoomOut;
    @FXML
    public Button buttonAddPolygon;
    @FXML
    public Button buttonClearPolygon;

    /**
     * Calculates the middle of a polygon
     * @param coordinates
     * @return
     */
    public Coordinate getPolygonMiddle(ArrayList<Coordinate> coordinates){
        Double lat  = coordinates.stream().map(Coordinate::getLatitude).mapToDouble(Double::doubleValue).sum() / coordinates.size();
        Double lon  = coordinates.stream().map(Coordinate::getLongitude).mapToDouble(Double::doubleValue).sum() / coordinates.size();
        System.out.println(lat + "," + lon);
        return new Coordinate(lat,lon);
    }

    public MapController(){

        // a couple of markers using the provided ones
        markerKotka = new Marker(Objects.requireNonNull(getClass().getResource("/pictures/map-icon.png")),-11,-11)
                .setPosition(getPolygonMiddle(coordKotka))
                .setVisible(true);

        markerTampere = new Marker(Objects.requireNonNull(getClass().getResource("/pictures/map-icon.png")),-11,-11)
                .setPosition(getPolygonMiddle(coordTampere))
                .setVisible(true);
        markerLahti = new Marker(Objects.requireNonNull(getClass().getResource("/pictures/map-icon.png")),-11,-11)
                .setPosition(getPolygonMiddle(coordLahti))
                .setVisible(true);
        markerTurku = new Marker(Objects.requireNonNull(getClass().getResource("/pictures/map-icon.png")),-11,-11)
                .setPosition(getPolygonMiddle(coordTurku))
                .setVisible(false);
        markerHyvinkaa = new Marker(Objects.requireNonNull(getClass().getResource("/pictures/map-icon.png")),-11,-11)
                .setPosition(getPolygonMiddle(coordHyvinkaa))
                .setVisible(false);
        markerPorvoo = new Marker(Objects.requireNonNull(getClass().getResource("/pictures/map-icon.png")),-11,-11)
                .setPosition(getPolygonMiddle(coordPorvoo))
                .setVisible(false);
        markerHelsinki = new Marker(Objects.requireNonNull(getClass().getResource("/pictures/map-icon.png")),-11,-11)
                .setPosition(getPolygonMiddle(coordHelsinki))
                .setVisible(false);
        markerJyvaskyla = new Marker(Objects.requireNonNull(getClass().getResource("/pictures/map-icon.png")),-11,-11)
                .setPosition(getPolygonMiddle(coordJyvaskyla))
                .setVisible(false);
        markerOulu = new Marker(Objects.requireNonNull(getClass().getResource("/pictures/map-icon.png")),-11,-11)
                .setPosition(getPolygonMiddle(coordOulu))
                .setVisible(false);
        markerRovaniemi = new Marker(Objects.requireNonNull(getClass().getResource("/pictures/map-icon.png")),-11,-11)
                .setPosition(getPolygonMiddle(coordRovaniemi))
                .setVisible(false);

        // no position for click marker yet
        markerClick = new Marker(Objects.requireNonNull(getClass().getResource("/pictures/map-click-icon.png")),-5,-5).setVisible(false);

    }

    /**
     * called after the fxml is loaded and all objects are created. This is not called initialize any more,
     * because we need to pass in the projection before initializing.
     *
     * @param projection
     *     the projection to use in the map.
     */
    public void initMapAndControls(Projection projection) {

        // set the custom css file for the MapView
        mapView.setCustomMapviewCssURL(Objects.requireNonNull(getClass().getResource("/custom_mapview.css")));

        leftControls.setExpandedPane(optionsLocations);

        // set the controls to disabled, this will be changed when the MapView is intialized
        setControlsDisable(true);

        // wire up the location buttons
        buttonKotka.setOnAction(event -> setLocationTo(coordKotka));
        buttonTampere.setOnAction(event -> setLocationTo(coordTampere));
        buttonLahti.setOnAction(event -> setLocationTo(coordLahti));
        buttonTurku.setOnAction(event -> setLocationTo(coordTurku));
        buttonHyvinkaa.setOnAction(event -> setLocationTo(coordHyvinkaa));
        buttonPorvoo.setOnAction(event -> setLocationTo(coordPorvoo));
        buttonHelsinki.setOnAction(event -> setLocationTo(coordHelsinki));
        buttonJyvaskyla.setOnAction(event -> setLocationTo(coordJyvaskyla));
        buttonOulu.setOnAction(event -> setLocationTo(coordOulu));
        buttonRovaniemi.setOnAction(event -> setLocationTo(coordRovaniemi));

        // wire the zoomout button
        buttonZoomOut.setOnAction(event -> mapView.setExtent(extentFinland));

        //wire the add button
        buttonAddPolygon.setOnAction(event -> {
            sessionData.calculateMinMaxCoordinates();

        });
        //wire the clear button
        buttonClearPolygon.setOnAction(event -> {
            if(polygonLine != null){
                mapView.removeCoordinateLine(polygonLine);
                polygonLine = null;
                sessionData.polyCoordinates.clear();
            }
        });

        // watch the MapView's initialized property to finish initialization
        mapView.initializedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                afterMapIsInitialized();
            }
        });

        mapView.setMapType(MapType.OSM);
        setupEventHandlers();

        // add the graphics to the checkboxes
        checkClickMarker.setGraphic(
            new ImageView(new Image(markerClick.getImageURL().toExternalForm(), 16.0, 16.0, true, true)));

        // bind the checkboxes to the markers visibility
        checkKotkaMarker.selectedProperty().bindBidirectional(markerKotka.visibleProperty());
        checkTampereMarker.selectedProperty().bindBidirectional(markerTampere.visibleProperty());
        checkLahtiMarker.selectedProperty().bindBidirectional(markerLahti.visibleProperty());
        checkTurkuMarker.selectedProperty().bindBidirectional(markerTurku.visibleProperty());
        checkHyvinkaaMarker.selectedProperty().bindBidirectional(markerHyvinkaa.visibleProperty());
        checkPorvooMarker.selectedProperty().bindBidirectional(markerPorvoo.visibleProperty());
        checkHelsinkiMarker.selectedProperty().bindBidirectional(markerHelsinki.visibleProperty());
        checkJyvaskylaMarker.selectedProperty().bindBidirectional(markerJyvaskyla.visibleProperty());
        checkOuluMarker.selectedProperty().bindBidirectional(markerOulu.visibleProperty());
        checkRovaniemiMarker.selectedProperty().bindBidirectional(markerRovaniemi.visibleProperty());

        checkClickMarker.selectedProperty().bindBidirectional(markerClick.visibleProperty());
        checkClickMarker.setSelected(true);


        // add the polygon check handler
        ChangeListener<Boolean> polygonListener =
            (observable, oldValue, newValue) -> {
                if (!newValue && polygonLine != null) {
                    mapView.removeCoordinateLine(polygonLine);
                    polygonLine = null;
                    sessionData.polyCoordinates.clear();
                }
            };
        checkDrawPolygon.selectedProperty().addListener(polygonListener);
        checkDrawPolygon.setSelected(true);

        mapView.constrainExtent(extentFinland);

        // finally initialize the map view
        mapView.initialize(Configuration.builder()
            .projection(projection)
            .showZoomControls(false)
            .build());

    }

    /**
     * sets the focus on given location, selects it on the Map
     */
    private void setLocationTo(ArrayList<Coordinate> location){
        buttonClearPolygon.fire(); //Clear any previous polygons from map

        // Draw new polygon for said location
        polygonLine = new CoordinateLine(location)
                .setColor(Color.DODGERBLUE)
                .setFillColor(Color.web("lawngreen", 0.4))
                .setClosed(true);
        mapView.addCoordinateLine(polygonLine);
        polygonLine.setVisible(true);

        // Set polygon data to sessiondata for weather and roaddata
        sessionData.helperFunctions.setPolygonCoordinates(location);
        buttonAddPolygon.fire();

        // Set mapview to location
        Extent extent = Extent.forCoordinates(sessionData.polyCoordinates);
        mapView.setExtent(extent);// Set mapview to location
    }

    /**
     * initializes the event handlers.
     */
    private void setupEventHandlers() {
        // add an event handler for singleclicks, set the click marker to the new position when it's visible
        mapView.addEventHandler(MapViewEvent.MAP_CLICKED, event -> {
            event.consume();
            final Coordinate newPosition = event.getCoordinate().normalize();
            // Set new current coordinate
            System.out.println("["+newPosition.getLatitude() + ", " + newPosition.getLongitude()+"]");

            if (checkDrawPolygon.isSelected()) {
                handlePolygonClick(event);
            }
            if (markerClick.getVisible()) {
                final Coordinate oldPosition = markerClick.getPosition();
                if (oldPosition != null) {
                    animateClickMarker(oldPosition, newPosition);
                } else {
                    markerClick.setPosition(newPosition);
                    // adding can only be done after coordinate is set
                    mapView.addMarker(markerClick);
                }
            }
        });

        // add an event handler for MapViewEvent#MAP_EXTENT and set the extent in the map
        mapView.addEventHandler(MapViewEvent.MAP_EXTENT, event -> {
            event.consume();
            mapView.setExtent(event.getExtent());
        });


    }

    /**
     * Animates the clickmarker movement between coordinates
     * @param oldPosition
     * @param newPosition
     */
    private void animateClickMarker(Coordinate oldPosition, Coordinate newPosition) {
        // animate the marker to the new position
        final Transition transition = new Transition() {
            private final Double oldPositionLongitude = oldPosition.getLongitude();
            private final Double oldPositionLatitude = oldPosition.getLatitude();
            private final double deltaLatitude = newPosition.getLatitude() - oldPositionLatitude;
            private final double deltaLongitude = newPosition.getLongitude() - oldPositionLongitude;

            {
                setCycleDuration(Duration.seconds(0.2));
                setOnFinished(evt -> markerClick.setPosition(newPosition));
            }

            @Override
            protected void interpolate(double v) {
                final double latitude = oldPosition.getLatitude() + v * deltaLatitude;
                final double longitude = oldPosition.getLongitude() + v * deltaLongitude;
                markerClick.setPosition(new Coordinate(latitude, longitude));
            }
        };
        transition.play();
    }

    /**
     * shows a new polygon with the coordinate from the added.
     *
     * @param event
     *     event with coordinates
     */
    private void handlePolygonClick(MapViewEvent event) {
        final List<Coordinate> coordinates = new ArrayList<>();
        if (polygonLine != null) {
            polygonLine.getCoordinateStream().forEach(coordinates::add);
            mapView.removeCoordinateLine(polygonLine);
            polygonLine = null;

        }
        coordinates.add(event.getCoordinate());
        polygonLine = new CoordinateLine(coordinates)
            .setColor(Color.DODGERBLUE)
            .setFillColor(Color.web("lawngreen", 0.4))
            .setClosed(true);
        mapView.addCoordinateLine(polygonLine);
        polygonLine.setVisible(true);
        //System.out.println(coordinates);
        sessionData.helperFunctions.setPolygonCoordinates(coordinates);
    }

    /**
     * enables / disables the different controls
     *
     * @param flag
     *     if true the controls are disabled
     */
    private void setControlsDisable(boolean flag) {
        leftControls.setDisable(flag);
        botControls.setDisable(flag);
        zoomControls.setDisable(flag);
    }

    /**
     * finishes setup after the mpa is initialzed
     */
    private void afterMapIsInitialized() {

        // Set constraints to Finland only
        mapView.constrainExtent(extentFinland);

        // start at the kotka with default zoom
        mapView.setZoom(0.0);
        mapView.setExtent(extentFinland);

        // add the markers to the map - they are still invisible
        mapView.addMarker(markerKotka);
        mapView.addMarker(markerTampere);
        mapView.addMarker(markerLahti);
        mapView.addMarker(markerTurku);
        mapView.addMarker(markerHyvinkaa);
        mapView.addMarker(markerPorvoo);
        mapView.addMarker(markerHelsinki);
        mapView.addMarker(markerJyvaskyla);
        mapView.addMarker(markerOulu);
        mapView.addMarker(markerRovaniemi);
        // can't add the markerClick at this moment, it has no position, so it would not be added to the map

        // now enable the controls
        setControlsDisable(false);

        //Initialize to preference
        for(Node location : optionsLocationsVbox.getChildren()){
            final Button button = (Button) location;
            if(Objects.equals(button.getText(), sessionData.locationPreference)){
                button.fire();
            }
        }
    }

    public void setSessionData(SessionData sessionData) {
        this.sessionData = sessionData;
    }

    public ArrayList<String> getLocationsAsList(){
        ArrayList<String> locations = new ArrayList<>();
        for(Node location : optionsLocationsVbox.getChildren()){
            final Button button = (Button) location;
            locations.add(button.getText());
        }
        return locations;
    }
}
