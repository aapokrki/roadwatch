package fi.tuni.roadwatch;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class WeatherAPILogic {
    ObjectMapper weather_mapper = new XmlMapper();

    String urlstring = "https://opendata.fmi.fi/wfs?request=getFeature&version=2.0.0&storedquery_id=fmi::observations::weather::simple&bbox=23,61,24,62&timestep=30&parameters=t2m,ws_10min,n_man";
    URL url = new URL(urlstring);



   WeatherData testdata = weather_mapper.readValue(url, WeatherData.class);

   WeatherData wd = new WeatherData(2.0, 2.9, 3.2);

    double tempereature = wd.getTemperature();
    public WeatherAPILogic() throws IOException {
    }



}
