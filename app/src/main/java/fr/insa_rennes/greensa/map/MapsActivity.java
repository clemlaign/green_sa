package fr.insa_rennes.greensa.map;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import fr.insa_rennes.greensa.MainActivity;
import fr.insa_rennes.greensa.R;
import fr.insa_rennes.greensa.WeatherReader;
import fr.insa_rennes.greensa.database.ClubsLoader;
import fr.insa_rennes.greensa.database.CoursesLoader;
import fr.insa_rennes.greensa.database.controller.ShotDAO;
import fr.insa_rennes.greensa.database.model.Club;
import fr.insa_rennes.greensa.database.model.Course;
import fr.insa_rennes.greensa.database.model.Shot;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private Spinner clubsSpinner = null;
    private Spinner toolsSpinner = null;
    private Button enregistrerPos = null;
    private Marker markerPosition;
    private Marker markerHole;
    private Marker markerObjectif;
    private Polyline polyline;

    private Course course = null;
    private LatLng hole;
    private LatLng positionTir;
    private LatLng position;
    private int current_hole;

    private GoogleMap mMap;

    //private LocationManager locationManager;
    private GpsLocation gps;
    private int id_course;

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
        Button puttButton = (Button)findViewById(R.id.puttButton);
        clubsSpinner = (Spinner)findViewById(R.id.clubsSpinner);
        toolsSpinner = (Spinner)findViewById(R.id.toolsSpinner);
        enregistrerPos = (Button)findViewById(R.id.enregistrerPos);

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

        // On récupère les coordonnées du 1er trou
        current_hole = -1;

        homeButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent activity = new Intent(MapsActivity.this, MainActivity.class);
                startActivity(activity);
            }

        });

        puttButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

            }

        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // On crée le marker
                if(mMap != null && markerObjectif == null){
                    LatLng pos = new LatLng((position.latitude + hole.latitude)/2,(position.longitude + hole.longitude)/2);
                    markerObjectif = mMap.addMarker(new MarkerOptions().position(pos).title("").draggable(true));
                }

                PolylineOptions rectOptions = new PolylineOptions()
                        .add(position)
                        .add(new LatLng((position.latitude + hole.latitude) / 2, (position.longitude + hole.longitude) / 2))  // North of the previous point, but at the same longitude
                        .add(hole); // Closes the polyline.

                rectOptions.color(getResources().getColor(R.color.white));
                // Get back the mutable Polyline
                polyline = mMap.addPolyline(rectOptions);
            }
        });

        // On remplit le spinner club
        List<String> list = new ArrayList<String>();
        for(Club club : ClubsLoader.getClubs()){
            list.add(club.getName());
        }

        ArrayAdapter<String> adapterClubs = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, list);
        clubsSpinner.setAdapter(adapterClubs);

        // Bouton position enregistrée
        enregistrerPos.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                positionSaved();
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

            String myLocation = "Latitude = " + location.getLatitude() + " Longitude = " + location.getLongitude();
            //Toast.makeText(this, myLocation, Toast.LENGTH_LONG).show();
            //I make a log to see the results
            System.out.println(myLocation);
        }

        public void onProviderDisabled(final String provider) {
            //Si le GPS est désactivé on se désabonne
            System.out.println("TEST1\n\n\n");
            if("gps".equals(provider)) {
                desabonnementGPS();
            }
        }

        public void onProviderEnabled(final String provider) {
            //Si le GPS est activé on s'abonne
            System.out.println("TEST2\n\n\n");
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
                    //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                } catch (SecurityException e) {
                    System.out.println(e.getMessage());
                }

            }
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

        // Lorsqu'on déplace un marqueur sur la map
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            // On commence le déplacement
            public void onMarkerDragStart(Marker markerDragStart) { }

            // On a finit le déplacement
            public void onMarkerDragEnd(Marker markerDragEnd) { }

            // En cours de déplacement
            // On met à jour les traits des distances entre les markers
            public void onMarkerDrag(Marker markerDrag) {
                List<LatLng> list = new ArrayList<LatLng>();

                list.add(position);
                list.add(markerDrag.getPosition());
                list.add(hole);
                polyline.setPoints(list);
            }
        });

        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        nextHole();
        // Sans gps, appeler ongreen (il faut qu'elle renvoie oui si l'utilisateur a dit oui et inversement)
        //avec gps, holeproximity doit créer une alerte de proximité et du coup appeler onGreen si on on se situe a sur le green
    }

    // Signaler à l'utilisateur qu'il doit se positionner sur le point de départ avant de commencer
    public void positionDepart(){
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Départ");
        alertDialog.setMessage("Positionnez vous sur le point de départ.");
        alertDialog.setButton("Go !", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                positionTir = position;
                markerPosition = mMap.addMarker(new MarkerOptions().position(positionTir).title("Position"));
                alertDialog.hide();
            }
        });
        alertDialog.show();
    }

    // On enregistre la position: prêt pour le tir
    public void positionSaved() {

        mMap.clear();
        markerHole = mMap.addMarker(new MarkerOptions().position(hole).title("Trou"));        positionTir = position;
        markerPosition = mMap.addMarker(new MarkerOptions().position(positionTir).title("Position"));

        Toast.makeText(this, "Position enregistrée, vous pouvez tirer !", Toast.LENGTH_LONG);

    }

    //Trou suivant
    //On efface tous les marqueurs précédents et on créé les nouveaux
    public void nextHole() {
        current_hole ++;
        hole = new LatLng(course.getHoles()[current_hole].getCoordLat(), course.getHoles()[current_hole].getCoordLong());

        mMap.clear();

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng((position.latitude + hole.latitude)/2,(position.longitude + hole.longitude)/2))
                .bearing(30)
                .tilt(20)
                .zoom(19)
                .build();

        markerObjectif = null;
        markerHole = mMap.addMarker(new MarkerOptions().position(hole).title("Trou"));
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        positionDepart();
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
        } catch (SecurityException e) {}
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

    // (lat1, lon1) debut (avant tir)
    // (lat2, lon2) fin (après tir)
    public void addShot(LatLng posTir, LatLng posBalleTheo, LatLng posBalleReel){

        ShotDAO sdao = new ShotDAO(this);
        sdao.open();

        // Calcul distance
        // On enregistre les infos entre posTir et posBalleReel
        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(posBalleReel.latitude - posTir.latitude);
        double lonDistance = Math.toRadians(posBalleReel.longitude - posTir.longitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(posTir.latitude)) * Math.cos(Math.toRadians(posBalleReel.latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = R * c * 1000; // convert to meters

        // Calcul angle
        double longDelta = posBalleReel.longitude - posTir.longitude;
        double y = Math.sin(longDelta) * Math.cos(posBalleReel.latitude);
        double x = Math.cos(posTir.latitude)*Math.sin(posBalleReel.latitude) -
                Math.sin(posTir.latitude)*Math.cos(posBalleReel.latitude)*Math.cos(longDelta);
        double angle = Math.toDegrees(Math.atan2(y, x));

        // On récupère le club selectionné
        int id_club = 0;
        String club = (String) clubsSpinner.getSelectedItem();

        for(Club cl : ClubsLoader.getClubs()){
            if(cl.getName().equals(club)) {
                id_club = cl.getId();
                break;
            }
        }

        // On récupère les infos sur le vent (vitesse + degrée)
        JSONObject json = WeatherReader.read(posTir.latitude, posTir.longitude);
        String wind = null;
        try {
            wind = json.getJSONObject("wind").get("speed") + " " + json.getJSONObject("wind").get("deg");
        } catch (JSONException e){

        }

        // On récupère la date courante
        Date actuelle = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String date = dateFormat.format(actuelle);

        sdao.ajouter(new Shot(current_hole + 1, id_course, id_club, posTir.latitude, posTir.longitude, posBalleTheo.latitude, posBalleTheo.latitude, posBalleReel.latitude, posBalleReel.longitude, dist, angle, wind, date));

        sdao.close();
    }

}
