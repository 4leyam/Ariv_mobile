package cg.code.aleyam.nzela_nzela.actu.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import cg.code.aleyam.nzela_nzela.OttoBus;
import cg.code.aleyam.nzela_nzela.R;
import cg.code.aleyam.nzela_nzela.actu.Report;
import cg.code.aleyam.nzela_nzela.actu.connexion.ActuObject;
import cg.code.aleyam.nzela_nzela.actu.connexion.EventObject;
import cg.code.aleyam.nzela_nzela.actu.connexion.PositionManager;
import cg.code.aleyam.nzela_nzela.actu.connexion.Upload;
import cg.code.aleyam.nzela_nzela.centrale.Centrale_activity;

public class AlertService extends Service {
    PositionManager pm = null;
    public static HashMap<String , DatabaseReference> referenceHashMap = new HashMap<>();
    static public List<String> data_key = new ArrayList<>();
    private SharedPreferences sp = null;
    NotificationManager nm = null;
    private boolean listeningAlerts = true;

    //la liste des evenements notifies
    LinkedList<ActuObject> importantEvents = new LinkedList<>();
    public static final String FROM_NOTIFICATION = "FROM_HERE";


    public class AlertBinder extends Binder {
        public AlertService getAlertService() {
            return AlertService.this;
        }
    }
    private IBinder ib = new AlertBinder();

    public AlertService() {

    }

    @Override
    public void onDestroy() {
        listeningAlerts = false;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
       return ib;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        //on lance le processus d'ecoute des notifications.
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (AlertService.this) {
                    try {
                        while (listeningAlerts) {
                            Log.e("test", "ecoute" );
                            listenFromEvent();
                            Log.e("test", "en attente de cle pour l'ecoute" );
                            AlertService.this.wait();
                        }
                    } catch (InterruptedException e) {
                        Log.e("test" , "Alert run: "+e.getMessage());
                    }
                }
            }
        }).start();

        return START_STICKY;
    }

    /**
     * cette methode ecoute tous les evenement sur un certains rayon definit par l'utilisateur.
     */
    private void listenFromEvent() {
        Log.e("test", "taille: "+referenceHashMap.size() );
        for (DatabaseReference ref : referenceHashMap.values()) {
            Log.e("test", "cherche Notification..." );
            //on recupere la liste des alerts dont l'utilisateur s'est inscrit.
            Set<String> alerts = getUserPreferedAlert();
            //on boucle pour ecouter tous les evenement concernant l'user
            for (String str : alerts) {
                ref.child(str).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        //si on a un childAdded on notifie l'utilisateur.
                        if(!data_key.contains(dataSnapshot.getKey())) {

                            Long wastedTime = new Date().getTime() - Long.parseLong(dataSnapshot.getKey());
                            if(wastedTime < (3*60*1000) ) {
                                //si l'evenement date de moins de 3 min il est aussi Notifie.
                                if(!Centrale_activity.isRunning && !Report.isRunning) {
                                    createNotification();
                                    //apres avoir notifier on recupere les information de l'evenement notifiee.
                                    EventObject eo = dataSnapshot.getValue(EventObject.class);
                                    ActuObject ao = new ActuObject(
                                            eo.getImage() ,
                                            eo.getOwner(),
                                            eo.getPost_text() ,
                                            eo.getPertinance(),
                                            eo.getLat() ,
                                            eo.getLng() ,
                                            Long.parseLong(""+dataSnapshot.getKey()) ,
                                            Integer.parseInt(dataSnapshot.getRef().getParent().getKey()),
                                            eo.getLast() ,
                                            eo.getVotes() ,
                                            eo.getTotal_votes());
                                    importantEvents.add(ao);
                                }
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

        }
    }

    public void addRefForListener(DatabaseReference dbref , List<String> keys) {
        Log.e("test" , "addRefForListener: eventParentRef "+dbref.getKey());
        synchronized (AlertService.this) {
            AlertService.data_key = keys;
            if (!referenceHashMap.containsKey(dbref.getKey())) {
                AlertService.this.notify();
                referenceHashMap.put(dbref.getKey(), dbref);
            }
        }
    }


    public LinkedList<ActuObject> getNofitiedEventList() {
        return importantEvents;
    }

    private Set<String> getUserPreferedAlert() {

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        return sp.getStringSet("key_pref_certification_alerts_type" , new HashSet<String>());

    }

    public void createNotification() {
        Bundle notificationBundle = new Bundle();
        notificationBundle.putBoolean(FROM_NOTIFICATION , true);
        CustomNotificationBuilder.getInstance().createNotification(
                AlertService.this.getBaseContext()
                , "ARIV-Navigateur"
                , "Nouvel evenement poste, Rendez vous sur l'application afin de le voire"
                , "Nouveau Post"
                , "Rendez vous sur l'application afin de voire les nouveaux evenements publies"
                , Centrale_activity.class
                , notificationBundle);
    }



}
