package cg.code.aleyam.nzela_nzela.actu;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.model.LatLng;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import cg.code.aleyam.nzela_nzela.OttoBus;
import cg.code.aleyam.nzela_nzela.R;
import cg.code.aleyam.nzela_nzela.actu.connexion.ActuObject;
import cg.code.aleyam.nzela_nzela.actu.connexion.UpdateFollowable;
import cg.code.aleyam.nzela_nzela.actu.connexion.Upload;
import cg.code.aleyam.nzela_nzela.actu.map.Positionement;
import cg.code.aleyam.nzela_nzela.authentication.Authentication;
import cg.code.aleyam.nzela_nzela.centrale.Centrale_activity;
import cg.code.aleyam.nzela_nzela.lite.DatabaseManager;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class Report extends AppCompatActivity implements
        constatFragment.OnListFragmentInteractionListener,
        View.OnClickListener ,
        PictureReport.OnFragmentInteractionListener ,
        UpdateFollowable{


    FloatingTextButton envoyer = null , ajouter = null;
    public static boolean isRunning = false;

    public static String FROM_REPORT = "cg.code.fileProvider";
    public static int TAKE_PIC = 4;
    private boolean comitPublication = false;

    TextInputEditText poste = null;
    int selected_index = -1;
    boolean onlyText = true;
    String[] current_user = null;
    private String postedImagePath;
    private Uri photoUri = null;
    boolean police_authority = false;
    public AlertDialog localisation = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        isRunning = true;
        if(savedInstanceState != null) {
            //recuperation de l'activite.
            postedImagePath = savedInstanceState.getString(PictureReport.SAVED_PATH_KEY);
        }

        DatabaseManager dm = DatabaseManager.getInstance(Report.this);
        current_user = dm.getCurrentUser();
        envoyer =  findViewById(R.id.send);
        envoyer.setOnClickListener(this);
        ajouter =  findViewById(R.id.add);
        ajouter.setOnClickListener(onClickAjouter);
        poste =  findViewById(R.id.inner_post);
        dm.closeDB();



    }

    private View.OnClickListener onClickAjouter = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(ContextCompat.checkSelfPermission(Report.this , android.Manifest.permission.CAMERA ) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(Report.this , android.Manifest.permission.WRITE_EXTERNAL_STORAGE ) == PackageManager.PERMISSION_GRANTED ) {
                if(!TextUtils.isEmpty(poste.getText().toString().trim())) {
                    launchCamera();
                } else {
                    Toast.makeText(Report.this , "accampagnez votre constant d'une description textuelle" , Toast.LENGTH_LONG).show();
                }
            } else {
//                Toast.makeText(Report.this , )
                ActivityCompat.requestPermissions(Report.this
                        , new String[]{android.Manifest.permission.CAMERA
                                , android.Manifest.permission.WRITE_EXTERNAL_STORAGE} , TAKE_PIC);
            }

        }
    };




    @Override
    public void onClick(View v) {
          comitPublication = true;
          publier();
    }

    public void publier() {

        if(selected_index == -1) {
            //impossible de faire la transaction car il manque des informations.
            AlertDialog no_event = new AlertDialog.Builder(Report.this)
                    .setMessage("votre constat n'a aucune signalisation veuillez selectionner une icon afin de publier votre constat ")
                    .setTitle("Incomplet").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            no_event.show();
            comitPublication = false;

        } else {
            String text_4_post = poste.getText().toString().trim();
            Log.e("test", "publier: "+text_4_post );
            if(photoUri == null) {
                //donc le constat n'a pas de photo
                Toast.makeText(Report.this , "Acompagnez votre constat d'une description photographique svp :)" , Toast.LENGTH_LONG).show();
                comitPublication = false;
                return;
            }
            if(!TextUtils.isEmpty(text_4_post)) {
                //toute cette mascarade de code n'a de sens que pour le test.

                   comitPublication = publierLeConstat(text_4_post , false);

            }
        }

    }


    /**
     * methode secondaire permettant de publier un constat
     * @param text_4_post
     * @param backOps boolean pour dire si la methode est appelee en premier ou arriere plan.
     * @return
     */
    public boolean publierLeConstat(final String text_4_post , boolean backOps) {

        Upload upload = Upload.getInstance(Report.this);
        //TODO regler le problem de permission de positionement

        Location location = Positionement.getInstance(Report.this).getUserLastLocation();
        LatLng position = null;
        if(location != null) {
            position = new LatLng(location.getLatitude() , location.getLongitude());
            comitPublication = false;
        }

        if(position == null) {
            //si la position est indisponble alors on afiche un message disant que cette derniere est en recuperation
            if(!backOps) {
                this.localisation = new AlertDialog.Builder(Report.this)
                        .setTitle("Loclisation")
                        .setCancelable(false)
                        .setMessage("Recuperation de la position, cela peut prendre jusqu'a une minute...")
                        .setPositiveButton("Annuler", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                comitPublication = false;
                            }
                        })
                        .create();
                localisation.show();
                //on le met a true parce que dans ce cas la on a besoin que la methode soit appelee une fois que la position est trouvee.
                comitPublication = true;
            } else {
                //et si apres avoir fait la demande de position on a toujours un null alors on passe directement ici sans reaficher la boite de dialogue au dessus
                this.localisation.setMessage("Impossible de recuperer votre position, verifiez votre connexion ainsi que vos capteurs puis reesayez");
                this.localisation.getButton(AlertDialog.BUTTON_POSITIVE).setText("Ok");
                comitPublication = false;
            }
        } else {
            try {

                if(current_user != null && photoUri != null) {
                    //on envoie le constat au serveur grace a la methode UploadConstat
                    upload.UploadConstat(
                            new ActuObject(
                                    "" ,
                                    current_user[1] ,
                                    text_4_post ,
                                    0 ,
                                    position.latitude ,
                                    position.longitude ,
                                    new Date().getTime() ,
                                    selected_index , new Date().getTime() , 0 ,0) , postedImagePath , Report.this);
                } else {

                    if(current_user == null) {
                        new AlertDialog.Builder(Report.this)
                                .setTitle("Authentification")
                                .setMessage("Impossible de Publier des constats, vous devez vous Authentifier avant")
                                .setPositiveButton("Compris", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .setNegativeButton("S'authentifier", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Report.this.startActivity(new Intent(Report.this , Authentication.class));
                                        Report.this.finish();
                                        try {
                                            Centrale_activity.getActivityInstance().finish();
                                        } catch (Exception e) {
                                            Log.e("test" , "onClick: Report central is null" );
                                        }
                                    }
                                }).create().show();
                    }

                }


            } catch (Exception e) {
                //exception de cast
                e.printStackTrace();
            }

            return false;

        }
        return true;

    }

    public void launchCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //on verifie dab si une application est capble de traiter l'intention que lance l'app a cette instant.
        if(cameraIntent.resolveActivity(getPackageManager())!= null) {
            //ici on est bien sur de pouvoir avoir une reponse venant d'une autre app
            //donc on lance l'activite en question.
            File imageFile = null;
            try {
                imageFile = createPostImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(imageFile != null) {
                try {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        //donc si l'appareil de l'utilisateur a la version Nougat ou plus.
                        photoUri = FileProvider.getUriForFile(
                                Report.this, Report.this.getApplicationContext().getPackageName() + ".actu.provider.FournisseurGenerale", createPostImageFile());
                    } else {
                        photoUri = Uri.fromFile(imageFile);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT , photoUri);
                //cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(cameraIntent , TAKE_PIC);
            } else {
                Toast.makeText(Report.this , "Ecriture Impossible, la permission d'ecriture a ete refusée" , Toast.LENGTH_LONG).show();
                imageFile = new File("emulated/0/pope.jpg");
                photoUri = Uri.fromFile(imageFile);
                startActivityForResult(cameraIntent , TAKE_PIC);
            }


        }



    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //on commence par sauvegarder le chemin du fichier jpg que la camera a capturee.
        outState.putString(PictureReport.SAVED_PATH_KEY , postedImagePath);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        OttoBus.bus.unregister(Report.this);
        isRunning = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        OttoBus.bus.register(Report.this);
        isRunning = true;
    }

    @Subscribe
    public void locationNotify(Location location) {
        //donc si le constat est toujours publiable
        if(comitPublication) {
            publierLeConstat(poste.getText().toString().trim() , true);
            //pour s'asurer que l'upload ne soit faite qu'une seule fois.
            comitPublication = false;
        }
    }

    private File createPostImageFile() throws IOException {

        //on cree un style bien definit de date pour avoir des fichier image unique

        String fileName = "JPEG_"+new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        boolean fCreated = false;
        try {
            if(storageDir.exists() && storageDir.isDirectory())
                fCreated = true;
            else
                fCreated = storageDir.mkdirs();

        } catch (Exception e) {
            Log.e("test", "stackTrace: "+Arrays.toString(e.getStackTrace()) );
        }
        File image = null;
        if(fCreated) {
            image = File.createTempFile(fileName , ".jpg" , storageDir);
            postedImagePath = image.getAbsolutePath();
        }

        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == TAKE_PIC) {
            Toast.makeText(Report.this , Arrays.toString(permissions)+"\n"+Arrays.toString(grantResults), Toast.LENGTH_LONG).show();
            for(int autorisee : grantResults) {
                if(autorisee != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(Report.this , "une des permission a ete refusee" ,Toast.LENGTH_SHORT).show();
                    return;
                }
            }
           launchCamera();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == TAKE_PIC) {
            if(resultCode == RESULT_OK) {
                Log.i("test", "onActivityResult: transfert de la photo prise");
                //donc tout ce dont on a faire ici c'est de transferer de partager dans l'application le chemin de la photo prise.
                if(postedImagePath != null)
                    OttoBus.bus.post(postedImagePath);
                else
                    OttoBus.bus.post("");

            }
        }
    }

    @Override
    public void onListFragmentInteraction(int event_selected) {

        this.selected_index = event_selected;
        if(selected_index == 0) {
            //donc si on a clicker sur l'evenement de police
            new AlertDialog.Builder(Report.this)
                    .setTitle("Precision")
                    .setSingleChoiceItems(new String[]{"Evenement suspect, Avertir la Police" , "Policier routier malsein"} , 0 , null)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            int selectedPos = ((AlertDialog)dialogInterface).getListView().getCheckedItemPosition();
                            //ensuite on envoie un ou une alert je sais plus trop.

                        }
                    })
                    .create()
                    .show();
        }
    }

    //permet de retirer la boite de dialogue de l'attente de la localisation
    @Override
    public void notifyDialogCreation() {
        if(this.localisation != null) {
            localisation.dismiss();
        }
    }


    @Override
    public void begin(Button cancel , final Dialog uploadDialog) {

        //TODO revenir pour mieux gerer l'anulation de l'envoie du constat.
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadDialog.dismiss();
            }
        });


    }

    //l'envoie de l'evenement a ete un echec.
    @Override
    public void onFailure(Dialog uploadDialog) {
        uploadDialog.dismiss();
    }

    //le transfert de l'image est entrain d'etre effectue
    @Override
    public void onProgress(Long sent , Long total , ProgressBar uploadState , TextView percent_indicator) {
        //if total => 100%
        //   sent  => x
        long percent = (sent * 100)/total;
        //puis on fait le transfert d'etat.
        //Toast.makeText(Report.this , ""+percent , Toast.LENGTH_SHORT).show();

        String tmp = (int) percent+" %";
        percent_indicator.setText(tmp);
        uploadState.setProgress((int)percent);

    }

    @Override
    public void onSucces(Dialog uploadDialog) {
        uploadDialog.dismiss();
        finish();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
