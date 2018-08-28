package cg.code.aleyam.nzela_nzela.analytics;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class Analytics {

    private static FirebaseAnalytics mFirebaseAnalytics;
    private static final String TRAJET = "amont_aval";
    private static final String SELECTED_AGENCE = "select_agence";
    private static final String AGENCE_REF = "select_agence";


    public Analytics(Context context) {
        Analytics.mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    /**
     * Notifie la selection d'une agence permet donc de determiner les agences les agences les plus solicitees
     *
     * @param agenceName
     */
    public void notifyAgenceSelected(String agenceName) {
        Bundle bdl = new Bundle();
        bdl.putString(FirebaseAnalytics.Param.ITEM_NAME , agenceName);
        new AsyncNotifier(SELECTED_AGENCE).execute(bdl);
    }

    /**
     * appelee quand un utilisateur est sur le point d'effectuer une transaction il permet donc de determiner les destination et les couts
     * les plus apprecie des utilisateurs
     *
     * @param agenceRef
     * @param destination
     * @param departId
     */
    public void notifyPreTransaction(String agenceRef , String destination , String departId) {
        Bundle bdl = new Bundle();
        bdl.putString(AGENCE_REF , agenceRef);
        bdl.putString(FirebaseAnalytics.Param.DESTINATION , destination);
        bdl.putString(FirebaseAnalytics.Param.FLIGHT_NUMBER , departId);
        new AsyncNotifier(TRAJET).execute(bdl);
    }


    /**
     * Notifier les transactions en progression
     * permet par la suite de connaitre le taux d'echec des transactions en comparent ce nombre de notification a celui
     * des notifiction des trasactions terminee avec succes, sans succes, et celles annules.
     *
     * @param agenceRef
     * @param departID
     * @param userId
     */
    public void notifyTransactionProgress(String agenceRef , String departID , String userId) {
        Bundle bdl = new Bundle();
        bdl.putString(FirebaseAnalytics.Param.ITEM_BRAND , agenceRef);
        bdl.putString(FirebaseAnalytics.Param.CHARACTER , userId);
        bdl.putString(FirebaseAnalytics.Param.FLIGHT_NUMBER , departID);
        new AsyncNotifier(FirebaseAnalytics.Event.CHECKOUT_PROGRESS).execute(bdl);
    }


    /**
     *
     * notifie le resultat des transaction en fonction des agences.
     *
     * @param agenceRef
     * @param userId
     * @param succes
     */
    public void notifyCompletedTransaction(String agenceRef , String userId , String succes) {
        Bundle bdl = new Bundle();
        bdl.putString(FirebaseAnalytics.Param.ITEM_BRAND , agenceRef);
        bdl.putString(FirebaseAnalytics.Param.CHARACTER , userId);
        bdl.putString(FirebaseAnalytics.Param.SUCCESS , succes);
        new AsyncNotifier(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE).execute(bdl);
    }


    private static class AsyncNotifier extends AsyncTask<Bundle, Void , Boolean> {

        private String eventType = null;
        public AsyncNotifier(String eventType) {
            this.eventType = eventType;
        }
        @Override
        protected Boolean doInBackground(Bundle... bundles) {

            Analytics.mFirebaseAnalytics.logEvent(this.eventType , bundles[0]);

            return true;
        }
    }

}
