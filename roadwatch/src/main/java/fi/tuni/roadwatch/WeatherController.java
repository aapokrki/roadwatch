package fi.tuni.roadwatch;

import javafx.fxml.FXML;

import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;


public class WeatherController {

    enum Datatype {
        TEMPERATURE,
        WIND,
        VISIBILITY
    }
    private final Integer timeline = 0;
    private Datatype datatype;
    private SessionData sessionData;
    private LocalDateTime currentDate = LocalDateTime.now();

    @FXML
    private ComboBox<String> comboBox;
    @FXML
    private Label datatypeLabel;

    // Temperature components
    private TreeMap<Date, Double> temperatureTimeMap = new TreeMap<Date, Double>();
    @FXML
    private AnchorPane temperaturePane;
    @FXML
    private  Label cityLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label nowLabel;
    @FXML
    private Label tomorrowLabel;
    @FXML
    private Label dATomorrowLabel;
    @FXML
    private Button avgTempButton;
    @FXML
    private Button minMaxTempButton;


    // ONNIN TESTI LABELIT TEE NÄISTÄ HIENOI RONJA
    private Date savedDate;

    @FXML
    private Label maxlabel;
    @FXML
    private Label minlabel;
    @FXML
    private Label avglabel;

    @FXML
    private DatePicker chooseDay;

    @FXML
    private Label tempRightNowLabel;
    @FXML
    private Label tempMaxLabel;
    @FXML
    private Label tempMinLabel;



    // Wind components
    @FXML
    private AnchorPane windPane;
    @FXML
    private Label windLabel;
    @FXML
    private LineChart windChart;
    @FXML
    private CategoryAxis xAxisWind;
    @FXML
    private NumberAxis yAxisWind;

    // Visibility components
    @FXML
    private AnchorPane visibilityPane;
    @FXML
    private Label  visibilityLabel ;
    @FXML
    private LineChart<String, Double> visibilityChart;

    public void setSessionData(SessionData sessionData) {
        this.sessionData = sessionData;
        datatype = Datatype.TEMPERATURE;
        // TODO: enemmän enum tyyppien hyödynnystä?
        visibilityPane.setVisible(false);
        windPane.setVisible(false);
    }

    @FXML
    private void changeDatatype() {
        if(comboBox.getValue().equalsIgnoreCase(Datatype.TEMPERATURE.toString())) {
            datatype = Datatype.TEMPERATURE;
            datatypeLabel.setText(datatype.toString());
            setTemperature();
            visibilityPane.setVisible(false);
            windPane.setVisible(false);
        }
        else if(comboBox.getValue().equalsIgnoreCase(Datatype.WIND.toString())) {
            datatype = Datatype.WIND;
            datatypeLabel.setText(datatype.toString());
            temperaturePane.setVisible(false);
            visibilityPane.setVisible(true);
            windPane.setVisible(false);
        } else {
            datatype = Datatype.VISIBILITY;
            datatypeLabel.setText(datatype.toString());
            temperaturePane.setVisible(false);
            visibilityPane.setVisible(false);
            windPane.setVisible(true);
        }
    }

