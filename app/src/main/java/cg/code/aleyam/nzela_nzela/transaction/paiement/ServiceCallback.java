package cg.code.aleyam.nzela_nzela.transaction.paiement;

import android.util.Log;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceCallback<T> extends Observable {

    public T response ;
    public String errorMessage;
    private String obs_token;
    private Observer obs ;


    public ServiceCallback(String obs_token , Observer observer) {
        this.obs_token = obs_token;
        this.obs = observer;
    }

    public class ResultCallback implements Callback<T>{

        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            Log.e("test", "cle: "+obs_token+" reponse: "+response.body());
            ServiceCallback.this.response = response.body();
            obs.update(ServiceCallback.this , obs_token);
        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            if(t != null) {
                if(t instanceof IOException) {
                    //le seul probleme d'entree et de sortie ici serai du a la connexion so...
                    ServiceCallback.this.errorMessage = "Impossible d'effectuer le paiement, verifiez votre connexion internet et reessayez svp :)";
                } else {
                    ServiceCallback.this.errorMessage = "le paiyment n'a pa pu etre effectué, reessayez plutard svp :)";
                }
            }
        }
    }


}
