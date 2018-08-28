package cg.code.aleyam.nzela_nzela.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Set;

import cg.code.aleyam.nzela_nzela.actu.connexion.Upload;
import cg.code.aleyam.nzela_nzela.lite.DatabaseManager;
import cg.code.aleyam.nzela_nzela.transaction.connexion.UserObject;

public class SettingsManager {

    private static SettingsManager instance = null;
    private SharedPreferences sp = null;
    private Context ct = null;
    public final static String CHANGE_NAME = "NAME" , CHANGE_LASTNAME = "LN" , CHANGE_PHONE = "CP" , CHANGE_CPHONE = "CCP" , CHANGE_ADRESS = "CA";
    private static String[] infoUser = null;

    public static SettingsManager getInstance(Context ct) {

        instance = new SettingsManager(ct);
        return instance;

    }

    public SharedPreferences getSharedPreferences() {
        return sp;
    }

    private SettingsManager(Context ct) {
      sp = PreferenceManager.getDefaultSharedPreferences(ct);
      this.ct = ct;
    }

    public void initUserInfo() {
        DatabaseManager db = DatabaseManager.getInstance(this.ct);
        infoUser = db.getCurrentUser();
        if(infoUser != null) {

            sp.edit().putString( "key_pref_user_name", infoUser[0]).apply();
            sp.edit().putString( "key_pref_user_lastname", infoUser[1]).apply();
            sp.edit().putString( "key_pref_user_pnumber", infoUser[2]).apply();
            sp.edit().putString( "key_pref_user_cpnumber", infoUser[3]).apply();
            sp.edit().putString( "key_pref_user_adress", infoUser[5]).apply();
        }
        db.closeDB();

    }

    public Set<String> getUserPreferedEvent() {
        return sp.getStringSet("key_pref_navigation_events" , new HashSet<String>());
    }

    public void verifyAdminPass(final String prefKey , final String pass , final SettingOperationListener sol) {


        Upload.data_root_ref.child("randomData/tmpPass").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(pass.equals(dataSnapshot.getValue())) {
                    sol.completed(prefKey , true);
                } else {
                    //attention pas connecte
                    Upload.data_root_ref.child("users/"+infoUser[2]+"/password").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists() && pass.equals(dataSnapshot.getValue())) {
                                sol.completed(prefKey , true);
                            } else {
                                sol.completed(prefKey , null);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            sol.completed(prefKey , databaseError);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                sol.completed(prefKey , databaseError);
            }
        });

    }

    public void setUserInfo(String change_key , final String value) {
        final DatabaseManager db = DatabaseManager.getInstance(this.ct);
        if(infoUser == null) {
            infoUser = db.getCurrentUser();
        }
        switch (change_key) {

            case CHANGE_NAME:
                if(infoUser != null) {

                    Upload.data_root_ref.child("users/"+infoUser[2]+"/nom").setValue(value).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            db.setName(value);
                        }
                    });

                }
                break;
            case CHANGE_LASTNAME:

                if(infoUser != null) {

                    Upload.data_root_ref.child("users/"+infoUser[2]+"/prenom").setValue(value).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            db.setLastName(value);
                        }
                    });

                }
                break;
            case CHANGE_PHONE:
                if(infoUser != null && !value.equalsIgnoreCase(infoUser[2])) {

                    Upload.data_root_ref.child("users/"+value).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.exists()) {
                                Upload.data_root_ref.child("users/"+infoUser[2]).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        UserObject uo = dataSnapshot.getValue(UserObject.class);
                                        dataSnapshot.getRef().getParent().child(value).setValue(uo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                db.setPhone(value);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            } else {
                                Toast.makeText(ct , "Un Compte est deja associe a ce numero" , Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                break;
            case CHANGE_CPHONE:
                if(infoUser != null) {

                    Upload.data_root_ref.child("users/"+infoUser[2]+"/contact_proche").setValue(value).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            db.setTelProch(value);
                        }
                    });

                }
                break;
            case CHANGE_ADRESS:
                if(infoUser != null) {

                    Upload.data_root_ref.child("users/"+infoUser[2]+"/adresse").setValue(value).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            db.setAdress(value);
                        }
                    });

                }
                break;
        }
        db.closeDB();

    }



    public interface SettingOperationListener {
        void completed(String key , Object object) ;
    }

    public void addUserInAlert(final String alerts , final String recipient , final String key , final SettingOperationListener sol) {
        Upload.data_root_ref.child("users/"+recipient).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Upload.data_root_ref.child("alertRequest/"+recipient).setValue(alerts);
                    sol.completed(key , true);
                } else {
                    sol.completed(key , null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                sol.completed(key , databaseError);
            }
        });

    }


}
