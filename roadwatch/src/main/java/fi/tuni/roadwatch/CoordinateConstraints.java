package fi.tuni.roadwatch;

/**
 * Holds the current bbox and helper functions
 */
public class CoordinateConstraints {

    public CoordinateConstraints(Double minLon, Double minLat, Double maxLon, Double maxLat) {
        this.minLon = minLon;
        this.minLat = minLat;

        this.maxLon = maxLon;
        this.maxLat = maxLat;

    }

    public final Double minLon;
    public final Double minLat;

    public final Double maxLon;
    public final Double maxLat;

    /**
     * Generates a string of the bbox with given parameter to divide bbox
     * @param c charachet parameter
     * @return String of bbox
     */
    public String getAsString(Character c) {
        return "" + minLon + c + minLat + c + maxLon + c + maxLat;
    }

    /**
     * Generates a string in the style needed for Maintenance API calls
     * @return String of bbox in MaintenanceAPI call format
     */
    public String getAsMaintenanceString(){
        return  "xMin="+minLon+"&"+
                "yMin="+minLat+"&"+
                "xMax="+maxLon+"&"+
                "yMax="+maxLat;
    }
}
