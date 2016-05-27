package fr.insa_rennes.greensa.database;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import fr.insa_rennes.greensa.database.model.Club;

/**
 * Classe pour charger la liste des clubs de golf<br/>
 *
 * Cette classe lit un fichier texte contenant les clubs de golf<br/>
 * Le fichier est disponible dans assets/clubs.txt<br/>
 */
public class ClubsLoader {
    /**
     * Liste des clubs
     */
    private static List<Club> clubs;

    public ClubsLoader() {

    }

    /**
     * Methode qui retourne la liste des clubs recuperes
     *
     * @return La liste de clubs
     */
    public static List<Club> getClubs(){
        return clubs;
    }

    /**
     * Methode qui sert a savoir si la liste a ete initialisee et l'initialise sinon
     *
     * @return TRUE si la liste a ete charge et contient des elements
     */
    public static boolean isLoaded(){
        if(clubs == null){
            clubs = new ArrayList<Club>(); // on initialise
            return false;
        }
        return clubs.size() != 0;
    }

    /**
     * Methode servant a lire le fichier des clubs<br/>
     * Chaque ligne du fichier est sous la forme :<br/>
     * nom_du_club id_club<br/>
     *
     * @param context Le contexte actuel
     * @param fileName Le nom du fichier contenant les clubs
     * @return True si le chargement a fonctionne, False sinon
     */
    public static boolean loadClubsFromFile(Context context, String fileName){
        InputStream in = null;
        try {
            in = context.getAssets().open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;

        try {

            while((line = reader.readLine()) != null) {
                String[] tab = line.split(" "); // on separe la ligne pour recuper les elements
                if(tab.length == 2){
                    String name = tab[0].replace("_", " ");
                    int id = Integer.parseInt(tab[1]);
                    clubs.add(new Club(id, name));
                }
            }
            reader.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}