package fi.tuni.roadwatch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class WeatherData {

    @JsonProperty("ParameterName")
    private double temperature;


    private double observed_windspeed;


    private double observed_cloudiness;

    WeatherData(double temperature, double observed_windspeed, double observed_cloudiness){
        this.temperature = temperature;
        this.observed_windspeed = observed_windspeed;
        this.observed_cloudiness = observed_cloudiness;
    }

    public double getTemperature(){
        return this.temperature;
    }

    public double getObserved_windspeed(){
        return this.observed_windspeed;
    }

    public double getObserved_cloudiness(){
        return this.observed_cloudiness;
    }
}
