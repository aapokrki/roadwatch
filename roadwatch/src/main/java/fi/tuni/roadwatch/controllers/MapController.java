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
package fi.tuni.roadwatch.controllers;

import com.sothawo.mapjfx.*;
import com.sothawo.mapjfx.event.MapLabelEvent;
import com.sothawo.mapjfx.event.MapViewEvent;
import com.sothawo.mapjfx.event.MarkerEvent;
import com.sothawo.mapjfx.offline.OfflineCache;
import fi.tuni.roadwatch.SessionData;
import javafx.animation.AnimationTimer;
import javafx.animation.Transition;
import javafx.beans.binding.Bindings;
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
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    /**
     * Previously clicked coordinate
     */
    public Coordinate currentCoordinate = new Coordinate(61.45, 23.85);

    /** some coordinates from around town. */
    private static final Coordinate coordHervanta = new Coordinate(61.45, 23.85);
    private static final Coordinate coordKotka = new Coordinate(60.4667,26.9458);
    private static final Coordinate coordLahti = new Coordinate(60.983692, 25.650317);
    private static final Extent extentAllLocations = Extent.forCoordinates(coordHervanta, coordKotka, coordLahti);

    private static final Coordinate coordNorhWest= new Coordinate(70.31778507753353, 17.503235177994913);
    private static final Coordinate coordNorthEast = new Coordinate(70.29756318095303, 32.20596093802763);
    private static final Coordinate coordSouthEast = new Coordinate(59.27583073122375, 31.965916435823015);
    private static final Coordinate coordSouthWest = new Coordinate(58.99877058368233, 18.94350219122261);

    private static final Extent extentFinland = Extent.forCoordinates(coordNorhWest, coordNorthEast, coordSouthEast, coordSouthWest);

    /** default zoom value. */
    private static final int ZOOM_DEFAULT = 14;

    /** the markers. */
    private final Marker markerKotka;
    private final Marker markerHervanta;
    private final Marker markerClick;

    /** the labels. */
    private final MapLabel labelHervanta;
    private final MapLabel labelClick;

    @FXML
    /** button to set the map's zoom. */
    private Button buttonZoom;

    /** the MapView containing the map */
    @FXML
    private MapView mapView;

    /** the box containing the top controls, must be enabled when mapView is initialized */
    @FXML
    private HBox topControls;

    /** Slider to change the zoom value */
    @FXML
    private Slider sliderZoom;

    /** Accordion for all the different options */
    @FXML
    private Accordion leftControls;

    /** section containing the location button */
    @FXML
    private TitledPane optionsLocations;

    /** button to set the map's center */
    @FXML
    private Button buttonKotka;

    /** button to set the map's center */
    @FXML
    private Button buttonHervanta;

    /** button to set the map's center */

    /** button to set the map's center */
    @FXML
    private Button buttonLahti;

    /** button to set the map's extent. */
    @FXML
    private Button buttonAllLocations;

    /** for editing the animation duration */
    @FXML
    private TextField animationDuration;

    /** Label to display the current center */
    @FXML
    private Label labelCenter;

    /** Label to display the current extent */
    @FXML
    private Label labelExtent;

    /** Label to display the current zoom */
    @FXML
    private Label labelZoom;

    /** label to display the last event. */
    @FXML
    private Label labelEvent;

    /** RadioButton for MapStyle OSM */
    @FXML
    private RadioButton radioMsOSM;

    /** RadioButton for MapStyle Stamen Watercolor */
    @FXML
    private RadioButton radioMsSTW;


    /** RadioButton for MapStyle WMS. */
    @FXML
    private RadioButton radioMsWMS;

    /** RadioButton for MapStyle XYZ */
    @FXML
    private RadioButton radioMsXYZ;

    /** ToggleGroup for the MapStyle radios */
    @FXML
    private ToggleGroup mapTypeGroup;

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

    /** the first CoordinateLine */
    private CoordinateLine trackMagenta;
    /** Check button for first track */
    @FXML
    private CheckBox checkTrackMagenta;

    /** the second CoordinateLine */
    private CoordinateLine trackCyan;
    /** Check button for first track */
    @FXML
    private CheckBox checkTrackCyan;

    /** Coordinateline for polygon drawing. */
    private CoordinateLine polygonLine;
    /** Check Button for polygon drawing mode. */
    @FXML
    private CheckBox checkDrawPolygon;

    /** Check Button for constraining th extent. */
    @FXML
    private CheckBox checkConstrainFinland;

    /** params for the WMS server. */
    private WMSParam wmsParam = new WMSParam()
        .setUrl("http://ows.terrestris.de/osm/service?")
        .addParam("layers", "OSM-WMS");

    private XYZParam xyzParams = new XYZParam()
        .withUrl("https://server.arcgisonline.com/ArcGIS/rest/services/World_Topo_Map/MapServer/tile/{z}/{y}/{x})")
        .withAttributions(
            "'Tiles &copy; <a href=\"https://services.arcgisonline.com/ArcGIS/rest/services/World_Topo_Map/MapServer\">ArcGIS</a>'");

    public MapController() {

        // a couple of markers using the provided ones
        markerKotka = Marker.createProvided(Marker.Provided.BLUE).setPosition(coordKotka).setVisible(
                true);
        markerHervanta = Marker.createProvided(Marker.Provided.GREEN).setPosition(coordHervanta).setVisible(
                true);
        // no position for click marker yet
        markerClick = Marker.createProvided(Marker.Provided.ORANGE).setVisible(false);

        // the attached labels, custom style
        labelHervanta = new MapLabel("hervanta", 10, -10).setVisible(false).setCssClass("green-label");
        labelClick = new MapLabel("click!", 10, -10).setVisible(false).setCssClass("orange-label");

        markerHervanta.attachLabel(labelHervanta);
        markerClick.attachLabel(labelClick);
    }

    /**
     * called after the fxml is loaded and all objects are created. This is not called initialize any more,
     * because we need to pass in the projection before initializing.
     *
     * @param projection
     *     the projection to use in the map.
     */
    public void initMapAndControls(Projection projection) {

        // init MapView-Cache
        final OfflineCache offlineCache = mapView.getOfflineCache();
        final String cacheDir = System.getProperty("java.io.tmpdir") + "/mapjfx-cache";
//        logger.info("using dir for cache: " + cacheDir);
//        try {
//            Files.createDirectories(Paths.get(cacheDir));
//            offlineCache.setCacheDirectory(cacheDir);
//            offlineCache.setActive(true);
//        } catch (IOException e) {
//            logger.warn("could not activate offline cache", e);
//        }

        // set the custom css file for the MapView
        mapView.setCustomMapviewCssURL(getClass().getResource("/custom_mapview.css"));

        leftControls.setExpandedPane(optionsLocations);

        // set the controls to disabled, this will be changed when the MapView is intialized
        setControlsDisable(true);

        // wire up the location buttons
        buttonKotka.setOnAction(event -> mapView.setCenter(coordKotka));
        buttonHervanta.setOnAction(event -> mapView.setCenter(coordHervanta));
        buttonLahti.setOnAction(event -> mapView.setCenter(coordLahti));

        buttonAllLocations.setOnAction(event -> mapView.setExtent(extentAllLocations));

        // wire the zoom button and connect the slider to the map's zoom
        buttonZoom.setOnAction(event -> {
            mapView.setZoom(ZOOM_DEFAULT);
        });
        sliderZoom.valueProperty().bindBidirectional(mapView.zoomProperty());

        // add a listener to the animationDuration field and make sure we only accept int values
        animationDuration.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                mapView.setAnimationDuration(0);
            } else {
                try {
                    mapView.setAnimationDuration(Integer.parseInt(newValue));
                } catch (NumberFormatException e) {
                    animationDuration.setText(oldValue);
                }
            }
        });
        animationDuration.setText("500");

        // bind the map's center and zoom properties to the corresponding labels and format them
        labelCenter.textProperty().bind(Bindings.format("center: %s", mapView.centerProperty()));
        labelZoom.textProperty().bind(Bindings.format("zoom: %.0f", mapView.zoomProperty()));

        // watch the MapView's initialized property to finish initialization
        mapView.initializedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                afterMapIsInitialized();
            }
        });

        // observe the map type radiobuttons
