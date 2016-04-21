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

import java.util.ArrayList;
import java.util.List;

import fr.insa_rennes.greensa.MainActivity;
import fr.insa_rennes.greensa.R;
import fr.insa_rennes.greensa.database.ClubsLoader;
import fr.insa_rennes.greensa.database.CoursesLoader;
import fr.insa_rennes.greensa.database.controller.ShotDAO;
import fr.insa_rennes.greensa.database.model.Club;
import fr.insa_rennes.greensa.database.model.Course;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private Spinner clubsSpinner = null;
    private Spinner toolsSpinner = null;
    private Marker marker;
    private Polyline polyline;

    private Course course = null;
    private LatLng hole;
    private int current_hole;

    private static final int MY_PERMISSIONS_REQUEST_ACCES_FINE_LOCATION = 1;
    private GoogleMap mMap;

    //private LocationManager locationManager;
    private GpsLocation gps;
    private double lattitude;
    private double longitude;

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

        ImageView homeButton = (ImageView) findViewById(R.id.homeButton);
        Button puttButton = (Button)findViewById(R.id.puttButton);
        clubsSpinner = (Spinner)findViewById(R.id.clubsSpinner);
        toolsSpinner = (Spinner)findViewById(R.id.toolsSpinner);

        // Chargement des infos sur le parcours

        // On récupère l'id du parcours
        Intent intent = getIntent();
        int id_course = intent.getIntExtra("id_parcours", 0);

        // On récupère le parcours grace à l'id
        for(Course cTmp : CoursesLoader.getCourses()){
            if(cTmp.getId() == id_course) {
                course = cTmp;
                break;
            }
        }

        // On récupère les coordonnées du 1er trou
        current_hole = 0;
        hole = new LatLng(course.getHoles()[current_hole].getCoordLat(), course.getHoles()[current_hole].getCoordLong());


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

                LatLng golf = new LatLng(48.067616, -1.746352);
                LatLng trou1 = new LatLng(48.068060, -1.745856);

                // On crée le marker
                if(mMap != null){
                    LatLng pos = new LatLng((golf.latitude + trou1.latitude)/2,(golf.longitude + trou1.longitude)/2);
                    marker = mMap.addMarker(new MarkerOptions().position(pos).title("").draggable(true));
                }

                PolylineOptions rectOptions = new PolylineOptions()
                        .add(golf)
                        .add(new LatLng((golf.latitude + trou1.latitude)/2,(golf.longitude + trou1.longitude)/2))  // North of the previous point, but at the same longitude
                        .add(trou1); // Closes the polyline.

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
            lattitude = location.getLatitude();
            longitude = location.getLongitude();

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

                list.add(new LatLng(48.067616, -1.746352));
                list.add(markerDrag.getPosition());
                list.add(new LatLng(48.068060, -1.745856));
                polyline.setPoints(list);
            }
        });

        LatLng golf = new LatLng(48.067616, -1.746352);
        LatLng trou1 = new LatLng(48.068060, -1.745856);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng((golf.latitude + trou1.latitude)/2,(golf.longitude + trou1.longitude)/2))
                .bearing(30)
                .tilt(20)
                .zoom(19)
                .build();

        mMap.addMarker(new MarkerOptions().position(golf).title("Départ"));
        mMap.addMarker(new MarkerOptions().position(trou1).title("Trou1"));
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        //Boucle des coups à écrire
        // Sans gps, appeler ongreen (il faut qu'elle renvoie oui si l'utilisateur a dit oui et inversement)
        //avec gps, holeproximity doit créer une alerte de proximité et du coup appeler onGreen si on on se situe a sur le green
    }

    // Attente du coup suivant
    public void nextShot() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Coup suivant:");
        alertDialog.setMessage("Attendez d'être au niveau de la balle.");
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //ShotDAO s = new ShotDAO(MapsActivity.this);
                //s.open();
                //s.ajouter(new Shot(0,0,0, float coordLat_start, float coordLong_start, float coordLatTheo_end, float getCoordLongTheo_end, float coordLatReal_end, float getCoordLongReal_end));
                //s.close();
                alertDialog.hide();
            }
        });
        alertDialog.show();
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCES_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_ACCES_FINE_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        onGreen();
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //locationManager.addProximityAlert(48.1199722, -1.6540202, 1000, -1, pending);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCES_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    // (lat1, lon1) debut
    // (lat2, lon2) fin
    public void addShot(double lat1, double lat2, double lon1, double lon2){

        ShotDAO sdao = new ShotDAO(this);
        sdao.open();

        // Calcul distance
        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = R * c * 1000; // convert to meters

        // Calcul angle
        double longDelta = lon2 - lon1;
        double y = Math.sin(longDelta) * Math.cos(lat2);
        double x = Math.cos(lat1)*Math.sin(lat2) -
                Math.sin(lat1)*Math.cos(lat2)*Math.cos(longDelta);
        double angle = Math.toDegrees(Math.atan2(y, x));

        //sdao.ajouter(new Shot(id_parcours, id_club, coordLat_start, coordLong_start, coordLatTheo_end, coordLongTheo_end, coordLatReal_end, coordLongReal_end, dist, angle));

        sdao.close();
    }

}
