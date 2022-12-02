package fi.tuni.roadwatch;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Date;

/**
 * Data class for TrafficMessages
 */
public class TrafficMessage {
    Date dataUpdatedTime;
    ArrayList<Feature> features;

    /**
     * Calculates the traffic messages in given area
     * @param c bbox constraints
     * @return amount of messages in area
     */
    public Integer messagesInArea(CoordinateConstraints c){
        int amount = 0;
        for (TrafficMessage.Feature feature : features){
            if(feature.geometry != null){
                for (ArrayList<ArrayList<Double>> coordinates : feature.geometry.coordinates) {
                    for (ArrayList<Double> coordinate : coordinates) {
                        if(coordinate.size() == 2){
                            if(coordinate.get(0) > c.minLon &&
                                    coordinate.get(0) < c.maxLon &&
                                    coordinate.get(1) > c.minLat &&
                                    coordinate.get(1) < c.maxLat){
                                amount = amount +1;
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        }
        return amount;
    }

    /**
     * Getter for dataUpdatedTime
     * @return Date object
     */
    @JsonProperty("dataUpdatedTime")
    public Date getDataUpdatedTime() {
        return this.dataUpdatedTime; }
    /**
     * Setter for dataUpdatedTime
     * @param dataUpdatedTime given dataUpdatedTime
     */
    public void setDataUpdatedTime(Date dataUpdatedTime) {
        this.dataUpdatedTime = dataUpdatedTime; }

    /**
    * Getter for features
    * @return ArrayList<Feature> object
    */
    @JsonProperty("features")
    public ArrayList<Feature> getFeatures() {
        return this.features; }
    /**
     * Setter for features
     * @param features Features object
     */
    public void setFeatures(ArrayList<Feature> features) {
        this.features = features; }

    /**
     * Data class for TrafficMessage features
     */
    public static class Feature{
        Geometry geometry;
        Properties properties;

        /**
         * Getter for geometry
         * @return Geometry object
         */
        @JsonProperty("geometry")
        public Geometry getGeometry() {
            return this.geometry; }
        /**
         * Setter for geometry
         * @param geometry given geometry
         */
        public void setGeometry(Geometry geometry) {
            this.geometry = geometry; }

        /**
         * Getter for properties
         * @return Properties object
         */
        @JsonProperty("properties")
        public Properties getProperties() {
            return this.properties; }
        /**
         * Setter for properties
         * @param properties Properties object
         */
        public void setProperties(Properties properties) {
            this.properties = properties; }
    }

    /**
     * Data class for Geometry
     */
    public static class Geometry{
        String type;
        ArrayList<ArrayList<ArrayList<Double>>> coordinates;

        /**
         * Getter for type
         * @return String object
         */
        @JsonProperty("type")
        public String getType() {
            return this.type; }
        /**
         * Setter for type
         * @param type given type
         */
        public void setType(String type) {
            this.type = type; }

        /**
         * Getter for coordinates
         * @return Nested Arraylist object
         */
        @JsonProperty("coordinates")
        public ArrayList<ArrayList<ArrayList<Double>>> getCoordinates() {
            return this.coordinates; }
        /**
         * Setter for coordinates
         * @param coordinates given coordinates
         */
        public void setCoordinates(ArrayList<ArrayList<ArrayList<Double>>> coordinates) {
            this.coordinates = coordinates; }
    }

    /**
     * Data class for Properties
     */
    public static class Properties{
        String situationType;
        ArrayList<Announcements> announcements;

        /**
         * Getter for situationType
         * @return String object
         */
        @JsonProperty("situationType")
        public String getSituationType() {
            return situationType;}
        /**
         * Setter for situationType
         * @param situationType given situationType
         */
        public void setSituationType(String situationType) {
            this.situationType = situationType;}

        /**
         * Getter for announcements
         * @return Announcements Arraylist object
         */
        @JsonProperty("announcements")
        public ArrayList<Announcements> getAnnouncements() {
            return announcements;}
        /**
         * Setter for announcements
         * @param announcements given announcements
         */
        public void setAnnouncements(ArrayList<Announcements> announcements) {
            this.announcements = announcements;}
    }

    /**
     * Data class for Announcements
     */
    public static class Announcements{
        String title;
        String situation;

        /**
         * Getter for title
         * @return String object
         */
        @JsonProperty("title")
        public String getTitle() {
            return title;}
        /**
         * Setter for title
         * @param title given title
         */
        public void setTitle(String title) {
            this.title = title;}
    }
}
