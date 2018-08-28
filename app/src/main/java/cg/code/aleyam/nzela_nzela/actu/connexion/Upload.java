package cg.code.aleyam.nzela_nzela.actu.connexion;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cg.code.aleyam.nzela_nzela.R;
import cg.code.aleyam.nzela_nzela.authentication.Authentication;
import cg.code.aleyam.nzela_nzela.centrale.Centrale_activity;
import cg.code.aleyam.nzela_nzela.check.CommunicationCheck;
import cg.code.aleyam.nzela_nzela.depart.InfoGenFragment;
import cg.code.aleyam.nzela_nzela.lite.DatabaseManager;
import cg.code.aleyam.nzela_nzela.offline.OffTransaction;

public class Upload{

    private ProgressBar uploadState = null;
    private Button cancelUpload = null;
    private TextView percent = null;
    private Dialog sendConstat = null;
    //TODO static fiel fixing.
    private static Upload instance = null;
    private Context ct = null;
    private String[] user_info = null;


    private static FirebaseStorage storageRoot = FirebaseStorage.getInstance();
    private static StorageReference root_ref = storageRoot.getReference();
    private static StorageReference constat_ref =  root_ref.child("posts");

    //realtime database fields
    private static FirebaseDatabase nzela_base = FirebaseDatabase.getInstance();
    public static DatabaseReference data_root_ref = nzela_base.getReference();
    public static final String  POST_REF = "posts/event";


    public static Upload getInstance(Context ct) {
        if(instance == null)
            instance = new Upload(ct);
            return instance;

    }

    private Upload(Context ct) {
        this.ct = ct;
        DatabaseManager dm = DatabaseManager.getInstance(ct);
        user_info = dm.getCurrentUser();
        dm.closeDB();
    }


