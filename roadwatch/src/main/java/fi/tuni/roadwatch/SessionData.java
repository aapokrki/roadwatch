package fi.tuni.roadwatch;

import com.sothawo.mapjfx.Coordinate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.SubScene;
import javafx.scene.chart.PieChart;
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
    private static final DecimalFormat df = new DecimalFormat("0.000");

    public List<Coordinate> polyCoordinates = new ArrayList<>();
    public Date dateAndTime = Calendar.getInstance().getTime();


    public ArrayList<WeatherData> wantedWeatherData = new ArrayList<>();
    public ArrayList<WeatherDataMinMaxAvg> wantedWeatherAVGMinMax = new ArrayList<>();
    public TreeMap<Date, WeatherData> savedWeatherData = new TreeMap<>();

    public TrafficMessage trafficMessage;
    public RoadData roadData;
    public ArrayList<Maintenance> maintenancesInTimeLine;
    public ArrayList<String> taskTypes;

    public CoordinateConstraints coordinateConstraints;

    public HelperFunctions helperFunctions;

    public static RoadAPILogic roadAPILogic;
    public static WeatherAPILogic weatherAPILogic;

    public static SavedDataLogic savedDataLogic;

    private enum DataClassType {
        WEATHER, WEATHERMINMAXAVG, ROAD, TRAFFIC, MAINTENANCE
    }


    public SessionData() throws URISyntaxException, IOException {
        roadAPILogic = new RoadAPILogic();
        weatherAPILogic = new WeatherAPILogic();
        trafficMessage = roadAPILogic.getTrafficMessages();
        savedDataLogic = new SavedDataLogic();
        createTaskTypes();
        helperFunctions = new HelperFunctions();

    }

    /**
     * Sets helper functions to sessionData
     * @param helperFunctions HelperFunctions object
     */
    public void setHelperFunctions(HelperFunctions helperFunctions){
        this.helperFunctions = helperFunctions;
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

                maxLongtitude = Double.parseDouble(df.format(maxLongtitude).replace(',','.'));
                maxLatitude = Double.parseDouble(df.format(maxLatitude).replace(',','.'));

                Double minLongtitude = polyCoordinates.stream().min(Comparator.comparing(Coordinate::getLongitude)).get().getLongitude();
                Double minLatitude = polyCoordinates.stream().min(Comparator.comparing(Coordinate::getLatitude)).get().getLatitude();

                minLongtitude = Double.parseDouble(df.format(minLongtitude).replace(',','.'));
                minLatitude = Double.parseDouble(df.format(minLatitude).replace(',','.'));

                coordinateConstraints = new CoordinateConstraints(minLongtitude, minLatitude, maxLongtitude, maxLatitude);
                System.out.println(coordinateConstraints.getAsString('/'));
            }
        }
    }

    public void createRoadData() throws IOException, URISyntaxException {
        roadData = roadAPILogic.getRoadData(coordinateConstraints.getAsString('/'));
        //TODO: ADD TIMEFRAME FILTERING OF TRAFFICMESSAGES
        roadData.trafficMessageAmount = trafficMessage.messagesInArea(coordinateConstraints);
    }

    public void createMaintenance(String taskId,LocalDate startDate, LocalDate endDate) throws IOException, URISyntaxException {

        maintenancesInTimeLine = new ArrayList<>();

        System.out.println(startDate + " -- " + endDate);
        for (LocalDate date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
            System.out.println(date);
            Date dayIndex = helperFunctions.convertToDateViaInstant(date);
            Maintenance maintenance = roadAPILogic.getMaintenances(taskId,coordinateConstraints.getAsMaintenanceString(), helperFunctions.trimToStart(dayIndex,0), helperFunctions.trimToEnd(dayIndex,0));
            maintenance.setTasksAndDate(dayIndex);
            maintenancesInTimeLine.add(maintenance);
        }
        System.out.println(getMaintenanceAverages());
    }

    /**
     * wantedWeatherAVGMinMax creation to sessionData
     * @param startTime Date startTime of data creation
     * @param endTime Date endTime of data creation
     * @return null checker
     * @throws ParseException
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public boolean createAvgMinMax(Date startTime, Date endTime) throws ParseException, ParserConfigurationException, IOException, SAXException {
        this.wantedWeatherAVGMinMax.clear();
        // Creates the URL String to be used according to parameters wanted that include coordinates and start and end time
        // than creates the document used to create the arraylist of WeatherData
        String startTimeString = helperFunctions.timeAndDateToIso8601Format(startTime);
        String endTimeString = helperFunctions.timeAndDateToIso8601Format(endTime);

        String urlstring = weatherAPILogic.createAVGMINMAXurlString(coordinateConstraints,  startTimeString, endTimeString);
        System.out.println(urlstring);
        this.wantedWeatherAVGMinMax = weatherAPILogic.creatingAvgMinMax(weatherAPILogic.GetApiDocument(urlstring));

        // If the created arraylist is empty, the API call failed, needs bigger bbox area
        // boolean check to display error messages in WeatherController
        return wantedWeatherAVGMinMax.size() != 0;
    }

    /**
     * WeatherData creation to sessionData
     * @param startTime Date startTime of data creation
     * @param endTime Date endTime of data creation
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws ParseException
     */
    public void createWeatherData(Date startTime, Date endTime) throws ParserConfigurationException, IOException, SAXException, ParseException {
        this.wantedWeatherData.clear();
        // Creates the URL String to be used according to parameters wanted that include coordinates and start and end time
        // than creates the document used to create the arraylist of WeatherData
        String startTimeString = helperFunctions.timeAndDateToIso8601Format(startTime);
        String endTimeString = helperFunctions.timeAndDateToIso8601Format(endTime);
        String urlstring = weatherAPILogic.createURLString(coordinateConstraints,  startTimeString, endTimeString);
        System.out.println(urlstring);
        // Compares current date to starTime to know if we want to create a weatherforecast or weather
        // observation
        if(startTime.after(dateAndTime)){
            this.wantedWeatherData = weatherAPILogic.creatingWeatherForecast(weatherAPILogic.GetApiDocument(urlstring));
        }
        this.wantedWeatherData = weatherAPILogic.creatingWeatherObservations(weatherAPILogic.GetApiDocument(urlstring));
    }

    public void createTaskTypes() throws URISyntaxException, IOException {
        taskTypes = roadAPILogic.getTaskTypes();
    }

    // Tää siirretään jotenki roaddataan
    public ObservableList<PieChart.Data> createRoadConditionChart(Map<String, Double> conditionMap) {
        ArrayList<PieChart.Data> pieChartData = new ArrayList<>();
        for(Map.Entry<String, Double> cond : conditionMap.entrySet()){

            pieChartData.add(new PieChart.Data(cond.getKey() + " (" + cond.getValue() + ")",cond.getValue()));
        }
        return FXCollections.observableArrayList(pieChartData);
    }
    public ObservableList<PieChart.Data> createMaintenanceChart() {
        ArrayList<PieChart.Data> pieChartData = new ArrayList<>();
        for(Map.Entry<String, Double> cond : getMaintenanceAverages().entrySet()){

            pieChartData.add(new PieChart.Data(cond.getKey() + " (" + cond.getValue() + "/DAY)",cond.getValue()));
        }
        return FXCollections.observableArrayList(pieChartData);
    }

    /**
     * Creates XYChart.Series from wanted objects
     * @param chart_type String specifying what type of data wanted
     * @return created XYChart.Series<String, Double>
     */
    public XYChart.Series<String, Double> createGraphSeries(String chart_type){
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("dd/HH:mm");

        XYChart.Series<String, Double> series = new XYChart.Series<>();
        Map<String, Double> seriesMap = new TreeMap<>();
        Double Y = null;

        ArrayList<String> coordsInArea = new ArrayList<>();

        for(WeatherData wd : this.wantedWeatherData){
            if(!coordsInArea.contains(wd.getCoordinates())){
                coordsInArea.add(wd.getCoordinates());
            }

                if(chart_type.equals("WIND")){
                    Y = wd.getWind();
                }
                if(chart_type.equals("VISIBILITY")){
                    Y = wd.getCloudiness();
                }

                assert Y != null;
                // Do not add NaN data to chart. It will break the charting.
                if(!Y.isNaN()){
                    Date date = wd.getDate();
                    String X = sdf.format(date.getTime());

                    Double finalY = Y;
                    seriesMap.compute(X, (key, val) -> {
                        if(val == null){
                            return finalY;
                        }
                        return finalY+val;
                    });
                }
        }

        //Calculate data averages of coordinates in area
        seriesMap.replaceAll( (k,v) -> v /coordsInArea.size());
        for(Map.Entry<String,Double> entry : seriesMap.entrySet()){
            series.getData().add(new XYChart.Data<>(entry.getKey(),entry.getValue()));
        }
        return series;
    }


    // Returns the average amount of tasks per day based on set timeline
    public Map<String, Double> getMaintenanceAverages(){
        Map<String, Double> averageMaintenanceAmount = new TreeMap<>();
        for(Maintenance maintenance : maintenancesInTimeLine){
            maintenance.tasks.forEach((task,amount)-> {
                averageMaintenanceAmount.compute(task, (key,val) ->{
                    if(val == null){
                        return Double.valueOf(amount);
                    }
                    return val + amount;
                });
            });
        }
        averageMaintenanceAmount.replaceAll( (k,v) -> v/maintenancesInTimeLine.size());
        return averageMaintenanceAmount;
    }

    /**
     * Saves weatherData from given day to map
     * @param savedDate wanted date to be saved
     */
    public void saveWeatherData(Date savedDate){
        for(WeatherData wd : wantedWeatherData){
            Date startDate = helperFunctions.trimToStart(savedDate, 0);
            Date endDate = helperFunctions.trimToEnd(savedDate, 0);
            if (wd.getDate() == startDate && wd.getDate().after(startDate) && wd.getDate().before(endDate)
            && wd.getDate() == endDate){
                if(!savedWeatherData.containsKey(wd.getDate())){
                    savedWeatherData.put(wd.getDate(), wd);
                }
            }
        }
    }

    /**
     * Coordinate null checker
     * @return boolean true or false
     */
    public boolean coordinateCheck(){
        if(coordinateConstraints == null){
            return false;
        }
        return true;
    }
    
    /**
     * Writes data to either a JSON or XML file, based on dataClassType
     * @param fileName the name of the file to write to
     * @param dataClassType the type of data to write
     * @return true if write was successful, false otherwise
     */
    public boolean writeDataToFile(String fileName, DataClassType dataClassType) {
        try {
            switch (dataClassType) {
                case WEATHER:
                    for(WeatherData wd : wantedWeatherData){
                        savedDataLogic.writeWeatherData(fileName, wd);
                    }
                case WEATHERMINMAXAVG:
                    for(WeatherDataMinMaxAvg wd : wantedWeatherAVGMinMax){
                        savedDataLogic.writeWeatherDataMinMaxAvg(fileName, wd);
                    }
                case MAINTENANCE:
                    for (Maintenance maintenance : this.maintenancesInTimeLine) {
                        savedDataLogic.writeMaintenance(fileName + dateAsDayString(maintenance.date), maintenance);
                    }
                case ROAD:
                    savedDataLogic.writeRoadData(fileName, this.roadData);
                case TRAFFIC:
                    savedDataLogic.writeTrafficMessage(fileName, this.trafficMessage);
                    

        }
    } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * Reads data from either a JSON or XML file, based on dataClassType
     * @param fileName the name of the file to read from
     * @param dataClassType the type of data to read
     * @return true if read was successful, false otherwise
     */
    public boolean readDataFromFile(String fileName, DataClassType dataClassType) throws IOException, URISyntaxException {
        switch (dataClassType){
            case WEATHER:
                wantedWeatherData.add(savedDataLogic.readWeatherData(fileName));
                return true;
            case WEATHERMINMAXAVG:
                wantedWeatherAVGMinMax.add(savedDataLogic.readWeatherDataMinMaxAvg(fileName));
                return true;
            case ROAD:
                roadData = savedDataLogic.readRoadData(fileName);
                return true;
            case TRAFFIC:
                trafficMessage = savedDataLogic.readTrafficMessage(fileName);
                roadData.trafficMessageAmount = trafficMessage.messagesInArea(coordinateConstraints);
                return true;
            case MAINTENANCE:
                maintenancesInTimeLine.add(savedDataLogic.readMaintenance(fileName));
                return true;
            default:
                return false;
        }
    }
    
    /**
     * Helper function for converting a date to a string
     * @return String of the date in the format yyyy-MM-dd
     */
    public String dateAsDayString(Date date){
        return new SimpleDateFormat("EEEE").format(date);
    }
}
