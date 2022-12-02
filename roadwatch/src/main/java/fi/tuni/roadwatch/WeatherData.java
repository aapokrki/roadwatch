package fi.tuni.roadwatch;
import java.util.Date;

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
    public Date getDate(){return this.dateAndTime;}

    /**
     * Getter for coordinates
     * @return String object
     */
    public String getCoordinates(){return this.coordinates;}

    /**
     * Getter for cloudiness
     * @return double
     */
    public double getCloudiness(){return this.cloudiness;}

    /**
     * Getter for wind
     * @return double
     */
    public double getWind(){return this.wind;}

    /**
     * Getter for temperature
     * @return double
     */
    public double getTemperature(){return this.temperature;}






}
