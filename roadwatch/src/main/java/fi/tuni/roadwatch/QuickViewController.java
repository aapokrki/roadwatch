package fi.tuni.roadwatch;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class QuickViewController {
    private SessionData sessionData;
    private BorderPane mapPane;
    private BorderPane infoPane;
    private Label siteLabel;

    // Temperature components.
    @FXML
    private Label dateLabel;
    @FXML
    private Label nowLabel;
    @FXML
    private Label tempRightNowLabel;
    @FXML
    private Label tempMaxLabel;
    @FXML
    private Label tempMinLabel;
    @FXML
    private Label tempErrorLabel;
    //Others
    @FXML
    public Pane combine;
    // TODO: Tiedot päivittyy tilanteen mukaan.
    @FXML
    private Label conditionLabel;
    @FXML
    private Label maintenanceLabel;
    @FXML
    private Label alertsLabel;

    public CombineController combineController;

    public void setSessionData(SessionData sessionData) {
        this.sessionData = sessionData;
    }

    public void setData(BorderPane mapPane, BorderPane infoPane, Label siteLabel) {
        this.mapPane = mapPane;
        this.infoPane = infoPane;
        this.siteLabel = siteLabel;
        dateLabel.setText(LocalDateTime.now().getDayOfMonth() + "." + LocalDateTime.now().getMonthValue() + "." +
                LocalDateTime.now().getYear());
    }

    public boolean coordinateCheck(){
        if(sessionData.coordinateConstraints == null){
            return false;
        }
        return true;
    }

    public void changeTempLabels(Boolean now){
        double min = sessionData.wantedWeatherData.get(0).getTemperature();
        double max = sessionData.wantedWeatherData.get(0).getTemperature();
        tempRightNowLabel.setVisible(false);

        //Sets min max and now labels according to newest weather information
        for(WeatherData wd : sessionData.wantedWeatherData){
            if(wd.getTemperature() <= min){
                min = wd.getTemperature();
            }
            if(wd.getTemperature() >= max){
                max = wd.getTemperature();
            }
            if(Objects.equals(wd.getDate(), sessionData.helperFunctions.getClosestDate())){
                if(now){
                    tempRightNowLabel.setVisible(true);
                    tempRightNowLabel.setText(wd.getTemperature() + "°");
                }
            }
        }
        tempMinLabel.setText(min + "°");
        tempMaxLabel.setText(max + "°");
    }

    @FXML
    private void onNowClick() throws ParseException, ParserConfigurationException, IOException, SAXException {
        //changeTimeColors(todayLabel, tomorrowLabel, dATomorrowLabel);
        nowLabel.setVisible(true);
        if(!coordinateCheck()){
            tempErrorLabel.setText("Choose coordinates, remember to add on map!");
        }
        else{
            // Gets the date right now and adds a few seconds to get forecast from API
            // Also getting the date and the end of day
            tempErrorLabel.setText("");
            Calendar cal = Calendar.getInstance();
            long timeInSecs = cal.getTimeInMillis();
            Date startTime = new Date(timeInSecs + (10*60*10));
            Date endTime = sessionData.helperFunctions.timeAndDateAsDate(LocalDate.now().atTime(23, 59, 59) + "Z");

            // Creates weather data according to new start and end time to sessionData
            sessionData.createWeatherData(startTime, endTime);
            changeTempLabels(true);
        }
    }

    @FXML
    private void onTomorrowClick() throws ParseException, ParserConfigurationException, IOException, SAXException {
        //changeTimeColors(tomorrowLabel, todayLabel, dATomorrowLabel);
        nowLabel.setVisible(false);
        if(!coordinateCheck()){
            tempErrorLabel.setText("Choose coordinates, remember to add on map!");
        }
        else {
            tempErrorLabel.setText("");
            // Sets the date to the next day to hours 00 - 24
            Date now = Calendar.getInstance().getTime();
            Date startTime = sessionData.helperFunctions.trimToStart(now, 1);
            Date endTime = sessionData.helperFunctions.trimToEnd(now, 1);

            // Creates weather data according to new start and end time to sessionData
            sessionData.createWeatherData(startTime, endTime);
            changeTempLabels(false);
        }
    }

    @FXML
    private void onDATomorrowClick() throws ParserConfigurationException, IOException, ParseException, SAXException {
        //changeTimeColors(dATomorrowLabel, todayLabel, tomorrowLabel);
        nowLabel.setVisible(false);
        if(!coordinateCheck()){
            tempErrorLabel.setText("Choose coordinates, remember to add on map!");
        }
        else {
            tempErrorLabel.setText("");
            // Sets the date to the next day to hours 00 - 24
            Date now = Calendar.getInstance().getTime();
            Date startTime = sessionData.helperFunctions.trimToStart(now, 2);
            Date endTime = sessionData.helperFunctions.trimToEnd(now, 2);

            // Creates weather data according to new start and end time
            sessionData.createWeatherData(startTime, endTime);
            changeTempLabels(false);
        }
    }

    public void loadCombine(ActionEvent event) throws IOException {
        if(combineController == null){
            FXMLLoader combineFxmlLoader = new FXMLLoader();
            Parent rootNode = null;
            try {
                rootNode = combineFxmlLoader.load(getClass().getResourceAsStream("fxml/combine.fxml"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            combineController = combineFxmlLoader.getController();
            combineController.setSessionData(sessionData);
            combine = (Pane) rootNode;
        }

        mapPane.setVisible(false);
        GridPane.setConstraints(infoPane,0,3);
        GridPane.setColumnSpan(infoPane,3);

        siteLabel.setText("COMBINE");
        infoPane.setCenter(combine);

    }
}
