package fr.insa_rennes.greensa.stats;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import fr.insa_rennes.greensa.MainActivity;
import fr.insa_rennes.greensa.R;

/*
Activité page d'accueil des statistiques
 */

public class Stats extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        ImageButton returnButton = (ImageButton)findViewById(R.id.returnButton);
        ImageButton distance = (ImageButton)findViewById(R.id.distanceStats);
        ImageButton angle = (ImageButton)findViewById(R.id.angleStats);

        returnButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent activity = new Intent(Stats.this, MainActivity.class);
                startActivity(activity);
            }

        });

        distance.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent activity = new Intent(Stats.this, graphStats.class);
                activity.putExtra("choix", ChoixStat.Distance);
                startActivity(activity);
            }

        });

        angle.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent activity = new Intent(Stats.this, graphStats.class);
                activity.putExtra("choix", ChoixStat.Angle);
                startActivity(activity);
            }

        });
    }
}
