package fr.insa_rennes.greensa.database.model;

/**
 * Classe modele pour representer un club de golf
 */
public class Club {

    /**
     * ID du club
     */
    private int id;

    /**
     * Nom du club
     */
    private String name;

    /**
     * Constructeur
     * @param id ID du club
     * @param name Nom du club
     */
    public Club(int id, String name){
        this.id = id;
        this.name = name;
    }

    /**
     * Obtenir l'id du club
     * @return ID du club
     */
    public int getId() {
        return id;
    }

    /**
     * Modifier ID du club
     * @param id Nouvel ID du club
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtenir le nom du club
     * @return Nom du club
     */
    public String getName() {
        return name;
    }

    /**
     * Modifier nom du club
     * @param name Nom du club
     */
    public void setName(String name) {
        this.name = name;
    }

}
