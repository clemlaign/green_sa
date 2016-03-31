package fr.insa_rennes.greensa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import fr.insa_rennes.greensa.database.CoursesLoader;
import fr.insa_rennes.greensa.stats.Stats;
import fr.insa_rennes.greensa.map.MapsActivity;
import fr.insa_rennes.greensa.stats.graphStats;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Chargement des parcours
        if(!CoursesLoader.isLoaded())
            CoursesLoader.loadCoursesFromFile(this, "holes.txt");

        Button newGame = (Button)findViewById(R.id.newGame);
        Button stats = (Button)findViewById(R.id.stats);
        Button help = (Button)findViewById(R.id.help);

        stats.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent activity = new Intent(MainActivity.this, graphStats.class);
                startActivity(activity);
            }

        });

        newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activity = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(activity);
            }
        });
    }
}
