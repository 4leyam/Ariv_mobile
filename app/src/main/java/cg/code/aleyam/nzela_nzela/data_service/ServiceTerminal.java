package cg.code.aleyam.nzela_nzela.data_service;

import android.content.Context;

import cg.code.aleyam.nzela_nzela.check.CommunicationCheck;
import cg.code.aleyam.nzela_nzela.transaction.paiement.PaiementObject;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ServiceTerminal {

    private static ServiceTerminal instance;
    private static Context ct = null;

    public static ServiceTerminal getInstance(Context ct) {
        ServiceTerminal.ct = ct;
        if(instance == null) {
            new ServiceTerminal();
        }
        return instance;
    }

    private ServiceTerminal() {
        instance = this;
    }

    private Retrofit initRetrofit() {
        //before the initialisation we need to check the network state.
        if(CommunicationCheck.isConnectionAvalable(ServiceTerminal.ct)) {
            return new Retrofit.Builder()
                    .baseUrl(Nzela_service.WEBSITE)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        } else return null;

    }

    public void getBraintreeToken(Callback<String> getTokenCallBack) {
        Retrofit rt = initRetrofit();
        if(rt != null) {
            rt.create(Nzela_service.class).braintree_api_get_token().enqueue(getTokenCallBack);
        }
    }

    public void submitbraintreePayment(PaiementObject payO , Callback<String> submitCallBack) {
        Retrofit rt = initRetrofit();
        if(rt != null) {
            rt.create(Nzela_service.class).braintree_submit_payment(payO).enqueue(submitCallBack);
        }
    }

    /**
     * return true si l'operation si y'a la connexion internet.
     * @param dfo
     * @param filterCallback
     * @return
     */
    public boolean requestFiteredDeparts(DepartFilterObject dfo ,  Callback<Depart_data<FilteredDepartItem>> filterCallback) {
        Retrofit rt = initRetrofit();
        if(rt != null) {
            rt.create(Nzela_service.class).postFilter(dfo).enqueue(filterCallback);
            return true;
        }
        return false;
    }

}
