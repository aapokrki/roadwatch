/*
 Copyright 2015-2020 Peter-Josef Meisch (pj.meisch@sothawo.com)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package fi.tuni.roadwatch;

import com.sothawo.mapjfx.*;
import com.sothawo.mapjfx.event.MapViewEvent;
import javafx.animation.Transition;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Controller for the FXML defined code.
 *
 * @author P.J. Meisch (pj.meisch@sothawo.com).
 */
public class MapController {


    // Sessiondata
    private SessionData sessionData;

    /** Coordinates to border Finland */
    private static final Coordinate coordNorhWest= new Coordinate(70.30824014125528, 16.352667758380154);
    private static final Coordinate coordNorthEast = new Coordinate(70.291002382638, 35.52837666547143);
    private static final Coordinate coordSouthEast = new Coordinate(59.27583073122375, 31.965916435823015);
    private static final Coordinate coordSouthWest = new Coordinate(59.26658753324523, 16.352667758380154);
    private static final Extent extentFinland = Extent.forCoordinates(coordNorhWest, coordNorthEast, coordSouthEast, coordSouthWest);


    private static final ArrayList<Coordinate> coordSmallRoadSegment = new ArrayList<Coordinate>(
            Arrays.asList(  new Coordinate(60.87613246629303, 25.582678576744584),
                            new Coordinate(60.87704865972839, 25.576224325490955),
                            new Coordinate(60.89857163657564, 25.596931714929674),
                            new Coordinate(60.89817925024559, 25.600831158395405))
    );

    private static final ArrayList<Coordinate> coordMediumRoadSegment = new ArrayList<Coordinate>(
            Arrays.asList(  new Coordinate(60.50156645624125, 26.847317797691495),
                    new Coordinate(60.50429554964796, 26.81313801316007),
                    new Coordinate(60.50838875892538, 26.765101559224036),
                    new Coordinate(60.50429554964796, 26.69027554443904),
                    new Coordinate(60.49747238526555, 26.629306199058682),
                    new Coordinate(60.488827648425996, 26.58958374676542))
    );


    private static final ArrayList<Coordinate> coordLargeRoadSegment = new ArrayList<Coordinate>(
            Arrays.asList(  new Coordinate(65.04267096153495, 25.65218096901351),
                    new Coordinate(65.34545645158161, 27.160031899047087),
                    new Coordinate(65.59691857097374, 28.435905762921653),
                    new Coordinate(65.91969438346197, 29.118949346612077),
                    new Coordinate(65.51158585366903, 27.31468327648643),
                    new Coordinate(65.14037262699148, 25.613518124653677))
    );

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

    /** default zoom value. */
    private static final int ZOOM_DEFAULT = 14;

    /** the markers. */
    private final Marker markerKotka;
    private final Marker markerTampere;
    private final Marker markerClick;

    /** the MapView containing the map */
    @FXML
    private MapView mapView;

    /** the box containing the top controls, must be enabled when mapView is initialized */

    /** Accordion for all the different options */
    @FXML
    private Accordion leftControls;

    @FXML
    private HBox botControls;
    @FXML
    private HBox zoomControls;
    /** section containing the location button */
    @FXML
    private TitledPane optionsLocations;

    @FXML
    private Button buttonKotka;

    @FXML
    private Button buttonTampere;

    @FXML
    private Button buttonLahti;



    @FXML
    private Button buttonSmallRoad;

    @FXML
    private Button buttonMediumRoad;

    @FXML
    private Button buttonLargeRoad;

    /** Check button for harbour marker */
    @FXML
    private CheckBox checkKotkaMarker;

    /** Check button for castle marker */
    @FXML
    private CheckBox checkHervantaMarker;

    /** Check button for Lahti marker */
    @FXML
    private CheckBox checkLahtiMarker;

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

    public Coordinate getPolygonMiddle(ArrayList<Coordinate> coordinates){
        Double lat  = coordinates.stream().map(Coordinate::getLatitude).mapToDouble(Double::doubleValue).sum() / coordinates.size();
        Double lon  = coordinates.stream().map(Coordinate::getLongitude).mapToDouble(Double::doubleValue).sum() / coordinates.size();
        System.out.println(lat + "," + lon);
        return new Coordinate(lat,lon);
    }

