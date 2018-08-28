package cg.code.aleyam.nzela_nzela.authentication;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import javax.xml.transform.Result;

import cg.code.aleyam.nzela_nzela.R;
import cg.code.aleyam.nzela_nzela.actu.Actu_route;
import cg.code.aleyam.nzela_nzela.centrale.Centrale_activity;
import cg.code.aleyam.nzela_nzela.check.CommunicationCheck;
import cg.code.aleyam.nzela_nzela.data_service.Loader;
import cg.code.aleyam.nzela_nzela.enregistrement.Enregistrement_fragment;
import cg.code.aleyam.nzela_nzela.lite.DatabaseManager;
import cg.code.aleyam.nzela_nzela.lite.UserManipulation;
import cg.code.aleyam.nzela_nzela.transaction.connexion.UserObject;
import cg.code.aleyam.nzela_nzela.transaction.connexion.UserOperationCallback;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class User_info_input extends AppCompatActivity implements
        Enregistrement_fragment.RegisterEvent ,
        View.OnClickListener , UserOperationCallback {

    public final static String DESTINATION = "cg.code.aleyam.nzela_nzela.authentication.me";
    boolean registed = false;
    Button terminer = null;
    Enregistrement_fragment fragment_info_user = null;
    String[] user_infos = null;
    String user_pnumber = "";


    private DatabaseManager databaseManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_input);
        databaseManager = DatabaseManager.getInstance(User_info_input.this);
        Intent from_firt_act = getIntent();
        user_pnumber = from_firt_act.getStringExtra(Authentication.fromAuth);

        terminer = findViewById(R.id.terminer);
        terminer.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.terminer) {
            if(CommunicationCheck.isConnectionAvalable(User_info_input.this)) {
                //si dans la finalisation de l'enregistrement y'a toujours la connexion internet alors on porsuit

                user_infos = fragment_info_user.getUser_infos();
                if(user_infos == null) {
                    Snackbar.make(terminer.getRootView() , "Formulaire Incomplet, Completez le SVP" , Snackbar.LENGTH_SHORT).show();
                } else {
                    Loader.getInstance(User_info_input.this).load(true);
                    UserManipulation.addUser(new UserObject(user_infos[0]
                            ,user_infos[1]
                            ,user_infos[3]
                            ,user_infos[2]
                            ,user_infos[5]
                            ,user_infos[4]  ) , User_info_input.this);
                }

            } else {
                //plus de conneion internet.

            }
            //exection code terminer



        }
    }

    @Override
    public void operationResult(String key, Object object) {

    }

    @Override
    public void succes(UserObject userObject) {
        finishRegister();
    }

    @Override
    public void failed() {

        AlertDialog al = new AlertDialog.Builder(User_info_input.this)
                .setTitle("Rapport Erreur")
                .setMessage("vos information n'ont pas totalement ete envoyer voulez vous reevoyer les informations?")
                .setNegativeButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        User_info_input.this.onClick(terminer);
                    }
                })
                .setPositiveButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        //
    }

    private void finishRegister() {
        databaseManager.setUserInfo(new String[]{user_infos[0] == null?"":user_infos[0] ,
                user_infos[1] == null?"":user_infos[1] ,
                user_pnumber ,
                user_infos[3] == null?"":user_infos[3] ,
                user_infos[4] == null?"":user_infos[4] ,
                user_infos[5] == null?"":user_infos[5]
        });

        //fin du chargement...
        Loader.dismiss();
        databaseManager.closeDB();
        goToHome();
    }

    public void goToHome() {
        Intent i = new Intent(User_info_input.this , Centrale_activity.class);
        startActivity(i);
        if(databaseManager != null)
            databaseManager.closeDB();
        finish();
    }

    //callback procedure
    @Override
    public void eventHandler(FloatingTextButton ftb_source, boolean inconformite, boolean empty_error) {
        //aucunement besoin
    }



    @Override
    protected void onResume() {

        super.onResume();
        if(fragment_info_user == null)
        notifyUserRegister();

    }


    public void notifyUserRegister() {
        //Toast.makeText(User_info_input.this , "Modifications en cours" , Toast.LENGTH_SHORT).show();

            Fragment tmp = getSupportFragmentManager().findFragmentById(R.id.user_info);
            if(tmp instanceof Enregistrement_fragment) {
                fragment_info_user = (Enregistrement_fragment)tmp;
                fragment_info_user.notifyOnRegisterUser(user_pnumber);
            }

    }

}
