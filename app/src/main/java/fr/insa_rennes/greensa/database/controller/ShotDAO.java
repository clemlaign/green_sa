package fr.insa_rennes.greensa.database.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import fr.insa_rennes.greensa.database.model.Shot;

/**
 * Cette classe s'occupe seulement de la lecture et ecriture dans la table tir<br/>
 *
 * Herite de la classe DAOBase<br/>
 */
public class ShotDAO extends DAOBase {

    /**
     * Nom de la table contenant les tirs
     */
    public static final String TABLE_NAME = "Shot";

    /**
     * Constructeur de la classe ShotDAO
     *
     * @param pContext Contexte actuel
     */
    public ShotDAO(Context pContext) {
        super(pContext);
    }

    /**
     * Methode pour ajouter un tir
     *
     * @param s Le tir a ajouter a la base
     */
    public void ajouter(Shot s) {
        ContentValues values = new ContentValues();
        values.put("id_course_hole", s.getId_course_hole());
        values.put("id_hole", s.getId_hole());
        values.put("id_course", s.getId_parcours());
        values.put("id_club", s.getId_club());
        values.put("coordLat_start", s.getCoordLat_start());
        values.put("coordLong_start", s.getCoordLong_start());
        values.put("coordLatTheo_end", s.getCoordLatTheo_end());
        values.put("coordLongTheo_end", s.getCoordLongTheo_end());
        values.put("coordLatReal_end", s.getCoordLatReal_end());
        values.put("coordLongReal_end", s.getCoordLongReal_end());
        values.put("distance", s.getDistance());
        values.put("angle", s.getAngle());
        values.put("wind", s.getWind());
        values.put("date", s.getDate());

        // Inserting Row
        mDb.insert(TABLE_NAME, null, values);
    }

    /**
     * Methode pour supprimer un tir
     *
     * @param id l'identifiant du tir a supprimer
     */
    public void delete(int id) {

    }

    /**
     * Methode pour modifier un tir
     *
     * @param s Le tir a modifier
     */
    public void update(Shot s) {

    }

    /**
     * Methode pour vider la table
     */
    public void clear() {
        mDb.delete(TABLE_NAME, null, null);
    }

    public List<Shot> selectElements(String query, String[] var){
        List<Shot> list = new ArrayList<Shot>();

        Cursor c = mDb.rawQuery(query, var);

        while(c.moveToNext()){
            Shot shot = new Shot(c.getInt(0), c.getInt(1), c.getInt(2), c.getInt(3), c.getFloat(4), c.getFloat(5), c.getFloat(6), c.getFloat(7), c.getFloat(8), c.getFloat(9), c.getFloat(10), c.getFloat(11), c.getString(12),  c.getString(13));
            list.add(shot);
        }
        c.close();

        return list;
    }
}
