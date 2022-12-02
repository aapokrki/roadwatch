package fi.tuni.roadwatch;

import com.sothawo.mapjfx.Coordinate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class HelperFunctions {

    SessionData sessionData;

    /**
     * Sets sessionData to the current state
     * @param sessionData Reference to sessionData
     */
    public void setSessionData(SessionData sessionData) {
        this.sessionData = sessionData;
    }


    /**
     * Retrieves the closest date to the current date from saved dates
     * @return Date object closest to current date
     */
    public Date getClosestDate(){
        ArrayList<Date> alldates = new ArrayList<>();
        for (WeatherData wd : sessionData.wantedWeatherData){
            alldates.add(wd.getDate());
        }

        Date closest = Collections.min(alldates, new Comparator<Date>() {
            @Override
            public int compare(Date o1, Date o2) {
                long diff1 = Math.abs(o1.getTime() - sessionData.dateAndTime.getTime());
                long diff2 = Math.abs(o2.getTime() - sessionData.dateAndTime.getTime());
                return diff1 < diff2 ? -1:1;
            }
        });
        return closest;
    }

    /**
     * Helper function to set the time of day to 00:00:00, also can add days to date
     * @param date Date object wanted to trim
     * @param Days How many days wanted to add to the date
     * @return Date Object trimmed
     */
    public Date trimToStart(Date date, int Days){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, Days);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);

        return cal.getTime();
    }

    /**
     * Helper function to set the time of day to 23:59:59, also can add days to date
     * @param date Date object wanted to trim
     * @param Days How many days wanted to add to the date
     * @return Date Object trimmed
     */
    public Date trimToEnd(Date date, int Days){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, Days);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE,59);
        cal.set(Calendar.SECOND,59);

        return cal.getTime();
    }


    /**
     * Transforms string in ISO8601 format to Date object
     * @param datestring string in ISO8601 format
     * @return Date object of datestring
     * @throws ParseException
     */
    public Date timeAndDateAsDate(String datestring) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(datestring);
    }


    /**
     * Converts localDate to Date object
     * @param dateToConvert localDate to convert
     * @return Date object
     */
    public Date convertToDateViaInstant(LocalDate dateToConvert) {
        return java.util.Date.from(dateToConvert.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }


    /**
     * Counts the min value of all wanted temperature data
     * @return double min value
     */
    public double getMIN_value(){
        double min = sessionData.wantedWeatherAVGMinMax.get(0).getTempMIN();
        for(WeatherDataMinMaxAvg wd : sessionData.wantedWeatherAVGMinMax){
            if(wd.getTempMIN() <= min){
                min = wd.getTempMIN();
            }
        }

        return min;
    }

    /**
     * Counts the max value of all wanted temperature data
     * @return double max value
     */
    public double getMAX_value(){
        double max = sessionData.wantedWeatherAVGMinMax.get(0).getTempMAX();
        for(WeatherDataMinMaxAvg wd : sessionData.wantedWeatherAVGMinMax){
            if(wd.getTempMAX() >= max){
                max = wd.getTempMAX();
            }
        }

        return max;
    }

    /**
     * Counts the average value of all wanted temperature data
     * @return String of average value
     */
    public String getAVG_value(){
        double average = sessionData.wantedWeatherAVGMinMax.get(0).getTempAverage();;
        for(WeatherDataMinMaxAvg wd : sessionData.wantedWeatherAVGMinMax){
            average += wd.getTempAverage();
        }
        DecimalFormat df = new DecimalFormat("0.00");
        average = average/sessionData.wantedWeatherAVGMinMax.size();

        return df.format(average);
    }


    /**
     * Changes date in to string 8601Format to use in urlstring
     * @param date Date object
     * @return String of date object in 8601 format
     */
    public String timeAndDateToIso8601Format(Date date){
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                .format(date);
    }


    /**
     * Helper function for converting a date to a specific string
     * @return String of the date in the format yyyy-MM-dd
     */
    public String dateAsDayString(Date date){
        return new SimpleDateFormat("EEEE").format(date);
    }

    /**
     * Setter for PolygonCoordinates in SessionData
     */
    public void setPolygonCoordinates(List<Coordinate> polyCoordinates){
        sessionData.polyCoordinates.addAll(polyCoordinates);
    }

    /**
     * Constructor for maintenance task types in SessionData
     */
    public void createTaskTypes(RoadAPILogic roadAPILogic, SessionData sessionData) throws URISyntaxException, IOException {
        if (sessionData != null) {
            sessionData.taskTypes = roadAPILogic.getTaskTypes();
        }
    }

    /**
     * Coordinate null checker
     * @return boolean true or false
     */
    public boolean coordinateCheck(){
        return sessionData.coordinateConstraints == null;
    }
}
