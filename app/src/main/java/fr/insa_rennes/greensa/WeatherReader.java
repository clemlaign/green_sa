package fr.insa_rennes.greensa;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Date;

/**
 * Created by Antoine on 08/04/2016.
 * Classe permettant de recuperer un ficher au format Json contenant
 * les informations météorologiques.
 *
 * http://www.openweathermap.com/
 * API key: fc7f2d6f55d2b25e62dfc457601169d9
 *
 * Limites :
 * 600 appels max toutes les 10mins
 * 50 000 appels max tous les jours
 *
 * FICHIER retour du style :
 * {"coord":{"lon":139,"lat":35},
    "sys":{"country":"JP","sunrise":1369769524,"sunset":1369821049},
    "weather":[{"id":804,"main":"clouds","description":"overcast clouds","icon":"04n"}],
    "main":{"temp":289.5,"humidity":89,"pressure":1013,"temp_min":287.04,"temp_max":292.04},
    "wind":{"speed":7.31,"deg":187.002},
    "rain":{"3h":0},
    "clouds":{"all":92},
    "dt":1369824698,
    "id":1851632,
    "name":"Shuzenji",
    "cod":200}
 *
 *  Unités :
    wind.speed Wind speed. Unit Default: meter/sec.
    wind.deg Wind direction, degrees (meteorological)

 */

public class WeatherReader {

    private static final String APPID = "fc7f2d6f55d2b25e62dfc457601169d9";
    private static long formerTimestamp = 0;

    public static JSONObject read(float lattitude, float longitude){
        JSONObject json = new JSONObject();
        try{

            Date atm = new Date();

            // On ne met à jour le temps que maximum 1 fois toutes les 5mins (la météo change peu)
            if(atm.getTime() > formerTimestamp + 5*60000) {
                // On recupere le JSONObject
                json = readJsonFromUrl("http://api.openweathermap.org/data/2.5/weather?APPID="+APPID+"&lat=" + Float.toString(lattitude) + "&lon=" + Float.toString(longitude));
                formerTimestamp = atm.getTime(); // On actualise la date
                System.out.println("On actualise !!");
            }

        } catch (IOException ioe){ }
          catch (JSONException je){ }

        return json;
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }
}