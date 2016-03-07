package fr.insa_rennes.greensa.database.model;

/**
 * Created by Antoine on 07/03/2016.s
 * Modèle pour les coups tirés
 */
public class Shot {

    private int id, id_parcours, id_club;
    private float coordLat_start, coordLong_start;
    private float coordLatTheo_end, getCoordLongTheo_end;
    private float coordLatReal_end, getCoordLongReal_end;

    public Shot(int id, int id_parcours, int id_club, float coordLat_start, float coordLong_start, float coordLatTheo_end, float getCoordLongTheo_end, float coordLatReal_end, float getCoordLongReal_end) {
        this.id = id;
        this.id_parcours = id_parcours;
        this.id_club = id_club;
        this.coordLat_start = coordLat_start;
        this.coordLong_start = coordLong_start;
        this.coordLatTheo_end = coordLatTheo_end;
        this.getCoordLongTheo_end = getCoordLongTheo_end;
        this.coordLatReal_end = coordLatReal_end;
        this.getCoordLongReal_end = getCoordLongReal_end;
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

    public float getCoordLatTheo_end() {
        return coordLatTheo_end;
    }

    public void setCoordLatTheo_end(float coordLatTheo_end) {
        this.coordLatTheo_end = coordLatTheo_end;
    }


    public float getGetCoordLongReal_end() {
        return getCoordLongReal_end;
    }

    public void setGetCoordLongReal_end(float getCoordLongReal_end) {
        this.getCoordLongReal_end = getCoordLongReal_end;
    }

    public float getCoordLatReal_end() {
        return coordLatReal_end;
    }

    public void setCoordLatReal_end(float coordLatReal_end) {
        this.coordLatReal_end = coordLatReal_end;
    }

    public float getGetCoordLongTheo_end() {
        return getCoordLongTheo_end;
    }

    public void setGetCoordLongTheo_end(float getCoordLongTheo_end) {
        this.getCoordLongTheo_end = getCoordLongTheo_end;
    }

    public float getCoordLong_start() {
        return coordLong_start;
    }

    public void setCoordLong_start(float coordLong_start) {
        this.coordLong_start = coordLong_start;
    }

    public float getCoordLat_start() {
        return coordLat_start;
    }

    public void setCoordLat_start(float coordLat_start) {
        this.coordLat_start = coordLat_start;
    }

}
