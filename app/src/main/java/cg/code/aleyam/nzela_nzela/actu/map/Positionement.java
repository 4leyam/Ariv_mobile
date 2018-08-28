package cg.code.aleyam.nzela_nzela.actu.map;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import cg.code.aleyam.nzela_nzela.OttoBus;
import cg.code.aleyam.nzela_nzela.actu.Actu_route;
import cg.code.aleyam.nzela_nzela.centrale.Centrale_activity;
import cg.code.aleyam.nzela_nzela.check.CommunicationCheck;


public class Positionement {
    public static final int REQUEST_CODE = 26;
    static LocationManager locMan = null;
    private static Location location = null;
    private static FusedLocationProviderClient mFusedLocationClient;
    private static LocationRequest lr = null;
    static LocationCallback lc = new LocationCallback();
    private static boolean listenUpdateCall = false;
    public static boolean settingsOk = false;
    private Context context = null;
    private static Positionement instance = null;




    public static Positionement getInstance(Context ct) {
        if(instance == null) {
            instance = new Positionement(ct);
        } return instance;
    }

    private Positionement(Context context) {
        this.context = context;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }


    private void listenUpdate() {
        if(lr == null) initLocation();

        lc = new LocationCallback(){

            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationResult == null) return;
                Location loc = null;

                for(Location location : locationResult.getLocations()) {
                    if(location!= null)
                        loc = location;
                }

                if(loc!= null) {
                    location = loc;
                    OttoBus.bus.post(loc);
                }

            }
        };

        if(ActivityCompat.checkSelfPermission(context , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //toutes permissions sont accordes donc pas de soucis
            ActivityCompat.requestPermissions( (Activity) context , new String[]{Manifest.permission.ACCESS_FINE_LOCATION , Manifest.permission.ACCESS_COARSE_LOCATION} , REQUEST_CODE);
            return ;
        } else {
            mFusedLocationClient.requestLocationUpdates(lr, lc , null);
        }

    }


    public void checkSettings(final boolean refresh) {

        Task<LocationSettingsResponse> responseTask = null;
        if(!settingsOk) {

            initLocation();
            LocationSettingsRequest.Builder lsrBuilder = new LocationSettingsRequest.Builder();
            lsrBuilder.addLocationRequest(lr);
            lsrBuilder.setNeedBle(true);
            //on va ensuite dans une task verifier si les paramettres necessaire sont actives.

            responseTask = LocationServices.getSettingsClient(context).checkLocationSettings(lsrBuilder.build());

            responseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    if(!settingsOk) {
                        settingsOk = true;
                        //if(refresh) {

                            if(context instanceof Centrale_activity) {
                                //on fait une recuperation de l'actu si l'activite correspond
                                Centrale_activity ca = (Centrale_activity) context;
                                ca.actuDownloadViaCentrale();
                            } else {
                                getUserLastLocation();
                            }

                        //}


                    }

                }
            });

            responseTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if(e instanceof ResolvableApiException) {
                        ResolvableApiException rae = (ResolvableApiException) e;
                        try {
                            rae.startResolutionForResult((Activity) context , 1536);
                        } catch (Exception e_prime) {

                        }
                        settingsOk = false;
                    }

                }
            });



        }

    }

    public static void initLocation() {
        lr = LocationRequest.create();
        lr.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        lr.setInterval(10000);
        lr.setFastestInterval(5000);

    }



    public Location getUserLastLocation() {

        if(CommunicationCheck.isConnectionAvalable(context)) {
            if(ActivityCompat.checkSelfPermission(context , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //toutes permissions sont accordes donc pas de soucis
                ActivityCompat.requestPermissions((Activity) context , new String[]{Manifest.permission.ACCESS_FINE_LOCATION , Manifest.permission.ACCESS_COARSE_LOCATION} , REQUEST_CODE);
            } else {
                //Location location = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                checkSettings( true );


                //Toast.makeText(context, "cheking..."+settingsOk, Toast.LENGTH_SHORT).show();

                if(settingsOk) {//si tous les paramettres sont ok pour la geolocalisation





                    Task location_request_task = mFusedLocationClient.getLastLocation();

                    location_request_task.addOnSuccessListener( new OnSuccessListener<Location>() {

                        @Override
                        public void onSuccess(Location location) {

                            Positionement.location = location;
                            if (Positionement.location != null) {
                                OttoBus.bus.post(Positionement.location);
                            }

                            if(!listenUpdateCall) {

                                listenUpdate();

                                listenUpdateCall = true;

                            }

                        }

                    });

                }

            }
        }
        return location;
    }
}
