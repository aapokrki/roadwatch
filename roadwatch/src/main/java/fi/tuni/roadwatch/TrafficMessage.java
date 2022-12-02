package fi.tuni.roadwatch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sothawo.mapjfx.Coordinate;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TrafficMessage {
    Date dataUpdatedTime;
    ArrayList<Feature> features;

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
     * Returns the amount of messages in the area within the given time frame
     * @param c CoordinateConstraints for the area
     * @param start Start time Date object
     * @param end End time Date object
     * @return Integer amount of messages
     */
    public Integer messagesInArea(CoordinateConstraints c, Date start, Date end){
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
                                if (dataUpdatedTime.after(start) && dataUpdatedTime.before(end)){
                                    amount = amount +1;
                                    break;
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
        return amount;
    }



    @JsonProperty("dataUpdatedTime")
    public Date getDataUpdatedTime() {
        return this.dataUpdatedTime; }
    public void setDataUpdatedTime(Date dataUpdatedTime) {
        this.dataUpdatedTime = dataUpdatedTime; }

    @JsonProperty("features")
    public ArrayList<Feature> getFeatures() {
        return this.features; }
    public void setFeatures(ArrayList<Feature> features) {
        this.features = features; }

    public static class Feature{
        Geometry geometry;
        Properties properties;

        @JsonProperty("geometry")
        public Geometry getGeometry() {
            return this.geometry; }
        public void setGeometry(Geometry geometry) {
            this.geometry = geometry; }

        @JsonProperty("properties")
        public Properties getProperties() {
            return this.properties; }
        public void setProperties(Properties properties) {
            this.properties = properties; }


    }
    public static class Geometry{
        String type;
        ArrayList<ArrayList<ArrayList<Double>>> coordinates;

        @JsonProperty("type")
        public String getType() {
            return this.type; }
        public void setType(String type) {
            this.type = type; }

        @JsonProperty("coordinates")
        public ArrayList<ArrayList<ArrayList<Double>>> getCoordinates() {
            return this.coordinates; }
        public void setCoordinates(ArrayList<ArrayList<ArrayList<Double>>> coordinates) {
            this.coordinates = coordinates; }
    }
    public static class Properties{
        String situationType;
        ArrayList<Announcements> announcements;

        @JsonProperty("situationType")
        public String getSituationType() {
            return situationType;}
        public void setSituationType(String situationType) {
            this.situationType = situationType;}

        @JsonProperty("announcements")
        public ArrayList<Announcements> getAnnouncements() {
            return announcements;}
        public void setAnnouncements(ArrayList<Announcements> announcements) {
            this.announcements = announcements;}
    }

    public static class Announcements{
        String title;
        String situation;

        @JsonProperty("title")
        public String getTitle() {
            return title;}
        public void setTitle(String title) {
            this.title = title;}


    }
}
