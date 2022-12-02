package fi.tuni.roadwatch;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data class for weatherData
 */
public class WeatherData {
    private Date dateAndTime;
    private String coordinates;
    private double temperature;
    private double cloudiness;
    private double wind;

    /**
     * Constructor of WeatherData
     * @param temperature given temperature
     * @param wind given wind
     * @param cloudiness given cloudiness
     * @param dateAndTime given dateAndTime
     * @param coordinates given coordinates
     */
    @JsonCreator
    public WeatherData(double temperature, double wind, double cloudiness, Date dateAndTime,
                       String coordinates){
        this.temperature = temperature;
        this.wind = wind;
        this.cloudiness = cloudiness;
        this.coordinates = coordinates;
        this.dateAndTime = dateAndTime;
    }

    /**
     * Getter for date
     * @return Date object
     */
    @JsonProperty("dateAndTime")
    public Date getDate(){return this.dateAndTime;}
    /**
     * Setter for date
     * @param date given date
     */
    public void setDate(Date date){this.dateAndTime = date;}

    /**
     * Getter for coordinates
     * @return String object
     */
    @JsonProperty("Coordinates")
    public String getCoordinates(){return this.coordinates;}
    /**
     * Setter for coordinates
     * @param coordinates given coordinates
     */
    public void setCoordinates(String coordinates){this.coordinates = coordinates;}

    /**
     * Getter for cloudiness
     * @return double
     */
    @JsonProperty("Cloudiness")
    public double getCloudiness(){return this.cloudiness;}
    /**
     * Setter for cloudiness
     * @param cloudiness given cloudiness
     */
    public void setCloudiness(double cloudiness){this.cloudiness = cloudiness;}

    /**
     * Getter for wind
     * @return double
     */
    @JsonProperty("Wind")
    public double getWind(){return this.wind;}
    /**
     * Setter for wind
     * @param wind given wind
     */
    public void setWind(double wind){this.wind = wind;}

    /**
     * Getter for temperature
     * @return double
     */
    @JsonProperty("Temperature")
    public double getTemperature(){return this.temperature;}
    /**
     * Setter for temperature
     * @param temperature given temperature
     */
    public void setTemperature(double temperature){this.temperature = temperature;}
}
