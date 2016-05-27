package fr.insa_rennes.greensa.database.model;

/**
 * Classe modele pour representer un tir/coup
 */
public class Shot {

    /**
     * ID du parcours dans la base de donnees (incrementation)
     */
    private int id_course_hole;

    /**
     * ID du trou
     */
    private int id_hole;

    /**
     * ID du parcours
     */
    private int id_parcours;

    /**
     * ID du club
     */
    private int id_club;

    /**
     * Latitude position depart (position de tir)
     */
    private double coordLat_start;

    /**
     * Longitude position depart (position de tir)
     */
    private double coordLong_start;

    /**
     * Latitude position objectif (theorique)
     */
    private double coordLatTheo_end;

    /**
     * Longitude position objectif (theorique)
     */
    private double coordLongTheo_end;

    /**
     * Latitude position de la balle (reelle)
     */
    private double coordLatReal_end;

    /**
     * Longitude position de la balle (reelle)
     */
    private double coordLongReal_end;

    /**
     * Distance du tir en metre
     */
    private double distance;

    /**
     * Angle de deviation : (position objectif, position tir, position balle) en degre
     */
    private double angle;

    /**
     * Donnees sur le vent au format suivant : "vitesse degre"
     */
    private String wind;

    /**
     * Date du tir/coup au format dd-MM-yyyy
     */
    private String date;

    /**
     * Constructeur
     * @param id_course_hole
     * @param id_hole
     * @param id_parcours
     * @param id_club
     * @param coordLat_start
     * @param coordLong_start
     * @param coordLatTheo_end
     * @param coordLongTheo_end
     * @param coordLatReal_end
     * @param coordLongReal_end
     * @param distance
     * @param angle
     * @param wind
     * @param date
     */
    public Shot(int id_course_hole, int id_hole, int id_parcours, int id_club, double coordLat_start, double coordLong_start, double coordLatTheo_end, double coordLongTheo_end, double coordLatReal_end, double coordLongReal_end, double distance, double angle, String wind, String date) {
        this.id_course_hole = id_course_hole;
        this.id_hole = id_hole;
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

    public int getId_course_hole() { return id_course_hole; }

    public void setId_course_hole(int id_course_hole) { this.id_course_hole = id_course_hole; }

    public int getId_hole() {
        return id_hole;
    }

    public void setId_hole(int id_hole) {
        this.id_hole = id_hole;
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
