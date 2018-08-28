package cg.code.aleyam.nzela_nzela.event;

import android.util.Log;

import cg.code.aleyam.nzela_nzela.data_service.Agence_data;
import cg.code.aleyam.nzela_nzela.data_service.Commentaire_data;
import cg.code.aleyam.nzela_nzela.data_service.Data_terminal;
import cg.code.aleyam.nzela_nzela.data_service.Depart_data;
import cg.code.aleyam.nzela_nzela.data_service.Depart_item;

public class Nzela_objet<T> {
   public T objet = null;
   public boolean ok = false;
   public Throwable t = null;

    public Nzela_objet(T objet , Throwable t) {
        this.objet = objet;
        if(objet != null && !objet.getClass().getName().equals(Object.class.getName())) {
            //donc si la requete est succes
            this.ok = true;
        }
        this.t = t;
        //on laisse des traces...
        notifyInputData(objet);
    }

    /**
     * methode permetant de laiser des traces des dernieres mise a jours des donnees.
     *
     */
    public void notifyInputData(T retreivedData) {

        if(retreivedData instanceof Agence_data) {
            Data_terminal.agences = (Agence_data) retreivedData;
        } else if (retreivedData instanceof Depart_data) {
            Data_terminal.departs = (Depart_data<Depart_item>) retreivedData;
        } else if (retreivedData instanceof Commentaire_data) {
            Log.e("test" , "trace de commentaire laissee");
            Data_terminal.commentaires = (Commentaire_data)retreivedData;
        }

    }
}