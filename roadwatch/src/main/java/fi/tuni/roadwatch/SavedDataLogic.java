package fi.tuni.roadwatch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.File;
import java.io.IOException;

public class SavedDataLogic {

    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private static final XmlMapper xmlMapper = new XmlMapper();


    /**
    * Constructor for SavedDataLogic, used for setting up the mappers.
     */
    public SavedDataLogic() {
        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        jsonMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        jsonMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        jsonMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        xmlMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }


    /**
     * @param fileName name of the file to be written to
     * @param weatherData WeatherData object
     */
    public void writeWeatherData(String fileName, WeatherData weatherData) throws IOException {
        xmlMapper.writeValue(new File(fileName + ".xml"), weatherData);
    }

    /**
     * @param fileName name of the file to be read from
     * @param weatherDataMinMaxAvg WeatherDataMinMaxAvg object
     */
    public void writeWeatherDataMinMaxAvg(String fileName, WeatherDataMinMaxAvg weatherDataMinMaxAvg) throws IOException {
        xmlMapper.writeValue(new File(fileName + ".xml"), weatherDataMinMaxAvg);
    }

    /**
     * @param fileName name of the file to be written to
     * @param maintenance Maintenance object
     */
    // TODO: Check writing of multiple days
    public void writeMaintenance(String fileName, Maintenance maintenance) throws IOException {
        jsonMapper.writeValue(new File(fileName + ".json"), maintenance);
    }

    /**
     * @param fileName name of the file to be written to
     * @param roadData RoadData object
     */
    public void writeRoadData(String fileName, RoadData roadData) throws IOException {
        jsonMapper.writeValue(new File(fileName + ".json"), roadData);
    }

    /**
     * @param fileName       name of the file to be written to
     * @param trafficMessage TrafficMessage object
     */
    public void writeTrafficMessage(String fileName, TrafficMessage trafficMessage) throws IOException {
        jsonMapper.writeValue(new File(fileName + ".json"), trafficMessage);
    }

    /**
     * @param fileName name of the file to be read from
     * @return WeatherData object
     */
    public WeatherData readWeatherData(String fileName) throws IOException {
        return xmlMapper.readValue(new File(fileName + ".json"), WeatherData.class);
    }

    /**
     * @param fileName name of the file to be read from
     * @return WeatherDataMinMaxAvg object
     */
    public WeatherDataMinMaxAvg readWeatherDataMinMaxAvg(String fileName) throws IOException {
        return xmlMapper.readValue(new File(fileName + ".json"), WeatherDataMinMaxAvg.class);
    }

    /**
     * @param fileName name of the file to be read from
     * @return Maintenance object
     */
    public RoadData readRoadData(String fileName) throws IOException {
        return jsonMapper.readValue(new File(fileName + ".json"), RoadData.class);
    }

    /**
     * @param fileName name of the file to be read from
     * @return TrafficMessage object
     */
    public TrafficMessage readTrafficMessage(String fileName) throws IOException {
        return jsonMapper.readValue(new File(fileName + ".json"), TrafficMessage.class);
    }

    /**
     * @param fileName name of the file to be read from
     * @return Maintenance object
     */
    // TODO: Check reading of multiple days
    public Maintenance readMaintenance(String fileName) throws IOException {
        return jsonMapper.readValue(new File(fileName + ".json"), Maintenance.class);
    }
}
