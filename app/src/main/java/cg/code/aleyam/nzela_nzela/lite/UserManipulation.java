package cg.code.aleyam.nzela_nzela.lite;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import cg.code.aleyam.nzela_nzela.actu.connexion.Upload;
import cg.code.aleyam.nzela_nzela.transaction.connexion.UserObject;
import cg.code.aleyam.nzela_nzela.transaction.connexion.UserOperationCallback;
import cg.code.aleyam.nzela_nzela.transaction.connexion.UserPostObject;

public class UserManipulation {


    private static String fire_user_ref = "users";


    public static void addUser(final UserObject user , final UserOperationCallback uoc) {

        //faudrait revoir l'utilite de UserID dans notre cas.
        //String userID = Authentication.authentication.getCurrentUser().getUid();

        Map<String , Object> userAddMap = new HashMap<>();
        userAddMap.put(fire_user_ref+"/"+user.getContact()
                        , new UserPostObject(user.getNom()
                            , user.getPrenom()
                            , user.getContact_proche()
                            , user.getAdresse()
                            , user.getSexe()  ));
        Task userAddTask = Upload.data_root_ref.updateChildren(userAddMap);
        userAddTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                //TODO traiter l ajout de l utilisateur
                uoc.succes(user);
            }
        });

        userAddTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //TODO traiter l echec de l ajout de l utilisateur
                uoc.failed();
            }
        });

    }
    public static void getUserByPhoneNumber(final String userPnumber , final UserOperationCallback uoc) {

        DatabaseReference user_ref = Upload.data_root_ref.child("users/"+userPnumber);
        user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    UserPostObject upo = dataSnapshot.getValue(UserPostObject.class);
                    uoc.succes(new UserObject(upo.getNom() ,
                            upo.getPrenom() ,
                            upo.getContact_proche() ,
                            userPnumber ,
                            upo.getAdresse() ,
                            upo.getSexe()));
                } else {
                    uoc.failed();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                uoc.failed();
            }
        });


    }

    public static void isCommentAllowed(final String userPnumber , final UserOperationCallback uoc ,final String key) {
        Log.e("test" ,  "isCommentAllowed: "+"users/"+userPnumber+"/comment");
        DatabaseReference user_ref = Upload.data_root_ref.child("users/"+userPnumber+"/comment");
        user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    //si la donnee pointee existe belle et bien alors au travail.
                    Object object = dataSnapshot.getValue();
                    uoc.operationResult(key , object);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                uoc.failed();
            }
        });
    }

    public static void notifyComment(final String userPnumber , final UserOperationCallback uoc ,final String key) {
        DatabaseReference user_ref = Upload.data_root_ref.child("users/"+userPnumber+"/comment");
        user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    //si la donnee pointee existe belle et bien alors au travail.
                    dataSnapshot.getRef().setValue(false);
                    uoc.operationResult(key , true );
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                uoc.failed();
            }
        });
    }





}