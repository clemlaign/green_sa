package fr.insa_rennes.greensa.database.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import fr.insa_rennes.greensa.database.model.Course;

/**
 * Created by Antoine on 07/03/2016.
 * Opérations de lecture et écriture pour les parcours
 */
public class CourseDAO extends DAOBase {

    public static final String TABLE_NAME = "Course";

    public CourseDAO(Context pContext) {
        super(pContext);
    }

    /**
     * @param c le parcours à ajouter à la base
     */
    public void ajouter(Course c) {
        ContentValues values = new ContentValues();
        values.put("id", c.getId());
        values.put("id_hole", c.getId_hole());
        values.put("coordLat_hole", c.getCoordLat_hole());
        values.put("coordLong_hole", c.getCoordLong_hole());

        // Inserting Row
        mDb.insert(TABLE_NAME, null, values);
    }

    /**
     * @param id l'identifiant du parcours à supprimer
     */
    public void supprimer(int id) {

    }

    /**
     * @param s le parcours modifié
     */
    public void modifier(Course s) {

    }

    /**
     * @param id l'identifiant du parcours à récupérer
     */
    public Course selectionner(int id) {
        Cursor c = mDb.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE id=?", new String[]{Integer.toString(id)});

        if (c != null)
            c.moveToFirst();

        Course course = new Course(c.getInt(0), c.getInt(1), c.getFloat(2), c.getFloat(3));

        return course;
    }

    /**
     *
     * @param context
     * @return
     *  Lis le fichier des trous (à modifier pour récupérer les infos sous forme de tableau)
     */
    public String ReadSettings(Context context){
        FileInputStream fIn = null;
        InputStreamReader isr = null;

        char[] inputBuffer = new char[255];
        String data = null;


        InputStream in = null;
        try {
            in = context.getAssets().open("holes.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder(1024);
        String line;

        try {
            while((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        data = sb.toString();

        return data;
    }
}
