package fr.insa_rennes.greensa.database.model;

/**
 * Classe modele pour representer un parcours
 */
public class Course {

    /**
     * ID du parcours
     */
    private int id;

    /**
     * Par du parcours
     */
    private int par;

    /**
     * Nom du parcours
     */
    private String name;

    /**
     * Tableau des trous du parcours
     */
    private Hole[] holes;

    /**
     * Constructeur
     * @param id ID du parcours
     * @param par Par du parcours
     * @param name Nom du parcours
     * @param holes Tableau de trous du parcours
     * @param size Taille du parcours
     */
    public Course(int id, int par, String name, Hole[] holes, int size) {
        this.id = id;
        this.par = par;
        this.name = name;
        this.holes = new Hole[size];
        for(int i=0; i<this.holes.length; i++)
            this.holes[i] = holes[i];
    }

    /**
     * Obtenir l'ID du parcours
     * @return ID du parcours
     */
    public int getId() {
        return id;
    }

    /**
     * Modifier ID du parcours
     * @param id ID du parcours
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtenir le par du parcours
     * @return
     */
    public int getPar() {
        return par;
    }

    /**
     * Modifier le par du parcours
     * @param par Par du parcours
     */
    public void setPar(int par) {
        this.par = par;
    }

    /**
     * Obtenir nom du parcours
     * @return Nom du parcours
     */
    public String getName() {
        return name;
    }

    /**
     * Modifier nom du parcours
     * @param name Nom du parcours
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Obtenir les trous du parcours
     * @return Tableau des trous du parcours
     */
    public Hole[] getHoles() {
        return holes;
    }

    /**
     * Modifier les trous du parcours
     * @param holes Tableau de trous
     */
    public void setHoles(Hole[] holes) {
        this.holes = holes;
    }
}
