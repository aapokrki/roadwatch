package fi.tuni.roadwatch;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.json.JsonMapper;

import com.fasterxml.jackson.databind.ser.std.StdSerializer;
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

public class RoadAPILogic {

    URI uri = new URI("https://tie.digitraffic.fi/api/traffic-message/v1/messages?inactiveHours=0&includeAreaGeometry=false&situationType=TRAFFIC_ANNOUNCEMENT");

    ObjectMapper roadMapper = new JsonMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    // Retrieves a specified dataset from the API
    public RoadData retrieveData(URI uri) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(uri);
        httpGet.addHeader("Accept-Encoding","gzip");
        httpGet.addHeader("Digitraffic-User","AMOR-TUNI");

        CloseableHttpResponse httpresponse = httpClient.execute(httpGet);

        InputStream in = httpresponse.getEntity().getContent();

        String body = IOUtils.toString(in, String.valueOf(StandardCharsets.UTF_8));

        whenWriteStringUsingBufferedWritter_thenCorrect(body);

        return stringToRoadData(body);
    }

    // Testing Json content
    public void whenWriteStringUsingBufferedWritter_thenCorrect(String s)
            throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("Test.json"));
        writer.write(s);

        writer.close();
    }




    private RoadData stringToRoadData(String body) {
        return new RoadData();
    }

    public RoadAPILogic() throws URISyntaxException, IOException {


        var test_data = retrieveData(uri);
        System.out.print("test_data");

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
