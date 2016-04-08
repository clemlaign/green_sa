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
 * Created by Antoine on 31/03/2016.
 * Contient la liste des clubs de golf chargés à partir d'un fichier
 */
public class ClubsLoader {
    private static List<Club> clubs;

    public ClubsLoader() {

    }

    public static List<Club> getClubs(){
        return clubs;
    }

    public static boolean isLoaded(){
        if(clubs == null){
            clubs = new ArrayList<Club>(); // on initialise
            return false;
        }
        return clubs.size() != 0;
    }

    /**
     *
     * @param context
     * @return
     *  Lis le fichier des trous (à modifier pour récupérer les infos sous forme de tableau)
     *  Revoit true si le chargement a fonctionné, false sinon
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
                String[] tab = line.split(" "); // on sépare la ligne pour récuper les élements
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