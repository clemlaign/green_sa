package fr.insa_rennes.greensa.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Classe qui gere la creation et la suppression de la Base de Donnees<br/>
 * Herite de la classe SQLiteOpenHelper
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    /**
     * Nom de la table contenant les tirs
     */
    public static final String SHOT_TABLE_NAME = "Shot";

    /**
     * Requete SQL pour la creation de la table tir
     */
    public static final String SHOT_TABLE_CREATE =
            "CREATE TABLE " + SHOT_TABLE_NAME + " (" +
                    "id_course_hole INTEGER, " +
                    "id_hole INTEGER, " +
                    "id_course INTEGER, " +
                    "id_club INTEGER, "+
                    "coordLat_start REAL, "+
                    "coordLong_start REAL, "+
                    "coordLatTheo_end REAL, "+
                    "coordLongTheo_end REAL, "+
                    "coordLatReal_end REAL, "+
                    "coordLongReal_end REAL, "+
                    "distance REAL, "+
                    "angle REAL,"+
                    "wind TEXT, "+
                    "date TEXT);";

    /**
     * Requete SQL pour la suppression de la table tir
     */
    public static final String METIER_TABLE_DROP = "DROP TABLE IF EXISTS " + SHOT_TABLE_NAME + ";";

    /**
     * Constructeur de la classe DatabaseHandler
     *
     * @param context Contexte actuel
     * @param name Nom du fichier de la base de donnee
     * @param factory null
     * @param version Version de la base de donnees
     */
    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * Methode pour la creation de la table tir
     *
     * @param db Base de donnees
     */
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SHOT_TABLE_CREATE);
    }

    /**
     * Methode pour la suppression de la table tir losqu'on change de version de bdd
     *
     * @param db Base de donnees
     * @param oldVersion Ancienne version de la bdd (entier)
     * @param newVersion Nouvelle version de la bdd (entier)
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(METIER_TABLE_DROP);
        onCreate(db);
    }
}