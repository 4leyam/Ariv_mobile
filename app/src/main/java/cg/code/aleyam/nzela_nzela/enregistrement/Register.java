package cg.code.aleyam.nzela_nzela.enregistrement;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import cg.code.aleyam.nzela_nzela.BottomSheetDialog;
import cg.code.aleyam.nzela_nzela.Settings.SettingsManager;
import cg.code.aleyam.nzela_nzela.depart.InfoGenFragment;
import cg.code.aleyam.nzela_nzela.R;
import cg.code.aleyam.nzela_nzela.check.CommunicationCheck;
import cg.code.aleyam.nzela_nzela.data_service.Loader;
import cg.code.aleyam.nzela_nzela.lite.DatabaseManager;
import cg.code.aleyam.nzela_nzela.offline.OffTransaction;
import cg.code.aleyam.nzela_nzela.transaction.FPMAdapter;
import cg.code.aleyam.nzela_nzela.transaction.FPMCallback;
import cg.code.aleyam.nzela_nzela.transaction.connexion.PostTransaction;
import cg.code.aleyam.nzela_nzela.transaction.connexion.TransactionCallback;
import cg.code.aleyam.nzela_nzela.transaction.connexion.UserObject;
import cg.code.aleyam.nzela_nzela.transaction.paiement.InternationnalPayment;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class Register extends AppCompatActivity implements
        Enregistrement_fragment.RegisterEvent ,
        TransactionCallback , FPMCallback , BottomSheetDialog.SheetCallback {

    private Toolbar register_tool = null;

    private Enregistrement_fragment fragment_user_info = null;
    private FloatingTextButton mes_info_bt;
    private Enregistrement_fragment my_fraFragment;
    private String user_info[] = null;
    private String userPhoneNumber = null;
    //boutton d'action reserver ou acheter
    private FloatingTextButton source;
    private int localPaymentMethode = -1;
    public static final int MOBICASH = 1;
    public static final int INTERNATIONAL = 100;

    String[] title_info = null;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Intent in_intent = getIntent();
        title_info = in_intent.getExtras().getStringArray("info transaction");

        DatabaseManager db_manager = DatabaseManager.getInstance(Register.this);
        user_info = db_manager.getCurrentUser();
        if(user_info != null && user_info.length != 0) {
            this.userPhoneNumber = user_info[2];
        } else {
            this.userPhoneNumber = ""+(new Date()).getTime();
        }


        register_tool = findViewById(R.id.register_toolbar);
        register_tool.setTitle("Agence: "+title_info[0]);
        register_tool.setSubtitle("Cout: "+title_info[1]+", Depart "+title_info[3]+" à "+title_info[2] );

        register_tool.setClickable(true);
        mes_info_bt = findViewById(R.id.bt_mes_info);
        mes_info_bt.setOnClickListener(bt_mes_info);
        isShortcuted(mes_info_bt);
        setSupportActionBar(register_tool);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db_manager.closeDB();

    }

    View.OnClickListener bt_mes_info = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            onOwnOffTransaction();
        }
    };

    public void isShortcuted(View v) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Register.this);
        boolean itIs = sp.getBoolean("pref_trip_shortcut" , SettingsManager.getInfoUser(Register.this) == null);
        if(!itIs)
            //donc le racourcis vers les information est autorise.
            v.setVisibility(View.GONE);
        else  v.setVisibility(View.VISIBLE);
    }

    public void onOwnOffTransaction() {
        android.support.v4.app.Fragment tmp = getSupportFragmentManager().findFragmentById(R.id.fragment_register);
        if(tmp instanceof Enregistrement_fragment) {
            my_fraFragment = (Enregistrement_fragment)tmp;
            if(user_info == null) {
                String mess = "Vous n'avez pas de compte N-N il est donc impossible pour vous d'effectuer des transactions";
                AlertDialog alt = new AlertDialog.Builder(Register.this)
                        .setTitle(InfoGenFragment.fromHtml(OffTransaction.fontdebut+"Aucun Compte"+OffTransaction.fontfin))
                        .setMessage(InfoGenFragment.fromHtml(OffTransaction.messfontdebut+" "+mess+" "+OffTransaction.fontfin))
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                                Register.this.finish();

                            }
                        })
                        .create();

            } else {
                my_fraFragment.onCurrentUserTransaction(user_info , true);
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(fragment_user_info == null) {
            android.support.v4.app.Fragment tmp = getSupportFragmentManager().findFragmentById(R.id.fragment_register);
            if(tmp instanceof Enregistrement_fragment)
                fragment_user_info = (Enregistrement_fragment) tmp;
        }

    }

    @Override
    public void finish() {
        //defini l'information necessaire pour le retour.

        setResult(Activity.RESULT_OK , new Intent() );
        super.finish();
    }

    @Override
    public void eventHandler(FloatingTextButton source , boolean inconformite , boolean empty_error ) {

        boolean achat = false;
        this.source = source;

        user_info = fragment_user_info.getUser_infos();

        if(user_info == null) {
            if (source.getId() == R.id.bt_reserver) {
                // action de realiser une reservation.
                achat = false;
                if(inconformite || empty_error) {
                    Snackbar.make(findViewById(R.id.id_layout_enregistrement)
                            ,"veuillez correctement renseigner les champs avant d"+(achat?new String("'acheter"):new String("e reserver")),Snackbar.LENGTH_SHORT )
                            .show();
                } else  {

                    Snackbar.make(findViewById(R.id.id_layout_enregistrement)
                            ,"veuillez finir le remplissage des champs SVP",Snackbar.LENGTH_SHORT )
                            .show();
                }


            } else if(source.getId() == R.id.bt_acheter) {
                //action de realiser un achat de billet.
                achat = true;
                if(inconformite || empty_error) {

                    Snackbar.make(findViewById(R.id.id_layout_enregistrement)
                            ,"veuillez correctement renseigner les champs avant d"+(achat?new String("'acheter"):new String("e reserver")),Snackbar.LENGTH_SHORT )
                            .show();

                } else {

                    Snackbar.make(findViewById(R.id.id_layout_enregistrement)
                            ,"veuillez finir le remplissage des champs SVP",Snackbar.LENGTH_SHORT )
                            .show();
                }

            }
        } else {

            if(CommunicationCheck.isConnectionAvalable(Register.this)) {
                //a ajouter au paramettre comme option pour retenir la methode de paiement de l'utilisateur
                //donc on commence par demander a l'utilisateur le modele de paiement qu'il veut utiliser
                new AlertDialog.Builder(Register.this)
                        .setTitle("Selection")
                        .setMessage("effectuer la transaction avec les methodes de paiement locales ou internationales?")
                        .setPositiveButton("Locale", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //on lance le bottom sheet dialog pour afficher les solution locales de paiment.
                                selectLocalFPM();
                            }
                        }).setNegativeButton("internationales", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FPMselected(INTERNATIONAL);
                            }
                        }).setCancelable(true).create().show();


            } else {

                AlertDialog alert_transaction = new AlertDialog.Builder(Register.this)
                        .setTitle("Redirection transaction")
                        .setMessage("indisponible pour l'instant poursuivre l'action hors ligne?")
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                OffTransaction.sendOfflineMess( OffTransaction.callCenterNo ,
                                        OffTransaction.transactionToString(new String[]{"\n"+OffTransaction.jour
                                                , "\n"+OffTransaction.amont
                                                , "\n"+OffTransaction.aval
                                                , "\n"+Arrays.toString(user_info) } , true)
                                        , Register.this
                                );
                            }
                        })
                        .setNegativeButton("Non Merci", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();

            }




            //Toast.makeText(Register.this , "Telechargez la dernier version de l'app afin de faire des reservations et des achats" , Toast.LENGTH_SHORT).show();
        }


    }

    public void selectLocalFPM() {

        BottomSheetDialog btsd = new BottomSheetDialog();
        btsd.setSheetCallBack(Register.this);
        btsd.show(getSupportFragmentManager() , btsd.getTag());

    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        View contentView =  View.inflate(Register.this , R.layout.bottom_sheet , null);
        ((TextView)contentView.findViewById(R.id.bottom_sheet_title)).setText("Selectionnez la methode de paiement");
        RecyclerView listePaymentMethod = contentView.findViewById(R.id.local_fpm_list);
        if(listePaymentMethod != null) {
            RecyclerView.LayoutManager lm = new LinearLayoutManager(Register.this , LinearLayout.VERTICAL , false);
            listePaymentMethod.addItemDecoration(new DividerItemDecoration(Register.this , LinearLayout.VERTICAL));
            listePaymentMethod.setHasFixedSize(true);
            listePaymentMethod.setLayoutManager(lm);
            listePaymentMethod.setAdapter(new FPMAdapter(new int[]{R.drawable.mobicash_ic} , new int[]{MOBICASH} , Register.this));
        }
        dialog.setContentView(contentView);
    }


    /**
     * methode appelle quand on a choisis la type de paement mobile et que le paiement est regle
     * cette methode permet de Notifier a la base de donnees de Nzela-Nzela que la transaction a bien ete effectuee et
     * que le client doit etre mis sur la liste des passager du bus
     */
    @Override
    public void FPMselected(int FPM) {
        if(FPM != -1) {
           localPaymentMethode = FPM;
            //ensuite on enregistre provisoirement l'utilisateur
            Loader.getInstance(Register.this).load(true);
            PostTransaction.getInstance(userPhoneNumber
                    , Register.this).requestTicket(new UserObject(user_info[0]
                            , user_info[1]
                            , user_info[3]
                            , user_info[2]
                            , user_info[5]
                            , user_info[4] )
                    , Integer.parseInt(title_info[4])
                    , Integer.parseInt(title_info[5])
                    ,  Register.this
                    , (this.source.getId() == R.id.bt_reserver)
                    , title_info[6]
            );
        } else {
            Toast.makeText(Register.this , "Impossible de proceder a la Transaction..." , Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void TransactionSucceded(String ok_message , HashMap dataKey) {
        Loader.dismiss();
        //on commence le transfert de l'argent.
        switch (localPaymentMethode) {
            case (MOBICASH):
                //comme y'a pas d'api on fait rien o affiche juste le message

                AlertDialog.Builder al = new AlertDialog.Builder(Register.this)
                        .setTitle(InfoGenFragment.fromHtml("<b><font color='#424242'>Information</font></b>"))
                        .setMessage(InfoGenFragment.fromHtml("<font color='#424242'>"+ok_message+"</font>"))
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        })
                        .setCancelable(true);

                al.create().show();
            break;
            case (INTERNATIONAL):
                Log.e("test", "debut de l'envoie de l'argent info: "+Arrays.toString(title_info));
                InternationnalPayment.getInstance(Register.this).LanchDropIn(dataKey , title_info[1]);
                break;
        }



    }

    @Override
    public void TransactionFailed(String error_message, boolean tryOption) {
        Loader.dismiss();
        AlertDialog.Builder al = new AlertDialog.Builder(Register.this)
                .setTitle(InfoGenFragment.fromHtml("<b><font color='#424242'>Information</font></b>"))
                .setMessage(InfoGenFragment.fromHtml("<font color='#424242'>"+error_message+"</font>"))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(true);

        if(tryOption) al.setNegativeButton("Reesayer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(Register.this , "impossible de reesayer" , Toast.LENGTH_SHORT).show();
            }
        });


        al.create().show();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {

            finish();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 123) {
            if(grantResults != null && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                OffTransaction.sendOfflineMess( OffTransaction.callCenterNo ,
                        OffTransaction.transactionToString(new String[]{"\n"+OffTransaction.jour
                                , "\n"+OffTransaction.amont
                                , "\n"+OffTransaction.aval
                                , "\n"+Arrays.toString(user_info) } , true)
                        , Register.this
                );

            } else {
                Toast.makeText(Register.this , "SVP? :( " , Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        InternationnalPayment.getInstance(Register.this).onActivityResult(requestCode, resultCode, data);
    }
}
