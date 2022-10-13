package fi.tuni.roadwatch;

import com.sothawo.mapjfx.Coordinate;

public class SessionData {

    private RoadWatchController roadWatchController;
    private  MapController mapController;

    public Coordinate currentCoordinates;


    SessionData(RoadWatchController roadWatchController, MapController mapController){
        this.mapController = mapController;
        this.roadWatchController = roadWatchController;
    }

    public void setCurrentCoordinates(Coordinate newCoordinate){
        currentCoordinates = newCoordinate;
//        roadWatchController.loadCombine();
    }

}