    public MapController() throws MalformedURLException {

        // a couple of markers using the provided ones
        markerKotka = new Marker(Objects.requireNonNull(getClass().getResource("/pictures/map-icon.png")),-11,-11)
                .setPosition(getPolygonMiddle(coordKotka))
                .setVisible(true);

        markerTampere = new Marker(Objects.requireNonNull(getClass().getResource("/pictures/map-icon.png")),-11,-11)
                .setPosition(getPolygonMiddle(coordTampere))
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

        buttonKotka.setOnAction(event -> {
            buttonClearPolygon.fire(); //Clear any previous polygons from map


            // Draw new polygon for said location
            polygonLine = new CoordinateLine(coordKotka)
                    .setColor(Color.DODGERBLUE)
                    .setFillColor(Color.web("lawngreen", 0.4))
                    .setClosed(true);
            mapView.addCoordinateLine(polygonLine);
            polygonLine.setVisible(true);

            // Set polygon data to sessiondata for weather and roaddata
            sessionData.setPolygonCoordinates(coordKotka);
            buttonAddPolygon.fire();

            Extent extent = Extent.forCoordinates(sessionData.polyCoordinates); // Set mapview to location
            mapView.setExtent(extent);// Set mapview to location
        });

        buttonTampere.setOnAction(event -> {
            buttonClearPolygon.fire(); //Clear any previous polygons from map


            // Draw new polygon for said location
            polygonLine = new CoordinateLine(coordTampere)
                    .setColor(Color.DODGERBLUE)
                    .setFillColor(Color.web("lawngreen", 0.4))
                    .setClosed(true);
            mapView.addCoordinateLine(polygonLine);
            polygonLine.setVisible(true);

            // Set polygon data to sessiondata for weather and roaddata
            sessionData.setPolygonCoordinates(coordTampere);
            buttonAddPolygon.fire();

            Extent extent = Extent.forCoordinates(sessionData.polyCoordinates); // Set mapview to location
            mapView.setExtent(extent);// Set mapview to location

        });
        buttonLahti.setOnAction(event -> {
            buttonClearPolygon.fire(); //Clear any previous polygons from map

            // Draw new polygon for said location
            polygonLine = new CoordinateLine(coordLahti)
                    .setColor(Color.DODGERBLUE)
                    .setFillColor(Color.web("lawngreen", 0.4))
                    .setClosed(true);
            mapView.addCoordinateLine(polygonLine);
            polygonLine.setVisible(true);

            // Set polygon data to sessiondata for weather and roaddata
            sessionData.setPolygonCoordinates(coordLahti);
            buttonAddPolygon.fire();

            Extent extent = Extent.forCoordinates(sessionData.polyCoordinates); // Set mapview to location
            mapView.setExtent(extent);// Set mapview to location
        });

        // Small segment preset
        buttonSmallRoad.setOnAction(event -> {

            buttonClearPolygon.fire(); //Clear any previous polygons from map


            // Draw new polygon for said location
            polygonLine = new CoordinateLine(coordSmallRoadSegment)
                    .setColor(Color.DODGERBLUE)
                    .setFillColor(Color.web("lawngreen", 0.4))
                    .setClosed(true);
            mapView.addCoordinateLine(polygonLine);
            polygonLine.setVisible(true);

            // Set polygon data to sessiondata for weather and roaddata
            sessionData.setPolygonCoordinates(coordSmallRoadSegment);
            buttonAddPolygon.fire();

            Extent extent = Extent.forCoordinates(sessionData.polyCoordinates); // Set mapview to location
            mapView.setExtent(extent);// Set mapview to location

        });
        // Medium segment preset
        buttonMediumRoad.setOnAction(event -> {

            buttonClearPolygon.fire(); //Clear any previous polygons from map


            // Draw new polygon for said location

            polygonLine = new CoordinateLine(coordMediumRoadSegment)
                    .setColor(Color.DODGERBLUE)
                    .setFillColor(Color.web("lawngreen", 0.4))
                    .setClosed(true);
            mapView.addCoordinateLine(polygonLine);
            polygonLine.setVisible(true);

            // Set polygon data to sessiondata for weather and roaddata
            sessionData.setPolygonCoordinates(coordMediumRoadSegment);
            buttonAddPolygon.fire();

            Extent extent = Extent.forCoordinates(sessionData.polyCoordinates); // Set mapview to location
            mapView.setExtent(extent);// Set mapview to location

        });

        // Large segment preset
        buttonLargeRoad.setOnAction(event -> {

            buttonClearPolygon.fire(); //Clear any previous polygons from map

            // Draw new polygon for said location

            polygonLine = new CoordinateLine(coordLargeRoadSegment)
                    .setColor(Color.DODGERBLUE)
                    .setFillColor(Color.web("lawngreen", 0.4))
                    .setClosed(true);
            mapView.addCoordinateLine(polygonLine);
            polygonLine.setVisible(true);

            // Set polygon data to sessiondata for weather and roaddata
            sessionData.setPolygonCoordinates(coordLargeRoadSegment);
            buttonAddPolygon.fire();

            // Set mapview to location
            Extent extent = Extent.forCoordinates(sessionData.polyCoordinates);
            mapView.setExtent(extent);// Set mapview to location

        });

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

        // Set map type
        mapView.setMapType(MapType.OSM);
        setupEventHandlers();

        // add the graphics to the checkboxes
        checkKotkaMarker.setGraphic(
            new ImageView(new Image(markerKotka.getImageURL().toExternalForm(), 16.0, 16.0, true, true)));
        checkHervantaMarker.setGraphic(
            new ImageView(new Image(markerTampere.getImageURL().toExternalForm(), 16.0, 16.0, true, true)));

        checkClickMarker.setGraphic(
            new ImageView(new Image(markerClick.getImageURL().toExternalForm(), 16.0, 16.0, true, true)));

        // bind the checkboxes to the markers visibility
        checkKotkaMarker.selectedProperty().bindBidirectional(markerKotka.visibleProperty());
        checkHervantaMarker.selectedProperty().bindBidirectional(markerTampere.visibleProperty());
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
        sessionData.setPolygonCoordinates(coordinates);
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
        // can't add the markerClick at this moment, it has no position, so it would not be added to the map

        // now enable the controls
        setControlsDisable(false);
    }

    /**
     * load a coordinateLine from the given uri in lat;lon csv format
     *
     * @param url
     *     url where to load from
     * @return optional CoordinateLine object
     * @throws NullPointerException
     *     if uri is null
     */
    private Optional<CoordinateLine> loadCoordinateLine(URL url) {
        try (
            Stream<String> lines = new BufferedReader(
                new InputStreamReader(url.openStream(), StandardCharsets.UTF_8)).lines()
        ) {
            return Optional.of(new CoordinateLine(
                lines.map(line -> line.split(";")).filter(array -> array.length == 2)
                    .map(values -> new Coordinate(Double.valueOf(values[0]), Double.valueOf(values[1])))
                    .collect(Collectors.toList())));
        } catch (IOException | NumberFormatException e) {
        }
        return Optional.empty();
    }

    public void setSessionData(SessionData sessionData) {
        this.sessionData = sessionData;
    }
}
