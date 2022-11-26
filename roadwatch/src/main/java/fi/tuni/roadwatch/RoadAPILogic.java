package fi.tuni.roadwatch;

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
import java.util.ArrayList;
import java.util.Objects;

public class RoadAPILogic {
    String road4SmallSegment= "https://tie.digitraffic.fi/api/v3/data/road-conditions/25.58893688730363/60.886638156574094/25.5977133197657/60.89613951610315";

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


    URI uriTrafficMessage = new URI("https://tie.digitraffic.fi/api/traffic-message/v1/messages?inactiveHours=0&includeAreaGeometry=false&situationType=TRAFFIC_ANNOUNCEMENT&situationType=EXEMPTED_TRANSPORT&situationType=WEIGHT_RESTRICTION&situationType=ROAD_WORK&");

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

        JsonNode roadMaintenanceNode = retrieveData(uriMaintenance);
        JsonNode roadConditionNode = retrieveData(uriRoadCondition);
        Maintenance maintenance = getMaintenance();
    }

    // Retrieves traffic messages and constructs a TrafficMessage object
    public TrafficMessage getTrafficMessages() throws IOException, URISyntaxException {
        System.out.println("TRAFFIC-MESSAGES API-LINK: \n"+uriTrafficMessage.toString());
        JsonNode trafficMessageNode = retrieveData(uriTrafficMessage);
        return RoadAPImapper.treeToValue(trafficMessageNode, TrafficMessage.class);
    }

    // Retrieves road conditions and constructs a RoadCondition object
    public RoadData getRoadData() throws IOException, URISyntaxException {
        System.out.println("ROAD-CONDITION API-LINK: \n"+uriRoadCondition.toString());
        JsonNode roadConditionNode = retrieveData(uriRoadCondition);
        return RoadAPImapper.treeToValue(roadConditionNode, RoadData.class);
    }

    // Retrieves road maintenance and constructs a Maintenance object
    public Maintenance getMaintenance() throws IOException, URISyntaxException {
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
    // TODO: Täytyy miettiä miten yhden tien dataa otetaan useasta nodesta
    // TODO: esim otetaanko keskiarvo kaikista 4.tieltä saaduista säätiedoista
    // TODO: vai annetaanko käyttäjän valita eri tiekohtien väliltä
    // TODO: tutki esimerkiksi: https://tie.digitraffic.fi/api/v3/data/road-conditions/25.58893688730363/60.886638156574094/25.5977133197657/60.89613951610315
    // TODO: Mitä jos valitulla alueella on useita teitä?

    // TODO: Ehdotus: lasketaan valitun alueen isot tiet yhteen ja niiden osien sääkeskiarvot myös.
    // TODO: esim. https://tie.digitraffic.fi/api/v3/data/road-conditions/25.72346721036581/60.98567993555781/25.745287003297207/60.99503275531163
    // TODO: jossa on tiet 24 ja 04 joista molemmista kaksi tiekohtaa, joiden keskiarvot lasketaan
    // TODO: ja tiestä 24 ja 04 tehdään omat RoadDatat

    // TODO: Tai sitte mite on jo tehty eli RoadDatassa arraylist tienosista

    // Tämä esimerkki ei ota kantaa eri teihin, vaan pelkästään annettuun bbox alueeseen
    // Jos halutaan koko tietyn tien säätiedot niin täytyy käyttää
    // esim: https://tie.digitraffic.fi/api/v3/metadata/forecast-sections/4
    // Ylhäällä Nelostien tiedatat kokonaisuudessaan, Saa myös koordinaatit esim kartalle maalaamista varten.
    // Täytyy varmaan ottaa joka kymmenes tielokaatio, liian raskasta muuten
    private void roadConditionRec(JsonNode node, RoadData roadData){

        if(!node.get("weatherData").isNull()){
            JsonNode weatherDataList = node.get("weatherData");

            for(JsonNode roadLocations : weatherDataList){

                roadConditionRec(roadLocations, roadData);
            }
        }
        if(!node.get("roadConditions").isNull()){
            JsonNode roadConditions = node.get("roadConditions");

            for (JsonNode roadCondition : roadConditions){
                roadConditionRec(roadCondition,roadData);
            }
        }

        if(!node.get("type").isNull()){

        }
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
