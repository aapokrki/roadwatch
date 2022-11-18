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

//@JsonDeserialize(using = RoadAPILogic.RoadDataDeserializer.class)
public class RoadData {

    public Date dataUpdatedTime;
    public ArrayList<WeatherData> weatherData;

    public class WeatherData {
        public String id;
        public ArrayList<RoadCondition> roadConditions;
    }

    public class RoadCondition {
        public Date time;
        public String type;
        public String forecastName;
        public boolean daylight;
        public String roadTemperature;
        public String temperature;
        public double windSpeed;
        public int windDirection;
        public String overallRoadCondition;
        public String weatherSymbol;
        public String reliability;
        public ForecastConditionReason forecastConditionReason;
    }

    public class ForecastConditionReason {
        public String precipitationCondition;
        public String roadCondition;
    }

    public RoadData(JsonNode json) throws IOException, ParseException {
        ObjectMapper mapper = new ObjectMapper();
        //RoadData rd = mapper.readTree(json.toString()).traverse(mapper).readValueAs(RoadData.class);
        this.dataUpdatedTime = stringToDate(json.get("dataUpdatedTime").asText());
        this.weatherData = readWeatherData(json.get("weatherData"));
        System.out.println("RoadData created");
    }

    private ArrayList<WeatherData> readWeatherData(JsonNode weatherData) {
        ArrayList<WeatherData> wd = new ArrayList<>();
        for (JsonNode node : weatherData) {
            WeatherData w = new WeatherData();
            w.id = node.get("id").asText();
            w.roadConditions = readRoadConditions(node.get("roadConditions"));
            wd.add(w);
        }
        return wd;
    }

    private ArrayList<RoadCondition> readRoadConditions(JsonNode roadConditions) {
        ArrayList<RoadCondition> rc = new ArrayList<>();
        for (JsonNode node : roadConditions) {
            RoadCondition r = new RoadCondition();
            try {
                r.time = stringToDate(node.get("time").asText());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            r.type = node.get("type").asText();
            r.forecastName = node.get("forecastName").asText();
            r.daylight = node.get("daylight").asBoolean();
            r.roadTemperature = node.get("roadTemperature").asText();
            r.temperature = node.get("temperature").asText();
            r.windSpeed = node.get("windSpeed").asDouble();
            r.windDirection = node.get("windDirection").asInt();
            r.overallRoadCondition = node.get("overallRoadCondition").asText();
            r.weatherSymbol = node.get("weatherSymbol").asText();
            r.reliability = node.get("reliability").asText();
            try {
                r.forecastConditionReason = readForecastConditionReason(node.get("forecastConditionReason"));
            } catch (NullPointerException e) {
                continue;
            }


            rc.add(r);
        }
        return rc;
    }

    private ForecastConditionReason readForecastConditionReason(JsonNode forecastConditionReason) {
        ForecastConditionReason fcr = new ForecastConditionReason();
        fcr.precipitationCondition = forecastConditionReason.get("precipitationCondition").asText();
        fcr.roadCondition = forecastConditionReason.get("roadCondition").asText();
        return fcr;
    }
    /*
    private boolean unpackWeatherData() {
        int i = 0;
        for (WeatherData wd : weatherData){
            int j = 0;
            for (RoadCondition rc : wd.roadConditions){
                unpackRoadCondition(rc, i, j);
                j++;
            }
            i++;
        }
        return true;
    }

    private boolean unpackRoadCondition(RoadCondition rc, int i, int j){
        this.weatherData.get(i).roadConditions.get(j).time = rc.time;
        this.weatherData.get(i).roadConditions.get(j).type = rc.type;
        this.weatherData.get(i).roadConditions.get(j).forecastName = rc.forecastName;
        this.weatherData.get(i).roadConditions.get(j).daylight = rc.daylight;
        this.weatherData.get(i).roadConditions.get(j).roadTemperature = rc.roadTemperature;
        this.weatherData.get(i).roadConditions.get(j).temperature = rc.temperature;
        this.weatherData.get(i).roadConditions.get(j).windSpeed = rc.windSpeed;
        this.weatherData.get(i).roadConditions.get(j).windDirection = rc.windDirection;
        this.weatherData.get(i).roadConditions.get(j).overallRoadCondition = rc.overallRoadCondition;
        this.weatherData.get(i).roadConditions.get(j).weatherSymbol = rc.weatherSymbol;
        this.weatherData.get(i).roadConditions.get(j).reliability = rc.reliability;
        this.weatherData.get(i).roadConditions.get(j).forecastConditionReason = rc.forecastConditionReason;

        return true;
    }
    */
    Date stringToDate(String s) throws ParseException {
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(s);
        }
        catch (ParseException e) {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(s);
        }

    }



}