    // Changes date String in to string 8601Format to use in urlstring
    public Date timeAndDateAsDate(String datestring) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(datestring);
    }

    // DONT DELETE YET HAJOTTAA SOFTAN ENNE KUN POISTAA FXML ton napin
    //Test to check if apiread works and gets data to weather controller
    @FXML
    private void calculateData() throws ParserConfigurationException, IOException, ParseException, SAXException {
        // Gets the date right now and adds a few seconds to get forecast from API
        // Also getting the date and the end of day
        Calendar cal = Calendar.getInstance();
        long timeInSecs = cal.getTimeInMillis();
        Date startTime = new Date(timeInSecs + (10*60*10));
        Date endTime = timeAndDateAsDate(LocalDate.now().atTime(23, 59, 59) + "Z");

        // Creates weather data according to new start and end time to sessionData
        sessionData.createWeatherData(startTime, endTime);

        if(comboBox.getValue().equals("WIND")){
            xAxisWind.setLabel("Time");
            yAxisWind.setLabel("km/h");
            windChart.getData().add(sessionData.createGraphSeriesWind());


            System.out.println(sessionData.WantedWeatherData.get(0).getWind());
            System.out.println(sessionData.WantedWeatherData.get(1).getWind());
            System.out.println(sessionData.WantedWeatherData.get(1).getDate());
        }

        System.out.println("Testi toinen testi");

    }

    @FXML
    private void saveDate() throws ParseException {
        LocalDate localDate = chooseDay.getValue();
        Instant instant = Instant.from(localDate.atStartOfDay(ZoneOffset.UTC));
        savedDate = Date.from(instant);

    }

    // Temperature actions
    @FXML
    private void setTemperature() {
        temperaturePane.setVisible(true);
        dateLabel.setText(currentDate.getDayOfMonth() + "." + currentDate.getMonthValue() + "." + currentDate.getYear());
    }

    // Changes temperature labels according to which day you want to see
    private void changeTempLabels(String nowOrNot){
        double min = sessionData.WantedWeatherData.get(0).getTemperature();
        double max = sessionData.WantedWeatherData.get(0).getTemperature();
        tempRightNowLabel.setVisible(false);

        //Sets min max and now labels according to newest weather information
        for(WeatherData wd : sessionData.WantedWeatherData){
            if(wd.getTemperature() <= min){
                min = wd.getTemperature();
            }
            if(wd.getTemperature() >= max){
                max = wd.getTemperature();
            }
            if(Objects.equals(wd.getDate(), sessionData.getClosestDate())){
                if(nowOrNot.equals("now")){
                    tempRightNowLabel.setVisible(true);
                    tempRightNowLabel.setText(String.valueOf(wd.getTemperature()));
                }

            }
        }
        // TEST LABELS
        // RONJA SITKU TEET NE SUN LABELIT NII VAIHA VAA NE TÄHÄN NIIN NE TOIMII
        // TEIN NYT MYÖS LABELIN JOKA NÄYTTÄÄ SEN PÄIVÄN MIN JA MAX LÄMPÖTILAN LISÄKSI MYÖS LÄMPÖTILAN JUST NYT
        // tai no 10 minuttii tulevaisuudessa. Laitoin sen tohon ylös if lauseesee niin näkyy vain now tabis
        tempMinLabel.setText(String.valueOf(min));
        tempMaxLabel.setText(String.valueOf(max));
    }

    // Helper function to set the time of day to 00:00:00, also can add days to date
    private Date trimToStart(Date date, int Days){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, Days);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);

        return cal.getTime();
    }

    // Helper function to set the time of day to 23:59:59, also can add days to date
    private Date trimToEnd(Date date, int Days){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, Days);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE,59);
        cal.set(Calendar.SECOND,59);

        return cal.getTime();
    }

    @FXML
    private void onNowClick() throws ParseException, ParserConfigurationException, IOException, SAXException {
        changeTimeColors(nowLabel, tomorrowLabel, dATomorrowLabel);

        // Gets the date right now and adds a few seconds to get forecast from API
        // Also getting the date and the end of day
        Calendar cal = Calendar.getInstance();
        long timeInSecs = cal.getTimeInMillis();
        Date startTime = new Date(timeInSecs + (10*60*10));
        Date endTime = timeAndDateAsDate(LocalDate.now().atTime(23, 59, 59) + "Z");

        // Creates weather data according to new start and end time to sessionData
        sessionData.createWeatherData(startTime, endTime);

        changeTempLabels("now");
    }

    @FXML
    private void onTomorrowClick() throws ParseException, ParserConfigurationException, IOException, SAXException {
        changeTimeColors(tomorrowLabel, nowLabel, dATomorrowLabel);

        // Sets the date to the next day to hours 00 - 24
        Date now = Calendar.getInstance().getTime();
        Date startTime = trimToStart(now,1);
        Date endTime = trimToEnd(now, 1);

        // Creates weather data according to new start and end time to sessionData
        sessionData.createWeatherData(startTime, endTime);
        changeTempLabels("not");

    }

    @FXML
    private void onDATomorrowClick() throws ParserConfigurationException, IOException, ParseException, SAXException {
        changeTimeColors(dATomorrowLabel, nowLabel, tomorrowLabel);

        // Sets the date to the next day to hours 00 - 24
        Date now = Calendar.getInstance().getTime();
        Date startTime = trimToStart(now,2);
        Date endTime = trimToEnd(now, 2);

        // Creates weather data according to new start and end time
        sessionData.createWeatherData(startTime, endTime);
        changeTempLabels("not");
    }

    @FXML
    private void changeTimeColors(Label selected, Label l2, Label l3) {
        selected.getStyleClass().add("basicHeadingGreen");
        l2.getStyleClass().removeAll("basicHeadingGreen");
        l2.getStyleClass().add("basicHeading");
        l3.getStyleClass().removeAll("basicHeadingGreen");
        l3.getStyleClass().add("basicHeading");
    }


    // Counts the average temperature of a certain day in certain month and year
    // at certain location
    @FXML
    private void onAvgBtnClick() throws ParseException, ParserConfigurationException, IOException, SAXException {
        Date startTime = savedDate;
        Date endTime = trimToEnd(savedDate,0);

        sessionData.createAvgMinMax(startTime, endTime);

        avglabel.setText(sessionData.getAVG_value());
    }

    // Counts the min and max temperature of a certain day in certain month and year
    // at certain location
    @FXML
    private void onMinMaxBtnClick() throws ParseException, ParserConfigurationException, IOException, SAXException {
        Date startTime = savedDate;
        Date endTime = trimToEnd(savedDate,0);

        sessionData.createAvgMinMax(startTime, endTime);

        minlabel.setText(String.valueOf(sessionData.getMIN_value()));
        maxlabel.setText(String.valueOf(sessionData.getMAX_value()));
    }
}
