package cg.code.aleyam.nzela_nzela.transaction.paiement;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.google.android.gms.security.ProviderInstaller;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import cg.code.aleyam.nzela_nzela.actu.connexion.Upload;
import cg.code.aleyam.nzela_nzela.analytics.Analytics;
import cg.code.aleyam.nzela_nzela.data_service.Loader;
import cg.code.aleyam.nzela_nzela.data_service.ServiceTerminal;
import cg.code.aleyam.nzela_nzela.depart.InfoGenFragment;
import cg.code.aleyam.nzela_nzela.transaction.connexion.PostTransaction;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class InternationnalPayment implements Observer{

    public static final int PAIEMENT_REQUEST_CODE = 4124;
    String token , amount = "2000";
    PaiementObject payO = null;
    ServiceCallback<String> pc = null;
    private Activity submitActivity ;
    private static InternationnalPayment instance = null;
    private static boolean reloadInstance = true;
    ResultCallback rc;
    HashMap<String , String> dataKey = new HashMap<>();
    String successMessage = "Tansaction Effectuee avec success, Consultez vos tickets " +
            "dans l'onglet ticket, puis rendez vous a l'agence le jours du Voyage Ariv vous souhaite un excellent Voyage :)"
            , unknowErrorMessage = "Impossible d'effectuer la transaction: erreur FPM. Svp, veuillez reessayer plus tard " ;

    public interface ResultCallback {
        void moneyTransSuccess() ;
        void moneyTransFailed(String t);
    }

    public static InternationnalPayment getInstance(Activity activity) {
        if(instance == null || reloadInstance) {
            instance = new InternationnalPayment(activity);
            reloadInstance = false;
        }
        return instance;
    }

    public void setResultCallback(ResultCallback rc) {
        this.rc = rc;
    }

    private InternationnalPayment(Activity ct) {
        this.submitActivity = ct;
    }


    /**
     * methode permettant de lancer l'interface drop-in de braintree et ainsi permettant
     * a l'utilisateur de commencer la transaction.
     */
    public void LanchDropIn (HashMap dataKey) {
        this.dataKey = dataKey;
        try {
            ProviderInstaller.installIfNeeded(this.submitActivity);
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            SSLEngine engine = sslContext.createSSLEngine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        pc = new ServiceCallback<String>("token" , InternationnalPayment.this);
        new GetToken().execute();
        Log.e("test", "");


    }


    private void beginPaymentOperation() {
        //Toast.makeText(submitActivity , "token: "+token, Toast.LENGTH_SHORT).show();
        DropInRequest dir = new DropInRequest().clientToken(token);
        this.submitActivity.startActivityForResult(dir.getIntent(this.submitActivity) , PAIEMENT_REQUEST_CODE);
    }


    @Override
    public void update(Observable o, Object arg) {
        String key = (String)arg;
        if(key.equals("token")) {
            Loader.dismiss();
            ServiceCallback pc = (ServiceCallback)o;
            if(pc.errorMessage == null) {
                token = (String) pc.response;
                beginPaymentOperation();
            } else {
                Toast.makeText(this.submitActivity, "Impossible de recuperer le jeton d'identification" , Toast.LENGTH_SHORT).show();
            }
        } else if(key.equals("submit")) {
            Loader.dismiss();
            String response = pc.response;
            //Toast.makeText(this.submitActivity, "recuperation de la reponse." , Toast.LENGTH_LONG).show();
            if (response.contains("Successful")) {
                AlertDialog.Builder al = new AlertDialog.Builder(this.submitActivity)
                        .setTitle(InfoGenFragment.fromHtml("<b><font color='#424242'>Information</font></b>"))
                        .setMessage(InfoGenFragment.fromHtml("<font color='#424242'>"+this.successMessage+"</font>"))
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                InternationnalPayment.this.submitActivity.finish();
                            }
                        })
                        .setCancelable(true);
                al.create().show();
                completeTransaction(dataKey);
                if(this.rc != null)
                this.rc.moneyTransSuccess();
                //pour dire qu'on peut creer une nouvelle instance de cette classe pour une nouvelle transaction
                reloadInstance = true;
            } else {
                //comme le transfert d'argent a echoue on retire donc l'utilisateur sont pre-ticket.
                removeCustomer(InternationnalPayment.this.dataKey);
                Toast.makeText(this.submitActivity , "Echec de la transaction Information de paiement incorrecte" , Toast.LENGTH_SHORT).show();
                if(this.rc != null)
                this.rc.moneyTransFailed(pc.errorMessage);

            }
        } //else Toast.makeText(this.submitActivity , "cle de reponse: "+key , Toast.LENGTH_SHORT).show();
    }

    private void completeTransaction(HashMap<String , String> dataKey) {

        Map<String , Object> update = new HashMap<>();
        //finalisation of transaction by removing procesing status
        update.put(dataKey.get(PostTransaction.TRANSACTION_MAIN_KEY_RESERVISTE)+"/state" , PostTransaction.TRANSACTION_ok);
        //finalisation of transaction by removing processing refs
        update.put(PostTransaction.user_pre_transaction_key , null);
        //setting of new comment permission
        update.put(PostTransaction.getCommentpermissionKey() , dataKey.get(PostTransaction.AGENCE_KEY));
        boolean complet = Upload.data_root_ref.updateChildren(update).isSuccessful();
        new Analytics(submitActivity).notifyCompletedTransaction(dataKey.get(PostTransaction.TRANSACTION_MAIN_KEY_PLACES)
                ,PostTransaction.userPhoneNumber , "Succes" );
    }

    private void removeCustomer(HashMap<String , String> dataKey) {


        Upload.data_root_ref.child(dataKey.get(PostTransaction.TRANSACTION_MAIN_KEY_PLACES)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("test", "chemin des places dispo de la transaction courante: "+PostTransaction.TRANSACTION_MAIN_KEY_PLACES);
                if(dataSnapshot.getValue() != null && dataSnapshot.exists()) {
                    remove(Long.parseLong(""+dataSnapshot.getValue()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void remove(long placedispo) {
        HashMap<String , Object> transactionUpdate = new HashMap<>();
        transactionUpdate.put(dataKey.get(PostTransaction.TRANSACTION_MAIN_KEY_RESERVISTE) , null);
        transactionUpdate.put(dataKey.get(PostTransaction.TRANSACTION_MAIN_KEY_USER) , null);
        transactionUpdate.put(dataKey.get(PostTransaction.TRANSACTION_MAIN_KEY_PLACES) , ""+(++placedispo));
        transactionUpdate.put(PostTransaction.user_pre_transaction_key , null);
        Upload.data_root_ref.updateChildren(transactionUpdate);
        new Analytics(submitActivity).notifyCompletedTransaction(dataKey.get(PostTransaction.TRANSACTION_MAIN_KEY_PLACES)
                ,PostTransaction.userPhoneNumber , "echec" );
    }

    public void notifyUnkowError(String message) {
        AlertDialog.Builder al = new AlertDialog.Builder(this.submitActivity)
                .setTitle(InfoGenFragment.fromHtml("<b><font color='#424242'>Information</font></b>"))
                .setMessage(InfoGenFragment.fromHtml("<font color='#424242'>"+message+"</font>"))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        InternationnalPayment.this.submitActivity.finish();
                    }
                })
                .setCancelable(true);
        al.create().show();
        removeCustomer(InternationnalPayment.this.dataKey);
    }

    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if(requestCode  == PAIEMENT_REQUEST_CODE) {
            boolean allSet = false;
            if(resultCode == RESULT_OK) {
                if(data != null) {
                    DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                    if(result != null) {
                        PaymentMethodNonce nonce = result.getPaymentMethodNonce();
                        if(nonce != null) {
                            String strNonce = nonce.getNonce();
                            if(!TextUtils.isEmpty(strNonce)) {
                                Log.i("test", "onActivityResult: nonce =  "+strNonce );
                                Toast.makeText(submitActivity , "nonce: "+strNonce , Toast.LENGTH_SHORT).show();
                                if(!TextUtils.isEmpty(amount.trim())) {
                                    //donc si on a bel et bien recu un montent pour la transaction.
                                    payO = new PaiementObject(amount , strNonce);
                                    sendPayments(payO);
                                    allSet = true;
                                }
                            }

                        }
                    }

                }

                if(!allSet) notifyUnkowError(this.unknowErrorMessage);

            } else if(resultCode == RESULT_CANCELED) {
                //annulation du paiement
                removeCustomer(InternationnalPayment.this.dataKey);
                Toast.makeText(this.submitActivity, "Vous avez annulé votre transaction, Merci de nous revenir tres prochainement." , Toast.LENGTH_LONG).show();
            } else {
                removeCustomer(InternationnalPayment.this.dataKey);
                Toast.makeText(this.submitActivity, "la transaction ne s'est pas correctement effectuee reessayer plus tard." , Toast.LENGTH_LONG).show();
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                Toast.makeText(this.submitActivity, error.getMessage() , Toast.LENGTH_LONG).show();


            }
        } else {
            //pour dire que dans tous les cas faudrait penser a annuler les transactions quand l'utisateur retourne sans succes.
            removeCustomer(InternationnalPayment.this.dataKey);
            Toast.makeText(this.submitActivity, "Un probleme est survenue lors de la transaction reessayer plus tard." , Toast.LENGTH_LONG).show();
        }
    }

    private void sendPayments(PaiementObject payO) {
       //comme tokenCallBack nous permet d'etre a l'ecoute d'un evenement on va egalement l'utiliser ici.
        pc = new ServiceCallback<>("submit" , InternationnalPayment.this);
        //on lance le chargement en attentant que la transaction soit effectuee.
        Loader.getInstance(InternationnalPayment.this.submitActivity).load(true);
        ServiceTerminal.getInstance(submitActivity).submitbraintreePayment(payO , pc.new ResultCallback());
    }





    private class GetToken extends AsyncTask<Object , Void , Void> {

        Loader lo;
        ServiceTerminal pt ;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //donc avant le debut du traitement
            lo = Loader.getInstance(InternationnalPayment.this.submitActivity);
            lo.load(true);
        }

        @Override
        protected Void doInBackground(Object[] objects) {

            pt = ServiceTerminal.getInstance(submitActivity);
            pt.getBraintreeToken(pc.new ResultCallback());
            return null;
        }

        @Override
        protected void onPostExecute(Void o) {
            super.onPostExecute(o);
        }
    }


}
