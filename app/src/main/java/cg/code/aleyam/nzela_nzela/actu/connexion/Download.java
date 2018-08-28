package cg.code.aleyam.nzela_nzela.actu.connexion;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import cg.code.aleyam.nzela_nzela.Settings.SettingsManager;
import cg.code.aleyam.nzela_nzela.actu.ActuRecyclerViewAdapter;
import cg.code.aleyam.nzela_nzela.actu.cycle.EventMonitor;
import cg.code.aleyam.nzela_nzela.actu.map.MapsActivity;
import cg.code.aleyam.nzela_nzela.actu.notification.AlertService;
import cg.code.aleyam.nzela_nzela.check.CommunicationCheck;

public class Download {

    private static ArrayList<ActuObject> evenements = null;
    private static FirebaseStorage storage = FirebaseStorage.getInstance();
    private static DownloadStateShare dss = null;
    private static DatabaseReference lastLocationRef = null;
    private static LatLng position = null;
    private static AlertService als = null;
    private static boolean isBound = false;
    private static boolean isServiceStarted = false;
    private static ServiceConnection scxion = null;
    private static boolean readNotifiedEvents = false;
    private static Intent alertServiceIntent = null;
    public static PositionManager pm = null;


    public static void readOnce(DatabaseReference targetRef , final DownloadStateShare dss , LatLng position ) {

        Context context = null;

        if(dss instanceof ActuRecyclerViewAdapter)  {
            context = ((ActuRecyclerViewAdapter)dss).getActivity();
            if(!CommunicationCheck.isConnectionAvalable(context)) {
                dss.onRequestFail();
                return;
            }

        }

        alertServiceIntent = new Intent(context , AlertService.class);
        if(!isServiceStarted) {
            context.startService(alertServiceIntent);
            isServiceStarted = true;
        }
        bindService(context);

        Download.position = position;
        lastLocationRef = targetRef;
        Download.dss = dss;
        //double[] minMaxLat = getLatMarge(position.latitude , 1 , 3);
        pm = PositionManager.getInstance(context);
        double[] latBound = latBoundaries(pm.getRadius() , position.latitude);
        ArrayList<String> longitudeLine =pm
                .getLongitudeLine(position);
        //on definit quel type d'evenement veut on lire.
        mel.setReadNotifiedEvent(readNotifiedEvents);
        Set<String> eventType = SettingsManager.getInstance(context).getUserPreferedEvent();
        for(int i = 0 ; i <longitudeLine.size() ; i++) {

            for(String str: eventType) {
                //on boucle sur tous les type d'evenement.
                targetRef
                        .child(longitudeLine.get(i)+"/"+str)
                        .orderByChild("lat")
                        .startAt(latBound[0])
                        .endAt(latBound[1])
                        .addListenerForSingleValueEvent(mel);
            }

        }




        evenements = new ArrayList<>();


    }

