package fi.tuni.roadwatch;

import com.sothawo.mapjfx.Coordinate;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class SessionData {

//    private RoadWatchController roadWatchController;
//    private MapController mapController;

    public Coordinate currentCoordinates;
    public List<Coordinate> polyCoordinates = new ArrayList<>();
    private Date dateAndTime = Calendar.getInstance().getTime();

    private ArrayList<WeatherData> WantedWeatherData = new ArrayList<>();
    private ArrayList<WeatherDataMinMaxAvg> wantedWeatherAVGMinMax = new ArrayList<>();

    // Used in creation of wantedWeatherData
    private double currentTemp;
    private double currentWind;
    private double currentCloud;

    public static class CoordinateConstraints{

        public CoordinateConstraints(Double minLon, Double minLat, Double maxLon, Double maxLat){
            this.minLon = minLon;
            this.minLat = minLat;

            this.maxLon = maxLon;
            this.maxLat = maxLat;

        }
        private final Double minLon;
        private final Double minLat;

        private final Double maxLon;
        private final Double maxLat;

        public String getAsString(Character c){
            return ""+minLon + c + minLat + c + maxLon + c + maxLat;
        }
    }

    public CoordinateConstraints coordinateConstraints;


    public SessionData() {

    }

    public void setCurrentCoordinates(Coordinate newCoordinate){
        currentCoordinates = newCoordinate;
    }

    public void setPolygonCoordinates(List<Coordinate> polyCoordinates){
        this.polyCoordinates.addAll(polyCoordinates);
    }

    public void calculateMinMaxCoordinates() {

        // TODO: Make more efficient
        if(polyCoordinates != null){
            if(!polyCoordinates.isEmpty()){
                Double maxLongtitude = polyCoordinates.stream().max(Comparator.comparing(Coordinate::getLongitude)).get().getLongitude();
                Double maxLatitude = polyCoordinates.stream().max(Comparator.comparing(Coordinate::getLatitude)).get().getLatitude();

                Double minLongtitude = polyCoordinates.stream().min(Comparator.comparing(Coordinate::getLongitude)).get().getLongitude();
                Double minLatitude = polyCoordinates.stream().min(Comparator.comparing(Coordinate::getLatitude)).get().getLatitude();

                coordinateConstraints = new CoordinateConstraints(minLongtitude, minLatitude, maxLongtitude, maxLatitude);
                System.out.println(coordinateConstraints.getAsString('/'));
            }

        }
    }

    public ArrayList<WeatherDataMinMaxAvg> createAvgMinMax(Date startTime, Date endTime) throws ParseException, ParserConfigurationException, IOException, SAXException {
        WeatherAPILogic weatherAPILogic = new WeatherAPILogic();
        // Creates the URL String to be used according to parameters wanted that include coordinates and start and end time
        // than creates the document used to create the arraylist of WeatherData
        String startTimeString = weatherAPILogic.timeAndDateToIso8601Format(startTime);
        String endTimeString = weatherAPILogic.timeAndDateToIso8601Format(endTime);
        String urlstring = weatherAPILogic.createAVGMINMAXurlString(currentCoordinates.getLatitude(),currentCoordinates.getLongitude(),  startTimeString, endTimeString);

        Document doc = weatherAPILogic.GetApiDocument(urlstring);
        wantedWeatherAVGMinMax = weatherAPILogic.creatingAvgMinMax(doc);

        return wantedWeatherAVGMinMax;
    }

    // WeatherData creation to sessionData
    public ArrayList<WeatherData> createWeatherData(Date startTime, Date endTime) throws ParserConfigurationException, IOException, SAXException, ParseException {
        WeatherAPILogic weatherAPILogic = new WeatherAPILogic();
        // Creates the URL String to be used according to parameters wanted that include coordinates and start and end time
        // than creates the document used to create the arraylist of WeatherData
        String startTimeString = weatherAPILogic.timeAndDateToIso8601Format(startTime);
        String endTimeString = weatherAPILogic.timeAndDateToIso8601Format(endTime);
        String urlstring = weatherAPILogic.createURLString(currentCoordinates.getLatitude(),currentCoordinates.getLongitude(),  startTimeString, endTimeString);


       //Test to see what api is found with parameters
        System.out.println(urlstring);


        Document doc = weatherAPILogic.GetApiDocument(urlstring);
        // Compares current date to starTime to know if we want to create a weatherforecast or weather
        // observation
        if(startTime.after(dateAndTime)){
            this.WantedWeatherData = weatherAPILogic.creatingWeatherForecast(doc);
        }
        this.WantedWeatherData = weatherAPILogic.creatingWeatherObservations(doc);
        return this.WantedWeatherData;
    }

    // Helper function to get the closest date to current
    public Date getClosestDate(){
        ArrayList<Date> alldates = new ArrayList<>();
        for (WeatherData wd : this.WantedWeatherData){
            alldates.add(wd.getDate());
        }

        Date closest = Collections.min(alldates, new Comparator<Date>() {
            @Override
            public int compare(Date o1, Date o2) {
                long diff1 = Math.abs(o1.getTime() - dateAndTime.getTime());
                long diff2 = Math.abs(o2.getTime() - dateAndTime.getTime());
                return diff1 < diff2 ? -1:1;
            }
        });
        return closest;
    }



}
