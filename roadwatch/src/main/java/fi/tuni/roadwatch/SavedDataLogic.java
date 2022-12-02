package fi.tuni.roadwatch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Class for handling file operations
 */
public class SavedDataLogic {

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    /**
    * Constructor for SavedDataLogic, used for setting up jsonMapper
     */
    public SavedDataLogic() {
        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        jsonMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        jsonMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        jsonMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }


    /**
     * Writes WeatherData to a file with the given name
     * @param fileName name of the file to be written to
     * @param weatherData WeatherData object
     */
    public void writeWeatherData(String fileName, ArrayList<WeatherData> weatherData) throws IOException {
        jsonMapper.writeValue(new File(fileName), weatherData);
    }

    /**
     * Writes WeatherDataMinMaxAvg to a file with the given name
     * @param fileName name of the file to be read from
     * @param weatherDataMinMaxAvg WeatherDataMinMaxAvg object
     */
    public void writeWeatherDataMinMaxAvg(String fileName, WeatherDataMinMaxAvg weatherDataMinMaxAvg) throws IOException {
        jsonMapper.writeValue(new File(fileName), weatherDataMinMaxAvg);
    }

    /**
     * Writes MaintenanceData to a file with the given name
     * @param fileName name of the file to be written to
     * @param maintenance Maintenance object
     */
    public void writeMaintenance(String fileName, Maintenance maintenance) throws IOException {
        jsonMapper.writeValue(new File(fileName + ".json"), maintenance);
    }

    /**
     * Writes RoadData to a file with the given name
     * @param fileName name of the file to be written to
     * @param roadData RoadData object
     */
    public void writeRoadData(String fileName, RoadData roadData) throws IOException {
        jsonMapper.writeValue(new File(fileName + ".json"), roadData);
    }

    /**
     * Writes TrafficMessages to a file with the given name
     * @param fileName       name of the file to be written to
     * @param trafficMessage TrafficMessage object
     */
    public void writeTrafficMessage(String fileName, TrafficMessage trafficMessage) throws IOException {
        jsonMapper.writeValue(new File(fileName + ".json"), trafficMessage);
    }

    /**
     * Reads WeatherData from a file with the given name
     * @param file name of the file to be read from
     * @return WeatherData object     * @return WeatherData object
     */
    public ArrayList<WeatherData> readWeatherData(File file) throws IOException {
        try{
            return jsonMapper.readValue(file, jsonMapper.getTypeFactory().constructCollectionType(ArrayList.class, WeatherData.class));
        } catch (IOException e) {
            e.printStackTrace();;
            return null;
        }
    }

    /**
     * Reads WeatherDataMinMaxAvg from a file with the given name
     * @param file name of the file to be read from
     * @return WeatherDataMinMaxAvg object, null if file read fails
     */
    public WeatherDataMinMaxAvg readWeatherDataMinMaxAvg(File file) throws IOException {

        try{
            return jsonMapper.readValue(file, WeatherDataMinMaxAvg.class);
        } catch (IOException e) {
            e.printStackTrace();;
            return null;
        }
    }

    /**
     * Reads RoadData from a file with the given name
     * @param file name of the file to be read from
     * @return Maintenance object, null if file read fails
     */
    public RoadData readRoadData(File file) throws IOException {
        try{
            return jsonMapper.readValue(file, RoadData.class);
        } catch (IOException e) {
            e.printStackTrace();;
            return null;
        }
    }

    /**
     * Reads TrafficMessages from a file with the given name
     * @param file name of the file to be read from
     * @return TrafficMessage object, null if file read fails
     */
    public TrafficMessage readTrafficMessage(File file) throws IOException {
        try{
            return jsonMapper.readValue(file, TrafficMessage.class);
        } catch (IOException e) {
            e.printStackTrace();;
            return null;
        }
    }

    /**
     * Reads MaintenanceData from a file with the given name
     * @param file name of the file to be read from
     * @return Maintenance object, null if file read fails
     */
    public Maintenance readMaintenance(File file) throws IOException {
        return jsonMapper.readValue(file, Maintenance.class);
    }

    /**
     * Writes preferences to json when closing the program
     * @param fileName name of the file to be read from
     */
    public void writePreferences(String fileName, Map<String,String> preferences) throws IOException {
        jsonMapper.writeValue(new File(fileName), preferences);
    }

    /**
     * Reads preferences from json when opening the program
     * @param fileName name of the file to be read from
     */
    public Map<String,String> readPreferences(String fileName) throws IOException {
        Preferences preferences = jsonMapper.readValue(new File(fileName), Preferences.class);
        return preferences.getPreferencesAsMap();
    }

    /**
     * Data class for reading/writing of preferences to/from json
     */
    public static class Preferences {
        private String weatherPreference;
        private String conditionPreference;
        private String maintenancePreference;
        private String locationPreference;

        /**
         * Getter for weatherPreference
         * @return weatherPreference as String
         */
        @JsonProperty("weatherPreference")
        public String getWeatherPreference() {
            return weatherPreference;
        }
        /**
         * Setter for weatherPreference
         * @param weatherPreference weatherPreference as String
         */
        public void setWeatherPreference(String weatherPreference) {
            this.weatherPreference = weatherPreference;
        }

        /**
         * Getter for conditionPreference
         * @return conditionPreference as String
         */
        @JsonProperty("conditionPreference")
        public String getConditionPreference() {
            return conditionPreference;
        }
        /**
         * Setter for conditionPreference
         * @param conditionPreference conditionPreference as String
         */
        public void setConditionPreference(String conditionPreference) {
            this.conditionPreference = conditionPreference;
        }

        /**
         * Getter for maintenancePreference
         * @return maintenancePreference as String
         */
        @JsonProperty("maintenancePreference")
        public String getMaintenancePreference() {
            return maintenancePreference;
        }
        /**
         * Setter for maintenancePreference
         * @param maintenancePreference maintenancePreference as String
         */
        public void setMaintenancePreference(String maintenancePreference) {
            this.maintenancePreference = maintenancePreference;
        }

        /**
         * Getter for locationPreference
         * @return locationPreference as String
         */
        @JsonProperty("locationPreference")
        public String getLocationPreference() {
            return locationPreference;
        }
        /**
         * Setter for locationPreference
         * @param locationPreference locationPreference as String
         */
        public void setLocationPreference(String locationPreference) {
            this.locationPreference = locationPreference;
        }

        /**
         * Getter for the preferences as a Map
         * @return Map of preferences
         */
        public Map<String, String> getPreferencesAsMap() {
            return Map.of("weatherPreference", weatherPreference,
                    "conditionPreference", conditionPreference,
                    "maintenancePreference", maintenancePreference,
                        "locationPreference", locationPreference);
        }
    }
}
