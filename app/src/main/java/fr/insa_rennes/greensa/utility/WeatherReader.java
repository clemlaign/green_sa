package fr.insa_rennes.greensa.utility;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Date;

/**
 * Classe permettant de recuperer un ficher au format Json contenant les informations meteorologiques.<br/>
 *
 * http://www.openweathermap.com/<br/>
 * API key: fc7f2d6f55d2b25e62dfc457601169d9<br/>
 *<br/>
 * Limites :<br/>
 * 600 appels max toutes les 10mins<br/>
 * 50 000 appels max tous les jours<br/>
 * <br/>
 * FICHIER retour du style :<br/>
 * {"coord":{"lon":139,"lat":35},<br/>
 *  "sys":{"country":"JP","sunrise":1369769524,"sunset":1369821049},<br/>
 *  "weather":[{"id":804,"main":"clouds","description":"overcast clouds","icon":"04n"}],<br/>
 *  "main":{"temp":289.5,"humidity":89,"pressure":1013,"temp_min":287.04,"temp_max":292.04},<br/>
 *  "wind":{"speed":7.31,"deg":187.002},<br/>
 *  "rain":{"3h":0},<br/>
 *  "clouds":{"all":92},<br/>
 *  "dt":1369824698,<br/>
 *  "id":1851632,<br/>
 *  "name":"Shuzenji",<br/>
 *  "cod":200}<br/>
 *<br/>
 *  Unites :<br/>
 *  wind.speed Wind speed. Unit Default: meter/sec.<br/>
 *  wind.deg Wind direction, degrees (meteorological), 0° = vent vient du Nord et 180° = vent vient du Sud<br/>
 *<br/>
 *  Herite de AsyncTask<Double, Void, JSONObject><br/>
 *  Permet de recuperer les informations en parallele du deroulement de l'activite en cours.<br/>
 *  On ne doit pas bloquer l'application lorsqu'on lance une requête HTTP<br/>
 */

public class WeatherReader extends AsyncTask<Double, Void, JSONObject> {

    /**
     * ID du compte OpenWeatherMap
     */
    private static final String APPID = "fc7f2d6f55d2b25e62dfc457601169d9";

    /**
     * Interface pour call back
     */
    public AsyncResponse delegate = null;

    /**
     * Donnees recuperees au format JSON
     */
    public static JSONObject weather;

    /**
     * Constructeur de la classe WeatherReader
     * @param asyncResponse Interface pour call back
     */
    public WeatherReader(AsyncResponse asyncResponse) {
        delegate = asyncResponse; //Assigning call back interfacethrough constructor
    }

    /**
     * Methode pour recuperer le vent a la position (latitude, longitude)
     * @param latitude Latitude de la position ou on souhaite recuperer le vent
     * @param longitude Longitude de la position ou on souhaite recuperer le vent
     * @return Donnees au format JSON
     */
    public JSONObject read(double latitude, double longitude){
        JSONObject json = new JSONObject();
        try{

            json = readJsonFromUrl("http://api.openweathermap.org/data/2.5/weather?APPID="+APPID+"&lat=" + Double.toString(latitude) + "&lon=" + Double.toString(longitude));

        } catch (IOException ioe){ }
          catch (JSONException je){ }

        return json;
    }

    /**
     * Methode pour passer du buffer a un String
     * @param rd Buffer
     * @return Les donnees sous forme textuelle
     * @throws IOException
     */
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    /**
     * Methode pour recuperer les donnees d'une requete HTTP
     * @param url Url vers l'API
     * @return Objet JSON contenant les donnees sur la meteo
     * @throws IOException
     * @throws JSONException
     */
    private static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;

        }finally {
            is.close();
        }
    }

    /**
     * Methode qui realise le traitement de maniere asynchrone dans un Thread separe
     * @param params Latitude et longitude
     * @return Donnees au format JSON
     */
    protected JSONObject doInBackground(Double... params) {
        double latitude = params[0];
        double longitude = params[1];

        return read(latitude, longitude);
    }

    /**
     * Methode appelee apres le traitement
     * Enregistre les donnees recuperees
     * @param jsonObject Donnees JSON recuperees
     */
    protected void onPostExecute(JSONObject jsonObject) {
        weather = jsonObject;
        delegate.processFinish(jsonObject);
    }
}