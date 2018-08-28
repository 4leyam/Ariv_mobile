package cg.code.aleyam.nzela_nzela.actu.connexion;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class PositionManager {


    public static final int RADIUS_3KM = 3 , RADIUS_1KM = 1 , RADIUS_10KM = 10 ;
    private double lat1 = 0.009d , lat3 = 0.027d , lat10 = 0.090;
    private static int radius = 1;

    private static PositionManager instance = null;

    public static PositionManager getInstance(Context context) {
        radius = getCurrentRadius(context);
        if(instance == null) {
            instance = new PositionManager();
        }
        return instance;
    }

    private PositionManager() {

    }

    /**
     * methode permettant de recuperer tous les espaces de stockage des evenements compris dans le
     * rayon d'action courant.
     * @param latLng
     * @return
     */
    public ArrayList<String> getLongitudeLine( LatLng latLng ) {
        String lngCenter = Upload.initLongitude(latLng.longitude);
        ArrayList<String> points = new ArrayList<>();
        //on commmence par un parcequ'on ne peut pas faire une recherche sur 0km
        points.add(lngCenter);
        for(int i = 1 ; i < radius+1 ; i++) {
            String[] bornes = getBounds(i , latLng);
            for(int j = 0 ; j < bornes.length ; j++) {
                //on est cense avoir deux elements.
                if(points.indexOf(bornes[j]) < 0) {
                    points.add(bornes[j]);
                }
            }
        }
        Log.e("test", "getLongitudeLine: poinst "+points.toString() );
        return points;
    }

    private String[] getBounds(int kilometerDistance , LatLng latLng) {
        double latitude = latLng.latitude , longitude = latLng.longitude;
        double scale = getLngScale(kilometerDistance , latitude);
        //au cas ou l'intervalle de killometrage est negatif (compte tenu du cosinus.)
        scale = scale > 0
                ?scale
                :scale * (-1d);

        return new String[]{Upload.initLongitude((longitude-scale)) , Upload.initLongitude((longitude+scale))};

    }

    public double getLatScale(int radius_level) {
        switch (radius_level) {
            case 1:
                return lat1;
            case 3 :
                return lat3;
            case 10:
                return lat10;
            default:
                return 0d;
        }
    }

    /**
     * selon l'endroit ou l'on se trouve trouve sur le globe le degree de longitude varie en fonction donc de la latitude d'ou cette petite fonction
     * @param latitude
     * @return
     */
    private double getLngKilometer(double latitude) {
        double longitude;
        //donc 40000 represent the circonference de la terre.
        longitude = (40000.0d*Math.cos(latitude))/360.0d;
        return longitude;
    }

    /**
     * a la difference de la latitude qui elle est invariable (donc pas besoins de faire des calculs les constantes font l'affaire)
     * pour la latitude un peu de calcul s'impose.
     * @param radius_level
     * @param latitude
     * @return
     */
    public double getLngScale(int radius_level , double latitude) {
        return (((double) radius_level)/getLngKilometer(latitude));
    }

    /**
     * methode permettant de retourner le rayon de recherche prefere de l'utilisateur.
     * @return
     */
    public static int getCurrentRadius(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.parseInt(sp.getString("key_pref_navigation_radius" , "1"));
    }

    public int getRadius() {
        return radius;
    }
}
