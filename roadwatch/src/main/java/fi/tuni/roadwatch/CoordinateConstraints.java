package fi.tuni.roadwatch;

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
     * Generates a string of the bbos with given parameter
     * @param c
     * @return
     */
    public String getAsString(Character c) {
        return "" + minLon + c + minLat + c + maxLon + c + maxLat;
    }

    /**
     * Generates a string in the style needed for Maintenance API calls
     * @return
     */
    public String getAsMaintenanceString(){
        return  "xMin="+minLon+"&"+
                "yMin="+minLat+"&"+
                "xMax="+maxLon+"&"+
                "yMax="+maxLat;
    }
}
