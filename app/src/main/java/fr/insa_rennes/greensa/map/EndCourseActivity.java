package fr.insa_rennes.greensa.map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import fr.insa_rennes.greensa.MainActivity;
import fr.insa_rennes.greensa.R;
import fr.insa_rennes.greensa.stats.StatsActivity;

/**
 * Cette classe recupere les informations de la partie terminee : nb_tirs, nb_putts, par<br/>
 * Elle affiche ces valeurs dans un tableau
 */
public class EndCourseActivity extends Activity {

    /**
     * Tableau contenant le nombre de tir par trou
     */
    private int[] nb_tirs;

    /**
     * Tableau contenant le nombre de putts par trou
     */
    private int[] nb_putts;

    /**
     * Tableau contenant le par de chaque trou
     */
    private int[] par;

    /**
     * Taille du texte affiche
     */
    public static final int textSize = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_course);

        Button newCourse = (Button)findViewById(R.id.startButton);
        Button stats = (Button)findViewById(R.id.statsButton);
        ImageView home = (ImageView)findViewById(R.id.homeButton);

        newCourse.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent activity = new Intent(EndCourseActivity.this, ChoiceCourseActivity.class);
                startActivity(activity);
            }
        });

        stats.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent activity = new Intent(EndCourseActivity.this, StatsActivity.class);
                startActivity(activity);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent activity = new Intent(EndCourseActivity.this, MainActivity.class);
                startActivity(activity);
            }
        });

        Intent intent = getIntent();

        nb_tirs = intent.getIntArrayExtra("nb_tirs");
        nb_putts = intent.getIntArrayExtra("putts");
        par = intent.getIntArrayExtra("par");

        TableLayout table9 = (TableLayout) findViewById(R.id.tabScore9); // on prend le tableau defini dans le layout
        TableLayout table18 = (TableLayout) findViewById(R.id.tabScore18); // on prend le tableau defini dans le layout


        if(nb_tirs.length > 9) { // 18 trous
            fillTable(table9, 0, 8, false); // On affiche les 9 premiers trous dans un tableau
            fillTable(table18, 9, 17, true); // On affiche les 9 autres trous dans un autre tableau
        }
        else
            fillTable(table9, 0, 8, true); // On affiche les 9 premiers trous dans un tableau

    }

    /**
     * Methode ajoutant les elements du trou "trou_start" au trou "trou_end" dans un tableau
     * Pour le dernier tableau, on ajoute une colonne qui calcul le total de chaque ligne
     * @param table TableLayout ou sont affiches les elements
     * @param trou_start Id du trou de depart
     * @param trou_end id du trou de fin
     * @param calculTotal TRUE si on souhaite afficher le total, FALSE sinon
     */
    public void fillTable(TableLayout table, int trou_start, int trou_end, boolean calculTotal){

        int somme_par = 0;
        int somme_tir = 0;
        int somme_putts = 0;
        // On calcule le total
        if(calculTotal) {
            for (int i = 0; i<=trou_end;i++){
                somme_par += par[i];
                somme_tir += nb_tirs[i];
                somme_putts += nb_putts[i];
            }
        }

        TableRow row; // creation d'un element : ligne
        TextView name, tv1; // creation des cellules

        name = new TextView(this);
        name.setText("N° trou");
        name.setTextSize(textSize);
        name.setPadding(2, 2, 2, 2);
        name.setTextColor(getResources().getColor(R.color.black));
        name.setBackground(getResources().getDrawable(R.drawable.cell_shape));
        name.setLayoutParams( new TableRow.LayoutParams( 0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 3 ) );

        row = new TableRow(this); // creation d'une nouvelle ligne
        row.addView(name);
        for(int i=trou_start;i<=trou_end;i++) {

            tv1 = new TextView(this); // creation cellule
            tv1.setText(Integer.toString(i + 1)); // ajout du texte
            tv1.setTextSize(textSize);
            tv1.setPadding(2, 2, 2, 2);
            tv1.setTextColor(getResources().getColor(R.color.black));
            tv1.setBackground(getResources().getDrawable(R.drawable.cell_shape));
            tv1.setGravity(Gravity.CENTER); // centrage dans la cellule
            // adaptation de la largeur de colonne a l'ecran :
            tv1.setLayoutParams( new TableRow.LayoutParams( 0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1 ) );

            // ajout de la cellule a la ligne
            row.addView(tv1);
        }

        if(calculTotal) {
            tv1 = new TextView(this); // creation cellule
            tv1.setText("Tot"); // ajout du texte
            tv1.setTextSize(textSize);
            tv1.setPadding(2, 2, 2, 2);
            tv1.setTextColor(getResources().getColor(R.color.black));
            tv1.setBackground(getResources().getDrawable(R.drawable.cell_shape));
            tv1.setGravity(Gravity.CENTER); // centrage dans la cellule
            // adaptation de la largeur de colonne a l'ecran :
            tv1.setLayoutParams(new TableRow.LayoutParams(0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 2));

            // ajout de la cellule a la ligne
            row.addView(tv1);
        }

        table.addView(row);


        // LE PAR
        name = new TextView(this);
        name.setText("Par");
        name.setTextSize(textSize);
        name.setPadding(2, 2, 2, 2);
        name.setTextColor(getResources().getColor(R.color.black));
        name.setBackground(getResources().getDrawable(R.drawable.cell_shape));
        name.setLayoutParams(new TableRow.LayoutParams(0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 3));

        row = new TableRow(this); // creation d'une nouvelle ligne
        row.addView(name);
        for(int i=trou_start;i<=trou_end;i++) {

            tv1 = new TextView(this); // creation cellule
            tv1.setText(Integer.toString(par[i])); // ajout du texte
            tv1.setTextSize(textSize);
            tv1.setPadding(2,2,2,2);
            tv1.setBackground(getResources().getDrawable(R.drawable.cell_shape));
            tv1.setGravity(Gravity.CENTER); // centrage dans la cellule
            // adaptation de la largeur de colonne a l'ecran :
            tv1.setLayoutParams( new TableRow.LayoutParams( 0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1 ) );

            // ajout de la cellule a la ligne
            row.addView(tv1);
        }

        if(calculTotal) {
            tv1 = new TextView(this); // creation cellule
            tv1.setText(Integer.toString(somme_par)); // ajout du texte
            tv1.setTextSize(textSize);
            tv1.setPadding(2, 2, 2, 2);
            tv1.setTextColor(getResources().getColor(R.color.black));
            tv1.setBackground(getResources().getDrawable(R.drawable.cell_shape));
            tv1.setGravity(Gravity.CENTER); // centrage dans la cellule
            // adaptation de la largeur de colonne a l'ecran :
            tv1.setLayoutParams(new TableRow.LayoutParams(0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 2));

            // ajout de la cellule a la ligne
            row.addView(tv1);
        }

        table.addView(row);

        // LE NB DE TIRS
        name = new TextView(this);
        name.setText("Tirs");
        name.setTextSize(textSize);
        name.setPadding(2, 2, 2, 2);
        name.setTextColor(getResources().getColor(R.color.black));
        name.setBackground(getResources().getDrawable(R.drawable.cell_shape));
        name.setLayoutParams(new TableRow.LayoutParams(0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 3));

        row = new TableRow(this); // creation d'une nouvelle ligne
        row.addView(name);
        for(int i=trou_start;i<=trou_end;i++) {

            tv1 = new TextView(this); // creation cellule
            tv1.setText(Integer.toString(nb_tirs[i])); // ajout du texte
            tv1.setTextSize(textSize);
            tv1.setPadding(2, 2, 2, 2);
            tv1.setBackground(getResources().getDrawable(R.drawable.cell_shape));
            tv1.setGravity(Gravity.CENTER); // centrage dans la cellule
            // adaptation de la largeur de colonne a l'ecran :
            tv1.setLayoutParams(new TableRow.LayoutParams(0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1));

            // ajout de la cellule a la ligne
            row.addView(tv1);
        }
        if(calculTotal) {
            tv1 = new TextView(this); // creation cellule
            tv1.setText(Integer.toString(somme_tir)); // ajout du texte
            tv1.setTextSize(textSize);
            tv1.setPadding(2, 2, 2, 2);
            tv1.setTextColor(getResources().getColor(R.color.black));
            tv1.setBackground(getResources().getDrawable(R.drawable.cell_shape));
            tv1.setGravity(Gravity.CENTER); // centrage dans la cellule
            // adaptation de la largeur de colonne a l'ecran :
            tv1.setLayoutParams(new TableRow.LayoutParams(0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 2));

            // ajout de la cellule a la ligne
            row.addView(tv1);
        }

        table.addView(row);

        // LE NB DE PUTTS
        name = new TextView(this);
        name.setText("Putts");
        name.setTextSize(textSize);
        name.setPadding(2, 2, 2, 2);
        name.setTextColor(getResources().getColor(R.color.black));
        name.setBackground(getResources().getDrawable(R.drawable.cell_shape));
        name.setLayoutParams(new TableRow.LayoutParams(0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 3));

        row = new TableRow(this); // creation d'une nouvelle ligne
        row.addView(name);
        for(int i=trou_start;i<=trou_end;i++) {

            tv1 = new TextView(this); // creation cellule
            tv1.setText(Integer.toString(nb_putts[i])); // ajout du texte
            tv1.setTextSize(textSize);
            tv1.setPadding(2, 2, 2, 2);
            tv1.setBackground(getResources().getDrawable(R.drawable.cell_shape));
            tv1.setGravity(Gravity.CENTER); // centrage dans la cellule
            // adaptation de la largeur de colonne a l'ecran :
            tv1.setLayoutParams(new TableRow.LayoutParams(0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1));

            // ajout de la cellule a la ligne
            row.addView(tv1);
        }
        if(calculTotal) {
            tv1 = new TextView(this); // creation cellule
            tv1.setText(Integer.toString(somme_putts)); // ajout du texte
            tv1.setTextSize(textSize);
            tv1.setPadding(2, 2, 2, 2);
            tv1.setTextColor(getResources().getColor(R.color.black));
            tv1.setBackground(getResources().getDrawable(R.drawable.cell_shape));
            tv1.setGravity(Gravity.CENTER); // centrage dans la cellule
            // adaptation de la largeur de colonne a l'ecran :
            tv1.setLayoutParams(new TableRow.LayoutParams(0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 2));

            // ajout de la cellule a la ligne
            row.addView(tv1);
        }

        table.addView(row);
    }
}
