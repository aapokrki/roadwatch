package fi.tuni.roadwatch;

import com.sothawo.mapjfx.Coordinate;
import fi.tuni.roadwatch.controllers.MapController;

public class SessionData {

    private RoadWatchController roadWatchController;
    private MapController mapController;

    public Coordinate currentCoordinates;


    public SessionData(RoadWatchController roadWatchController, MapController mapController){
        this.mapController = mapController;
        this.roadWatchController = roadWatchController;
    }

    public void setCurrentCoordinates(Coordinate newCoordinate){
        currentCoordinates = newCoordinate;
//        roadWatchController.loadCombine();
    }

}
