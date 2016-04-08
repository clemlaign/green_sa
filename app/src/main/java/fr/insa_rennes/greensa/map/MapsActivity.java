package fr.insa_rennes.greensa.map;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import fr.insa_rennes.greensa.MainActivity;
import fr.insa_rennes.greensa.R;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

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

    }

    protected boolean isRouteDisplayed() {
        return false;
    }

}
