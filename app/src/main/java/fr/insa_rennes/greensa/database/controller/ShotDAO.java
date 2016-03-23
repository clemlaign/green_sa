package fr.insa_rennes.greensa.database.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import fr.insa_rennes.greensa.database.model.Shot;

/**
 * Created by Antoine on 07/03/2016.
 * Opérations de lecture et écriture pour les tires
 */
public class ShotDAO extends DAOBase {

    public static final String TABLE_NAME = "Shot";

    public ShotDAO(Context pContext) {
        super(pContext);
    }

    /**
     * @param s le coup à ajouter à la base
     */
    public void ajouter(Shot s) {
        ContentValues values = new ContentValues();
        values.put("id_course", s.getId_parcours());
        values.put("id_club", s.getId_club());
        values.put("coordLat_start", s.getCoordLat_start());
        values.put("coordLong_start", s.getCoordLong_start());
        values.put("coordLatTheo_end", s.getCoordLatTheo_end());
        values.put("coordLongTheo_end", s.getCoordLongTheo_end());
        values.put("coordLatReal_end", s.getCoordLatReal_end());
        values.put("coordLongReal_end", s.getCoordLongReal_end());

        // Inserting Row
        mDb.insert(TABLE_NAME, null, values);
    }

    /**
     * @param id l'identifiant du coup à supprimer
     */
    public void delete(int id) {

    }

    /**
     * @param s le coup modifié
     */
    public void update(Shot s) {

    }

    /**
     * @param id l'identifiant du coup à récupérer
     */
    public Shot select(int id) {
        Cursor c = mDb.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE id=?", new String[]{Integer.toString(id)});

        if (c != null)
            c.moveToFirst();

        Shot shot = new Shot(c.getInt(0), c.getInt(1), c.getInt(2), c.getFloat(3), c.getFloat(4), c.getFloat(5), c.getFloat(6), c.getFloat(7), c.getFloat(8));
        c.close();

        return shot;
    }

    public List<Shot> selectElements(String query, String[] var){
        List<Shot> list = new ArrayList<Shot>();

        Cursor c = mDb.rawQuery(query, var);

        while(c.moveToNext()){
            list.add(new Shot(c.getInt(0), c.getInt(1), c.getInt(2), c.getFloat(3), c.getFloat(4), c.getFloat(5), c.getFloat(6), c.getFloat(7), c.getFloat(8)));
        }
        c.close();

        return list;
    }
}