//        mapTypeGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
//            MapType mapType = MapType.OSM;
//            if (newValue == radioMsOSM) {
//                mapType = MapType.OSM;
//            } else if (newValue == radioMsWMS) {
//                mapView.setWMSParam(wmsParam);
//                mapType = MapType.WMS;
//            } else if (newValue == radioMsXYZ) {
//                mapView.setXYZParam(xyzParams);
//                mapType = MapType.XYZ;
//            }
//            mapView.setMapType(mapType);
//        });
//        mapTypeGroup.selectToggle(radioMsOSM);

        // Set map type
        mapView.setMapType(MapType.OSM);

        setupEventHandlers();

        // add the graphics to the checkboxes
        checkKotkaMarker.setGraphic(
            new ImageView(new Image(markerKotka.getImageURL().toExternalForm(), 16.0, 16.0, true, true)));
        checkHervantaMarker.setGraphic(
            new ImageView(new Image(markerHervanta.getImageURL().toExternalForm(), 16.0, 16.0, true, true)));

        checkClickMarker.setGraphic(
            new ImageView(new Image(markerClick.getImageURL().toExternalForm(), 16.0, 16.0, true, true)));

        // bind the checkboxes to the markers visibility
        checkKotkaMarker.selectedProperty().bindBidirectional(markerKotka.visibleProperty());
        checkHervantaMarker.selectedProperty().bindBidirectional(markerHervanta.visibleProperty());
        checkClickMarker.selectedProperty().bindBidirectional(markerClick.visibleProperty());
        checkClickMarker.setSelected(true);

        // load two coordinate lines
        trackMagenta = loadCoordinateLine(getClass().getResource("/M1.csv")).orElse(new CoordinateLine
            ()).setColor(Color.MAGENTA);
        trackCyan = loadCoordinateLine(getClass().getResource("/M2.csv")).orElse(new CoordinateLine
            ()).setColor(Color.CYAN).setWidth(7);
        checkTrackMagenta.selectedProperty().bindBidirectional(trackMagenta.visibleProperty());
        checkTrackCyan.selectedProperty().bindBidirectional(trackCyan.visibleProperty());

        // get the extent of both tracks
        Extent tracksExtent = Extent.forCoordinates(
            Stream.concat(trackMagenta.getCoordinateStream(), trackCyan.getCoordinateStream())
                .collect(Collectors.toList()));
        ChangeListener<Boolean> trackVisibleListener =
            (observable, oldValue, newValue) -> mapView.setExtent(tracksExtent);
        trackMagenta.visibleProperty().addListener(trackVisibleListener);
        trackCyan.visibleProperty().addListener(trackVisibleListener);

        // add the polygon check handler
        ChangeListener<Boolean> polygonListener =
            (observable, oldValue, newValue) -> {
                if (!newValue && polygonLine != null) {
                    mapView.removeCoordinateLine(polygonLine);
                    polygonLine = null;
                }
            };
        checkDrawPolygon.selectedProperty().addListener(polygonListener);

        // add the constrain listener
        checkConstrainFinland.selectedProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue) {
                mapView.constrainExtent(extentFinland);
            } else {
                mapView.clearConstrainExtent();
            }
        }));



        // finally initialize the map view
        mapView.initialize(Configuration.builder()
            .projection(projection)
            .showZoomControls(false)
            .build());

