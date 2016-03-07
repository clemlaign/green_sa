package fr.insa_rennes.greensa.database.model;

/**
 * Created by Antoine on 07/03/2016.
 * Modèle pour les parcours existants
 */
public class Course {

    private int id, id_hole;
    private float coordLat_hole;
    private float coordLong_hole;

    public Course(int id, int id_hole, float coordLat_hole, float coordLong_hole) {
        this.id = id;
        this.id_hole = id_hole;
        this.coordLat_hole = coordLat_hole;
        this.coordLong_hole = coordLong_hole;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_hole() {
        return id_hole;
    }

    public void setId_hole(int id_hole) {
        this.id_hole = id_hole;
    }

    public float getCoordLat_hole() {
        return coordLat_hole;
    }

    public void setCoordLat_hole(float coordLat_hole) {
        this.coordLat_hole = coordLat_hole;
    }

    public float getCoordLong_hole() {
        return coordLong_hole;
    }

    public void setCoordLong_hole(float coordLong_hole) {
        this.coordLong_hole = coordLong_hole;
    }

}
