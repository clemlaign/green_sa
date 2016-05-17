package fr.insa_rennes.greensa.utility;

import org.json.JSONObject;

/**
 * Created by Antoine on 28/04/2016.
 * Interface permettant de créer une methode call back pour la méteo
 */
public interface AsyncResponse {
    void processFinish(JSONObject output);
}