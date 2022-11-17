package fi.tuni.roadwatch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.Date;

public class WeatherData {

    private Date dateAndTime;

    private String coordinates;

    private double temperature;

    private double cloudiness;

    private double wind;



    public WeatherData(double temperature, double wind, double cloudiness, Date dateAndTime,
                       String coordinates){
        this.temperature = temperature;
        this.wind = wind;
        this.cloudiness = cloudiness;
        this.coordinates = coordinates;
        this.dateAndTime = dateAndTime;
    }



    public Date getDate(){return this.dateAndTime;}

    public String getCoordinates(){return this.coordinates;}

    public double getCloudiness(){return this.cloudiness;}

    public double getWind(){return this.wind;}

    public double getTemperature(){return this.temperature;}
}
