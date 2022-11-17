package fi.tuni.roadwatch;

import java.util.Date;

public class WeatherDataMinMaxAvg {
    private Date dateAndTime;

    private String coordinates;

    private double tempAverage;

    private double tempMIN;

    private double tempMAX;

    public WeatherDataMinMaxAvg(Date dateAndTime, String coordinates, double tempAverage, double tempMIN, double tempMAX){
        this.dateAndTime = dateAndTime;
        this.coordinates = coordinates;
        this.tempAverage = tempAverage;
        this.tempMIN = tempMIN;
        this.tempMAX = tempMAX;
    }

    public Date getDate(){return this.dateAndTime;}

    public String getCoordinates(){return this.coordinates;}

    public double getTempAverage(){return this.tempAverage;}

    public double getTempMIN(){return this.tempMIN;}

    public double getTempMAX(){return this.tempMAX;}
}
