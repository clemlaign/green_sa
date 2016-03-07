package fr.insa_rennes.greensa.database.controller;

import android.content.Context;
import fr.insa_rennes.greensa.database.model.Shot;

/**
 * Created by Antoine on 07/03/2016.
 */
public class ShotDAO extends DAOBase {

    public ShotDAO(Context pContext) {
        super(pContext);
    }

    /**
     * @param s le coup à ajouter à la base
     */
    public void ajouter(Shot s) {

    }

    /**
     * @param id l'identifiant du coup à supprimer
     */
    public void supprimer(int id) {

    }

    /**
     * @param s le coup modifié
     */
    public void modifier(Shot s) {

    }

    /**
     * @param id l'identifiant du coup à récupérer
     */
    public Shot selectionner(int id) {
        // CODE
        return null;
    }
}
