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

/**
 * Cette classe gere la partie interface de jeu
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    /**
     * Liste deroulante de clubs
     */
    private Spinner clubsSpinner = null;

    /**
     * Liste a choix multiple pour les outils de mesure a afficher
     */
    private MultiSpinner multiSpinner = null;

    /**
     * Bouton d'enregistrement de position de la balle
     */
    private Button enregistrerPos = null;

    /**
     * Bouton d'ajout d'objectif
     */
    private FloatingActionButton fab = null;

    /**
     * Texte contenant le numero du trou actuel
     */
    private TextView header_title = null;

    /**
     * Texte informatif lorsqu'on affiche un outil de mesure
     */
    private TextView infoText = null;

    /**
     * Texte informatif lorqu'on affiche l'intervalle de confiance
     */
    private TextView infoTextIC = null;

    /**
     * Image de la fleche pour le sens du vent
     */
    private ImageView imageWind = null;

    /**
     * Texte contenant le numero du trou suivant
     */
    private TextView nextHole = null;

    /**
     * Marqueur de la position de tir
     */
    private Marker markerPosition;

    /**
     * Marqueur de la position du trou actuel
     */
    private Marker markerHole;

    /**
     * Marqueur de la position de l'objectif
     */
    private Marker markerObjectif;

    /**
     * Parcours joue
     */
    private Course course = null;

    /**
     * Coordonnees du trou actuel
     */
    private LatLng hole;

    /**
     * Coordonnees de la position de tir
     */
    private LatLng positionTir;

    /**
     * Coordonnees de la position de l'objectif
     */
    private LatLng positionObjectif;

    /**
     * Position de l'utilisateur en temps reel
     */
    private LatLng position;

    /**
     * ID du trou actuel
     */
    private int current_hole;

    /**
     * Orientation du vent (degre)
     */
    private float windDegre;

    /**
     * Orientation de la map (degre) : on s'en sert pour l'angle de la fleche du vent
     */
    private float bearing;

    /**
     * Carte GoogleMap
     */
    private GoogleMap mMap;

    /**
     * Objet pour la lecture de la meteo
     */
    private WeatherReader weather;

    /**
     * Objet gerant le GPS
     */
    private GpsLocation gps;

    /**
     * ID du parcours joue
     */
    private int id_course;

    /**
     * Distance entre le trou et la position de tir
     */
    private double distance_hole_positionTir;

    /**
     * Liste deroulante pour les putts a la fin d'un trou
     */
    private Spinner listePutts = null;

    /**
     * Liste deroulante pour les penalites a la fin d'un trou
     */
    private Spinner listePenalites = null;

    /**
     * Boite de dialogue pour rentrer les putts et penalites a la fin d'un trou
     */
    private Dialog dialog;

    /**
     * Nombre de tirs/coups par trou pour l'affichage des scores
     */
    private int[] nb_tirs;

    /**
     * Nombre de putts par trou pour l'affichage des scores
     */
    private int[] putts;

    /**
     * Par du trou pour l'affichage des scores
     */
    private int[] par;

    /**
     * Methode permettant de sauvegarder l'etat de la classe lorsqu'on quitte l'application
     * @param outState Etat actuel
     */
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * Methode d'initialisation des elements de l'interface.
     * Gere les evenements sur les boutons et listes deroulantes
     * @param savedInstanceState
     */
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
                if (mMap != null){
                    if(markerObjectif == null) {
                        positionObjectif = new LatLng((positionTir.latitude + hole.latitude) / 2, (positionTir.longitude + hole.longitude) / 2);
                        markerObjectif = mMap.addMarker(new MarkerOptions().position(positionObjectif).title("Objectif").draggable(true));

                        // On modifie la polyline en ajoutant un 3e point (positionObjectif)
                        List<LatLng> list = new ArrayList<LatLng>();
                        list.add(position);
                        list.add(positionObjectif);
                        list.add(hole);

                        drawOnMap.setPolylinePoints(list);

                        // On calcule les distances
                        drawOnMap.updateLabel("posTir-posObj", Double.toString(MathMap.calculateDistance(positionTir, positionObjectif)) + " m", new LatLng((positionTir.latitude + positionObjectif.latitude) / 2, (positionTir.longitude + positionObjectif.longitude) / 2 + drawOnMap.ECART_TEXTDIST_LINE), Color.WHITE);
                        drawOnMap.updateLabel("posObj-posHole", Double.toString(MathMap.calculateDistance(positionObjectif, hole)) + " m", new LatLng((hole.latitude + positionObjectif.latitude) / 2, (hole.longitude + positionObjectif.longitude) / 2 + drawOnMap.ECART_TEXTDIST_LINE), Color.WHITE);

                        // On affiche les labels utiles
                        drawOnMap.updateLabelsVisibility("posTir-posObj", true);
                        drawOnMap.updateLabelsVisibility("posObj-posHole", true);

                        // On cache les labels inutiles
                        drawOnMap.updateLabelsVisibility("posTir-posHole", false);

                        multiSpinner.setItemSelected(0);

                        fab.setAlpha(0.5f);
                        Toast.makeText(MapsActivity.this, "Vous pouvez tirer après avoir défini votre objectif", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(MapsActivity.this, "Impossible de choisir un objectif, attendez le chargement de la carte", Toast.LENGTH_LONG).show();
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

                drawOnMap.updateLabelsVisibility("", selected[0]);

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

    /**
    * Classe interne GpsLocation<br/>
    * Récupère la lattitude et la longitude de l'appareil<br/>
    * Abonnement/désabonnement permet d'économiser la batterie en activant/désactivant le gps<br/>
    * Toutes les manip à faire suite à un changement de position sont à faire dans la méthode onLocationChanged
    */
    public class GpsLocation implements LocationListener {

        /**
         * Objet gerant le service de localisation
         */
        private LocationManager locationManager;
        //private String provider;

        /**
         * Initialisation du service de localisation
         * @param context
         */
        public GpsLocation(Context context) {
            this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }

        /**
         * Methode appelee lorsque l'utilisateur change de position
         * On garde en memoire sa nouvelle position
         * @param location Position de l'utilisateur
         */
        public void onLocationChanged(Location location) {
            position = new LatLng(location.getLatitude(), location.getLongitude());
        }

        /**
         * Methode appelee lorsque le GPS est desactive
         * @param provider Fournisseur de la localisation
         */
        public void onProviderDisabled(final String provider) {
            //Si le GPS est désactivé on se désabonne
            if(provider.equals(LocationManager.GPS_PROVIDER)){// || provider.equals(LocationManager.NETWORK_PROVIDER)) {
                desabonnementGPS();
            }
        }

        /**
         * Methode appelee lorsque le GPS est active
         * @param provider Fournisseur de la localisation
         */
        public void onProviderEnabled(final String provider) {
            //Si le GPS est activé on s'abonne
            //if(provider.equals(LocationManager.GPS_PROVIDER)){// || provider.equals(LocationManager.NETWORK_PROVIDER)) {
            if("gps".equals(provider)){
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
                    Toast.makeText(MapsActivity.this, "Problème récupération position GPS", Toast.LENGTH_LONG).show();
                }
            }
            /*else if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                provider = LocationManager.NETWORK_PROVIDER;
                try {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                } catch (SecurityException e) {
                    Toast.makeText(MapsActivity.this, "Problème récupération position GPS", Toast.LENGTH_LONG).show();
                }
            }*/
            else
                Toast.makeText(MapsActivity.this, "Impossible d'utiliser le GPS", Toast.LENGTH_LONG).show();
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

        /**
         * Methode pour obtenir la derniere position de l'utilisateur
         * @param provider Fournisseur de la localisation
         * @return Dernieres coordonnees de l'utilisateur connues
         */
        public LatLng lastKnownLocation(String provider){
            try {
                Location location = locationManager.getLastKnownLocation(provider);

                return new LatLng(location.getLatitude(), location.getLongitude());
            } catch(SecurityException e){
            }

            return null;
        }
    }


    /** Methode appelee lorsqu'on reprend l'activite
     * On reactive le GPS
     */
    public void onResume() {
        super.onResume();

        // On s'abonne de nouveau
        gps.abonnementGPS();
    }

    /**
     * Methode appelee lorsque l'activite est suspendue
     * On desactive le GPS pour economiser la batterie
     */
    public void onPause() {
        super.onPause();

        //On appelle la méthode pour se désabonner
        gps.desabonnementGPS();
    }


    /**
     * Manipulation de la carte une fois disponible<br/>
     * Cette methode callback est appelee une fois que la carte est prete a etre utilisee.
     * Contient les Listener lorsqu'on deplace un marqueur ou on fait pivoter la carte.
     * On lance le trou numero un
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false); // Disable Map Toolbar
        mMap.getUiSettings().setZoomControlsEnabled(false); // Disable Zoom Buttons

        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        //On modifie l'orientation de la fleche du vent lorsqu'on fait pivoter la carte
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

    /**
     * Methode pour recuperer la position du point de depart du trou.
     * Signale à l'utilisateur qu'il doit se positionner sur le point de départ avant de commencer
     */
    public void positionDepart(){

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Départ");
        alertDialog.setMessage("Positionnez vous sur le point de départ.");
        alertDialog.setButton("Go !", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                // On récupère la dernière position connue
                position = gps.lastKnownLocation("gps");
                if (position != null) {
                    positionTir = position;

                    markerPosition = mMap.addMarker(new MarkerOptions().position(positionTir).title("Départ"));

                    initialiseCirclesLabels();

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

    /**
     * On enregistre la position de la balle
     * Methode appelee lors du clique sur le bouton "Enregistrer position"
     * @param endOfHole TRUE si on a fini le trou, FALSE sinon
     */
    public void positionSaved(final boolean endOfHole) {

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

                // Si on enregistre le dernier tir, on indique la position de la balle comme étant celle du trou
                if(endOfHole)
                    position = markerHole.getPosition();

                // On calcule la distance
                double distance = addShot(positionTir, positionObjectif, position, 0, 0);

                // On clear la map pour placer de nouveaux markers
                drawOnMap.clear(mMap);

                markerObjectif = null;
                fab.setAlpha(1f);

                positionTir = position;
                positionObjectif = null;

                markerHole = mMap.addMarker(new MarkerOptions().position(hole).title("Trou"));
                markerPosition = mMap.addMarker(new MarkerOptions().position(positionTir).title("Position Tir"));

                initialiseCirclesLabels();

                infoText.setVisibility(TextView.INVISIBLE);
                infoTextIC.setVisibility(TextView.INVISIBLE);

                // On remet les paramètres des tools comme au début
                multiSpinner.unselectAll();
                multiSpinner.setItemSelected(0);

                Toast.makeText(MapsActivity.this, "Distance du tir : "+Double.toString(distance)+ " m", Toast.LENGTH_LONG).show();

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

    /**
     * Methode pour passer au trou suivant.
     * On efface tous les marqueurs précédents et on créé les nouveaux.
     * Si le parcours est fini, on lance l'activite de fin de parcours
     */
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

    /**
     * Boite de dialogue pour enregistrement des putts et penalites.
     * Affiche deux listes deroulantes : putts et penalites
     * @param id ID dialogue
     * @return Boite de dialogue
     */
    protected Dialog onCreateDialog(int id) {

        dialog=new Dialog(MapsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_endofhole);

        if(listePutts == null || listePenalites == null) {
            listePutts = (Spinner) dialog.findViewById(R.id.spinnerPutts);
            listePenalites = (Spinner) dialog.findViewById(R.id.spinnerPenalites);

            List<String> putts = new ArrayList<String>();
            for(int i=0;i<10;i++)
                putts.add(Integer.toString(i));

            List<String> penalites = new ArrayList<String>();
            for(int i=0;i<5;i++)
                penalites.add(Integer.toString(i));

            //Le layout par défaut est android.R.layout.simple_spinner_dropdown_item
            ArrayAdapter<String> adapterPutts = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, putts);
            ArrayAdapter<String> adapterPenalites = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, penalites);

            listePutts.setAdapter(adapterPutts);
            listePenalites.setAdapter(adapterPenalites);
        }

        listePutts.setSelection(0);
        listePenalites.setSelection(0);

        Button enregistrer = (Button)dialog.findViewById(R.id.save);
        enregistrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Si on a pas selectionnée d'objectif, on choisit comme objectif le trou
                if (markerObjectif == null)
                    positionObjectif = hole;

                putts[current_hole] = Integer.parseInt(listePutts.getSelectedItem().toString());
                nb_tirs[current_hole] += Integer.parseInt(listePenalites.getSelectedItem().toString());

                addShot(positionTir, positionObjectif, hole, Integer.parseInt(listePutts.getSelectedItem().toString()), Integer.parseInt(listePenalites.getSelectedItem().toString()));
                // on cache la fenetre
                dialog.dismiss();

                // On peut maintenant changer de trou
                nextHole();
            }
        });

        return dialog;
    }

    /*
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

    */

    /**
     * Methode qui initialise les cercles et labels pour les futurs affichages des outils de mesures.
     * Cette methode est appelee apres avoir fait un clear de la carte
     */
    public void initialiseCirclesLabels(){

        // On calcule la distance tir-trou maintenant car elle n'est pas modifiée
        distance_hole_positionTir = MathMap.calculateDistance(positionTir, hole);

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
            drawOnMap.addCircle(mMap, "positionTir", positionTir, 10 * i, 2, new int[]{0, 17, 36, 117});
        drawOnMap.addCircle(mMap, "positionTir", positionTir, 100, 2, new int[]{70, 64, 157, 40});

        // On crée les cercles autour du trou
        for(int i=1;i<rayons.length;i++)
            drawOnMap.addCircle(mMap, "hole", hole, 10 * i, 2, new int[]{0, 17, 36, 117});
        drawOnMap.addCircle(mMap, "hole", hole, 100, 2, new int[]{70, 64, 157, 40});

        // On crée les cercles qui englobe l'intervalle de confiance
        drawOnMap.addCircle(mMap, "confInterval", positionTir, 0, 5, new int[]{0, 255, 0, 0});
        drawOnMap.addCircle(mMap, "confInterval", positionTir, 0, 5, new int[]{0, 255, 0, 0});

        ////////////////////////////////////////////////////
        // On cree les labels pour afficher les distances //
        ////////////////////////////////////////////////////
        drawOnMap.addLabel(mMap, "0 m", "posTir-posObj", new LatLng(0, 0), Color.WHITE);
        drawOnMap.updateLabelsVisibility("posTir-posObj", false); // Invisible par defaut

        drawOnMap.addLabel(mMap, "0 m", "posObj-posHole", new LatLng(0, 0), Color.WHITE);
        drawOnMap.updateLabelsVisibility("posObj-posHole", false); // Invisible par defaut

        drawOnMap.addLabel(mMap, distance_hole_positionTir + " m", "posTir-posHole", new LatLng((positionTir.latitude + hole.latitude) / 2, (positionTir.longitude + hole.longitude) / 2 + drawOnMap.ECART_TEXTDIST_LINE), Color.WHITE);

        // On crée la ligne pour representer la distance
        drawOnMap.addPolyline(mMap, new LatLng[]{positionTir, hole});
    }

    /**
     * Methode pour enregistrer les tirs/coups dans la base de donnees
     * @param posTir Coordonnees de la position de tir de l'utilisateur
     * @param posBalleTheo Coordonnees de la position de l'objectif
     * @param posBalleReel Coordonnees de la position de la balle apres tir
     * @param putts Nombre de putts entre a la fin du trou
     * @param penalites Nombre de penalites entre a la fin du trou
     * @return La distance du tir en metre
     */
    public double addShot(LatLng posTir, LatLng posBalleTheo, LatLng posBalleReel, int putts, int penalites){

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

        double dist = 0;

        // Si le nb de putts > 0, on ajoute les putts
        // Sinon nb putts = 0 donc on enregistre le dernier coup qui va au trou
        if(putts > 0){
            for(int i=0;i<putts;i++)
                sdao.ajouter(new Shot(id + i, current_hole + 1, id_course, -1, 0, 0, 0, 0, 0, 0, 0, 0, wind, date));
        }
        else {
            // Calcul distance
            dist = MathMap.calculateDistance(posTir, posBalleReel);

            // On n'enregistre pas les distances > 500m (s'il y a un bug, on ne met pas de donnés faussées)
            if(dist < 400) {
                // Calcul l'angle (positionObjectif, positionTir, positionBalleRelle)
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
        }

        // On ajoute des pénalités s'il y en a
        if(penalites > 0){
            for(int i=0;i<penalites;i++)
                sdao.ajouter(new Shot(id + i, current_hole + 1, id_course, -1, 0, 0, 0, 0, 0, 0, 0, 0, wind, date));
        }

        sdao.close();

        return dist;
    }

}
