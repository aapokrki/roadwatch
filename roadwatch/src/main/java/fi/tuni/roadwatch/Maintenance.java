package fi.tuni.roadwatch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Date;

public class Maintenance {

    String type;
    Date dataUpdatedTime;
    ArrayList<Feature> features;

    @JsonProperty("type")
    public String getType() {
        return this.type; }
    public void setType(String type) {
        this.type = type; }

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
        String type;
        Properties properties;
        Geometry geometry;

        @JsonProperty("type")
        public String getType() {
            return this.type; }
        public void setType(String type) {
            this.type = type; }

        @JsonProperty("properties")
        public Properties getProperties() {
            return this.properties; }
        public void setProperties(Properties properties) {
            this.properties = properties; }

        @JsonProperty("geometry")
        public Geometry getGeometry() {
            return this.geometry; }
        public void setGeometry(Geometry geometry) {
            this.geometry = geometry; }

    }

    public static class Geometry{
        String type;
        ArrayList<ArrayList<Double>> coordinates;

        @JsonProperty("type")
        public String getType() {
            return this.type; }
        public void setType(String type) {
            this.type = type; }

        @JsonProperty("coordinates")
        public ArrayList<ArrayList<Double>> getCoordinates() {
            return this.coordinates; }
        public void setCoordinates(ArrayList<ArrayList<Double>> coordinates) {
            this.coordinates = coordinates; }

    }

    public static class Properties{
        int id;
        Object previousId;
        Date sendingTime;
        Date created;
        ArrayList<String> tasks;
        Date startTime;
        Date endTime;
        int direction;
        String domain;
        String source;

        @JsonProperty("id")
        public int getId() {
            return this.id; }
        public void setId(int id) {
            this.id = id; }

        @JsonProperty("previousId")
        public Object getPreviousId() {
            return this.previousId; }
        public void setPreviousId(Object previousId) {
            this.previousId = previousId; }

        @JsonProperty("sendingTime")
        public Date getSendingTime() {
            return this.sendingTime; }
        public void setSendingTime(Date sendingTime) {
            this.sendingTime = sendingTime; }

        @JsonProperty("created")
        public Date getCreated() {
            return this.created; }
        public void setCreated(Date created) {
            this.created = created; }

        @JsonProperty("tasks")
        public ArrayList<String> getTasks() {
            return this.tasks; }
        public void setTasks(ArrayList<String> tasks) {
            this.tasks = tasks; }

        @JsonProperty("startTime")
        public Date getStartTime() {
            return this.startTime; }
        public void setStartTime(Date startTime) {
            this.startTime = startTime; }

        @JsonProperty("endTime")
        public Date getEndTime() {
            return this.endTime; }
        public void setEndTime(Date endTime) {
            this.endTime = endTime; }

        @JsonProperty("direction")
        public int getDirection() {
            return this.direction; }
        public void setDirection(int direction) {
            this.direction = direction; }

        @JsonProperty("domain")
        public String getDomain() {
            return this.domain; }
        public void setDomain(String domain) {
            this.domain = domain; }

        @JsonProperty("source")
        public String getSource() {
            return this.source; }
        public void setSource(String source) {
            this.source = source; }
    }

}
