package cg.code.aleyam.nzela_nzela.actu.notification;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.Arrays;
import java.util.HashSet;

import cg.code.aleyam.nzela_nzela.Settings.SettingsManager;
import cg.code.aleyam.nzela_nzela.actu.connexion.Upload;
import cg.code.aleyam.nzela_nzela.centrale.Centrale_activity;
import cg.code.aleyam.nzela_nzela.lite.DatabaseManager;

public class AlertsRequestsService extends Service {


    public class RequestBinder extends Binder {
        public AlertsRequestsService getHostService() {
            return AlertsRequestsService.this;
        }
    }

    private ServiceConnection serviceConnection = null;
    private String[] userInfo = null;
    RequestBinder rb = new RequestBinder();

    public AlertsRequestsService() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return rb;
    }

    @Override
    public void onDestroy() {
        Log.e("test", "onStartCommand: service request lance ");

        unbindService(serviceConnection);
        Upload.data_root_ref.child("alertRequest").removeEventListener(cel);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.e("test", "onStartCommand: service request lance ");
        userInfo = DatabaseManager.getInstance(AlertsRequestsService.this.getBaseContext()).getCurrentUser();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //mise a l'ecoutes des requetes qui concernent l'utilisateur courant
                if(userInfo != null) {
                    Upload.data_root_ref.child("alertRequest").addChildEventListener(cel);
                }

            }
        }).start();


        return START_STICKY;
    }

    ChildEventListener cel = new ChildEventListener() {



        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            if(dataSnapshot.getKey().equals(userInfo[2])) {
                Log.e("test", "onChildAdded: ui "+userInfo[2]);
                //donc si la cle correspond au phone number du current user
                //ca veut dire que l'user est authorise
                SettingsManager
                        .getInstance(AlertsRequestsService.this)
                        .setUserEventsListner(new HashSet<String>(Arrays.asList(dataSnapshot.getValue(String.class).split("$"))));
                //apres qu'on ai fait ca on notifie l'utilisateur ce qui se passe.
                createAlertNotification();
                dataSnapshot.getRef().setValue(null);
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
    };


    public void setServiceConnection(ServiceConnection sc) {
        serviceConnection = sc;
    }


    public void createAlertNotification() {
        CustomNotificationBuilder.getInstance().createNotification(AlertsRequestsService.this.getBaseContext()
                , "ARIV-Alerts"
                , "Invitation de suivit"
                , "ARIV-Alerts"
                , "desormais vous pouvez Intervenir aux evenement qui surviennent autour de vous, nous vous tiendrons au courant."
                , Centrale_activity.class
                , null);
    }
}
