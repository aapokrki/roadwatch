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

    // !!!IN REAL PROGRAM, SET THESE WHEN CONSTRUCTING, BASED ON USER INPUT!!!

    // Situation type for traffic messages (can be chained)
    String situationType = "TRAFFIC_ANNOUNCEMENT";

    // Values for RoadMaintenance calls
    String endFrom = "2022-11-14T00%3A00%3A00Z&";
    String endBefore = "2022-11-14T12%3A00%3A00Z&";
    // bbox coordinates fork for road conditions and maintenance reports
    String xMin = "21";
    String yMin = "61";
    String xMax = "22";
    String yMax = "62";


    URI uriTrafficMessage = new URI("https://tie.digitraffic.fi/api/traffic-message/v1/messages?inactiveHours=0&includeAreaGeometry=false" +
            "&situationType=TRAFFIC_ANNOUNCEMENT" +
            "&situationType=EXEMPTED_TRANSPORT" +
            "&situationType=WEIGHT_RESTRICTION"
            +
            "&situationType=ROAD_WORK&"
    );

    URI uriRoadCondition = new URI("https://tie.digitraffic.fi/api/v3/data/road-conditions/" +
            xMin + "/" + yMin + "/" + xMax + "/" + yMax);

    URI uriMaintenance = new URI("https://tie.digitraffic.fi/api/maintenance/v1/tracking/routes?" +
            "endFrom=" + endFrom + "&"+
            "endBefore=" + endBefore +"&"+
            "xMin=" + xMin + "&"+
            "yMin=" + yMin + "&"+
            "xMax=" + xMax + "&"+
            "yMax=" + yMax + "&"+
            "taskId=&domain=state-roads");

    ObjectMapper RoadAPImapper = new ObjectMapper();

    public RoadAPILogic() throws URISyntaxException, IOException {
        RoadAPImapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        RoadAPImapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        RoadAPImapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        RoadAPImapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

    }

    // Retrieves traffic messages and constructs a TrafficMessage object
    public TrafficMessage getTrafficMessages() throws IOException, URISyntaxException {
        System.out.println("TRAFFIC-MESSAGES API-LINK: \n"+uriTrafficMessage.toString());
        JsonNode trafficMessageNode = retrieveData(uriTrafficMessage);
        return RoadAPImapper.treeToValue(trafficMessageNode, TrafficMessage.class);
    }

    // Retrieves road conditions and constructs a RoadCondition object
    public RoadData getRoadData(String bbox) throws IOException, URISyntaxException {
        URI uriRoadCondition = new URI("https://tie.digitraffic.fi/api/v3/data/road-conditions/" +
                bbox);
        System.out.println("ROAD-CONDITION API-LINK: \n"+uriRoadCondition.toString());

        JsonNode roadConditionNode = retrieveData(uriRoadCondition);
        return RoadAPImapper.treeToValue(roadConditionNode, RoadData.class);
    }

    // Retrieves road maintenance and constructs a Maintenance object
    public Maintenance getMaintenances(String taskId,String bbox, Date endFrom, Date endBefore) throws IOException, URISyntaxException {
        String startString = new SimpleDateFormat("yyyy-MM-dd'T'HH'%3A'mm'%3A'ss'Z'").format(endFrom);
        String endString = new SimpleDateFormat("yyyy-MM-dd'T'HH'%3A'mm'%3A'ss'Z'").format(endBefore);

        URI uriMaintenance = new URI("https://tie.digitraffic.fi/api/maintenance/v1/tracking/routes?" +
                "endFrom=" + startString + "&"+
                "endBefore=" + endString +"&"+
                bbox + "&" +
                "taskId="+ taskId + "&" +
                "&domain=state-roads");

        System.out.println("MAINTENANCE API-LINK: \n"+uriMaintenance.toString());
        JsonNode roadMaintenanceNode = retrieveData(uriMaintenance);
        return RoadAPImapper.treeToValue(roadMaintenanceNode, Maintenance.class);
    }


    // Retrieves a specified dataset from the API
    public JsonNode retrieveData(URI uri) throws IOException, URISyntaxException {
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


    public static void main(String[] args) {
        try {
            RoadAPILogic test = new RoadAPILogic();
            test.run(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run (String[] args) throws Exception {
        System.out.print("run");
    }


}
