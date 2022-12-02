package fi.tuni.roadwatch;

import java.util.Date;

/**
 * Data class for weatherDataMinMaxAvg
 */
public class WeatherDataMinMaxAvg {
    private Date dateAndTime;
    private String coordinates;
    private double tempAverage;
    private double tempMIN;
    private double tempMAX;

    /**
     * Constructor of WeatherDataMinMaxAvg
     * @param dateAndTime given date and time as date object
     * @param coordinates given coordinates
     * @param tempAverage given temperature Average
     * @param tempMIN given temperature min
     * @param tempMAX given temperature max
     */
    public WeatherDataMinMaxAvg(Date dateAndTime, String coordinates, double tempAverage, double tempMIN, double tempMAX){
        this.dateAndTime = dateAndTime;
        this.coordinates = coordinates;
        this.tempAverage = tempAverage;
        this.tempMIN = tempMIN;
        this.tempMAX = tempMAX;
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
     * Getter for average temperature
     * @return Date object
     */
    public double getTempAverage(){return this.tempAverage;}

    /**
     * Getter for min temperature
     * @return double
     */
    public double getTempMIN(){return this.tempMIN;}

    /**
     * Getter for max temperature
     * @return double
     */
    public double getTempMAX(){return this.tempMAX;}
}
