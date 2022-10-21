package fi.tuni.roadwatch;

import com.sothawo.mapjfx.Coordinate;
import fi.tuni.roadwatch.controllers.MapController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SessionData {

    private RoadWatchController roadWatchController;
    private MapController mapController;

    public Coordinate currentCoordinates;
    public List<Coordinate> polyCoordinates;

    public static class CoordinateConstraints{

        public CoordinateConstraints(Double minLon, Double minLat, Double maxLon, Double maxLat){
            this.minLon = minLon;
            this.minLat = minLat;

            this.maxLon = maxLon;
            this.maxLat = maxLat;

        }
        private final Double minLon;
        private final Double minLat;

        private final Double maxLon;
        private final Double maxLat;

        public String getAsString(){
            return minLon + "/" + minLat +"/" + maxLon +"/" + maxLat;
        }
    }

    public CoordinateConstraints coordinateConstraints;


    public SessionData(RoadWatchController roadWatchController, MapController mapController){
        this.mapController = mapController;
        this.roadWatchController = roadWatchController;
    }

    public void setCurrentCoordinates(Coordinate newCoordinate){
        currentCoordinates = newCoordinate;
//        roadWatchController.loadCombine();
    }

    public void setPolygonCoordinates(List<Coordinate> polyCoordinates){
        this.polyCoordinates = polyCoordinates;
    }

    public void calculateMinMaxCoordinates() {

        // TODO: Make more efficient
        Double maxLongtitude = polyCoordinates.stream().max(Comparator.comparing(Coordinate::getLongitude)).get().getLongitude();
        Double maxLatitude = polyCoordinates.stream().max(Comparator.comparing(Coordinate::getLatitude)).get().getLatitude();

        Double minLongtitude = polyCoordinates.stream().min(Comparator.comparing(Coordinate::getLongitude)).get().getLongitude();
        Double minLatitude = polyCoordinates.stream().min(Comparator.comparing(Coordinate::getLatitude)).get().getLatitude();

        coordinateConstraints = new CoordinateConstraints(minLongtitude, minLatitude, maxLongtitude, maxLatitude);
        System.out.println(coordinateConstraints.getAsString());



    }

}
