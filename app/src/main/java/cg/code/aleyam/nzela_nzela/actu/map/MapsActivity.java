package cg.code.aleyam.nzela_nzela.actu.map;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

import cg.code.aleyam.nzela_nzela.R;
import cg.code.aleyam.nzela_nzela.actu.Actu_route;
import cg.code.aleyam.nzela_nzela.lite.DatabaseManager;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LatLng event_coo = null;
    String title = null;
    int event_type = 0;
    MapView mMapView = null;
    public static final int PICKER_COUNT = 7;
    String[] userInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        event_type = getIntent().getIntExtra(Actu_route.FROM_ACTU_ROUTE , -1);
        //mMapView = (MapView) findViewById(R.id.map);
        if(Actu_route.event_data != null ) {
            event_coo = (LatLng) Actu_route.event_data.get("coordonnees");
            title = (String) Actu_route.event_data.get("post");
        }
        DatabaseManager dbm = DatabaseManager.getInstance(MapsActivity.this);
        userInfo = dbm.getCurrentUser();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        dbm.closeDB();
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

        // Add a marker in Sydney and move the camera
        ArrayList<HashMap<String , Object>> around = Actu_route.aroundEvent;
        for(int i = 0 ; i<around.size() ; i++) {
            Log.e("test" , "les voisins sont bien la");
            Marker tmp_mark = mMap.addMarker(new MarkerOptions().position( (LatLng) around.get(i).get("coordonnees")));
            tmp_mark.setTitle((String) around.get(i).get("post"));
            tmp_mark.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMarkerIcon(getPicker((int) around.get(i).get("type")) , 50 , 50)));
        }

        Marker real_event = mMap.addMarker(new MarkerOptions().position(event_coo));
        real_event.setTitle(title);
        real_event.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMarkerIcon(getPicker(event_type) , 50 , 50)));

        Location position = Positionement.getInstance(MapsActivity.this).getUserLastLocation();

        if(position != null && userInfo!= null) {
            Marker me = mMap.addMarker(new MarkerOptions().position(new LatLng(position.getLatitude() , position.getLongitude())));
            me.setTitle("Moi: "+userInfo[1]);
        }

        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(event_coo , 15));

    }

    public static int getPicker(int selectedItem) {

        switch (selectedItem) {
            case 0:
                return  R.drawable.policier;
            case 1:
                return R.drawable.accident;
            case 2:
                return R.drawable.traveaux;
            case 3:
                return R.drawable.station;
            case 4:
                return R.drawable.incivisme;
            case 5:
                return R.drawable.danger;
            case 6:
                return R.drawable.interdit;
        }

        return -1;

    }

    public Bitmap resizeMarkerIcon(int resId , int width , int height) {
        BitmapDrawable ressource = (BitmapDrawable) getResources().getDrawable(resId);
        Bitmap resized = Bitmap.createScaledBitmap(ressource.getBitmap() , width , height , false) ;
        return resized;
    }


}
