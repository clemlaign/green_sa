package fr.insa_rennes.greensa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import fr.insa_rennes.greensa.database.graphStats;

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

        returnButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent activity = new Intent(Stats.this, MainActivity.class);
                startActivity(activity);
            }

        });

        distance.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent activity = new Intent(Stats.this, graphStats.class);
                startActivity(activity);
            }

        });
    }
}