    public static void bindService(final Context context) {
        if(scxion == null) {
            if(!isBound) {
                scxion = new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                        AlertService.AlertBinder alb = (AlertService.AlertBinder)iBinder;
                        als = alb.getAlertService();
                        isBound = true;
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName componentName) {
                        if(isBound) {
                            context.unbindService(scxion);
                        }
                        scxion = null;
                        isBound = false;
                    }
                };
            }
        }
        context.bindService(alertServiceIntent , scxion , Context.BIND_AUTO_CREATE);

    }

    private static double[] latBoundaries(int radius , double lat) {
        if(pm != null) {
            double latScale = pm.getLatScale(radius);
            return new double[]{lat-latScale , lat+latScale };
        }
       return new double[]{0,0};
    }
    //la precision est la moitie de la diagonale de la surface de repertoation des evenements.
    //decimal count nombre de chiffre apres la virgule 4
    //pour l'instant aucune precision superieure a 9 ne sera prise en compte
    private static double[] getLatMarge(double lat , int rayon , int decimalCount) {

        decimalCount = ++decimalCount;

        double[] minMaxLat = new double[2];
        String str_lat = ""+lat ;

        int indexOfPoint = str_lat.indexOf('.');
        String perimetreLat = str_lat.substring(0 , indexOfPoint+decimalCount) ,
                min = perimetreLat.substring(0 , perimetreLat.length()-2)+"",
                max = perimetreLat.substring(0 , perimetreLat.length()-2)+"";

        int lastDigit = Integer.parseInt(""+perimetreLat.charAt(perimetreLat.length()-1)) ,
            beforeLast = Integer.parseInt(""+perimetreLat.charAt(perimetreLat.length()-2));

        if(lastDigit == 0) {

            min = min+""+(--beforeLast)+""+(10-rayon);
            max = max+""+beforeLast+""+(lastDigit+rayon);

        } else if(lastDigit == 9) {

            max = max+""+(++beforeLast)+""+(--rayon);
            min = min+""+beforeLast+""+(lastDigit-rayon);

        } else {
            min = min+""+beforeLast+""+(lastDigit-rayon);
            max = max+""+beforeLast+""+(lastDigit+rayon);
        }


        minMaxLat[0] = Double.parseDouble(min);
        minMaxLat[1] = Double.parseDouble(max);

        return minMaxLat;
    }


    static MyEventListener mel = new MyEventListener();

    //TODO dans les prochaines versions il faut eliminer les evenement passe (ceux qui sont plus dans le rayon d'action de l'user.)
    static class MyEventListener implements ValueEventListener {

        LinkedList<ActuObject> my_events = new LinkedList<>();
        LinkedList<String> data_key = new LinkedList<>();
        boolean readNotifiedEvent = false;
        //ArrayList<DatabaseReference> ref_array = new ArrayList<>();


        public void setReadNotifiedEvent(boolean readNotifiedEvent) {
            this.readNotifiedEvent = readNotifiedEvent;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

                Log.e("Test",  "second");

                if(dataSnapshot.hasChildren()) {
                    Log.e("Test",  "secondS");
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        EventObject event = snapshot.getValue(EventObject.class);
                        if(readNotifiedEvent) {
                            Log.e("Test",  "alerts");
                            if(isBound) {
                                Log.e("Test",  "l");
                                my_events.clear();
                                my_events = als.getNofitiedEventList();
                            } else
                                //ce cas ne devrait en principe jamais arriver
                                verify(snapshot.getKey() , event , snapshot.getRef());

                        } else {
                            if(my_events.size() != data_key.size()) {
                                //ca veut dire que precedement l'utisateur a demande de voire les alerts.
                                my_events.clear();
                                data_key.clear();
                            }
                            verify(snapshot.getKey() , event , snapshot.getRef());
                        }
                        Download.dss.notifyDataReady(my_events);
                        //ensuite on met la racine de cette reference a l'ecoute pour les notifications.
                        if(isBound) {
                            als.addRefForListener(snapshot.getRef().getParent().getParent() , data_key);
                        } else {
                            Log.e("test", "onDataChange: not bound");
                        }
                    }
                }
                Download.dss.notifyDataReady(my_events);


        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            //on onvoie juste un message de cancel quoi!.
            Log.e("test" , databaseError.toString());

        }

        /**
         * methode permettant de verifier les cles des element afin d'eviter d'avoir des doublons d'objets
         * @param key de l'objet courant
         * @param event l'evenement recuperer sur RTDB
         * @param databaseReference pour recuperer la cle parente
         */
        private void verify(String key , EventObject event , DatabaseReference databaseReference) {

            ActuObject ao = new ActuObject(
                    event.image ,
                    event.owner ,
                    event.post_text ,
                    event.pertinance ,
                    event.lat ,
                    event.lng ,
                    Long.parseLong(""+key) ,
                    Integer.parseInt(databaseReference.getParent().getKey()),
                    event.getLast() ,
                    event.getVotes() ,
                    event.getTotal_votes());

            //TODO mettre databaseReference a l'ecoute des modification.
            if(data_key.contains(key)) {

                my_events.set(data_key.indexOf(key) , ao);
            } else {
                //avant l'ajout on verifie si la longitude repond au criteres de selection.
                double scale = 0d;
                if(pm != null) {
                   scale = pm.getLngScale(pm.getRadius() , position.latitude);
                    //pour s'assurrer que l'echel soit toujours superieur a 0
                    scale = scale > 0
                            ?scale
                            :scale*(-1d);
                }



                double d = Download.position.longitude+scale;
                double d0 = Download.position.latitude-scale;
                if(ao.getLng()>=d0 && ao.getLng() <= d) {
                    //si la longitude correspond bien au criteres de selection alors
                    //on verifie ensuite si l'evenement vaut toujours la peine d'etre affiche
                    boolean isUpdtodate = true;
                    if(ao.getType() != 4) {
                        //ne concerne pas les evenements d'incivisme.
                        //TODO mettre deleteifNot a true avant la production de l'app.
                       isUpdtodate =
                                EventMonitor
                                        .getInstance()
                                        .isEventUptoDate(databaseReference ,
                                                ao.getLast() ,
                                                ao.getDate() ,//le jour ou l'evenement a ete publie.
                                                (int) ao.getTotal_votes() ,
                                                (int) ao.getPertinance() ,
                                                false);
                    }

                    if(isUpdtodate) {
                        my_events.addFirst(ao);
                        data_key.addFirst(key);
                    } else {
                        my_events.remove(ao);
                        data_key.remove(key);
                    }

                }
            }

        }

    }



    public static ArrayList<EventObject> beginListening(DownloadStateShare dss , LatLng position , boolean readNotifiedEvents) {
        ArrayList<EventObject> flow = new ArrayList<>();
        if(CommunicationCheck.isConnectionAvalable(((ActuRecyclerViewAdapter)dss).getActivity())) {

            Download.readNotifiedEvents = readNotifiedEvents;
            DatabaseReference post_ref = Upload.data_root_ref.child(Upload.POST_REF);

            //on lis pour la premiere fois afin de recuperer les posts
            readOnce(post_ref , dss , position);
            //puis on mets les post sur ecoute.
            //listenList(post_ref);
        }



        return flow;
    }

}