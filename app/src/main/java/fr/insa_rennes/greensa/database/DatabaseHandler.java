package fr.insa_rennes.greensa.database;


/* Created by Antoine on 07/03/2016.
*
* Gestion base de données
*/

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

    public static final String SHOT_TABLE_NAME = "Shot";
    public static final String COURSE_TABLE_NAME = "Course";

    public static final String SHOT_TABLE_CREATE =
            "CREATE TABLE " + SHOT_TABLE_NAME + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "id_course INTEGER FOREIGN KEY REFERENCES Course(id), " +
                    "id_club INTEGER, "+
                    "coordLat_start REAL, "+
                    "coordLong_start REAL, "+
                    "coordLatTheo_end REAL, "+
                    "coordLongTheo_end REAL, "+
                    "coordLatReal_end REAL, "+
                    "coordLongReal_end REAL);";

    public static final String COURSE_TABLE_CREATE =
            "CREATE TABLE " + COURSE_TABLE_NAME + " (" +
                    "id INTEGER, " +
                    "id_hole INTEGER, " +
                    "coordLat_hole REAL, "+
                    "coordLong_hole REAL);";

    public static final String METIER_TABLE_DROP = "DROP TABLE IF EXISTS " + SHOT_TABLE_NAME + ";";
    public static final String COURSE_TABLE_DROP = "DROP TABLE IF EXISTS " + COURSE_TABLE_NAME + ";";

    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(COURSE_TABLE_CREATE);
        db.execSQL(SHOT_TABLE_CREATE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(COURSE_TABLE_DROP);
        db.execSQL(METIER_TABLE_DROP);
        onCreate(db);
    }
}