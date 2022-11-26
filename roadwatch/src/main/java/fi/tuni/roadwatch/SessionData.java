package fi.tuni.roadwatch;

import com.sothawo.mapjfx.Coordinate;
import javafx.scene.chart.XYChart;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class SessionData {

//    private RoadWatchController roadWatchController;
//    private MapController mapController;

    public Coordinate currentCoordinates;
    public List<Coordinate> polyCoordinates = new ArrayList<>();
    private Date dateAndTime = Calendar.getInstance().getTime();


    public ArrayList<WeatherData> wantedWeatherData = new ArrayList<>();
    public ArrayList<WeatherDataMinMaxAvg> wantedWeatherAVGMinMax = new ArrayList<>();
    public TreeMap<Date, WeatherData> savedWeatherData = new TreeMap<>();

    public TrafficMessage trafficMessage = new TrafficMessage();

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
        public final Double minLon;
        public final Double minLat;

        private final Double maxLon;
        private final Double maxLat;

        public String getAsString(Character c){
            return ""+minLon + c + minLat + c + maxLon + c + maxLat;
        }
    }

    public CoordinateConstraints coordinateConstraints;

    public static RoadAPILogic roadAPILogic;
    public static WeatherAPILogic weatherAPILogic;


    public SessionData() throws URISyntaxException, IOException {
        roadAPILogic = new RoadAPILogic();
        weatherAPILogic = new WeatherAPILogic();
        trafficMessage = roadAPILogic.getTrafficMessages();

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
            // TODO: ADD THIS TO ROADDATA CONSTRUCTOR
            // TODO: Make nicer maybe
            // Checks the traffic messages in a given area
            ArrayList<TrafficMessage.Feature> messagesInArea = new ArrayList<>();
            for (TrafficMessage.Feature feature : trafficMessage.features){
                if(feature.geometry != null){
                    for (ArrayList<ArrayList<Double>> coordinates : feature.geometry.coordinates) {
                        for (ArrayList<Double> coordinate : coordinates) {
                            if(coordinate.size() == 2){
                                if(coordinate.get(0) > coordinateConstraints.minLon &&
                                        coordinate.get(0) < coordinateConstraints.maxLon &&
                                        coordinate.get(1) > coordinateConstraints.minLat &&
                                        coordinate.get(1) < coordinateConstraints.maxLat){
                                    messagesInArea.add(feature);
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }
            }
            // TEST PRINTS
            System.out.println(messagesInArea.size() + " Traffic messages in the area");
//            for (TrafficMessage.Feature feature : messagesInArea){
//                if(feature.properties.announcements != null) {
//                    for(TrafficMessage.Announcements announcement: feature.properties.announcements){
//                        System.out.println(announcement.title);
//                    }
//                }
//            }
        }

    }

    public void createAvgMinMax(Date startTime, Date endTime) throws ParseException, ParserConfigurationException, IOException, SAXException {
        this.wantedWeatherAVGMinMax.clear();
        // Creates the URL String to be used according to parameters wanted that include coordinates and start and end time
        // than creates the document used to create the arraylist of WeatherData
        String startTimeString = weatherAPILogic.timeAndDateToIso8601Format(startTime);
        String endTimeString = weatherAPILogic.timeAndDateToIso8601Format(endTime);


        String urlstring = weatherAPILogic.createAVGMINMAXurlString(coordinateConstraints.minLat, coordinateConstraints.minLon,  startTimeString, endTimeString);

        this.wantedWeatherAVGMinMax = weatherAPILogic.creatingAvgMinMax(weatherAPILogic.GetApiDocument(urlstring));

    }

    // WeatherData creation to sessionData
    public void createWeatherData(Date startTime, Date endTime) throws ParserConfigurationException, IOException, SAXException, ParseException {
        this.wantedWeatherData.clear();
        // Creates the URL String to be used according to parameters wanted that include coordinates and start and end time
        // than creates the document used to create the arraylist of WeatherData
        String startTimeString = weatherAPILogic.timeAndDateToIso8601Format(startTime);
        String endTimeString = weatherAPILogic.timeAndDateToIso8601Format(endTime);
        String urlstring = weatherAPILogic.createURLString(coordinateConstraints.minLat, coordinateConstraints.minLon,  startTimeString, endTimeString);
        System.out.println(urlstring);
        // Compares current date to starTime to know if we want to create a weatherforecast or weather
        // observation
        if(startTime.after(dateAndTime)){
            this.wantedWeatherData = weatherAPILogic.creatingWeatherForecast(weatherAPILogic.GetApiDocument(urlstring));
        }
        this.wantedWeatherData = weatherAPILogic.creatingWeatherObservations(weatherAPILogic.GetApiDocument(urlstring));

    }

    // Helper function to get the closest date to current
    public Date getClosestDate(){
        ArrayList<Date> alldates = new ArrayList<>();
        for (WeatherData wd : this.wantedWeatherData){
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

    public double getMIN_value(){
        double min = wantedWeatherAVGMinMax.get(0).getTempMIN();

        for(WeatherDataMinMaxAvg wd : wantedWeatherAVGMinMax){
            if(wd.getTempMIN() <= min){
                min = wd.getTempMIN();
            }

        }

        return min;
    }

    public double getMAX_value(){
        double max = wantedWeatherAVGMinMax.get(0).getTempMAX();

        for(WeatherDataMinMaxAvg wd : wantedWeatherAVGMinMax){
            if(wd.getTempMAX() >= max){
                max = wd.getTempMAX();
            }
        }

        return max;
    }

    public String getAVG_value(){

        double average = wantedWeatherAVGMinMax.get(0).getTempAverage();;
        for(WeatherDataMinMaxAvg wd : wantedWeatherAVGMinMax){
            average += wd.getTempAverage();
        }

        DecimalFormat df = new DecimalFormat("0.00");
        average = average/wantedWeatherAVGMinMax.size();

        return df.format(average);
    }

    public XYChart.Series<String, Double> createGraphSeries(String chart_type){

        XYChart.Series<String, Double> wind_series = new XYChart.Series<>();
        XYChart.Series<String, Double> visibility_series = new XYChart.Series<>();

        for(WeatherData wd : this.wantedWeatherData){
            Date datecheck = wd.getDate();
            Calendar date = Calendar.getInstance();
            date.setTime(datecheck);
            int hours = date.get(Calendar.HOUR_OF_DAY);
            int minutes = date.get(Calendar.MINUTE);
            if(hours % 2 == 0 && minutes == 0){
                if(chart_type.equals("WIND")){
                    Double Y = wd.getWind();
                    String X = weatherAPILogic.timeAndDateToIso8601Format(wd.getDate());
                    wind_series.getData().add(new XYChart.Data<>(X, Y));
                }
                if(chart_type.equals("VISIBILITY")){
                    Double Y = wd.getCloudiness();
                    String X = weatherAPILogic.timeAndDateToIso8601Format(wd.getDate());
                    visibility_series.getData().add(new XYChart.Data<>(X, Y));
                }
            }
        }
        if(Objects.equals(chart_type, "VISIBILITY")){
            return visibility_series;
        }
        if(Objects.equals(chart_type, "WIND")){
            return wind_series;
        }
        return null;

    }

    // Helper function to set the time of day to 00:00:00, also can add days to date
    public Date trimToStart(Date date, int Days){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, Days);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);

        return cal.getTime();
    }

    // Helper function to set the time of day to 23:59:59, also can add days to date
    public Date trimToEnd(Date date, int Days){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, Days);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE,59);
        cal.set(Calendar.SECOND,59);

        return cal.getTime();
    }

    // Changes date String in to string 8601Format to use in urlstring
    public Date timeAndDateAsDate(String datestring) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(datestring);
    }

    public Date convertToDateViaInstant(LocalDate dateToConvert) {
        return java.util.Date.from(dateToConvert.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    public void saveWeatherData(Date savedDate){
        for(WeatherData wd : wantedWeatherData){
            Date startDate = trimToStart(savedDate, 0);
            Date endDate = trimToEnd(savedDate, 0);
            if (wd.getDate() == startDate && wd.getDate().after(startDate) && wd.getDate().before(endDate)
            && wd.getDate() == endDate){
                if(!savedWeatherData.containsKey(wd.getDate())){
                    savedWeatherData.put(wd.getDate(), wd);
                }

            }
        }

    }





}
