package fr.insa_rennes.greensa.utility;

import org.json.JSONObject;

/**
 * Interface permettant de creer une methode call back pour la meteo
 */
public interface AsyncResponse {
    void processFinish(JSONObject output);
}