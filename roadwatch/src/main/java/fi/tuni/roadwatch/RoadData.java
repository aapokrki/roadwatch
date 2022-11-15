package fi.tuni.roadwatch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.*;

//@JsonDeserialize(using = RoadAPILogic.RoadDataDeserializer.class)
public class RoadData {

    public Date dataUpdatedTime;
    public ArrayList<WeatherData> weatherData;

    public class WeatherData{
        public String id;
        public ArrayList<RoadCondition> roadConditions;
    }

    public class RoadCondition{
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

    public class ForecastConditionReason{
        public String precipitationCondition;
        public String roadCondition;
    }






}
