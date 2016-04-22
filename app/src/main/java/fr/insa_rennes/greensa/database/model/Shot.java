package fr.insa_rennes.greensa.database.model;

/**
 * Created by Antoine on 07/03/2016.s
 * Modèle pour les coups tirés
 */
public class Shot {

    private int id; // id du trou
    private int id_parcours, id_club;
    private double coordLat_start, coordLong_start;
    private double coordLatTheo_end, coordLongTheo_end; // coordonnées que l'on souhaite atteindre
    private double coordLatReal_end, coordLongReal_end; // coordonnées réelles atteintes
    private double distance, angle; // distance en mètre et angle (deviation) en degré du tir
    private String wind; // format suivant: "vitesse degré"
    private String date; // format dd-MM-yyyy

    public Shot(int id, int id_parcours, int id_club, double coordLat_start, double coordLong_start, double coordLatTheo_end, double coordLongTheo_end, double coordLatReal_end, double coordLongReal_end, double distance, double angle, String wind, String date) {
        this.id = id;
        this.id_parcours = id_parcours;
        this.id_club = id_club;
        this.coordLat_start = coordLat_start;
        this.coordLong_start = coordLong_start;
        this.coordLatTheo_end = coordLatTheo_end;
        this.coordLongTheo_end = coordLongTheo_end;
        this.coordLatReal_end = coordLatReal_end;
        this.coordLongReal_end = coordLongReal_end;
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

    public double getCoordLat_start() {
        return coordLat_start;
    }

    public void setCoordLat_start(double coordLat_start) {
        this.coordLat_start = coordLat_start;
    }

    public double getCoordLong_start() {
        return coordLong_start;
    }

    public void setCoordLong_start(double coordLong_start) {
        this.coordLong_start = coordLong_start;
    }

    public double getCoordLatTheo_end() {
        return coordLatTheo_end;
    }

    public void setCoordLatTheo_end(double coordLatTheo_end) {
        this.coordLatTheo_end = coordLatTheo_end;
    }

    public double getCoordLongTheo_end() {
        return coordLongTheo_end;
    }

    public void setCoordLongTheo_end(double coordLongTheo_end) {
        this.coordLongTheo_end = coordLongTheo_end;
    }

    public double getCoordLatReal_end() {
        return coordLatReal_end;
    }

    public void setCoordLatReal_end(double coordLatReal_end) {
        this.coordLatReal_end = coordLatReal_end;
    }

    public double getCoordLongReal_end() {
        return coordLongReal_end;
    }

    public void setCoordLongReal_end(double coordLongReal_end) {
        this.coordLongReal_end = coordLongReal_end;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
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
