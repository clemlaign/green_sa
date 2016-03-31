package fr.insa_rennes.greensa.database.model;

/**
 * Created by Antoine on 30/03/2016.
 * Modèle pour representer un trou pour un parcours
 */
public class Hole {

    private int id, id_course, par;
    private float coordLat;
    private float coordLong;

    public Hole(int id, int id_course, int par, float coordLat, float coordLong) {
        this.id = id;
        this.id_course = id_course;
        this.par = par;
        this.coordLat = coordLat;
        this.coordLong = coordLong;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_course() {
        return id_course;
    }

    public void setId_course(int id_course) {
        this.id_course = id;
    }

    public int getPar() {
        return par;
    }

    public void setPar(int par) {
        this.par = par;
    }


    public float getCoordLat() {
        return coordLat;
    }

    public void setCoordLat(float coordLat) {
        this.coordLat = coordLat;
    }

    public float getCoordLong() {
        return coordLong;
    }

    public void setCoordLong(float coordLong) {
        this.coordLong = coordLong;
    }
}
