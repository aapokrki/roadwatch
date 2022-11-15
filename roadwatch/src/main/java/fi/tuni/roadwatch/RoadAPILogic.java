package fi.tuni.roadwatch;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.json.JsonMapper;

import com.fasterxml.jackson.databind.ser.std.StdSerializer;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
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


    URI uriTrafficMessage = new URI("https://tie.digitraffic.fi/api/traffic-message/v1/messages?inactiveHours=0&includeAreaGeometry=false&" +
            "situationType=" + situationType + "&");

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

    //ObjectMapper roadMapper = new JsonMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public RoadAPILogic() throws URISyntaxException, IOException {
        System.out.println("MAINTENANCE API-LINK: \n"+uriMaintenance.toString());
        System.out.println("ROAD-CONDITION API-LINK: \n"+uriRoadCondition.toString());
        System.out.println("TRAFFIC-MESSAGES API-LINK: \n"+uriTrafficMessage.toString());

        JsonNode roadMaintenanceNode = retrieveData(uriMaintenance);
        JsonNode roadConditionNode = retrieveData(uriRoadCondition);
        JsonNode trafficMessagesNode = retrieveData(uriTrafficMessage);

        // TODO:Required recursive functions must be done to extract needed data for RoadData

        // Construct RoadData
        RoadData roadData = new RoadData();
        // Fill roadData with data from road-condition API for now
        roadConditionRec(roadConditionNode, roadData);

        // Example recursive model for getting data of all traffic messages
        // with the given situation type in the url.
        // Currently only prints the result, since a solid solution has not been done
        // TODO: create solid way to save traffic messages or just their amount.
        // TODO: To SessionData for example
        //roadAPIAlertRec(trafficMessagesNode);

    }

    // Retrieves a specified dataset from the API
    public JsonNode retrieveData(URI uri) throws IOException, URISyntaxException {


        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(uri);
        httpGet.addHeader("Accept-Encoding","gzip");
        httpGet.addHeader("Digitraffic-User","AMOR-TUNI");

        CloseableHttpResponse httpresponse = httpClient.execute(httpGet);

        InputStream in = httpresponse.getEntity().getContent();

        String body = IOUtils.toString(in, String.valueOf(StandardCharsets.UTF_8));

        //whenWriteStringUsingBufferedWritter_thenCorrect(body);
        JsonNode dataNode = new ObjectMapper().readTree(body);

        return dataNode;
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

    private void roadAPIAlertRec(JsonNode dataNode){

        //System.out.println("hep");
        if(dataNode.get("type").asText().equals("FeatureCollection")){

            JsonNode features = dataNode.get("features");
            for(JsonNode feature : features){
                //System.out.println(feature);
                roadAPIAlertRec(feature);
                System.out.println();
            }
        }

        if(dataNode.get("type").asText().equals("Feature")){
            JsonNode properties = dataNode.get("properties");
            JsonNode announcements = properties.get("announcements");
            for(JsonNode announcement : announcements){

                // Title of the announcement, usually contains location and situation
                JsonNode title = announcement.get("title");

                // More accurate information about the location
                JsonNode location = announcement.get("location").get("description");

                // List of the situations eg. "Tie suljettu". Can be empty
                JsonNode situations = announcement.get("features");

                // Test print with the necessary info
                if(!title.isNull()){
                    System.out.println(title.asText());
                }
                if(!location.isNull()){
                    System.out.println(" - " + location.asText());
                }
                if(!situations.isNull() && !situations.isEmpty()){
                    JsonNode situation = situations.get(0).get("name");
                    System.out.println(" -- " + situation.asText());
                }
            }


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
    // Testing Json content
    public void whenWriteStringUsingBufferedWritter_thenCorrect(String s) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("Test.json"));
        writer.write(s);

        writer.close();
    }


}
