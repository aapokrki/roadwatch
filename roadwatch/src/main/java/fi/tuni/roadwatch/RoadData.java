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




    // Attributes for road maintenance task responses

    public String type;
    // public Date dataUpdatedTime;
    public ArrayList<maintenanceFeature> features;

    public class maintenanceFeature{
        public String type;
        public Properties properties;
        public maintenanceGeometry geometry;
    }

    public class maintenanceGeometry{
        public String type;
        public ArrayList<ArrayList<Double>> coordinates;
    }

    public class maintenanceProperties{
        public int id;
        public Object previousId;
        public Date sendingTime;
        public Date created;
        public ArrayList<String> tasks;
        public Date startTime;
        public Date endTime;
        public double direction;
        public String domain;
        public String source;
    }

    // Attributes for traffic messages

    //public String type;
    //public Date dataUpdatedTime;
    //public ArrayList<Feature> features;


    public class AlertCLocation{
        public int locationCode;
        public String name;
        public int distance;
    }

    public class Announcement{
        public String language;
        public String title;
        public Location location;
        public LocationDetails locationDetails;
        public ArrayList<messageFeature> features;
        public ArrayList<Object> roadWorkPhases;
        public String comment;
        public TimeAndDuration timeAndDuration;
        public String additionalInformation;
        public String sender;
    }

    public class Contact{
        public String phone;
        public String email;
    }

    public class messageFeature{
        public String type;
        public messageGeometry geometry;
        public Properties properties;
        public String name;
    }

    public class messageGeometry{
        public String type;
        public ArrayList<Object> coordinates;
    }

    public class Location{
        public int countryCode;
        public int locationTableNumber;
        public String locationTableVersion;
        public String description;
    }

    public class LocationDetails{
        public RoadAddressLocation roadAddressLocation;
    }

    public class PrimaryPoint{
        public String municipality;
        public String province;
        public String country;
        public RoadAddress roadAddress;
        public AlertCLocation alertCLocation;
        public String roadName;
    }

    public class messageProperties{
        public String situationId;
        public String situationType;
        public String trafficAnnouncementType;
        public int version;
        public Date releaseTime;
        public Date versionTime;
        public ArrayList<Announcement> announcements;
        public Contact contact;
        public Date dataUpdatedTime;
    }

    public class RoadAddress{
        public int road;
        public int roadSection;
        public int distance;
    }

    public class RoadAddressLocation{
        public PrimaryPoint primaryPoint;
        public SecondaryPoint secondaryPoint;
        public String direction;
    }


    public class SecondaryPoint{
        public String municipality;
        public String province;
        public String country;
        public RoadAddress roadAddress;
        public AlertCLocation alertCLocation;
        public String roadName;
    }

    public class TimeAndDuration{
        public Date startTime;
        public Date endTime;
    }





}
