package fr.insa_rennes.greensa.map;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import fr.insa_rennes.greensa.MainActivity;
import fr.insa_rennes.greensa.R;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int MY_PERMISSIONS_REQUEST_ACCES_FINE_LOCATION = 1;
    private GoogleMap mMap;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        ImageView homeButton = (ImageView) findViewById(R.id.homeButton);

        homeButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent activity = new Intent(MapsActivity.this, MainActivity.class);
                startActivity(activity);
            }

        });

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
        locationManager.addProximityAlert(48.1199722, -1.6540202, 1000, -1, pending);
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

}
