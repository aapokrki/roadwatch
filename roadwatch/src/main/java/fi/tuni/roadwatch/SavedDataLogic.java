package fi.tuni.roadwatch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SavedDataLogic {

    private static final ObjectMapper fileMapper = new ObjectMapper();

    public SavedDataLogic() {
        fileMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        fileMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        fileMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        fileMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }


    /**
     * @param fileName name of the file to be written to
     * @param weatherData WeatherData object
     */
    public void writeWeatherData(String fileName, WeatherData weatherData) throws IOException {
        fileMapper.writeValue(new File(fileName + ".json"), weatherData);
    }

    /**
     * @param fileName name of the file to be read from
     * @param weatherDataMinMaxAvg WeatherDataMinMaxAvg object
     */
    public void writeWeatherDataMinMaxAvg(String fileName, WeatherDataMinMaxAvg weatherDataMinMaxAvg) throws IOException {
        fileMapper.writeValue(new File(fileName + ".json"), weatherDataMinMaxAvg);
    }

    /**
     * @param fileName name of the file to be written to
     * @param maintenance Maintenance object
     */
    public void writeMaintenance(String fileName, Maintenance maintenance) throws IOException {
        fileMapper.writeValue(new File(fileName + ".json"), maintenance);
    }

    /**
     * @param fileName name of the file to be written to
     * @param roadData RoadData object
     */
    public void writeRoadData(String fileName, RoadData roadData) throws IOException {
        fileMapper.writeValue(new File(fileName + ".json"), roadData);
    }

    /**
     * @param fileName name of the file to be written to
     * @param trafficMessage TrafficMessage object
     */
    public void writeTrafficMessage(String fileName, TrafficMessage trafficMessage) throws IOException {
        fileMapper.writeValue(new File(fileName + ".json"), trafficMessage);
    }

    // TODO: Enable reading of multiple days
    public Maintenance readMaintenance(String fileName) throws IOException {
        return fileMapper.readValue(new File(fileName + ".json"), Maintenance.class);
    }
}
