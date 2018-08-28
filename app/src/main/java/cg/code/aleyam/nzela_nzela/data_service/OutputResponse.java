package cg.code.aleyam.nzela_nzela.data_service;

import cg.code.aleyam.nzela_nzela.refresh.Updater;

public class OutputResponse {
    /**
     * classe permettant de connaitre le status d'une requete post put ou delete
     * deja utilise pour les commentaires et peut etre pour d autres options avenir
     */

    private String status = null;
    private String message = null;

    public OutputResponse() {

    }
    //methode de verification commentaire.
    public boolean isOk(String agence_name) {
        if(status!=null && status.equals("ok")) {
            Updater.updateComment(agence_name);
            return true;
        }
        else return false;
    }

    public String getMessage() {
        if(message != null)
        return message;
        else return "objet non instancie";
    }
}