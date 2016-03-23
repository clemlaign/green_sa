package fr.insa_rennes.greensa.map;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

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


        /*ImageButton returnButton = (ImageButton)findViewById(R.id.returnButton);

        returnButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent activity = new Intent(MapsActivity.this, MainActivity.class);
                startActivity(activity);
            }

        });*/
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

        ArrayList<Overlay> mapOverlays = new ArrayList<Overlay>();
        Drawable drawable = this.getResources().getDrawable(android.R.drawable.star_big_on);
        MapOverlay itemizedOverlay = new MapOverlay(drawable, this);
        GeoPoint geoPoint = new GeoPoint((int)48.067650,(int)-1.746380);
        OverlayItem overlayitem = new OverlayItem(geoPoint, "Hello", "Sample Overlay item");

        itemizedOverlay.addOverlay(overlayitem);
        mapOverlays.add(itemizedOverlay);

        //OverlayItem m1 = new OverlayItem(new GeoPoint((int)48.068059, (int)-1.745856), "Trou 1", "1er trou du parcourt");
        //moMap.addOverlay(m1);
        LatLng golf = new LatLng(48.067616, -1.746352);

        mMap.addMarker(new MarkerOptions().position(golf).title("Départ"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(golf));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(19));
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

    }

    protected boolean isRouteDisplayed() {
        return false;
    }

}
