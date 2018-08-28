package cg.code.aleyam.nzela_nzela.transaction.connexion;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import cg.code.aleyam.nzela_nzela.actu.connexion.Upload;
import cg.code.aleyam.nzela_nzela.analytics.Analytics;
import cg.code.aleyam.nzela_nzela.lite.UserManipulation;
import cg.code.aleyam.nzela_nzela.transaction.paiement.InternationnalPayment;

public class PostTransaction implements UserOperationCallback {

    public static String transaction_root = "transaction" , depart_root = "Departs" , userRoot = "users" , reserviste_root = "Reservistes" ;
    private static boolean exist = false;
    //retiens les chemins cle de la transaction afin de les retirer au cas ou...
    private HashMap<String , String> transactionKeys = new HashMap<>();
    private ArrayList<UserObject> requestUser = new ArrayList<>() ;
    private ArrayList<Integer> requestdep = new ArrayList<>() , requestAg = new ArrayList<>();
    private ArrayList<String> couple = new ArrayList<>();
    private ArrayList<Boolean> requestIsReservation = new ArrayList<>();
    private static Context context = null;
    private static PostTransaction instance = null;
    private TransactionCallback transactionCallback = null;
    public static String userPhoneNumber = "";
    public final static String TRANSACTION_MAIN_KEY_RESERVISTE = "reserviste";
    public final static String TRANSACTION_MAIN_KEY_USER = "user_transaction";
    public final static String TRANSACTION_MAIN_KEY_PLACES = "places_nbr";
    public final static String AGENCE_KEY = "agence_id";
    public final static String TRANSACTION_RUNNING = "en cours...";
    public final static String TRANSACTION_ok = "confirmée";
    public static String user_pre_transaction_key = "preTrans/";



    public static PostTransaction getInstance(String userPhoneNumber , Context context) {
        if(instance == null) {
            instance = new PostTransaction();
            PostTransaction.userPhoneNumber = userPhoneNumber;
            user_pre_transaction_key+=userPhoneNumber;
            PostTransaction.context = context;
        }
        return instance;
    }

    public static String getCommentpermissionKey() {
        return userRoot+"/"+userPhoneNumber+"/comment";
    }

    public  void requestTicket(final UserObject userObject
            , final int id_depart
            , final int id_agence
            , TransactionCallback transactionCallback
            , final boolean isReservation
            , final String coupleAmontAval
    ) {

        this.transactionCallback = transactionCallback;

        Query retreiveUserQuery = Upload.data_root_ref.child(userRoot).orderByKey().equalTo(userObject.getContact());
        retreiveUserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                exist = !(dataSnapshot.getValue() == null) ;
                if(exist) {

                    beginTransaction(userObject.getContact() , id_depart , id_agence ,  isReservation , coupleAmontAval);

                } else {
                    //on cree un utilisateur
                    requestUser.add(userObject);
                    requestdep.add(id_depart);
                    requestAg.add(id_agence);
                    couple.add(coupleAmontAval);
                    requestIsReservation.add(isReservation);
                    UserManipulation.addUser(userObject , PostTransaction.this);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void operationResult(String key, Object object) {

    }

    //on avait pas d'utilisateur on en a mainternant
    @Override
    public void succes(UserObject userObject) {
        int id_depart = requestdep.get(requestUser.indexOf(userObject));
        int id_agence = requestAg.get(requestUser.indexOf(userObject));
        Boolean isReservation = requestIsReservation.get(requestUser.indexOf(userObject));
        String couple = this.couple.get(requestUser.indexOf(userObject));
        beginTransaction(userObject.getContact() , id_depart , id_agence , isReservation , couple);
    }

    @Override
    public void failed() {
        transactionCallback.TransactionFailed("Erreur survenue lors du debut de la transaction, verifier votre connection Internet" , true);
    }

    private void beginTransaction (final String user_contact , final int id_depart , final int id_agence , final boolean isReservation , final String coupleAmontAval) {
        final  long transaction_time = new Date().getTime();
        final String reserviste_path = reserviste_root+"/"+id_depart+"/"+transaction_time;

        Upload.data_root_ref.child(user_pre_transaction_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    //ca veut dire que cette utilisateur etait entrain de faire une transaction qu'il n'a pas finis
                    //on passe directement a l'etape suivante qui consiste a transferer l'argent.
                    HashMap<String , String> dataKey = new HashMap<>();
                    dataKey.put(TRANSACTION_MAIN_KEY_RESERVISTE , dataSnapshot.child(TRANSACTION_MAIN_KEY_RESERVISTE).getValue().toString());
                    dataKey.put( TRANSACTION_MAIN_KEY_USER, dataSnapshot.child(TRANSACTION_MAIN_KEY_USER).getValue().toString());
                    dataKey.put(TRANSACTION_MAIN_KEY_PLACES , dataSnapshot.child(TRANSACTION_MAIN_KEY_PLACES).getValue().toString());
                    dataKey.put(AGENCE_KEY , dataSnapshot.child(AGENCE_KEY).getValue().toString());
                    transactionCallback.TransactionSucceded("cette transaction est Inachevee voulez vous la finaliser?", dataKey);
                } else {
                    addNewTempTransaction(transaction_time , user_contact , reserviste_path , id_depart , isReservation , id_agence , coupleAmontAval);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                transactionCallback.TransactionFailed("Transaction impossible code erreure: "+databaseError.getCode() , false);
            }
        });
    }

