package fr.insa_rennes.greensa.map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.insa_rennes.greensa.MainActivity;
import fr.insa_rennes.greensa.R;
import fr.insa_rennes.greensa.utility.AsyncResponse;
import fr.insa_rennes.greensa.utility.WeatherReader;
import fr.insa_rennes.greensa.database.ClubsLoader;
import fr.insa_rennes.greensa.database.CoursesLoader;
import fr.insa_rennes.greensa.database.controller.ShotDAO;
import fr.insa_rennes.greensa.database.model.Club;
import fr.insa_rennes.greensa.database.model.Course;
import fr.insa_rennes.greensa.database.model.Shot;
import fr.insa_rennes.greensa.utility.MultiSpinner;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private Spinner clubsSpinner = null;
    private MultiSpinner multiSpinner = null;
    private Button enregistrerPos = null;
    private FloatingActionButton fab = null;
    private TextView header_title = null;
    private TextView infoText = null;
    private TextView infoTextIC = null;
    private ImageView imageWind = null;
    private TextView nextHole = null;
    private Marker markerPosition;
    private Marker markerHole;
    private Marker markerObjectif;

    private Course course = null;
    private LatLng hole;
    private LatLng positionTir;
    private LatLng positionObjectif;
    private LatLng position;
    private int current_hole;

    private float windDegre;
    private float bearing; // Orientation de la map (degré) : on s'en sert pour l'angle de la fleche du vent
    private GoogleMap mMap;
    private WeatherReader weather;
    private GpsLocation gps;
    private int id_course;

    private double distance_hole_positionTir;

    private Spinner listePutts = null;
    private Dialog dialog;

    // Pour l'affichage du tableau des scores
    int[] nb_tirs;
    int[] putts;
    int[] par;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // On intialise le GPS
        gps = new GpsLocation(this);

        ImageView homeButton = (ImageView)findViewById(R.id.homeButton);
        LinearLayout nextHoleLayout = (LinearLayout)findViewById(R.id.nextHoleLayout);
        nextHole = (TextView)findViewById(R.id.nextHole);
        clubsSpinner = (Spinner)findViewById(R.id.clubsSpinner);
        multiSpinner = (MultiSpinner) findViewById(R.id.toolsSpinner);
        enregistrerPos = (Button)findViewById(R.id.enregistrerPos);
        header_title = (TextView)findViewById(R.id.heading_text);
        infoText = (TextView)findViewById(R.id.infoText); // contient la légende sur les rayons des cercles de distance
        infoTextIC = (TextView)findViewById(R.id.infoTextIC); // contient les infos sur l'Intervalle de Conf

        imageWind = (ImageView)findViewById(R.id.arrow_wind);

        // Chargement des infos sur le parcours

        // On récupère l'id du parcours
        Intent intent = getIntent();
        id_course = intent.getIntExtra("id_parcours", 0);

        // On récupère le parcours grace à l'id
        for(Course cTmp : CoursesLoader.getCourses()){
            if(cTmp.getId() == id_course) {
                course = cTmp;
                break;
            }
        }

        nb_tirs = new int[course.getHoles().length];
        putts = new int[course.getHoles().length];
        par = new int[course.getHoles().length];

        // On récupère les coordonnées du 1er trou
        current_hole = -1;

        homeButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MapsActivity.this);
                alertDialogBuilder
                        .setTitle("Quitter")
                        .setMessage("Êtes-vous bien sur d'arrêter le parcours ?")
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent activity = new Intent(MapsActivity.this, MainActivity.class);
                                startActivity(activity);
                            }
                        })
                        .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
            }

        });

        nextHoleLayout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                showDialog(1);
            }

        });

        // Bouton position enregistrée
        enregistrerPos.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                positionSaved(false);
            }

        });

        // Bouton flottant pour ajouter un markerObjectif
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                // On crée le markerObjectif si la map est chargée et qu'il n'y a pas de markerObjectif
                if (mMap != null && markerObjectif == null) {
                    positionObjectif = new LatLng((positionTir.latitude + hole.latitude) / 2, (positionTir.longitude + hole.longitude) / 2);
                    markerObjectif = mMap.addMarker(new MarkerOptions().position(positionObjectif).title("Objectif").draggable(true));

                    drawOnMap.addPolyline(mMap, new LatLng[]{positionTir, positionObjectif, hole});
                    drawOnMap.addLabel(mMap, Double.toString(MathMap.calculateDistance(positionTir, positionObjectif)) + " m", "posTir-posObj", new LatLng((positionTir.latitude + positionObjectif.latitude) / 2, (positionTir.longitude + positionObjectif.longitude) / 2 + drawOnMap.ECART_TEXTDIST_LINE), Color.WHITE);
                    drawOnMap.addLabel(mMap, Double.toString(MathMap.calculateDistance(positionObjectif, hole)) + " m", "posObj-posHole", new LatLng((hole.latitude + positionObjectif.latitude) / 2, (hole.longitude + positionObjectif.longitude) / 2 + drawOnMap.ECART_TEXTDIST_LINE), Color.WHITE);

                    multiSpinner.setItemSelected(0);

                    fab.setAlpha(0.5f);
                    Toast.makeText(MapsActivity.this, "Vous pouvez tirer après avoir défini votre objectif", Toast.LENGTH_LONG).show();
                }
            }
        });



        // On remplit le spinner club
        List<String> list = new ArrayList<String>();
        for(Club club : ClubsLoader.getClubs()){
            list.add(club.getName());
        }

        ArrayAdapter<String> adapterClubs = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, list);
        clubsSpinner.setAdapter(adapterClubs);

        clubsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // On récupère le club selectionné
                int id_club = 0;
                String club = (String) clubsSpinner.getSelectedItem();

                for (Club cl : ClubsLoader.getClubs()) {
                    if (cl.getName().equals(club)) {
                        id_club = cl.getId();
                        break;
                    }
                }

                // On recupère les tirs suivant les infos
                ShotDAO sdao = new ShotDAO(MapsActivity.this);
                sdao.open();
                List<Shot> listShots = sdao.selectElements("SELECT * FROM Shot WHERE distance <> 0 AND id_club=?", new String[]{Integer.toString(id_club)});
                sdao.close();

                // si pas de tirs enregistrés avec ce club
                if(listShots.size() != 0) {
                    float moyenne = 0;
                    float variance = 0;
                    for (Shot s : listShots) {
                        moyenne += s.getDistance();
                    }
                    moyenne /= listShots.size();

                    for (Shot s : listShots) {
                        variance += Math.pow(s.getDistance() - moyenne, 2);
                    }
                    variance /= listShots.size();

                    // Indice de confiance avec une loi normale à 80%
                    double var = 1.2816 * (variance / Math.sqrt(listShots.size()));

                    int min = (int) (moyenne - var);// on arrondit à l'unité
                    int max = (int) (moyenne + var);// on arrondit à l'unité

                    if(min < 0)
                        min = 0;

                    drawOnMap.updateCircles("confInterval", new double[]{min, max});

                    infoTextIC.setText("IC : " + min + " à " + max + " m");
                }
                else if(infoTextIC.getVisibility() == TextView.VISIBLE) { // si l'intervalle de conf est affiché
                    Toast.makeText(MapsActivity.this, "Vous n'avez pas encore tiré avec ce club", Toast.LENGTH_SHORT).show();
                    drawOnMap.updateCircles("confInterval", false);
                    infoTextIC.setText("IC : 0m - 0m");
                }
                else {
                    infoTextIC.setText("IC : 0m - 0m");
                    drawOnMap.updateCircles("confInterval", false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        List<String> tools = new ArrayList<String>();
        tools.add("Afficher distances");
        tools.add("Afficher cercles / trou");
        tools.add("Afficher cercles / position actuelle");
        tools.add("Afficher intervalle de confiance à 80%");


        multiSpinner.setItems(tools, "Outils", new MultiSpinner.MultiSpinnerListener() {

            public void onItemsSelected(boolean[] selected) {

                drawOnMap.updateLabelsVisibility(selected[0]);

                drawOnMap.updateCircles("hole", selected[1]);
                drawOnMap.updateCircles("positionTir", selected[2]);
                drawOnMap.updateCircles("confInterval", selected[3]);
                if(selected[1] || selected[2])
                    infoText.setVisibility(TextView.VISIBLE);
                else
                    infoText.setVisibility(TextView.INVISIBLE);

                if(selected[3])
                    infoTextIC.setVisibility(TextView.VISIBLE);
                else
                    infoTextIC.setVisibility(TextView.INVISIBLE);
            }
        });
    }

    /*
    * Classe interne GpsLocation
    * Récupère la lattitude et la longitude de l'appareil
    * Abonnement/désabonnement permet d'économiser la batterie en activant/désactivant le gps
    * Toutes les manip à faire suite à un changement de position sont à faire dans la méthode onLocationChanged
    */
    public class GpsLocation implements LocationListener {

        private LocationManager locationManager;

        public GpsLocation(Context context) {
            this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }

        // Lorsque la position change
        public void onLocationChanged(Location location) {
            position = new LatLng(location.getLatitude(), location.getLongitude());
        }

        public void onProviderDisabled(final String provider) {
            //Si le GPS est désactivé on se désabonne
            if("gps".equals(provider)) {
                desabonnementGPS();
            }
        }

        public void onProviderEnabled(final String provider) {
            //Si le GPS est activé on s'abonne
            if("gps".equals(provider)) {
                abonnementGPS();
            }
        }

        public void onStatusChanged(final String provider, final int status, final Bundle extras) { }

        /**
         * Méthode permettant de s'abonner à la localisation par GPS.
         */
        public void abonnementGPS() {
            //Si le GPS est disponible, on s'y abonne

            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                try {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                } catch (SecurityException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        public Location lastKnownLocation(String provider){
            try {
                return locationManager.getLastKnownLocation(provider);
            } catch(SecurityException e){
            }

            return null;
        }

        /**
         * Méthode permettant de se désabonner de la localisation par GPS.
         */
        public void desabonnementGPS() {
            // On se désabonne
            try{
                locationManager.removeUpdates(this);
            } catch (SecurityException e) {

            }
        }
    }


    public void onResume() {
        super.onResume();

        // On s'abonne de nouveau
        gps.abonnementGPS();
    }
    @Override
    public void onPause() {
        super.onPause();

        //On appelle la méthode pour se désabonner
        gps.desabonnementGPS();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false); // Disable Map Toolbar
        mMap.getUiSettings().setZoomControlsEnabled(false); // Disable Zoom Buttons

        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if (bearing != cameraPosition.bearing) {
                    bearing = cameraPosition.bearing;
                    imageWind.setRotation(windDegre - bearing);
                    System.out.println(bearing);
                }
            }
        });

        // Lorsqu'on déplace un marqueur sur la map
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            // On commence le déplacement
            public void onMarkerDragStart(Marker markerDragStart) {
            }

            // On a finit le déplacement
            public void onMarkerDragEnd(Marker markerDragEnd) {
            }

            // En cours de déplacement
            // On met à jour les traits des distances entre les markers ainsi que les textes contenants les distances
            public void onMarkerDrag(Marker markerDrag) {
                List<LatLng> list = new ArrayList<LatLng>();

                positionObjectif = markerDrag.getPosition();

                list.add(position);
                list.add(positionObjectif);
                list.add(hole);

                drawOnMap.setPolylinePoints(list);
                drawOnMap.updateLabel("posTir-posObj", Double.toString(MathMap.calculateDistance(positionTir, positionObjectif)) + " m", new LatLng((positionTir.latitude + positionObjectif.latitude) / 2, (positionTir.longitude + positionObjectif.longitude) / 2 + drawOnMap.ECART_TEXTDIST_LINE), Color.WHITE);
                drawOnMap.updateLabel("posObj-posHole", Double.toString(MathMap.calculateDistance(positionObjectif, hole)) + " m", new LatLng((hole.latitude + positionObjectif.latitude) / 2, (hole.longitude + positionObjectif.longitude) / 2 + drawOnMap.ECART_TEXTDIST_LINE), Color.WHITE);
            }
        });

        nextHole();
    }

    // Signaler à l'utilisateur qu'il doit se positionner sur le point de départ avant de commencer
    public void positionDepart(){

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Départ");
        alertDialog.setMessage("Positionnez vous sur le point de départ.");
        alertDialog.setButton("Go !", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                // On récupère la dernière position connue
                Location loc = gps.lastKnownLocation("gps");
                if (loc != null)
                    position = new LatLng(loc.getLatitude(), loc.getLongitude());
                if (position != null) {
                    positionTir = position;
                    distance_hole_positionTir = MathMap.calculateDistance(positionTir, hole);

                    markerPosition = mMap.addMarker(new MarkerOptions().position(positionTir).title("Départ"));

                    initialiseCircles();

                    // On peut mettre à jour la caméra ! On la centre entre le départ et le trou
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng((positionTir.latitude + hole.latitude) / 2, (positionTir.longitude + hole.longitude) / 2))
                            .bearing(bearing)
                            .tilt(0)
                            .zoom(17)
                            .build();

                    // On applique les paramètres de la caméra
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    weather = new WeatherReader(new AsyncResponse() {

                        // On determine le temps une 1ere fois
                        //Ensuite on l'actualise à chaque enregistrement de position
                        // On modifie le TextView contenant les infos sur le vent
                        public void processFinish(JSONObject output) {
                            try {
                                TextView weather_text = (TextView) findViewById(R.id.windText);
                                double kmh = (Math.round(Double.parseDouble(output.getJSONObject("wind").get("speed").toString()) * 3.6 * 10.0) / 10.0); // km/h = m/s * 3.6

                                weather_text.setText(kmh + " km/h");
                                windDegre = (float) Double.parseDouble(output.getJSONObject("wind").get("deg").toString());
                                imageWind.setRotation(windDegre);

                            } catch (JSONException e) {

                            }
                        }
                    });
                    weather.execute(positionTir.latitude, positionTir.longitude);

                    alertDialog.hide();
                }
            }
        });
        alertDialog.show();
    }

    // On enregistre la position: prêt pour le tir
    // endOfHole = true si on a fini le trou
    public void positionSaved(boolean endOfHole) {

        // Si on enregistre le dernier tir, on indique la position de la balle comme étant celle du trou
        if(endOfHole)
            position = markerHole.getPosition();

        // Si on a pas selectionnée d'objectif, on choisit comme objectif le trou
        if(markerObjectif == null)
            positionObjectif = hole;

        // On ajoute un tir en plus
        nb_tirs[current_hole]++;

        // On crée une alertDialog pour confirmer la position
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MapsActivity.this);
        alertDialogBuilder
            .setTitle("Enregistrer position")
            .setMessage("Êtes-vous bien sur la position de la balle ?")
            .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                addShot(positionTir, positionObjectif, position, 0);

                drawOnMap.clear(mMap);

                markerObjectif = null;
                fab.setAlpha(1f);

                positionTir = position;
                positionObjectif = null;

                markerHole = mMap.addMarker(new MarkerOptions().position(hole).title("Trou"));
                markerPosition = mMap.addMarker(new MarkerOptions().position(positionTir).title("Position Tir"));

                distance_hole_positionTir = MathMap.calculateDistance(positionTir, hole);

                initialiseCircles();

                infoText.setVisibility(TextView.INVISIBLE);
                infoTextIC.setVisibility(TextView.INVISIBLE);

                // On remet les paramètres des tools comme au début
                multiSpinner.unselectAll();
                multiSpinner.setItemSelected(0);

                //On actualise le vent à chaque enregistrement de position
                /*weather = new WeatherReader(new AsyncResponse() {

                    // On modifie le TextView contenant les infos sur le vent
                    public void processFinish(JSONObject output) {
                        try {
                            TextView weather_text = (TextView) findViewById(R.id.windText);
                            double kmh = (Math.round(Double.parseDouble(output.getJSONObject("wind").get("speed").toString()) * 3.6*10.0)/10.0); // km/h = m/s * 3.6

                            weather_text.setText(kmh + " km/h");

                            windDegre = (float)Double.parseDouble(output.getJSONObject("wind").get("deg").toString());
                            imageWind.setRotation(windDegre);

                        } catch (JSONException e){
                            System.out.println(e.getMessage()+"\n\n");
                        }
                    }
                });
                weather.execute(positionTir.latitude, positionTir.longitude);
*/
                Toast.makeText(MapsActivity.this, "Position enregistrée !", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
                    }
            })
            .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    //Trou suivant
    //On efface tous les marqueurs précédents et on créé les nouveaux
    public void nextHole() {

        current_hole++;
        // Si on a pas atteint le dernier trou
        if(current_hole < course.getHoles().length) {
            hole = new LatLng(course.getHoles()[current_hole].getCoordLat(), course.getHoles()[current_hole].getCoordLong());

            par[current_hole] = course.getHoles()[current_hole].getPar();

            drawOnMap.clear(mMap);

            multiSpinner.unselectAll();
            multiSpinner.setItemSelected(0);

            bearing = 0;
            // Au début on centre la camera sur le trou
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(hole)
                    .bearing(bearing)
                    .tilt(0)
                    .zoom(17)
                    .build();

            // On applique les paramètres de la caméra
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            markerObjectif = null;
            markerHole = mMap.addMarker(new MarkerOptions().position(hole).title("Trou"));

            header_title.setText("Trou "+Integer.toString(current_hole+1));
            infoText.setVisibility(TextView.INVISIBLE);
            infoTextIC.setVisibility(TextView.INVISIBLE);

            // On modifie le texte indiquant le prochain trou
            if(current_hole + 1 == course.getHoles().length)
                nextHole.setText("Fin");
            else
                nextHole.setText("Trou "+(current_hole+2));

            fab.setAlpha(1f);

            // On remet les paramètres des tools comme au début
            multiSpinner.unselectAll();
            multiSpinner.setItemSelected(0);

            positionDepart();
        }
        else{
            // on change d'activité
            Intent activity = new Intent(MapsActivity.this, EndCourseActivity.class);
            activity.putExtra("nb_tirs", nb_tirs);
            activity.putExtra("putts", putts);
            activity.putExtra("par", par);
            startActivity(activity);
        }
    }

    // Boite de dialogue pour enregistrement des putts
    protected Dialog onCreateDialog(int id) {

        dialog=new Dialog(MapsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_endofhole);

        if(listePutts == null) {
            listePutts = (Spinner) dialog.findViewById(R.id.spinnerPutts);

            List<String> putts = new ArrayList<String>();
            for(int i=0;i<10;i++)
                putts.add(Integer.toString(i));

            //Le layout par défaut est android.R.layout.simple_spinner_dropdown_item
            ArrayAdapter<String> adapterPutts = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, putts);

            listePutts.setAdapter(adapterPutts);
        }

        Button enregistrer = (Button)dialog.findViewById(R.id.save);
        enregistrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Si on a pas selectionnée d'objectif, on choisit comme objectif le trou
                if (markerObjectif == null)
                    positionObjectif = hole;

                putts[current_hole] = Integer.parseInt(listePutts.getSelectedItem().toString());

                addShot(positionTir, positionObjectif, hole, Integer.parseInt(listePutts.getSelectedItem().toString()));
                // on cache la fenetre
                dialog.dismiss();

                // On peut maintenant changer de trou
                nextHole();
            }
        });

        return dialog;
    }



    // Appelée par la AlertReceiver
    public void onGreen() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("");
        alertDialog.setMessage("Etes-vous sur le green?");
        alertDialog.setButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.hide();
            }
        });
        alertDialog.setButton2("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.hide();
            }
        });
        alertDialog.show();
    }

    // Si on s'approche du green
    public void holeProximity() {
        onGreen();
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            gps.locationManager.addProximityAlert(hole.latitude, hole.longitude, 1000, -1, pending);
        } catch (SecurityException e) {

        }
    }

    // Sous classe utilisée par la classe HoleProximity
    private class AlertReceiver {

        public void onReceive(Context context, Intent intent) {
            // Vaudra true par défaut si on ne trouve pas l'extra booléen dont la clé est LocationManager.KEY_PROXIMITY_ENTERING
            boolean entrer = intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, true);
            if(entrer){
                onGreen();
            }
        }
    }

    public void initialiseCircles(){
        // On définit les rayons - les positions sont fixes
        int rayons[];
        if(distance_hole_positionTir > 80) {
            rayons = new int[]{10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
            infoText.setText("Cercle bleu tous les 10 m");
        }
        else{
            rayons = new int[]{5,10,15,20,25,30,35,40,45,50};
            infoText.setText("Cercle bleu tous les 5 m");
        }

        // On crée les cercles autour du joueur
        for(int i=1;i<rayons.length;i++)
            drawOnMap.addCircles(mMap, "positionTir", positionTir, 10*i, 2, new int[]{0, 17, 36, 117});
        drawOnMap.addCircles(mMap, "positionTir", positionTir, 100, 2, new int[]{70, 64, 157, 40});

        // On crée les cercles autour du trou
        for(int i=1;i<rayons.length;i++)
            drawOnMap.addCircles(mMap, "hole", hole, 10*i, 2, new int[]{0, 17, 36, 117});
        drawOnMap.addCircles(mMap, "hole", hole, 100, 2, new int[]{70, 64, 157, 40});

        // On crée les cercles qui englobe l'intervalle de confiance
        drawOnMap.addCircles(mMap, "confInterval", positionTir, 0, 5, new int[]{0, 255, 0,0});
        drawOnMap.addCircles(mMap, "confInterval", positionTir, 0, 5, new int[]{0, 255, 0, 0});

        // On crée les labels associés à l'intervalle de conf
        //drawOnMap.addLabel(mMap, "0", "confIntervalMin", new LatLng(0,0), Color.RED);
        //drawOnMap.addLabel(mMap, "0", "confIntervalMax", new LatLng(0,0), Color.RED);
    }

    // (lat1, lon1) debut (avant tir)
    // (lat2, lon2) fin (après tir)
    public void addShot(LatLng posTir, LatLng posBalleTheo, LatLng posBalleReel, int putts){

        // On ouvre la bdd
        ShotDAO sdao = new ShotDAO(this);
        sdao.open();

        // On récupère le vent
        String wind = null;
        try {
            wind = WeatherReader.weather.getJSONObject("wind").get("speed") + " " + WeatherReader.weather.getJSONObject("wind").get("deg");
        } catch (JSONException e) {

        }

        // Pour obtenir l'id du nouveau parcours, on prend celui du precedent +1
        List<Shot> list = sdao.selectElements("SELECT * FROM Shot", null);
        int id = 0;
        if (list.size() != 0)
            id = list.get(list.size() - 1).getId_course_hole() + 1;

        // On récupère la date courante
        Date actuelle = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String date = dateFormat.format(actuelle);

        // Si le nb de putts > 0, on ajoute les putts
        // Sinon nb putts = 0 donc on enregistre le dernier coup qui va au trou
        if(putts > 0){
            for(int i=0;i<putts;i++)
                sdao.ajouter(new Shot(id + i, current_hole + 1, id_course, -1, 0, 0, 0, 0, 0, 0, 0, 0, wind, date));
        }
        else {
            // Calcul distance
            // On enregistre les infos entre posTir et posBalleReel
            /*final int R = 6371; // Radius of the earth

            double latDistance = Math.toRadians(posBalleReel.latitude - posTir.latitude);
            double lonDistance = Math.toRadians(posBalleReel.longitude - posTir.longitude);
            double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                    + Math.cos(Math.toRadians(posTir.latitude)) * Math.cos(Math.toRadians(posBalleReel.latitude))
                    * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double dist = R * c * 1000; // convert to meters*/

            double dist = MathMap.calculateDistance(posTir, posBalleReel);

            // Calcul angle
            /*double longDelta = posBalleReel.longitude - posTir.longitude;
            double y = Math.sin(longDelta) * Math.cos(posBalleReel.latitude);
            double x = Math.cos(posTir.latitude) * Math.sin(posBalleReel.latitude) -
                    Math.sin(posTir.latitude) * Math.cos(posBalleReel.latitude) * Math.cos(longDelta);
            double angle = Math.toDegrees(Math.atan2(y, x));*/

            double angle = MathMap.calculateAngle(posBalleTheo, posTir, posBalleReel);

            // On récupère le club selectionné
            int id_club = 0;
            String club = (String) clubsSpinner.getSelectedItem();

            for (Club cl : ClubsLoader.getClubs()) {
                if (cl.getName().equals(club)) {
                    id_club = cl.getId();
                    break;
                }
            }

            sdao.ajouter(new Shot(id, current_hole + 1, id_course, id_club, posTir.latitude, posTir.longitude, posBalleTheo.latitude, posBalleTheo.latitude, posBalleReel.latitude, posBalleReel.longitude, dist, angle, wind, date));
        }

        sdao.close();
    }

}
