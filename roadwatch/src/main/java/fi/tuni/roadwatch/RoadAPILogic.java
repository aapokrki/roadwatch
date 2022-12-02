package fi.tuni.roadwatch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.json.JsonMapper;

import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.sothawo.mapjfx.Coordinate;
import javafx.scene.SubScene;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.commons.io.IOUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class RoadAPILogic {

    // Static URI for fetching traffic messages for all locations
    private static URI uriTrafficMessage;

    ObjectMapper RoadAPImapper = new ObjectMapper();

    /**
     * Constructor for RoadAPILogic, used for setting up the mapper and traffic message URI.
     */
    public RoadAPILogic() throws URISyntaxException {
        RoadAPImapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        RoadAPImapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        RoadAPImapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        RoadAPImapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        uriTrafficMessage = new URI("https://tie.digitraffic.fi/api/traffic-message/v1/messages?inactiveHours=0&includeAreaGeometry=false" +
                "&situationType=TRAFFIC_ANNOUNCEMENT" +
                "&situationType=EXEMPTED_TRANSPORT" +
                "&situationType=WEIGHT_RESTRICTION"
                +
                "&situationType=ROAD_WORK&");
    }

    /**
     * Retrieves traffic messages and constructs a TrafficMessage object
     * @return TrafficMessage data object
     */
    public TrafficMessage getTrafficMessages() throws IOException{
        JsonNode trafficMessageNode = retrieveData(uriTrafficMessage);
        return RoadAPImapper.treeToValue(trafficMessageNode, TrafficMessage.class);
    }

    /**
     * Retrieves road conditions and constructs a RoadCondition object
     * @param bbox Bounding box coordinates
     * @return RoadCondition data object
     */
    public RoadData getRoadData(String bbox) throws IOException, URISyntaxException {
        URI uriRoadCondition = new URI("https://tie.digitraffic.fi/api/v3/data/road-conditions/" +
                bbox);

        JsonNode roadConditionNode = retrieveData(uriRoadCondition);
        return RoadAPImapper.treeToValue(roadConditionNode, RoadData.class);
    }

    /**
     * Retrieves road maintenance and constructs a RoadMaintenance object
     * @param taskId Task ID for maintenance reports
     * @param bbox Bounding box coordinates
     * @param endFrom Start date
     * @param endBefore End date
     * @return RoadMaintenance data object
     */
    public Maintenance getMaintenances(String taskId,String bbox, Date endFrom, Date endBefore) throws IOException, URISyntaxException {
        String startString = new SimpleDateFormat("yyyy-MM-dd'T'HH'%3A'mm'%3A'ss'Z'").format(endFrom);
        String endString = new SimpleDateFormat("yyyy-MM-dd'T'HH'%3A'mm'%3A'ss'Z'").format(endBefore);

        URI uriMaintenance = new URI("https://tie.digitraffic.fi/api/maintenance/v1/tracking/routes?" +
                "endFrom=" + startString + "&"+
                "endBefore=" + endString +"&"+
                bbox + "&" +
                "taskId="+ taskId + "&" +
                "&domain=state-roads");

        JsonNode roadMaintenanceNode = retrieveData(uriMaintenance);
        return RoadAPImapper.treeToValue(roadMaintenanceNode, Maintenance.class);
    }

    public ArrayList<String> getTaskTypes() throws URISyntaxException, IOException {
        ArrayList<String> taskTypes = new ArrayList<>();
        URI uriTaskTypes = new URI("https://tie.digitraffic.fi/api/maintenance/v1/tracking/tasks");
        JsonNode taskTypesNode = retrieveData(uriTaskTypes);
        taskTypesNode.forEach(x -> {
            String str = x.get("id").toString();
            taskTypes.add(str.substring(1, str.length()-1));
        });
        return taskTypes;
    };


    /**
     * Retrieves data from the given URI
     * @param uri URI to retrieve data from
     * @return JsonNode containing the data
     */
    public JsonNode retrieveData(URI uri) throws IOException {
        // Creates an HTTP request with the specified URI and required parameters
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(uri);
        httpGet.addHeader("Accept-Encoding","gzip");
        httpGet.addHeader("Digitraffic-User","AMOR-TUNI");

        // Executes the request and retrieves the response
        CloseableHttpResponse httpresponse = httpClient.execute(httpGet);
        InputStream in = httpresponse.getEntity().getContent();

        // Reads the response to a JsonNode
        String body = IOUtils.toString(in, String.valueOf(StandardCharsets.UTF_8));
        return RoadAPImapper.readTree(body);
    }

}
