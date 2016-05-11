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
import fr.insa_rennes.greensa.stats.Stats;

public class EndCourseActivity extends Activity {

    private int[] nb_tirs;
    private int[] nb_putts;
    private int[] par;

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
                Intent activity = new Intent(EndCourseActivity.this, Stats.class);
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

        TableLayout table9 = (TableLayout) findViewById(R.id.tabScore9); // on prend le tableau défini dans le layout
        TableLayout table18 = (TableLayout) findViewById(R.id.tabScore18); // on prend le tableau défini dans le layout
        fillTable(table9, 0, 8);

        if(nb_tirs.length > 9) // 18 trous
            fillTable(table18, 9, 17);

    }

    public void fillTable(TableLayout table, int trou_start, int trou_end){
        //TableLayout table = (TableLayout) findViewById(R.id.tabScore); // on prend le tableau défini dans le layout
        TableRow row; // création d'un élément : ligne
        TextView name, tv1; // création des cellules

        name = new TextView(this);
        name.setText("N° trou");
        name.setTextSize(textSize);
        name.setPadding(2, 2, 2, 2);
        name.setTextColor(getResources().getColor(R.color.black));
        name.setBackground(getResources().getDrawable(R.drawable.cell_shape));
        name.setLayoutParams( new TableRow.LayoutParams( 0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 3 ) );

        row = new TableRow(this); // création d'une nouvelle ligne
        row.addView(name);
        for(int i=trou_start;i<=trou_end;i++) {

            tv1 = new TextView(this); // création cellule
            tv1.setText(Integer.toString(i + 1)); // ajout du texte
            tv1.setTextSize(textSize);
            tv1.setPadding(2, 2, 2, 2);
            tv1.setTextColor(getResources().getColor(R.color.black));
            tv1.setBackground(getResources().getDrawable(R.drawable.cell_shape));
            tv1.setGravity(Gravity.CENTER); // centrage dans la cellule
            // adaptation de la largeur de colonne à l'écran :
            tv1.setLayoutParams( new TableRow.LayoutParams( 0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1 ) );

            // ajout de la cellule à la ligne
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

        row = new TableRow(this); // création d'une nouvelle ligne
        row.addView(name);
        for(int i=trou_start;i<=trou_end;i++) {

            tv1 = new TextView(this); // création cellule
            tv1.setText(Integer.toString(par[i])); // ajout du texte
            tv1.setTextSize(textSize);
            tv1.setPadding(2,2,2,2);
            tv1.setBackground(getResources().getDrawable(R.drawable.cell_shape));
            tv1.setGravity(Gravity.CENTER); // centrage dans la cellule
            // adaptation de la largeur de colonne à l'écran :
            tv1.setLayoutParams( new TableRow.LayoutParams( 0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1 ) );

            // ajout de la cellule à la ligne
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

        row = new TableRow(this); // création d'une nouvelle ligne
        row.addView(name);
        for(int i=trou_start;i<=trou_end;i++) {

            tv1 = new TextView(this); // création cellule
            tv1.setText(Integer.toString(nb_tirs[i])); // ajout du texte
            tv1.setTextSize(textSize);
            tv1.setPadding(2, 2, 2, 2);
            tv1.setBackground(getResources().getDrawable(R.drawable.cell_shape));
            tv1.setGravity(Gravity.CENTER); // centrage dans la cellule
            // adaptation de la largeur de colonne à l'écran :
            tv1.setLayoutParams(new TableRow.LayoutParams(0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1));

            // ajout de la cellule à la ligne
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

        row = new TableRow(this); // création d'une nouvelle ligne
        row.addView(name);
        for(int i=trou_start;i<=trou_end;i++) {

            tv1 = new TextView(this); // création cellule
            tv1.setText(Integer.toString(nb_putts[i])); // ajout du texte
            tv1.setTextSize(textSize);
            tv1.setPadding(2, 2, 2, 2);
            tv1.setBackground(getResources().getDrawable(R.drawable.cell_shape));
            tv1.setGravity(Gravity.CENTER); // centrage dans la cellule
            // adaptation de la largeur de colonne à l'écran :
            tv1.setLayoutParams(new TableRow.LayoutParams(0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1));

            // ajout de la cellule à la ligne
            row.addView(tv1);
        }
        table.addView(row);
    }
}