    public void UploadConstat(final ActuObject actuObject , String localImagePath , final Activity uploadActivity) throws Exception {
       // String image_path = null;

        if(uploadActivity instanceof UpdateFollowable) {
            if(CommunicationCheck.isConnectionAvalable(uploadActivity)) {
                final UpdateFollowable updateFollowable = (UpdateFollowable) uploadActivity;

                //on retire la boite de dialogue de l'attente de la localisation
                updateFollowable.notifyDialogCreation();
                //on cree la boite de dialogue pour l'envoie du constant(barre de chargement.)
                initDialog(uploadActivity);
                //on renvoie le traitement des possibilites d'annulation de l'envoi chez report.
                updateFollowable.begin(cancelUpload , sendConstat);

                //ensuite on commence l'envoie
                Uri uploadUri = Uri.fromFile(new File(localImagePath));
                UploadTask uploadTask = constat_ref.child("post_"+actuObject.getDate()).putFile(uploadUri);
                //on ecoute les resultats
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //updateFollowable.onSucces(sendConstat);
                        actuObject.image = taskSnapshot.getDownloadUrl().toString();
                        registPost(uploadActivity , actuObject);

                    }
                });
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        updateFollowable.onFailure(sendConstat);
                    }
                });
                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        long sent = taskSnapshot.getBytesTransferred();
                        long total = taskSnapshot.getTotalByteCount();
                        updateFollowable.onProgress(sent , total , uploadState , percent);
                    }
                });

            }
        } else {
            throw new Exception("l'activitee doit implementer l'interface update followable");
        }


    }

    private void registPost(Activity uploadActivity  , ActuObject actuObject ) {

        if(uploadActivity instanceof UpdateFollowable) {
            final UpdateFollowable updateFollowable = (UpdateFollowable) uploadActivity;

            //on se dirige vers le referent concerne
            ActuObject actu = actuObject;
            Map<String , Object> update = new HashMap<>();
            update.put(initLongitude(actu.getLng())+"/"+actu.getType()+"/"+actu.getDate() , actu.getEvent());
            Task t = data_root_ref.child(POST_REF).updateChildren(update);

            t.addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    updateFollowable.onSucces(sendConstat);
                }
            });
            t.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    updateFollowable.onFailure(sendConstat);
                }
            });

        }

    }

    public void voteEvent(final ActuObject ao , final int vote , final RateNotifier rnotifier) {
        //on lis les info du post pour commencer.
        String ratePath = "tendances/"+ao.getType()+"/"+ao.getDate();

        if(CommunicationCheck.isConnectionAvalable(this.ct)) {

            //avant on test dabord si l'utilisateur est connecte genre authentifie.
            if(user_info!= null && user_info.length >0) {

                final String user = user_info[2];

                Query rateAllowQ = data_root_ref.child(ratePath).orderByChild("user").equalTo(user);
                rateAllowQ.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.getValue() == null) {//
                            voteProcess(user , ao , vote , rnotifier);
                        } else {
                            rnotifier.notAllow();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            } else {
                //donc l'utilisateur n'est pas
                try {
                    Activity act = Centrale_activity.getActivityInstance();
                    this.ct = act;
                    new AlertDialog.Builder(this.ct)
                            .setTitle(InfoGenFragment.fromHtml(OffTransaction.fontdebut+"Authentification"+OffTransaction.fontfin))
                            .setMessage(InfoGenFragment.fromHtml(OffTransaction.fontdebut+"Pour donner votre avis sur un evenement vous devez vous connecter"+OffTransaction.fontfin))
                            .setPositiveButton("S'authentifier", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Upload.this.ct.startActivity(new Intent(Upload.this.ct , Authentication.class));
                                    if(ct instanceof Activity) {
                                        //il faut donc s'arranger que le contexte soit une activite
                                        ((Activity)ct).finish();
                                    }
                                }
                            })
                            .setNegativeButton("Compris", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).create().show();
                } catch (Exception e) {
                    Log.e("test", "voteEvent: accession a centrale impossible car l'activite est null");
                }

            }

        }


    }
    private void voteProcess(final String user , final ActuObject ao ,  final int vote ,  final RateNotifier rnotifier) {

        final long rate_instant = new Date().getTime();
        //rate instant is da key of rated user.
        String ratepath = POST_REF+"/"+initLongitude(ao.getLng())+"/"+ao.getType()+"/"+ao.getDate();

        data_root_ref.child(ratepath)
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {

                        EventObject eo = mutableData.getValue(EventObject.class);
                        if(eo == null) {

                            return  Transaction.success(mutableData);

                        }
                        eo.setVotes(eo.getVotes()+1);
                        eo.setTotal_votes(eo.getTotal_votes()+vote);

                        float pertinance = ((eo.getTotal_votes())/eo.getVotes());
                        //on calcule le nouvel avis de l info puis on met a jours l objet
                        eo.setPertinance(pertinance);
                        eo.setLast(rate_instant);
                        Map<String , Object> update = new HashMap<>();
                        update.put(POST_REF+"/"+initLongitude(ao.getLng())+"/"+ao.getType()+"/"+ao.getDate() , eo);

                        update.put("tendances/"+ao.getType()+"/"+ao.getDate()+"/"+rate_instant+"/user" , user);
                        //on prepare l acces des mise a jours
                        data_root_ref.updateChildren(update);

                        return Transaction.success(mutableData);

                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                        if(databaseError == null) {
                            if(b) {
                                //donc tout s est bien passe
                                rnotifier.rateDone();
                            } else {
                                rnotifier.rateFailure();
                            }

                        } else {
                            rnotifier.rateFailure();
                        }
                    }
                });

    }

    private void initDialog(Context context) {

        try {
            View loader = LayoutInflater.from(context).inflate(R.layout.dialog_constat_upload, null);

            uploadState = loader.findViewById(R.id.evolution);
            cancelUpload = loader.findViewById(R.id.cancel);
            percent = loader.findViewById(R.id.percent);

            sendConstat = new Dialog(context);

            sendConstat.setContentView(loader);
            sendConstat.setTitle("Publication");
            sendConstat.setCancelable(false);
            sendConstat.show();
        } catch (Exception e) {
            e.printStackTrace();
        }



    }


    /**
     * methode permettant de recupere la longitude plus deux chiffres apres la virgule.
     * @param longitude
     * @return
     */
    public static String initLongitude(double longitude) {
        String str_longitude = ""+longitude;
        //recherche le point
        int indexOfPoint = str_longitude.indexOf('.');
        String tmp = str_longitude.substring(0 , indexOfPoint+3);
        //on retourne le longitude a deux chiffre pres mais sans la virgule ou sans le point.
        return tmp.substring(0 , indexOfPoint)+tmp.substring(indexOfPoint+1);

    }


}