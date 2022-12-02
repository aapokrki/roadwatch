package fi.tuni.roadwatch;

import com.sothawo.mapjfx.Coordinate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class SessionData {
    private static final DecimalFormat df = new DecimalFormat("0.000");

    public List<Coordinate> polyCoordinates = new ArrayList<>();
    public Date dateAndTime = Calendar.getInstance().getTime();
    public ArrayList<String> presetLocations;

    public String weatherPreference;
    public String conditionPreference;
    public String maintenancePreference;
    public String locationPreference;

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

    public SavedDataLogic savedDataLogic;

    public enum DataClassType {
        WEATHER, WEATHERMINMAXAVG, ROAD, TRAFFIC, MAINTENANCE
    }

    /**
     * Constructor for SessionData
     */
    public SessionData() throws URISyntaxException, IOException {
        roadAPILogic = new RoadAPILogic();
        weatherAPILogic = new WeatherAPILogic();
        trafficMessage = roadAPILogic.getTrafficMessages();
        savedDataLogic = new SavedDataLogic();
        helperFunctions = new HelperFunctions();
        helperFunctions.createTaskTypes(roadAPILogic, this);

        try {
            loadPreferencesFromJSON("preferences.json");
        } catch (IOException e) {
            weatherPreference = "TEMPERATURE";
            conditionPreference = "OVERALL";
            maintenancePreference = "ALL";
            locationPreference = "Tampere";
        }



    }

    /**
     * Sets helper functions to sessionData
     * @param helperFunctions HelperFunctions object
     */
    public void setHelperFunctions(HelperFunctions helperFunctions){
        this.helperFunctions = helperFunctions;
    }


    /**
     * Calculates the bbox of saved coordinates in map
     */
    public void calculateMinMaxCoordinates() {
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

            }
        }
    }

    /**
     * Creates a RoadData class to SessionData
     */
    public void createRoadData() throws IOException, URISyntaxException {
        roadData = roadAPILogic.getRoadData(coordinateConstraints.getAsString('/'));
        //TODO: ADD TIMEFRAME FILTERING OF TRAFFICMESSAGES
        roadData.trafficMessageAmount = trafficMessage.messagesInArea(coordinateConstraints);
    }

    /**
     * Creates Maintenance class to SessionData based on task type between given timeline
     * @param taskId
     * @param startDate
     * @param endDate
     */
    public void createMaintenance(String taskId,LocalDate startDate, LocalDate endDate) throws IOException, URISyntaxException {

        maintenancesInTimeLine = new ArrayList<>();

        for (LocalDate date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
            Date dayIndex = helperFunctions.convertToDateViaInstant(date);
            Maintenance maintenance = roadAPILogic.getMaintenances(taskId,coordinateConstraints.getAsMaintenanceString(), helperFunctions.trimToStart(dayIndex,0), helperFunctions.trimToEnd(dayIndex,0));
            maintenance.setTasksAndDate(dayIndex);
            maintenancesInTimeLine.add(maintenance);
        }
    }

    /**
     * wantedWeatherAVGMinMax creation to sessionData
     * @param startTime Date startTime of data creation
     * @param endTime Date endTime of data creation
     * @return null checker
     */
    public boolean createAvgMinMax(Date startTime, Date endTime) throws ParseException, ParserConfigurationException, IOException, SAXException {
        this.wantedWeatherAVGMinMax.clear();
        // Creates the URL String to be used according to parameters wanted that include coordinates and start and end time
        // than creates the document used to create the arraylist of WeatherData
        String startTimeString = helperFunctions.timeAndDateToIso8601Format(startTime);
        String endTimeString = helperFunctions.timeAndDateToIso8601Format(endTime);

        String urlstring = weatherAPILogic.createAVGMINMAXurlString(coordinateConstraints,  startTimeString, endTimeString);

        this.wantedWeatherAVGMinMax = weatherAPILogic.creatingAvgMinMax(weatherAPILogic.GetApiDocument(urlstring));

        // If the created arraylist is empty, the API call failed, needs bigger bbox area
        // boolean check to display error messages in WeatherController
        return wantedWeatherAVGMinMax.size() != 0;
    }

    /**
     * WeatherData creation to sessionData
     * @param startTime Date startTime of data creation
     * @param endTime Date endTime of data creation
     */
    public void createWeatherData(Date startTime, Date endTime) throws ParserConfigurationException, IOException, SAXException, ParseException {
        this.wantedWeatherData.clear();
        // Creates the URL String to be used according to parameters wanted that include coordinates and start and end time
        // than creates the document used to create the arraylist of WeatherData
        String startTimeString = helperFunctions.timeAndDateToIso8601Format(startTime);
        String endTimeString = helperFunctions.timeAndDateToIso8601Format(endTime);
        String urlstring = weatherAPILogic.createURLString(coordinateConstraints,  startTimeString, endTimeString);

        // Compares current date to starTime to know if we want to create a weatherforecast or weather
        // observation
        if(startTime.after(dateAndTime)){
            this.wantedWeatherData = weatherAPILogic.creatingWeatherForecast(weatherAPILogic.GetApiDocument(urlstring));
        }
        this.wantedWeatherData = weatherAPILogic.creatingWeatherObservations(weatherAPILogic.GetApiDocument(urlstring));
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
                if(chart_type.equals("TEMPERATURE")){
                    Y = wd.getTemperature();
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

    /**
     * Calculates the average amount of maintenance tasks
     * @return Average amount of maintenance tasks by task type
     */
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
        averageMaintenanceAmount.replaceAll( (k,v) -> Double.valueOf(df.format(v/maintenancesInTimeLine.size()).replace(',','.')));
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
     * Writes data to either a JSON or XML file, based on dataClassType
     * @param fileName      the name of the file to write to
     * @param dataClassType the type of data to write
     */
    public void writeDataToFile(String fileName, DataClassType dataClassType) throws IOException, NullPointerException {

        switch (dataClassType) {
            case WEATHER:
                    savedDataLogic.writeWeatherData(fileName, wantedWeatherData);

            case WEATHERMINMAXAVG:
                for (WeatherDataMinMaxAvg wd : wantedWeatherAVGMinMax) {
                    savedDataLogic.writeWeatherDataMinMaxAvg(fileName, wd);
                }
            case MAINTENANCE:
                for (Maintenance maintenance : this.maintenancesInTimeLine) {
                    savedDataLogic.writeMaintenance(fileName + helperFunctions.dateAsDayString(maintenance.date), maintenance);
                }
            case ROAD:
                savedDataLogic.writeRoadData(fileName + helperFunctions.dateAsDayString(this.roadData.getDataUpdatedTime()), this.roadData);
            case TRAFFIC:
                savedDataLogic.writeTrafficMessage(fileName + helperFunctions.dateAsDayString(this.trafficMessage.getDataUpdatedTime()), this.trafficMessage);

        }
    }

    /**
     * Reads data from either a JSON or XML file, based on dataClassType
     * @param file the name of the file to read from
     * @param dataClassType the type of data to read
     */
    public void readDataFromFile(File file, DataClassType dataClassType) throws URISyntaxException, IOException {
        switch (dataClassType) {
            case WEATHER:
                wantedWeatherData.addAll(savedDataLogic.readWeatherData(file));
            case WEATHERMINMAXAVG:
                wantedWeatherAVGMinMax.add(savedDataLogic.readWeatherDataMinMaxAvg(file));
            case ROAD:
                roadData = savedDataLogic.readRoadData(file);
            case TRAFFIC:
                trafficMessage = savedDataLogic.readTrafficMessage(file);
                roadData.trafficMessageAmount = trafficMessage.messagesInArea(coordinateConstraints);
            case MAINTENANCE:
                maintenancesInTimeLine.add(savedDataLogic.readMaintenance(file));
            }
    }

    /**
     * Saves the preferences to a JSON file
     * @param fileName the name of the file to write to
     */
    public void savePreferencesToJSON(String fileName) throws IOException {
        Map<String,String> preferences = new HashMap<>();
        preferences.put("weatherPreference", weatherPreference);
        preferences.put("conditionPreference", conditionPreference);
        preferences.put("maintenancePreference", maintenancePreference);
        preferences.put("locationPreference", locationPreference);
        savedDataLogic.writePreferences(fileName, preferences);
    }

    /**
     * Reads the preferences from a JSON file
     * @param fileName the name of the file to read from
     */
    public void loadPreferencesFromJSON(String fileName) throws IOException {
        Map<String,String> preferences = savedDataLogic.readPreferences(fileName);
        weatherPreference = preferences.get("weatherPreference");
        conditionPreference = preferences.get("conditionPreference");
        maintenancePreference = preferences.get("maintenancePreference");
        locationPreference = preferences.get("locationPreference");
    }

    /**
     * Returns all dataClassTypes as list To combinepage for saving data
     * @return ArrayList<String> of wanted classtypes
     */
    public ArrayList<String> getDataClassTypesAsList(){
        ArrayList<String> classTypes = new ArrayList<>();
        for (DataClassType value : DataClassType.values()) {
            classTypes.add(value.toString());
        }
        return classTypes;
    }

}
