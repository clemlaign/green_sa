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
 * Classe permettant d'afficher des objets sur la map : lines, circles, labels...
 */
public class drawOnMap {

    /**
     * Objet polyline qui affiche des trais sur la carte
     */
    private static Polyline polyline;

    /**
     * Associe une cle a un texte (Le texte est affiche a l'aide d'un marker)
     */
    private static Map<String, Marker> labelList = new HashMap<String, Marker>();

    /**
     * Associe une cle a une liste de cercles (qui affichent le meme outil de mesure)
     */
    private static Map<String, List<Circle>> circleList = new HashMap<String, List<Circle>>();

    /**
     * Associe une cle (la meme que celle pour la liste de cercles) a une visibilite pour les cercles
     */
    private static Map<String, Boolean> circleVisibility = new HashMap<String, Boolean>();

    /**
     * Couleur de la polyline par defaut
     */
    public static int COLOR = Color.argb(255,187,187,187);

    /**
     * Ecart pour afficher les textes sur le cote de la polyline
     */
    public static final double ECART_TEXTDIST_LINE = 0.0004;

    /**
     * Methode pour supprimer tous les elements affiches sur la carte
     * (Utile lorsqu'on change de trou ou on enregistre la position de la balle
     * @param mMap Carte GoogleMap
     */
    public static void clear(GoogleMap mMap){
        mMap.clear();

        labelList.clear();
        circleList.clear();
        circleVisibility.clear();
    }

    /**
     * Methode pour ajouter des trais sur la carte
     * @param mMap Carte GoogleMap
     * @param tab Tableau de coordonnees par lequels passent les trais
     */
    public static void addPolyline( GoogleMap mMap, LatLng[] tab){
        // Affichage des trais entre les markers
        PolylineOptions polylineOptions = new PolylineOptions();
        for(int i=0;i<tab.length;i++) {
            if(tab[i] != null)
                polylineOptions.add(tab[i]);
        }

        polylineOptions.color(drawOnMap.COLOR);

        // Get back the mutable Polyline
        polyline = mMap.addPolyline(polylineOptions);
    }

    /**
     * Methode pour modifier les trais sur la carte (nouvelles coordonnees)
     * @param list Liste de coordonnees GPS
     */
    public static void setPolylinePoints(List<LatLng> list){
        if(polyline != null)
            polyline.setPoints(list);
    }

    /**
     * Methode pour ajouter un texte sur une coordonnee GPS
     * La cle sert a enregistrer le marker en memoire pour pouvoir modifier le texte par la suite
     * @param mMap Carte GoogleMap
     * @param strText Texte a afficher
     * @param key Cle pour garder le texte en memoire (utile pour modifier le texte)
     * @param position Coordonnees GPS où centrer le texte
     * @param color Couleur du texte
     */
    public static void addLabel(GoogleMap mMap, String strText, String key, LatLng position, int color){
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
                .anchor(0.5f, 1)
                .visible(true);

        labelList.put(key, mMap.addMarker(markerOptions));
    }

    /**
     * Methode pour modifier un texte deja cree
     * @param cle Cle utilisee lors de la creation du texte
     * @param newText Texte a afficher
     * @param newPos Coordonnees GPS où centrer le texte
     * @param color Couleur du texte
     */
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

    /**
     * Methode pour modifier la visibilite d'un texte
     * @param key Cle utilisee lors de la creation du texte
     * @param visibility Visibilite du texte (TRUE=visible, FALSE=invisible)
     */
    public static void updateLabelsVisibility(String key, boolean visibility){
        if(labelList.get(key) != null){
            Marker m = labelList.get(key);
            m.setVisible(visibility);
            labelList.put(key, m);
        }
    }

    /**
     * Methode pour ajouter un cercle sur la carte
     * @param mMap Carte GoogleMap
     * @param key Cle pour modifier le cercle plus tard
     * @param center Coordonnees GPS du centre du cercle
     * @param radius Rayon du cercle (en metre)
     * @param strokeWidth Epaisseur du cercle
     * @param argb Couleur du cercle [alpha, red, green, blue]
     */
    public static void addCircle(GoogleMap mMap, String key, LatLng center, int radius, int strokeWidth, int[] argb){
        Circle circle = mMap.addCircle(new CircleOptions()
                .center(center)
                .radius(radius)
                .strokeColor(Color.argb(255, argb[1], argb[2], argb[3]))
                .strokeWidth(strokeWidth)
                .fillColor(Color.argb(argb[0], argb[1], argb[2], argb[3]))
                .visible(false));

        // On ajoute le cercle dans la liste de la cle key pour pouvoir le modifier plus tard si besoin
        if(circleList.get(key) != null) {
            List list = circleList.get(key);
            list.add(circle);
            circleList.put(key, list);
        }
        else{
            List<Circle> list = new ArrayList<Circle>();
            list.add(circle);
            circleList.put(key, list);
            circleVisibility.put(key, false); // On le decrit comme etant invisible par defaut
        }
    }

    /**
     * Methode pour modifier la visibilite des cercles appartenants a la liste de la cle key
     * @param key Cle de la liste contenant le cercle
     * @param visibility Visibilite du cercle (TRUE=visible, FALSE=invisible)
     */
    public static void updateCircles(String key, boolean visibility){
        // On modifie la visibilite des cercles de cle key si celle-ci est differente
        if(circleList.get(key) != null && circleVisibility.get(key) != visibility) {
            List<Circle> list = circleList.get(key);

            for (Circle circle : list) {
                circle.setVisible(visibility);
            }
            circleVisibility.put(key, visibility);
        }
    }

    /**
     * Methode pour modifier les rayons des cercles appartenants a la liste de la cle key
     * @param key Cle de la liste contenant les cercles a modifier
     * @param radius Tableau des nouveaux rayons des cercles
     */
    public static void updateCircles(String key, double[] radius){
        // On modifie le rayon des cercles de la cle key
        if(circleList.get(key) != null) {
            List<Circle> list = circleList.get(key);

            for(int i=0;i<radius.length;i++) {
                if (list.get(i) != null)
                    list.get(i).setRadius(radius[i]);
            }
        }
    }

}
