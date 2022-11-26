package fi.tuni.roadwatch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

// Data class for road condition data from Digitraffic
public class RoadData {

    Date dataUpdatedTime;
    ArrayList<roadWeatherData> roadWeatherData;
    @JsonProperty("dataUpdatedTime")
    public Date getDataUpdatedTime() {
        return this.dataUpdatedTime; }
    public void setDataUpdatedTime(Date dataUpdatedTime) {
        this.dataUpdatedTime = dataUpdatedTime; }

    @JsonProperty("WeatherData")
    public ArrayList<roadWeatherData> getRoadWeatherData() {
        return this.roadWeatherData; }
    public void setroadWeatherData(ArrayList<roadWeatherData> roadWeatherData) {
        this.roadWeatherData = roadWeatherData; }

    public class roadWeatherData{

        String id;
        ArrayList<RoadCondition> roadConditions;
        @JsonProperty("id")
        public String getId() {
            return this.id; }
        public void setId(String id) {
            this.id = id; }

        @JsonProperty("roadConditions")
        public ArrayList<RoadCondition> getRoadConditions() {
            return this.roadConditions; }
        public void setRoadConditions(ArrayList<RoadCondition> roadConditions) {
            this.roadConditions = roadConditions; }

    }

    public class RoadCondition{

        Date time;
        String type;
        String forecastName;
        boolean daylight;
        String roadTemperature;
        String temperature;
        int windSpeed;
        int windDirection;
        String overallRoadCondition;
        String weatherSymbol;
        String reliability;
        ForecastConditionReason forecastConditionReason;
        @JsonProperty("time")
        public Date getTime() {
            return this.time; }
        public void setTime(Date time) {
            this.time = time; }

        @JsonProperty("type")
        public String getType() {
            return this.type; }
        public void setType(String type) {
            this.type = type; }

        @JsonProperty("forecastName")
        public String getForecastName() {
            return this.forecastName; }
        public void setForecastName(String forecastName) {
            this.forecastName = forecastName; }

        @JsonProperty("daylight")
        public boolean getDaylight() {
            return this.daylight; }
        public void setDaylight(boolean daylight) {
            this.daylight = daylight; }

        @JsonProperty("roadTemperature")
        public String getRoadTemperature() {
            return this.roadTemperature; }
        public void setRoadTemperature(String roadTemperature) {
            this.roadTemperature = roadTemperature; }

        @JsonProperty("temperature")
        public String getTemperature() {
            return this.temperature; }
        public void setTemperature(String temperature) {
            this.temperature = temperature; }

        @JsonProperty("windSpeed")
        public int getWindSpeed() {
            return this.windSpeed; }
        public void setWindSpeed(int windSpeed) {
            this.windSpeed = windSpeed; }

        @JsonProperty("windDirection")
        public int getWindDirection() {
            return this.windDirection; }
        public void setWindDirection(int windDirection) {
            this.windDirection = windDirection; }

        @JsonProperty("overallRoadCondition")
        public String getOverallRoadCondition() {
            return this.overallRoadCondition; }
        public void setOverallRoadCondition(String overallRoadCondition) {
            this.overallRoadCondition = overallRoadCondition; }

        @JsonProperty("weatherSymbol")
        public String getWeatherSymbol() {
            return this.weatherSymbol; }
        public void setWeatherSymbol(String weatherSymbol) {
            this.weatherSymbol = weatherSymbol; }

        @JsonProperty("reliability")
        public String getReliability() {
            return this.reliability; }
        public void setReliability(String reliability) {
            this.reliability = reliability; }

        @JsonProperty("forecastConditionReason")
        public ForecastConditionReason getForecastConditionReason() {
            return this.forecastConditionReason; }
        public void setForecastConditionReason(ForecastConditionReason forecastConditionReason) {
            this.forecastConditionReason = forecastConditionReason; }
    }

    public class ForecastConditionReason{

        String precipitationCondition;
        String roadCondition;
        String frictionCondition;
        String windCondition;
        @JsonProperty("precipitationCondition")
        public String getPrecipitationCondition() {
            return this.precipitationCondition; }
        public void setPrecipitationCondition(String precipitationCondition) {
            this.precipitationCondition = precipitationCondition; }

        @JsonProperty("roadCondition")
        public String getRoadCondition() {
            return this.roadCondition; }
        public void setRoadCondition(String roadCondition) {
            this.roadCondition = roadCondition; }

        @JsonProperty("frictionCondition")
        public String getFrictionCondition() {
            return this.frictionCondition; }
        public void setFrictionCondition(String frictionCondition) {
            this.frictionCondition = frictionCondition; }

        @JsonProperty("windCondition")
        public String getWindCondition() {
            return this.windCondition; }
        public void setWindCondition(String windCondition) {
            this.windCondition = windCondition; }
    }
}
