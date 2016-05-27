package fr.insa_rennes.greensa.database;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import fr.insa_rennes.greensa.database.model.Course;
import fr.insa_rennes.greensa.database.model.Hole;

/**
 * Classe pour charger la liste des parcours<br/>
 *
 * Cette classe lit un fichier texte contenant les parcours<br/>
 * Le fichier est disponible dans assets/holes.txt<br/>
 */
public class CoursesLoader {
    /**
     * Liste des parcours
     */
    private static List<Course> courses;

    public CoursesLoader() {

    }

    /**
     * Methode qui retourne la liste des parcours
     *
     * @return La liste de parcours
     */
    public static List<Course> getCourses(){
        return courses;
    }

    /**
     * Methode qui sert a savoir si la liste a ete initialisee et l'initialise sinon
     *
     * @return TRUE si la liste a ete charge et contient des elements
     */
    public static boolean isLoaded(){
        if(courses == null){
            courses = new ArrayList<Course>(); // on initialise
            return false;
        }
        return courses.size() != 0;
    }

    /**
     * Methode servant a lire le fichier des parcours<br/>
     * Chaque ligne du fichier est sous la forme :<br/>
     * latitude_trou1 longitude_trou1 par_trou1<br/>
     * latitude_trou2 longitude_trou2 par_trou2<br/>
     * latitude_trou3 longitude_trou3 par_trou3<br/>
     * [...]<br/>
     * nom_du_parcours par_total<br/>
     *
     * @param context Le contexte actuel
     * @param fileName Le nom du fichier contenant les parcours
     * @return TRUE si le chargement a fonctionne, FALSE sinon
     */
    public static boolean loadCoursesFromFile(Context context, String fileName){
        InputStream in = null;
        try {
            in = context.getAssets().open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;

        try {
            int id = 0;
            int card = 0;
            Hole[] h = new Hole[18]; // 18 trous max

            while((line = reader.readLine()) != null) {
                String[] tab = line.split(" "); // on separe la ligne pour recuper les elements
                if(tab.length == 2){
                    // on peut ajouter un nouveau parcours
                    String name = tab[0].replace("_", " ");
                    int par = Integer.parseInt(tab[1]);
                    courses.add(new Course(id, par, name, h, card));

                    id++;
                    card = 0;
                }
                else if(tab.length == 3){
                    // on cree un trou avec les nouvelles informations
                    float lattitude = Float.parseFloat(tab[0]);
                    float longitude = Float.parseFloat(tab[1]);
                    int par = Integer.parseInt(tab[2]);
                    h[card] = new Hole(card, id, par, lattitude, longitude);

                    card++;
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
