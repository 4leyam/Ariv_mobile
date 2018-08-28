package cg.code.aleyam.nzela_nzela.actu.cycle;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;

import java.util.Date;

public class EventMonitor {

    private final int MIN_RATE_NUMBER = 10;
    private static EventMonitor em = null;

    public static EventMonitor getInstance() {
        if(em == null) {
            em = new EventMonitor();
        }
        return em;
    }

    private EventMonitor() {

    }

    /**
     * methode permettant de supprimer les evenements qui ne sont plus eligible.
     * @param dbref
     * @param lastActionTime
     * @param totalVote
     * @param rate
     * @param deleteIfNot a true? supprime l'evenement quand ce dernier n'est pas a jours
     * @return
     */
    public boolean isEventUptoDate(DatabaseReference dbref ,long inactiveTime ,  long lastActionTime , int totalVote , int rate , boolean deleteIfNot) {

        boolean stillAvailable = isStillAvailable( inactiveTime , lastActionTime , totalVote , rate);
        if(deleteIfNot) {
            if(!stillAvailable) {
                dbref.setValue(null);
            }
        }
        return stillAvailable;
    }

    /**
     * fonction permettant de determiner si un evenement est toujours succeptible d'etre la
     * (si ce dernier est une bonne Information ou pas si c'est elle est expiree ou pas)
     * @param lastActionTime
     * @param totalVote
     * @param rate
     * @return
     */
    private boolean isStillAvailable(long releasedTime , long lastActionTime , int totalVote , int rate) {
        boolean avalable = true;
        long actuTime = new Date().getTime();
        long wastedTime = actuTime - releasedTime;
        long inactiveTime = actuTime - lastActionTime;

        //inactive pendant toute une journee supprimee
        if(inactiveTime >= (1000*60*60*24)) {
            return false;
        }

        //a partir de 24h tous les evenement sont susceptible d'etre ejecte
        if(wastedTime >= (1000*60*60*24)) {

            int dayNbr = (int)(wastedTime/(1000*60*60*24));
            int slope = getRateSlope(dayNbr);
            //on multipli par le nombre de jour parceque ce dernier evolue en fonction des jours.
            if(totalVote < slope) {
                //si apres 24 l'evenement ne depasse pas nombre minimale de vote il est automatiquement supprime
                return false;
            } if(rate < 3) {
                //si 70 pourcent des avis est negatif on supprime aussi
                avalable = false;
            }

        } else {
            //si on a pas encore depasse les 24h mais qu'on a plus de MIN_RATE_NUMBER avis et que les 10 donnent une moyenne de 2 on supprime.
            if(rate < 2) {
                if(totalVote >= MIN_RATE_NUMBER || wastedTime >= (1000*60*60*12)) {
                    avalable = false;
                }
            }


        }


        return avalable;
    }

    public int getRateSlope(int stampedDay) {
        //-1/6x+10 cette equation traduit la pente ou la decroissance du suivit des evenements en fonction des jours.
        //apres 2 mois ce suivit est null du coup un evenement ne vit pas plus de 2 mois
        return  (int)(-(stampedDay/6d)*stampedDay+(stampedDay*MIN_RATE_NUMBER));
    }

}
