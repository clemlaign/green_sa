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
 * Created by Antoine on 30/03/2016.
 * Contient la liste des parcours de golf chargé à partir d'un fichier
 */
public class CoursesLoader {

    private static List<Course> courses;

    public CoursesLoader() {

    }

    public static List<Course> getCourses(){
        return courses;
    }

    public static boolean isLoaded(){
        if(courses == null){
            courses = new ArrayList<Course>(); // on initialise
            return courses.size() != 0;
        }
        return false;
    }

    /**
     *
     * @param context
     * @return
     *  Lis le fichier des trous (à modifier pour récupérer les infos sous forme de tableau)
     *  Revoit true si le chargement a fonctionné, false sinon
     */
    public static boolean loadCoursesFromFile(Context context, String fileName){
        FileInputStream fIn = null;
        InputStreamReader isr = null;

        char[] inputBuffer = new char[255];
        String data = null;

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
                String[] tab = line.split(" "); // on sépare la ligne pour récuper les élements
                if(tab.length == 2){
                    // on peut ajouter un nouveau parcours
                    String name = tab[0].replace("_", " ");
                    int par = Integer.parseInt(tab[1]);
                    courses.add(new Course(id, par, name, h, card));

                    id++;
                    card = 0;
                }
                else if(tab.length == 3){
                    // on crée un trou avec les nouvelles informations
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
