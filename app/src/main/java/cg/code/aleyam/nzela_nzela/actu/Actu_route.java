package cg.code.aleyam.nzela_nzela.actu;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import cg.code.aleyam.nzela_nzela.OttoBus;
import cg.code.aleyam.nzela_nzela.R;

import cg.code.aleyam.nzela_nzela.actu.connexion.Download;
import cg.code.aleyam.nzela_nzela.actu.map.MapsActivity;
import cg.code.aleyam.nzela_nzela.actu.map.Positionement;
import cg.code.aleyam.nzela_nzela.centrale.CentraleInterface;
import cg.code.aleyam.nzela_nzela.centrale.Centrale_activity;
import cg.code.aleyam.nzela_nzela.check.CommunicationCheck;
import cg.code.aleyam.nzela_nzela.home.Home;
import cg.code.aleyam.nzela_nzela.lite.DatabaseManager;
import cg.code.aleyam.nzela_nzela.transaction.TicketActivity;
import cg.code.aleyam.nzela_nzela.transaction.connexion.PostTransaction;

import static android.app.Activity.RESULT_OK;

public class Actu_route extends Fragment implements
        Actu_fragment.OnListFragmentInteractionListener , CentraleInterface ,
        Executor{


    boolean onDownload = false;
    FloatingActionButton report = null;
    private boolean readNotified = false;
    public static final String FROM_ACTU_ROUTE = "cg.code.aleyam.nzela_nzela.actu.Actu_route";
    String post_info[] = null;
    public static Map event_data = new HashMap();
    public static ArrayList<HashMap<String , Object>> aroundEvent = null;

    private static ProgressBar loader_indicator = null;
    View actu_view = null;

    public void setReadNotified(boolean readNotified) {
        this.readNotified = readNotified;
    }

    public boolean isReadNotified() {
        return readNotified;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        actu_view = inflater.inflate(R.layout.top_fragment_actu_route , container , false);
        report = actu_view.findViewById(R.id.report);
        report.setOnClickListener(reportHandler);
        loader_indicator = Centrale_activity.getLoader_indicator();
        if(getActivity() != null)
        post_info = getActivity().getIntent().getStringArrayExtra(Report.FROM_REPORT);

        return actu_view;
    }


    @Override
    public void onPause() {
        super.onPause();
        OttoBus.bus.unregister(Actu_route.this);
    }

    @Override
    public void onResume() {
        super.onResume();
        OttoBus.bus.register(Actu_route.this);
    }

    @Override
    public void centraleOnPause() {

        super.onPause();
    }

    @Override
    public void centraleOnResume() {

         super.onResume();
    }

    @Override
    public void centraleNotification(String key, Object forShare) {

    }

    @Override
    public void fragmentEventHandler(String key, Object value) {

    }

    public synchronized void enableLoad (boolean enable , Location position) {
        //TODO si une certaine requete de loader les actu bloque il faut voir ce bout de code
        onDownload = true;
        if(enable) {
            if(CommunicationCheck.isConnectionAvalable(Actu_route.this.getActivity())) {

                loader_indicator.setVisibility(View.VISIBLE);

                        if(position == null) position = Positionement.getInstance(Actu_route.this.getActivity()).getUserLastLocation();

                        final Location pos = position;



                        if (pos == null) {

                            //Toast.makeText(Actu_route.this , "impossible de recuperer votre position" , Toast.LENGTH_LONG).show();
                            loader_indicator.setVisibility(View.GONE);
                        }else {
                            this.execute(new Runnable() {
                                @Override
                                public void run() {

                            ActuRecyclerViewAdapter arva =  ((Actu_fragment)Actu_route.this.getChildFragmentManager().findFragmentById(R.id.fragment_actu)).adapter;
                            Download.beginListening(arva , new LatLng(pos.getLatitude() , pos.getLongitude()) , readNotified);
                        }


                    });

                }

            } else {
                loader_indicator.setVisibility(View.GONE);
            }



        } else {
            loader_indicator.setVisibility(View.GONE);
        }
        onDownload = false;
    }


    @Override
    public void execute(@NonNull Runnable command) {

        new Thread(command).start();

    }


    /**
     * lance l'activiter qui permet de reporter (ou partager) des evenements
     */
    public View.OnClickListener reportHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           Intent i = new Intent(Actu_route.this.getActivity() , Report.class);
           startActivity(i);
        }
    };

    @Subscribe
    public void busDownload(Location location) {

        if(Positionement.settingsOk) {
            if(!onDownload) {
                if(loader_indicator != null) {
                    enableLoad(true , location);
                }
            }
        }

    }

    @Override
    public void download() {
        if(Positionement.settingsOk) {
            if(!onDownload) {
                if(loader_indicator != null) {
                    enableLoad(true , null);
                }
            }
        }
    }

    @Override
    public void onListFragmentInteraction(LatLng coordonnee , String post , int event_type , ArrayList<HashMap<String , Object>> aroundEvent) {
        event_data.put("coordonnees" ,  coordonnee) ;
        event_data.put("post" , post );

        Actu_route.aroundEvent = aroundEvent;

        Intent i = new Intent(Actu_route.this.getActivity() , MapsActivity.class);
        i.putExtra(FROM_ACTU_ROUTE , event_type);
        startActivity(i);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1536) {
            if(resultCode == RESULT_OK) {
                Positionement.settingsOk = true;
                //Toast.makeText(Actu_route.this , "recuperation de votre position" , Toast.LENGTH_SHORT).show();
                if(loader_indicator != null) {
                    enableLoad(true , null);
                }

            } else {
                if(loader_indicator != null) {
                    enableLoad(false , null);
                }
                Positionement.settingsOk = false;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == Positionement.REQUEST_CODE) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Positionement.getInstance(Actu_route.this.getActivity()).getUserLastLocation();
            } else {
                if(loader_indicator != null){
                    enableLoad(false , null);
                }
                Toast.makeText(Actu_route.this.getContext() , "Permission refusée" , Toast.LENGTH_LONG).show();
            }
        }
    }





}