    /**
     * methode permettant d'ajouter toutes les cles principale de la transaction dans un nouveau noeud au cas les
     * transactions ne se deroulaient pas correctement ou seraient interrompuent afin de les reprendre.
     *
     * @param update_map
     * @param transMainKeys
     * @param PreTrans_userKey
     * @return
     */
    private Map<String , Object> notifyPreTransaction(Map<String , Object> update_map , HashMap<String , String> transMainKeys , String PreTrans_userKey) {
        update_map.put(PreTrans_userKey+"/"+TRANSACTION_MAIN_KEY_RESERVISTE , transMainKeys.get(TRANSACTION_MAIN_KEY_RESERVISTE));
        update_map.put(PreTrans_userKey+"/"+TRANSACTION_MAIN_KEY_USER , transMainKeys.get(TRANSACTION_MAIN_KEY_USER));
        update_map.put(PreTrans_userKey+"/"+TRANSACTION_MAIN_KEY_PLACES , transMainKeys.get(TRANSACTION_MAIN_KEY_PLACES));
        update_map.put(PreTrans_userKey+"/"+AGENCE_KEY , transMainKeys.get(AGENCE_KEY));
        return update_map;
    }

    public void removeIncompletTransactions() {
        if(userPhoneNumber != null && !TextUtils.isEmpty(userPhoneNumber.trim())){

            Upload.data_root_ref.child(user_pre_transaction_key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        final Map<String , Object> delete_map = new HashMap<>();
                        delete_map.put((String) dataSnapshot.child(TRANSACTION_MAIN_KEY_RESERVISTE).getValue() , null);
                        delete_map.put((String) dataSnapshot.child(TRANSACTION_MAIN_KEY_USER).getValue() , null);
                        //on recupere le nombre de place disponible sur la transaction precedement entamme
                        final String place_dispo_key = (String) dataSnapshot.child(TRANSACTION_MAIN_KEY_PLACES).getValue();
                        Log.e("test", "place_dispo_key: "+place_dispo_key );
                        Upload.data_root_ref.child(place_dispo_key).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if(snapshot.exists()) {
                                    //on retire toutes les traces de la precedente transaction en cours
                                    long place_dispo = Long.parseLong(""+snapshot.getValue());
                                    delete_map.put(place_dispo_key , ""+(++place_dispo));
                                    Upload.data_root_ref.updateChildren(delete_map);
                                    dataSnapshot.getRef().setValue(null);
                                    new Analytics(PostTransaction.context).notifyCompletedTransaction(place_dispo_key , userPhoneNumber , "annulé");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e("test", "impossible de recupere le nombre de place actuelle " );
                            }
                        });
                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("test", "impossible de recupere les derniere donees" );
                }
            });

        }
    }

    private   void addNewTempTransaction(long transaction_time
            , final String user_contact
            , final String reserviste_path
            , final int id_depart
            , final boolean isReservation
            , final int id_agence
            , final String coupleAmontAval
    ) {

        //on commence par specifier les differents chemins principaux pour effectuer une transaction particulere.
        final String transaction_path = transaction_root+"/"+user_contact+"/"+transaction_time;
        final String depart_path = depart_root+"/"+id_depart+"/place_dispo";

        //et ensuite ces chemins seront conseres sous la cle de l'utilisateur courant just le temps de la transaction.
        transactionKeys.put(TRANSACTION_MAIN_KEY_RESERVISTE, reserviste_path);
        transactionKeys.put(TRANSACTION_MAIN_KEY_USER, transaction_path);
        transactionKeys.put(TRANSACTION_MAIN_KEY_PLACES, depart_path);
        transactionKeys.put(AGENCE_KEY, ""+id_agence);


        //puis on instancie la map qui fait toute l'operation de transaction sur firebase.
        final Map<String , Object> update_map = new HashMap<>();

        //alicia keys blind that family :)
        final DatabaseReference root_ref = Upload.data_root_ref;
        DatabaseReference remainPlaceRef = root_ref.child(depart_path);

        //on lance la transaction sur Firebase.
        remainPlaceRef.runTransaction(new Transaction.Handler() {

            String errorMessage = null;
            int place_dispo = 0 ;
            MutableData mutableData = null;
            TransactionPost tp = null;
            String log = "**************debut de la transaction sur: "+transaction_path+"****************\n";

            //le resultat de la transaction, place de maniere globale afin d'evter le definir final
            Transaction.Result result = Transaction.abort();

            @Override
            public Transaction.Result doTransaction(final MutableData mutable) {

                mutableData = mutable;
                log+=" mutable_data: "+mutableData.toString()+"\n";

                if(mutableData.getValue() == null) {
                    errorMessage = "Impossible de commencer la transaction";
                    log+=" errorMessage : "+errorMessage+"\n";
                    return result;
                }

                place_dispo = Integer.parseInt(""+mutableData.getValue());
                log+=" place_dispo : "+place_dispo+"\n";

                if(place_dispo == 0) {
                    errorMessage = "plus de place pour effectuer une Transaction";
                    return result;
                } else {

                    update_map.put(reserviste_path+"/reservation" , isReservation);
                    update_map.put(reserviste_path+"/user" , user_contact);
                    update_map.put(reserviste_path+"/state" , TRANSACTION_RUNNING);
                    Map<String , Object> update = notifyPreTransaction(update_map , transactionKeys , user_pre_transaction_key);

                    tp = new TransactionPost(id_depart
                            , place_dispo+"-"+id_agence+""+id_depart
                            , isReservation);

                    update.put(transaction_path , tp);

                    log+=" update_map : "+update.toString()+"\n";

                    //TODO mise en place de la recuperation en temps reel des places disponible
                    mutableData.setValue(--place_dispo);

                    //ensuite on submit toute la sauce en attendant le resultat.
                    Task updateTask = root_ref.updateChildren(update);
                    updateTask.addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            new Analytics(context).notifyPreTransaction(""+id_agence , coupleAmontAval , ""+id_depart);
                            transactionCallback.TransactionSucceded("transaction en progression" , transactionKeys);
                        }
                    });
                    updateTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            transactionCallback.TransactionFailed("une Erreur est survenue pendant la transaction" , false);
                        }
                    });

                    return Transaction.success(mutableData);

                }


            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                if(databaseError != null || !b) {
                    if(errorMessage != null) {
                        transactionCallback.TransactionFailed(errorMessage , false);
                        return;
                    }
                    transactionCallback.TransactionFailed("Transaction Impossible erreur_"+databaseError.getCode() , true);
                    transactionElog(log , tp);
                }
            }
        });

    }

    public void transactionElog(String log , TransactionPost tp) {

        DatabaseReference root_ref = Upload.data_root_ref.child("bug");
        root_ref.child("TransactionElog").setValue(tp);
        root_ref.child("Rpath").setValue(log);


    }




}