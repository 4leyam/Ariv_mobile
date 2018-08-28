package cg.code.aleyam.nzela_nzela.data_service;

import android.content.Context;
import android.util.Log;

import cg.code.aleyam.nzela_nzela.OttoBus;
import cg.code.aleyam.nzela_nzela.event.Nzela_objet;
import cg.code.aleyam.nzela_nzela.transaction.TicketAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Data_terminal {

    private static Data_terminal instance = null;
    private static Retrofit retrofit = null;
    private static Nzela_service service = null;
    public static Agence_data agences = null;
    public static Commentaire_data commentaires = null;
    public static Depart_data<Depart_item> departs = null;
    public static boolean onDepartWaiting = false;

    private Data_terminal() {
        instance = this;
    }

    public static Data_terminal getInstance() {
        if(instance == null) {
            new Data_terminal();
        }
        return instance;
    }

    //methode permettant de lancer le processus de recuperations des depart
    public static void getDepart(int id_agence , Context context , boolean isRunning) {
        if(context != null)
        Loader.getInstance(context).load(isRunning );

        //comme son nom l'indique la variable permet de derminer l etat de la requete.
        onDepartWaiting = true;
        Depart_data<Depart_item> les_depart = null;
        Nzela_service service = getNzela_service();
        service.getDepart_data(id_agence , Nzela_service.TOKEN).enqueue(new CallBackNzelaService<Depart_data<Depart_item>>());

    }

    //n est pas une fonction de premier plan donc no need loader

    /**
     * methode permettant de recuper les information d un depart de bus
     * grace a son identifiant.
     */
    public static void getOneDepart(int id_depart , TicketAdapter.DepartCallback rappel ) {
        Nzela_service ns = getNzela_service();

        ns.getOneDepartInfo(id_depart , Nzela_service.TOKEN).enqueue(rappel);

    }

    /**
     * mehode permettant de recuperer les informations d'une seule agence.
     * @param identifiant
     * @param rappel
     */
    public static void getOneAgence(int identifiant , TicketAdapter.AgenceCallback rappel ) {

        Nzela_service ns = getNzela_service();

        ns.getOneAgenceInfo(identifiant , Nzela_service.TOKEN).enqueue(rappel);


    }

    //not a front end fonction then no need loader.
    public static void getOneOperation() {



    }

    public static void requestPlace() {



    }



    //methode permettant de lancer le processus de recuperation des agences
    public static void getAgence(String filtre , Context context , boolean isRunning) {
        if(context != null)
        Loader.getInstance(context).load(isRunning);

        final Agence_data les_agences = new Agence_data();
        Nzela_service service = getNzela_service();
        service.getAgence_data(filtre , Nzela_service.TOKEN).enqueue(new CallBackNzelaService<Agence_data>());

    }
    public static void getAgenceComment(int id_agence , Context context , boolean isRunning) {
        if(context != null)
        Loader.getInstance(context).load(isRunning);

        //Log.e("test", "demande de recuperation de commentaire. de id: "+id_agence);

        Nzela_service service = getNzela_service();
        service.getAgenceComment(id_agence , Nzela_service.TOKEN).enqueue(new CallBackNzelaService<Commentaire_data>());

    }
    public static void postComment(Comment_obj commentaire , Context context , boolean isRunning) {
        if(context != null)
        Loader.getInstance(context).load(isRunning);

        Nzela_service service = getNzela_service();
        service.postComment(commentaire).enqueue(new CallBackNzelaService<OutputResponse>());
    }


    private static Nzela_service getNzela_service() {

        Nzela_service service_ = Data_terminal.service;
        if(service_ == null) {
           service_ = getRetrofit().create(Nzela_service.class);
        }
        return service_;
    }
    private  static Retrofit getRetrofit() {
        Retrofit r = retrofit;

        if(r == null) {
            r = new Retrofit.Builder()
                    .baseUrl(Nzela_service.WEBSITE)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }
        return r;

    }

}

class CallBackNzelaService<T> implements Callback<T> {

    @Override
    public void onResponse(Call<T> call, Response<T> response) {

        if(response.isSuccessful()) {
            T obj  = response.body();

            if(obj instanceof Commentaire_data) {
                Commentaire_data comment = (Commentaire_data) obj;
                for(int i = 0 ; i<comment.getCommentaire().size() ; i++) {
                    Log.e("test" , comment.getCommentaire().get(i).getCommentaire_texte());
                }
            }

            OttoBus.bus.post(new Nzela_objet<T>(response.body() , null));

        } else {
            //probleme d'accession aux donnees.

            OttoBus.bus.post(new Nzela_objet<Object>(new Object() , null));
        }
        Data_terminal.onDepartWaiting = false;
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {

        OttoBus.bus.post(new Nzela_objet<Object>(new Object() , t));
    }
}