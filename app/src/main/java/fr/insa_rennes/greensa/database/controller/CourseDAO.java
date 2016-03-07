package fr.insa_rennes.greensa.database.controller;

import android.content.Context;
import fr.insa_rennes.greensa.database.model.Course;

/**
 * Created by Antoine on 07/03/2016.
 */
public class CourseDAO extends DAOBase {

    public CourseDAO(Context pContext) {
        super(pContext);
    }

    /**
     * @param s le coup à ajouter à la base
     */
    public void ajouter(Course s) {

    }

    /**
     * @param id l'identifiant du coup à supprimer
     */
    public void supprimer(int id) {

    }

    /**
     * @param s le coup modifié
     */
    public void modifier(Course s) {

    }

    /**
     * @param id l'identifiant du coup à récupérer
     */
    public Course selectionner(int id) {
        // CODE
        return null;
    }
}
