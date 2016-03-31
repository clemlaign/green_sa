package fr.insa_rennes.greensa.database.model;

/**
 * Created by Antoine on 07/03/2016.
 * Modèle pour les parcours existants
 */
public class Course {

    private int id, par;
    private String name;
    private Hole[] holes;

    public Course(int id, int par, String name, Hole[] holes, int size) {
        this.id = id;
        this.par = par;
        this.name = name;
        this.holes = new Hole[size];
        for(int i=0; i<this.holes.length; i++)
            this.holes[i] = holes[i];
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

    public Hole[] getHoles() {
        return holes;
    }

    public void setHoles(Hole[] holes) {
        this.holes = holes;
    }
}
