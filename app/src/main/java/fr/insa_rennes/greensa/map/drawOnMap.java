package fr.insa_rennes.greensa.map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Antoine on 01/05/2016.
 * Classe permettant d'afficher des objets sur la map : lines, circles, labels...
 */
public class drawOnMap {

    private static Polyline polyline;
    private static Map<String, Marker> labelList = new HashMap<String, Marker>();
    private static Map<String, List<Circle>> circleList = new HashMap<String, List<Circle>>();
    private static Map<String, Boolean> circleVisibility = new HashMap<String, Boolean>();
    private static Map<String, MarkerOptions> labelOptionList = new HashMap<String, MarkerOptions>();
    private static boolean labelVisibility = true;

    public static final double ECART_TEXTDIST_LINE = 0.0004;

    public static void clear(GoogleMap mMap){
        mMap.clear();

        labelList.clear();
        circleList.clear();
        circleVisibility.clear();
        labelOptionList.clear();
        labelVisibility = true;
    }

    public static void addPolyline( GoogleMap mMap, LatLng[] tab){
        // Affichage des trais entre les markers
        PolylineOptions polylineOptions = new PolylineOptions();
        for(int i=0;i<tab.length;i++) {
            if(tab[i] != null)
                polylineOptions.add(tab[i]);
        }

        polylineOptions.color(Color.WHITE);

        // Get back the mutable Polyline
        polyline = mMap.addPolyline(polylineOptions);
    }

    public static void setPolylinePoints(List<LatLng> list){
        if(polyline != null)
            polyline.setPoints(list);
    }

    public static void addLabel(GoogleMap mMap, String strText, String cle, LatLng position, int color){
        Rect boundsText = new Rect();

        Paint paintText = new Paint();
        paintText.setTextSize(60);
        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setColor(color);
        paintText.getTextBounds(strText, 0, strText.length(), boundsText);

        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmpText = Bitmap.createBitmap(boundsText.width(), boundsText.height(), conf);

        Canvas canvasText = new Canvas(bmpText);
        canvasText.drawText(strText, canvasText.getWidth() / 2, canvasText.getHeight(), paintText);

        MarkerOptions markerOptions = new MarkerOptions()
                .position(position)
                .icon(BitmapDescriptorFactory.fromBitmap(bmpText))
                .anchor(0.5f, 1);

        labelOptionList.put(cle, markerOptions);
        labelList.put(cle, mMap.addMarker(markerOptions));
    }

    public static void updateLabel(String cle, String newText, LatLng newPos, int color){
        Marker m = labelList.get(cle);

        if(m != null) {
            Rect boundsText = new Rect();

            Paint paintText = new Paint();
            paintText.setTextSize(60);
            paintText.setTextAlign(Paint.Align.CENTER);
            paintText.setColor(Color.WHITE);
            paintText.getTextBounds(newText, 0, newText.length(), boundsText);

            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            Bitmap bmpText = Bitmap.createBitmap(boundsText.width(), boundsText.height(), conf);

            Canvas canvasText = new Canvas(bmpText);
            canvasText.drawText(newText, canvasText.getWidth() / 2, canvasText.getHeight(), paintText);

            m.setPosition(newPos);
            m.setIcon(BitmapDescriptorFactory.fromBitmap(bmpText));
        }
    }

    public static void addCircles(GoogleMap mMap, String key, LatLng center, int radius, int strokeWidth, int[] argb){
        Circle circle = mMap.addCircle(new CircleOptions()
                .center(center)
                .radius(radius)
                .strokeColor(Color.argb(255, argb[1], argb[2], argb[3]))
                .strokeWidth(strokeWidth)
                .fillColor(Color.argb(argb[0], argb[1], argb[2], argb[3]))
                .visible(false));

        // On ajoute le cercle dans la liste de la clé key pour pouvoir le modifier plus tard si besoin
        if(circleList.get(key) != null) {
            List list = circleList.get(key);
            list.add(circle);
            circleList.put(key, list);
        }
        else{
            List<Circle> list = new ArrayList<Circle>();
            list.add(circle);
            circleList.put(key, list);
            circleVisibility.put(key, false); // On le décrit comme étant invisible par défaut
        }
    }

    public static void updateCircles(String key, boolean visibility){
        // On modifie la visibilité des cercles de clé key si celle-ci est differente
        if(circleList.get(key) != null && circleVisibility.get(key) != visibility) {
            List<Circle> list = circleList.get(key);

            for (Circle circle : list) {
                circle.setVisible(visibility);
            }
            circleVisibility.put(key, visibility);
        }
    }

    public static void updateCircles(String key, double[] radius){
        // On modifie le rayon des cercles de la clé key
        if(circleList.get(key) != null) {
            List<Circle> list = circleList.get(key);

            for(int i=0;i<radius.length;i++) {
                if (list.get(i) != null)
                    list.get(i).setRadius(radius[i]);
            }
        }
    }

    public static void updateLabelsVisibility(boolean visibility){
        if(labelVisibility != visibility) {
            for (Map.Entry<String, Marker> entry : labelList.entrySet()) {
                Marker m = entry.getValue();
                m.setVisible(visibility);
                labelList.put(entry.getKey(), m);
            }
            labelVisibility = visibility;
        }
    }

}
