package cg.code.aleyam.nzela_nzela.data_service;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;

import cg.code.aleyam.nzela_nzela.R;

public class Loader {

    private static Loader instance = null;
    private  Dialog dial = null;
    private  boolean isRunning = true;


    public static Loader getInstance(Context context) {

        //si il y a un chargement en cours ce dernier doit etre stoppe pour que celui demande puisse se lancer
        if(instance != null ) {
//          Log.e("test", "le focus pour l'instant "+instance.dial.getCurrentFocus());
            dismiss();

        }
        instance = new Loader();






            if (context != null) {
                if(((Activity)context).isFinishing()) {
                    //on tate l'etat de l'activitee appelante pour eviter un crash en bas
                    instance.isRunning = false;
                    instance.dial = null;
                }
                if(instance.isRunning) {
                    //on lance le chargement en arriere plan...
                    instance.dial = new Dialog(context);
                    instance.dial.setContentView(R.layout.dialog_loader);
                    instance.dial.setTitle("chargement");
                    instance.dial.setCancelable(false);
                }
            }

        //}
        return instance;
    }

    private Loader() {

    }

    public void load (boolean isRunning) {
        if(isRunning) {

            Log.e("test", "dialogue de chargement: "+instance.dial );
            if(instance.dial != null){
                instance.dial.show();
                instance.isRunning = true;
            }

        }


    }
    public  static void dismiss () {
        if(instance != null) {
            if(instance.isRunning) {
                if(instance.dial != null && instance.dial.isShowing()) {
                    instance.dial.dismiss();


                }
                instance = null;

            }
        }
    }


}