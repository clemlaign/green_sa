package fr.insa_rennes.greensa.database.model;

/**
 * Classe modele pour representer un trou pour un parcours
 */
public class Hole {

    /**
     * ID du trou (numero)
     */
    private int id;

    /**
     * ID du parcours
     */
    private int id_course;

    /**
     * Par du trou
     */
    private int par;

    /**
     * Coordonnee latitude du trou
     */
    private float coordLat;

    /**
     * Coordonnee longitude du trou
     */
    private float coordLong;

    /**
     * Constructeur
     * @param id ID du trou
     * @param id_course ID du parcours
     * @param par Par du trou
     * @param coordLat Coordonnee latitude du trou
     * @param coordLong Coordonnee longitude du trou
     */
    public Hole(int id, int id_course, int par, float coordLat, float coordLong) {
        this.id = id;
        this.id_course = id_course;
        this.par = par;
        this.coordLat = coordLat;
        this.coordLong = coordLong;
    }

    /**
     * Obtenir ID du trou
     * @return ID du trou
     */
    public int getId() {
        return id;
    }

    /**
     * Modifier ID du trou
     * @param id ID du trou
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtenir ID du parcours du trou
     * @return ID du parcours
     */
    public int getId_course() {
        return id_course;
    }

    /**
     * Modifier ID du parcours du trou
     * @param ID du parcours
     */
    public void setId_course(int id_course) {
        this.id_course = id;
    }

    /**
     * Obtenir par du trou
     * @return Par du trou
     */
    public int getPar() {
        return par;
    }

    /**
     * Modifier par du trou
     * @param par Par du trou
     */
    public void setPar(int par) {
        this.par = par;
    }

    /**
     * Obtenir latitude du trou
     * @return Latitude du trou
     */
    public float getCoordLat() {
        return coordLat;
    }

    /**
     * Modifier latitude du trou
     * @param coordLat Latitude du trou
     */
    public void setCoordLat(float coordLat) {
        this.coordLat = coordLat;
    }

    /**
     * Obtenir longitude du trou
     * @return Longitude du trou
     */
    public float getCoordLong() {
        return coordLong;
    }

    /**
     * Modifier longitude du trou
     * @param coordLong Longitude du trou
     */
    public void setCoordLong(float coordLong) {
        this.coordLong = coordLong;
    }
}
