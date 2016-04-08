package fr.insa_rennes.greensa.stats;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.insa_rennes.greensa.MainActivity;
import fr.insa_rennes.greensa.R;
import fr.insa_rennes.greensa.database.CoursesLoader;
import fr.insa_rennes.greensa.database.controller.ShotDAO;
import fr.insa_rennes.greensa.database.model.Shot;

/*
Activité page d'accueil des statistiques
 */

public class Stats extends Activity {

    public static final float ALPHA_LEVEL = 0.4f; // niveau de transparence pour elements du menu non select.

    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        ImageView homeButton = (ImageView)findViewById(R.id.homeButton);
        ImageView settingsButton = (ImageView)findViewById(R.id.settingsButton);
        ImageView distance = (ImageView)findViewById(R.id.distanceStats);
        distance.setAlpha(ALPHA_LEVEL);
        ImageView angle = (ImageView)findViewById(R.id.angleStats);
        angle.setAlpha(ALPHA_LEVEL);

        homeButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent activity = new Intent(Stats.this, MainActivity.class);
                startActivity(activity);
            }

        });

        settingsButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                showDialog(1);
            }

        });

        distance.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent activity = new Intent(Stats.this, graphStats.class);
                activity.putExtra("choix", graphStats.DISTANCE);
                startActivity(activity);
            }

        });

        angle.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent activity = new Intent(Stats.this, graphStats.class);
                activity.putExtra("choix", graphStats.ANGLE);
                startActivity(activity);
            }

        });
/*
        ShotDAO sdao = new ShotDAO(this);
        sdao.open();
        for(int i=0;i<200;i++){
            sdao.ajouter(new Shot((int)(Math.random()*2), (int)(Math.random()*10), 0, 0, 0, 0, 0, 0, (float)(Math.random()*220), (float)(Math.random()*60 - 30), "", "06-04-2016"));
        }
        sdao.close();
*/
        ActualizeStats();
    }

    // Boite de dialogue pour les settings
    protected Dialog onCreateDialog(int id) {

        dialog=new Dialog(Stats.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_stats_settings);


        ImageButton delete = (ImageButton)dialog.findViewById(R.id.deleteButton);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // On affiche une AlertDialog pour confirmer
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        Stats.this);

                // set title
                alertDialogBuilder.setTitle("Suppression des données");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Cette action est irréversible !")
                        .setCancelable(false)
                        .setPositiveButton("Supprimer",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogAlert,int id) {
                                // On supprime les données

                                ShotDAO sdao = new ShotDAO(Stats.this);
                                sdao.open();
                                sdao.clear();
                                sdao.close();

                                // On confirme que les données ont été supprimées
                                Toast.makeText(getApplicationContext(), "Données supprimées", Toast.LENGTH_LONG).show();
                                // On actualise les infos
                                ActualizeStats();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogAlert, int id) {
                                // On annule donc on retourne dans les settings
                                dialogAlert.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
            }
        });

        return dialog;
    }

    public void ActualizeStats(){
        TextView nb_tirs = (TextView)findViewById(R.id.nb_tirs);
        TextView date_lastShot = (TextView)findViewById(R.id.date);
        TextView max_distance = (TextView)findViewById(R.id.distance);
        TextView best_course = (TextView)findViewById(R.id.parcours);
        TextView average_angle = (TextView)findViewById(R.id.angleMoyen);
        TextView description_angle = (TextView)findViewById(R.id.angleMoyenDescription);

        ShotDAO sdao = new ShotDAO(this);
        sdao.open();
        List<Shot> list = sdao.selectElements("SELECT * FROM " + sdao.TABLE_NAME, null);
        sdao.close();

        // On parcourt des tirs enregistrés pour afficher des stats

        float max_dist = 0;
        float av_angle = 0;
        // nombre de tir par parcours - clé: id_parcours, valeur: nb tirs
        HashMap<Integer, Integer> pref_courses = new HashMap<Integer, Integer>();
        for(Shot tmp : list){
            // Calcul distance max
            if(tmp.getDistance() > max_dist)
                max_dist = tmp.getDistance();

            // Pour le calcul de l'angle moyen
            av_angle += tmp.getAngle();

            // on ajoute 1 tir pour le parcours
            if(pref_courses.containsKey(tmp.getId_parcours()))
                pref_courses.put(tmp.getId_parcours(), pref_courses.get(tmp.getId_parcours()) + 1);
            else
                pref_courses.put(tmp.getId_parcours(), 1);
        }

        av_angle /= list.size();
        av_angle = (float)(Math.round(av_angle*10.0)/10.0); // on arrondit au dixième

        int max_parcours = 0;
        int id_pref_parcours = -1;
        // On parcourt la map pour savoir quel parcours est le plus joué
        for(Map.Entry<Integer, Integer> tmp : pref_courses.entrySet()){
            if(tmp.getValue() > max_parcours || id_pref_parcours == -1)
                id_pref_parcours = tmp.getKey();
        }

        nb_tirs.setText(Integer.toString(list.size()));

        if(list.size() > 0)
            date_lastShot.setText(list.get(list.size() - 1).getDate());
        else
            date_lastShot.setText("Jamais");

        max_dist = (float)(Math.round(max_dist*10.0)/10.0); // on arrondit au dixième
        max_distance.setText(Float.toString(max_dist)+"m");

        if(id_pref_parcours != -1 && id_pref_parcours < CoursesLoader.getCourses().size())
            best_course.setText(CoursesLoader.getCourses().get(id_pref_parcours).getName());
        else
            best_course.setText("Aucun");

        average_angle.setText(Float.toString(av_angle)+"°");
        if(av_angle > 0)
            description_angle.setText("L'angle de déviation moyen. Tendance à droite");
        else if(av_angle < 0)
            description_angle.setText("L'angle de déviation moyen. Tendance à gauche");
        else
            description_angle.setText("L'angle de déviation moyen");
    }
}