package cg.code.aleyam.nzela_nzela.authentication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import cg.code.aleyam.nzela_nzela.centrale.Centrale_activity;
import cg.code.aleyam.nzela_nzela.depart.InfoGenFragment;
import cg.code.aleyam.nzela_nzela.R;
import cg.code.aleyam.nzela_nzela.actu.Actu_route;
import cg.code.aleyam.nzela_nzela.data_service.Loader;
import cg.code.aleyam.nzela_nzela.lite.DatabaseManager;
import cg.code.aleyam.nzela_nzela.lite.UserManipulation;
import cg.code.aleyam.nzela_nzela.transaction.connexion.UserObject;
import cg.code.aleyam.nzela_nzela.transaction.connexion.UserOperationCallback;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class Authentication extends AppCompatActivity implements View.OnClickListener , UserOperationCallback{

    public static FirebaseAuth authentication = FirebaseAuth.getInstance();
    private FirebaseUser utilisateur = null;
    private String[] submitText = {"envoyer" , "verifier le code" };
    private boolean textChanged = false;
    private DatabaseManager dbManager = null;
    private ProgressBar progress = null;
    private TextInputEditText phone_number = null , code = null;
    private TextInputLayout code_layout = null;
    private FloatingTextButton valider = null;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks ovscc = null;
    private String id = null;
    private TextView info_sup = null;
    private Button passer = null;
    private String user_phone = "";
    private UserObject userObject = null;
    private Loader chargeur = null;
    public static final String fromAuth = "cg.code.aleyam.nzela_nzela.authentication.me";

    private AlertDialog dialog = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        dialog = new AlertDialog.Builder(Authentication.this)
                .setMessage(InfoGenFragment.fromHtml("le numero entré est <strong>incorrecte</strong>"))
                .setTitle("Erreur")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
        .create();

        utilisateur = authentication.getCurrentUser();
        DatabaseManager db_manger = DatabaseManager.getInstance(Authentication.this);
        String[] user_info = db_manger.getCurrentUser();
        if(isLogged(utilisateur) && user_info != null) {
           goToHome();
        }
        phone_number = findViewById(R.id.edit_number);
        code = findViewById(R.id.edit_code);
        code_layout = findViewById(R.id.code);
        valider = findViewById(R.id.valider);
        progress = findViewById(R.id.progress);
        info_sup = findViewById(R.id.information);
        info_sup.setText(InfoGenFragment.fromHtml("Assurez vous d'avoir les services <strong>google play store</strong> à " +
                "jours avant de vous authentifier, <strong>sinon</strong> cliquez sur <strong>Passer</strong> afin de s'authentifier plus tard"));
        info_sup.setTextSize(11);
        passer = findViewById(R.id.passer);
        passer.setOnClickListener(Authentication.this);

        ovscc = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                e.printStackTrace();
                Toast.makeText(Authentication.this , "Impossible d'envoyer le code "+e.getMessage() , Toast.LENGTH_LONG).show();
                progress.setVisibility(View.INVISIBLE);
                phone_number.setEnabled(true);
                valider.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                id = s;
                code_layout.setVisibility(View.VISIBLE);
                valider.setTitle(submitText[1]);
                valider.setVisibility(View.VISIBLE);
                textChanged = true;
                progress.setVisibility(View.INVISIBLE);
                valider.setEnabled(true);
                Toast.makeText(Authentication.this , "code envoye" , Toast.LENGTH_LONG).show();

            }
        };


        valider.setOnClickListener(this);
        dbManager = DatabaseManager.getInstance(Authentication.this);


    }

    public boolean isLogged(FirebaseUser utilisateur) {
        return utilisateur != null ;
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.passer) {
            Intent i = new Intent(Authentication.this , Centrale_activity.class);
            Authentication.this.startActivity(i);
            if(dbManager != null)
                dbManager.closeDB();
            Authentication.this.finish();
        } else {
            user_phone = phone_number.getText().toString();
            if (textChanged) {
                //ici on a recu le code de verification
                //on se retrouve donc ici quand c est pour faire la verification finale
                if(chargeur == null) {
                    chargeur = Loader.getInstance(Authentication.this);
                    chargeur.load(true);
                }

                String activation_code = code.getText().toString();
                PhoneAuthCredential user_credential = PhoneAuthProvider.getCredential(id, activation_code);
                signInWithCredential(user_credential);


            } else {
                //event quand on a fait la demande de code
                final String num_tel = phone_number.getText().toString();

                if(TextUtils.isEmpty(num_tel)) {
                    //donc si le numero est vide
                    dialog.show();

                } else {

                    AlertDialog dialog = new AlertDialog.Builder(Authentication.this)
                            .setMessage(InfoGenFragment.fromHtml("vous confirmez que votre numero est bien le <strong>" + num_tel + "</strong>"))
                            .setTitle("confirmation")
                            .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    phoneVerify(num_tel);
                                    phone_number.setEnabled(false);
                                    valider.setVisibility(View.INVISIBLE);
                                    progress.setVisibility(View.VISIBLE);

                                }
                            })
                            .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create();
                    dialog.show();

                }


            }
        }

    }


    public void phoneVerify(String phone) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,
                60,
                TimeUnit.SECONDS,
                Authentication.this,
                ovscc

        );
    }

    public void signInWithCredential(PhoneAuthCredential phoneAuthCredential) {
        authentication.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {
                    //on verifie si un utilisateur est connecte avec ce meme numero
                    UserManipulation.getUserByPhoneNumber(user_phone , Authentication.this);
                } else {
                    Loader.dismiss();
                    //puis on demande de renvoyer le code
                    Toast.makeText(Authentication.this , "impossible de verifier le code" , Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void succes(UserObject userObject) {
        this.userObject = userObject;
        Loader.dismiss();
        finishRegister(this.userObject.getPrenom());
    }

    @Override
    public void failed() {
        Loader.dismiss();
        goToRegister();
    }

    @Override
    public void operationResult(String key, Object object) {

    }

    public void finishRegister(String prenom) {
        AlertDialog ad = new AlertDialog.Builder(Authentication.this)
                .setTitle("Enregistrement")
                .setMessage(InfoGenFragment.fromHtml("Content de vous revoir parmis nous <strong>"+prenom+"</strong>, " +
                        "voulez vous <strong>redefinir</strong> vos informations personnelles ou <strong>conserver</strong> les ancienne?"))
                .setPositiveButton("Redefinir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goToRegister();
                    }
                })
                .setCancelable(false)
                .setNegativeButton("Conserver", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbManager.setUserInfo(new String[]{ userObject.getNom() ,
                                userObject.getPrenom() ,
                                userObject.getContact() ,
                                userObject.getContact_proche() ,
                                userObject.getSexe() ,
                                userObject.getAdresse()
                        });
                        goToHome();
                    }
                })
                .create();
        ad.show();
    }


    public void goToHome() {
        Intent home_intent = new Intent(Authentication.this , Centrale_activity.class);
        if(dbManager != null)
            dbManager.closeDB();
        startActivity(home_intent);
        finish();
    }
    public void goToRegister() {
        Intent i = new Intent(Authentication.this , User_info_input.class);
        i.putExtra(fromAuth , user_phone);
        startActivity(i);
        if(dbManager != null)
            dbManager.closeDB();
        finish();
    }

}
