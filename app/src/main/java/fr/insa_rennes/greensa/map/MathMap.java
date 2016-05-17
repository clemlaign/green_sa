package fr.insa_rennes.greensa.map;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Antoine on 01/05/2016.
 * Methodes effectuant divers calculs
 */
public class MathMap {

    public static final int R = 6371; // Radius of the earth

    public static double calculateDistance(LatLng p1, LatLng p2){

        double latDistance = Math.toRadians(p2.latitude - p1.latitude);
        double lonDistance = Math.toRadians(p2.longitude - p1.longitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(p1.latitude)) * Math.cos(Math.toRadians(p2.latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = R * c * 1000; // convert to meters

        dist = (Math.round((dist*10.0))/10.0); // on arrondit au dixième

        return dist;
    }

    // Calcul l'angle (positionObjectif, positionTir, positionBalleRelle)
    public static double calculateAngle(LatLng p1, LatLng p2, LatLng p3){

        double longDelta = p3.longitude - p2.longitude;
        double y = Math.sin(longDelta) * Math.cos(p3.latitude);
        double x = Math.cos(p2.latitude) * Math.sin(p3.latitude) -
                Math.sin(p2.latitude) * Math.cos(p3.latitude) * Math.cos(longDelta);
        double angleP3P2 = Math.atan2(y, x); // Angle p3 p2 par rapport à l'horizontale

        longDelta = p1.longitude - p2.longitude;
        y = Math.sin(longDelta) * Math.cos(p1.latitude);
        x = Math.cos(p2.latitude) * Math.sin(p1.latitude) -
                Math.sin(p2.latitude) * Math.cos(p1.latitude) * Math.cos(longDelta);
        double angleP1P2 = Math.atan2(y, x); // Angle p1 p2 par rapport à l'horizontale

        return Math.toDegrees(angleP1P2 - angleP3P2);
    }
}
