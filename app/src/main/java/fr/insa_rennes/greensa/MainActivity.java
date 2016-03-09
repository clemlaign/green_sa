package fr.insa_rennes.greensa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import fr.insa_rennes.greensa.database.controller.CourseDAO;
import fr.insa_rennes.greensa.database.model.Course;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        CourseDAO coursebd = new CourseDAO(this);
        coursebd.open();
        coursebd.ajouter(new Course(2,9, 0.5f, 0.5f));
        coursebd.close();
        */

        TextView newGame = (TextView)findViewById(R.id.newGame);
        TextView stats = (TextView)findViewById(R.id.stats);
        TextView help = (TextView)findViewById(R.id.help);

        stats.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent activity = new Intent(MainActivity.this, Stats.class);
                startActivity(activity);
            }

        });
    }
}