//        long animationStart = System.nanoTime();
//        new AnimationTimer() {
//            @Override
//            public void handle(long nanoSecondsNow) {
//                if (markerLahti.getVisible()) {
//                    // every 100ms, increase the rotation of the markerLahti by 9 degrees, make a turn in 4 seconds
//                    long milliSecondsDelta = (nanoSecondsNow - animationStart) / 1_000_000;
//                    long numSteps = milliSecondsDelta / 100;
//                    int angle = (int) ((numSteps * 9) % 360);
//                    if (markerLahti.getRotation() != angle) {
//                        markerLahti.setRotation(angle);
//                    }
//                }
//            }
//        }.start();
    }

    /**
     * initializes the event handlers.
     */
    private void setupEventHandlers() {
        // add an event handler for singleclicks, set the click marker to the new position when it's visible
        mapView.addEventHandler(MapViewEvent.MAP_CLICKED, event -> {
            event.consume();
            final Coordinate newPosition = event.getCoordinate().normalize();
            labelEvent.setText("Event: map clicked at: " + newPosition);

            // Set new current coordinate
            System.out.println(newPosition);
            currentCoordinate = newPosition;
            sessionData.setCurrentCoordinates(newPosition);

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

        // add an event handler for extent changes and display them in the status label
        mapView.addEventHandler(MapViewEvent.MAP_BOUNDING_EXTENT, event -> {
            event.consume();
            labelExtent.setText(event.getExtent().toString());
        });

        mapView.addEventHandler(MapViewEvent.MAP_RIGHTCLICKED, event -> {
            event.consume();
            labelEvent.setText("Event: map right clicked at: " + event.getCoordinate());
        });
        mapView.addEventHandler(MarkerEvent.MARKER_CLICKED, event -> {
            event.consume();
            labelEvent.setText("Event: marker clicked: " + event.getMarker().getId());
        });
        mapView.addEventHandler(MarkerEvent.MARKER_RIGHTCLICKED, event -> {
            event.consume();
            labelEvent.setText("Event: marker right clicked: " + event.getMarker().getId());
        });
        mapView.addEventHandler(MapLabelEvent.MAPLABEL_CLICKED, event -> {
            event.consume();
            labelEvent.setText("Event: label clicked: " + event.getMapLabel().getText());
        });
        mapView.addEventHandler(MapLabelEvent.MAPLABEL_RIGHTCLICKED, event -> {
            event.consume();
            labelEvent.setText("Event: label right clicked: " + event.getMapLabel().getText());
        });

        mapView.addEventHandler(MapViewEvent.MAP_POINTER_MOVED, event -> {
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
                setCycleDuration(Duration.seconds(1.0));
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
    }

    /**
     * enables / disables the different controls
     *
     * @param flag
     *     if true the controls are disabled
     */
    private void setControlsDisable(boolean flag) {
        topControls.setDisable(flag);
        leftControls.setDisable(flag);
    }

    /**
     * finishes setup after the mpa is initialzed
     */
    private void afterMapIsInitialized() {
        // start at the kotka with default zoom
        mapView.setZoom(ZOOM_DEFAULT);
        mapView.setCenter(coordKotka);
        // add the markers to the map - they are still invisible
        mapView.addMarker(markerKotka);
        mapView.addMarker(markerHervanta);
        // can't add the markerClick at this moment, it has no position, so it would not be added to the map

        // add the tracks
        mapView.addCoordinateLine(trackMagenta);
        mapView.addCoordinateLine(trackCyan);

        // add the circle

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
