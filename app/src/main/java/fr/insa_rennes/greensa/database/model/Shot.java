package fr.insa_rennes.greensa.database.model;

/**
 * Created by Antoine on 07/03/2016.s
 * Modèle pour les coups tirés
 */
public class Shot {

    private int id; // id en autoincrement
    private int id_parcours, id_club;
    private float coordLat_start, coordLong_start;
    private float coordLatTheo_end, getCoordLongTheo_end; // coordonnées que l'on souhaite atteindre
    private float coordLatReal_end, getCoordLongReal_end; // coordonnées réelles atteintes
    private float distance, angle; // distance en mètre et angle (deviation) en degré du tir
    private String wind; // format suivant: "vitesse degré"
    private String date; // format dd-MM-yyyy

    public Shot(int id_parcours, int id_club, float coordLat_start, float coordLong_start, float coordLatTheo_end, float getCoordLongTheo_end, float coordLatReal_end, float getCoordLongReal_end, float distance, float angle, String wind, String date) {
        this.id_parcours = id_parcours;
        this.id_club = id_club;
        this.coordLat_start = coordLat_start;
        this.coordLong_start = coordLong_start;
        this.coordLatTheo_end = coordLatTheo_end;
        this.getCoordLongTheo_end = getCoordLongTheo_end;
        this.coordLatReal_end = coordLatReal_end;
        this.getCoordLongReal_end = getCoordLongReal_end;
        this.distance = distance;
        this.angle = angle;
        this.wind = wind;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_parcours() {
        return id_parcours;
    }

    public void setId_parcours(int id_parcours) {
        this.id_parcours = id_parcours;
    }

    public int getId_club() {
        return id_club;
    }

    public void setId_club(int id_club) {
        this.id_club = id_club;
    }

    public float getCoordLat_start() {
        return coordLat_start;
    }

    public void setCoordLat_start(float coordLat_start) {
        this.coordLat_start = coordLat_start;
    }

    public float getCoordLong_start() {
        return coordLong_start;
    }

    public void setCoordLong_start(float coordLong_start) {
        this.coordLong_start = coordLong_start;
    }

    public float getCoordLatTheo_end() {
        return coordLatTheo_end;
    }

    public void setCoordLatTheo_end(float coordLatTheo_end) {
        this.coordLatTheo_end = coordLatTheo_end;
    }

    public float getCoordLongTheo_end() {
        return getCoordLongTheo_end;
    }

    public void setCoordLongTheo_end(float getCoordLongTheo_end) {
        this.getCoordLongTheo_end = getCoordLongTheo_end;
    }

    public float getCoordLatReal_end() {
        return coordLatReal_end;
    }

    public void setCoordLatReal_end(float coordLatReal_end) {
        this.coordLatReal_end = coordLatReal_end;
    }

    public float getCoordLongReal_end() {
        return getCoordLongReal_end;
    }

    public void setCoordLongReal_end(float getCoordLongReal_end) {
        this.getCoordLongReal_end = getCoordLongReal_end;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
