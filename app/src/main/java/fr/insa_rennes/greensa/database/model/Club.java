package fr.insa_rennes.greensa.database.model;

/**
 * Created by Antoine on 31/03/2016.
 * Modèle pour les clubs de golf
 */
public class Club {

    private int id;
    private String name;

    public Club(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
