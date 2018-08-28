package cg.code.aleyam.nzela_nzela.offline;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import cg.code.aleyam.nzela_nzela.depart.InfoGenFragment;
import cg.code.aleyam.nzela_nzela.R;
import cg.code.aleyam.nzela_nzela.enregistrement.Enregistrement_fragment;
import cg.code.aleyam.nzela_nzela.home.Home;
import cg.code.aleyam.nzela_nzela.lite.DatabaseManager;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class OffTransaction extends AppCompatActivity implements  Enregistrement_fragment.RegisterEvent {

    public static String reponses[] = {"demain " , "dans 2-J" , "dans 3-J" , "dans 4-J" , "dans 5-J"  }
            , amonts[]
            , jour = null
            , amont = null
            , aval = null
        //doudou hamed fafana
            , callCenterNo = "+22670009888"
            , fontdebut = "<font color='#000'>"
            , fontfin = "</font>"
            , messfontdebut = "<font color='#333'>"

            ;



    TextView hour_text  = null;
    Button edit_hour = null;
    Spinner reponse_view = null , de , pour;
    Enregistrement_fragment my_fraFragment = null;
    DatabaseManager db_manager = null;
    public static  String[] user_info = null , friend_info = null;
    boolean forMe = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_off_transaction);
        amonts = getResources().getStringArray(R.array.destinations);
        edit_hour = findViewById(R.id.edit_houre);
        hour_text = findViewById(R.id.houre_dep);
        edit_hour.setOnClickListener(listener_for_time);
        reponse_view = findViewById(R.id.jours);
        de = findViewById(R.id.de_spin);
        pour = findViewById(R.id.pour_spin);
        forMe = getIntent().getBooleanExtra(Home.fromHome , false);
        db_manager = DatabaseManager.getInstance(OffTransaction.this);
        user_info = db_manager.getCurrentUser();



        ArrayAdapter<String> day_adapter = new ArrayAdapter<String>(OffTransaction.this , android.R.layout.simple_spinner_item , reponses);
        day_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> de_adapter = new ArrayAdapter<String>(OffTransaction.this , android.R.layout.simple_spinner_item , amonts);
        de_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> pour_adapter = new ArrayAdapter<String>(OffTransaction.this , android.R.layout.simple_spinner_item , amonts);
        pour_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        reponse_view.setAdapter(day_adapter);

        de.setAdapter(de_adapter);

        pour.setAdapter(pour_adapter);
       // pour.setOnItemSelectedListener(OffTransaction.this);
        if(db_manager!=null)
            db_manager.closeDB();
    }



    View.OnClickListener listener_for_time = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Calendar calendar = Calendar.getInstance();
           if(view.getId() == R.id.edit_houre) {
                //donc l'utilisateur veut inserer l'heure a laquelle il voyage.
                int h , m;
                h = calendar.get(Calendar.HOUR);
                m = calendar.get(Calendar.MINUTE);
                if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP ) {
                    TimePickerDialog tpd = new TimePickerDialog(OffTransaction.this, android.R.style.Theme_Material_Dialog_Alert , new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int i, int i1) {
                            hour_text.setText(i+"H:"+i1);
                        }
                    } , h , m , true );
                    tpd.show();
                } else {
                    TimePickerDialog tpd = new TimePickerDialog(OffTransaction.this , new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int i, int i1) {
                            hour_text.setText(i+"H:"+i1);
                        }
                    } , h , m , true );
                    tpd.show();
                }

            }
        }
    };



    @Override
    protected void onResume() {
        super.onResume();
        if(forMe) {
            isReallyForMe(forMe , user_info);

        } else {
            if(my_fraFragment == null)
                onOffTransaction();
        }

    }

    public void isReallyForMe(boolean forMe , String[] user_info) {
        if(forMe) {
            Toast.makeText(this, "valuere: "+user_info, Toast.LENGTH_SHORT).show();
            if(user_info == null) {

                AlertDialog alt = new AlertDialog.Builder(OffTransaction.this)
                        .setTitle(InfoGenFragment.fromHtml(fontdebut+"Aucun Compte"+fontfin))
                        .setMessage(InfoGenFragment.fromHtml(messfontdebut+"Aucun compte retrouvé, cette operation ne peut donc pas etre realisée. selectionnez" +
                                "<b> ok->reservation Offline->un proche </b> pour faire une reservation anonyme"+fontfin))
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                                OffTransaction.this.finish();

                            }
                        })
                        .create();
                alt.show();
                Button positive = alt.getButton(AlertDialog.BUTTON_POSITIVE);
                positive.setTextColor(Color.GRAY);
                Button negative = alt.getButton(AlertDialog.BUTTON_NEGATIVE);
                negative.setTextColor(Color.GRAY);

            } else {
                onOwnOffTransaction();
            }

        }
    }

    public void onOffTransaction() {
        Fragment tmp = getSupportFragmentManager().findFragmentById(R.id.off_fragment);
        if(tmp instanceof Enregistrement_fragment) {
            my_fraFragment = (Enregistrement_fragment)tmp;
            my_fraFragment.notifyOnRegisterUser(user_info == null?"":user_info[2]);
            my_fraFragment.setOffMode();
        }
    }
    public void onOwnOffTransaction() {
        Fragment tmp = getSupportFragmentManager().findFragmentById(R.id.off_fragment);
        if(tmp instanceof Enregistrement_fragment) {
            my_fraFragment = (Enregistrement_fragment)tmp;
            my_fraFragment.onCurrentUserTransaction(user_info , false);


        }
    }



    @Override
    public void eventHandler(FloatingTextButton ftb_source, boolean inconformite, boolean empty_error) {

        friend_info = my_fraFragment.getUser_infos();

        if(friend_info == null) {

            Snackbar.make(reponse_view.getRootView() , "Formulaire Incomplet, Completez le SVP" , Snackbar.LENGTH_SHORT).show();

        } else {



            amont = de.getSelectedItem().toString();
            aval = pour.getSelectedItem().toString();
            jour = reponse_view.getSelectedItem().toString();

            Toast.makeText(OffTransaction.this , ""+amont+" "+aval+" "+jour , Toast.LENGTH_SHORT).show();

            if(amont != null && aval != null && jour != null ) {

                final AlertDialog alert = new AlertDialog.Builder(OffTransaction.this)
                        .setTitle(InfoGenFragment.fromHtml(fontdebut+"Confirmation"+fontfin))
                        .setMessage(InfoGenFragment.fromHtml(messfontdebut+"Etes vous sure de voiloir reserver?"+fontfin))
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                int permissionVerif = ContextCompat.checkSelfPermission(OffTransaction.this , android.Manifest.permission.SEND_SMS);
                                if(permissionVerif == PackageManager.PERMISSION_GRANTED) {
                                    // Send a text based SMS
                                    sendOfflineMess( callCenterNo ,
                                            transactionToString(new String[]{"\n"+jour , "\n"+amont , "\n"+aval , "\n"+hour_text.getText() ,  "\n"+Arrays.toString(friend_info) } , forMe)
                                            , OffTransaction.this
                                    );
                                } else {
                                    dialog.dismiss();
                                    ActivityCompat.requestPermissions(OffTransaction.this , new String[]{android.Manifest.permission.SEND_SMS} , 123);
                                }

                                dialog.dismiss();

                            }
                        })
                        .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                alert.show();
                Button positive = alert.getButton(AlertDialog.BUTTON_POSITIVE);
                positive.setTextColor(Color.GRAY);
                Button negative = alert.getButton(AlertDialog.BUTTON_NEGATIVE);
                negative.setTextColor(Color.GRAY);


            }
        }

    }

    public static String transactionToString(String[] data , boolean forMe ) {


        if(data != null ) {
                if(forMe) {
                    return "Call Center Transaction ARIV , voici mes info:"+Arrays.toString(data)+" pour une reservation";
                } else {
                    return "Call Center Transaction ARIV , voici les info de mon contact:"+Arrays.toString(data)+" pour une reservation";
                }


        }

        return null;
    }


    public static void sendOfflineMess(String callCenterNo , String smsBody , final Activity activity) {

        // Get the default instance of SmsManager
        SmsManager smsManager = SmsManager.getDefault();


        String SMS_SENT = "SMS_SENT";
        String SMS_DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(activity, 0, new Intent(SMS_SENT), 0);
        PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(activity, 0, new Intent(SMS_DELIVERED), 0);

        ArrayList<String> smsBodyParts = smsManager.divideMessage(smsBody);

        ArrayList<PendingIntent> sentPendingIntents = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> deliveredPendingIntents = new ArrayList<PendingIntent>();

        for (int i = 0; i < smsBodyParts.size(); i++) {
            sentPendingIntents.add(sentPendingIntent);
            deliveredPendingIntents.add(deliveredPendingIntent);
        }

        Toast.makeText(activity , "Debut du traitement de la requete" , Toast.LENGTH_SHORT ).show();

        // For when the SMS has been sent
        activity.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS sent successfully", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        //quand on a pas de forfait ou de credit
                        Toast.makeText(context, "Generic failure cause", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(context, "Service is currently unavailable", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(context, "No pdu provided", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(context, "Radio was explicitly turned off", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SMS_SENT));

        // For when the SMS has been delivered
        activity.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(activity, "SMS delivered", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(activity, "SMS not delivered", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SMS_DELIVERED));
        smsManager.sendMultipartTextMessage(callCenterNo, null, smsBodyParts, sentPendingIntents, deliveredPendingIntents);






    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 123) {
            if(grantResults != null && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendOfflineMess( callCenterNo ,
                        transactionToString(new String[]{"\n"+jour , "\n"+amont , "\n"+aval , "\n"+Arrays.toString(friend_info) } , false)
                        , OffTransaction.this
                );

            } else {
               Toast.makeText(OffTransaction.this , "SVP? :( " , Toast.LENGTH_LONG).show();
            }
        }
    }
}
