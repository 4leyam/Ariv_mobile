package cg.code.aleyam.nzela_nzela.check;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cg.code.aleyam.nzela_nzela.R;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class CommunicationCheck{

   public static class NoConnexionHolder extends RecyclerView.ViewHolder{


        public View errorView = null;
        public Button reessayer = null;
        public TextView message = null;
        public CardView card = null;

        public NoConnexionHolder (View errorView) {
            super(errorView);
            this.errorView = errorView;
            this.message = errorView.findViewById(R.id.error_message);
            this.reessayer = errorView.findViewById(R.id.retry_button);
            this.card = errorView.findViewById(R.id.card_pasDeConnection);

        }

        public void setErrorMessage(String errorMessage , boolean disable_bt) {
            message.setText(errorMessage);
            if(disable_bt) reessayer.setVisibility(View.INVISIBLE);
        }
        public void setRetryAction(View.OnClickListener oclis) {
            this.reessayer.setOnClickListener(oclis);
        }

    }

    public final static String connectionErrorMessage =  new String("Aucune connexion internet, connectez vous puis reessayez");

    public  interface ConnexionError{
       public void setErrorMessage(String message);
    }

    private static boolean isAirPlaneMode(Context context) {
        try {
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
                return Settings.System.getInt(context.getContentResolver() , Settings.System.AIRPLANE_MODE_ON , 0) != 0 ;
             else
                return Settings.System.getInt(context.getContentResolver() , Settings.Global.AIRPLANE_MODE_ON , 0) != 0 ;
        } catch (Exception e) {

        }
        return true;

    }

    public static boolean isConnectionAvalable(Context context) {
        if(!isAirPlaneMode(context)) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);
            NetworkInfo internet_etat = manager.getActiveNetworkInfo();

            if(internet_etat != null && (internet_etat.isAvailable() && internet_etat.isConnectedOrConnecting())) {

                return true;

            } else {

                return false;


            }
        } else return false;

    }
    public static void displayErrorMessage(RecyclerView liste , RecyclerView.Adapter adapter , String message) {
       if(!(liste.getLayoutManager() instanceof LinearLayoutManager)) {
           Toast.makeText(liste.getContext() , "linear manager" , Toast.LENGTH_LONG).show();

           liste.setLayoutManager(new LinearLayoutManager(liste.getContext() , LinearLayout.HORIZONTAL , false));
       }

       if(message == null) {
           message = connectionErrorMessage;
       }

        if(adapter instanceof ConnexionError) {

            ConnexionError handler = (ConnexionError) adapter;
            handler.setErrorMessage(message);

        }

    }
}
