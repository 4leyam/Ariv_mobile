package cg.code.aleyam.nzela_nzela.transaction.connexion;

import com.google.android.gms.maps.model.LatLng;

import cg.code.aleyam.nzela_nzela.authentication.User_info_input;

public class UserPostObject {

    private String  nom ,
            prenom ,
            contact_proche ,

            adresse ,
            sexe ;


    public UserPostObject () {

    }


    public UserPostObject(String  nom ,
                          String prenom ,
                          String contact_proche ,

                          String adresse ,
                          String sexe ) {
        this.contact_proche = contact_proche;
        this.nom = nom;
        this.prenom = prenom;
        //this.userID = userID;
        this.adresse = adresse;
        this.sexe = sexe;

    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getContact_proche() {
        return contact_proche;
    }

    public void setContact_proche(String contact_proche) {
        this.contact_proche = contact_proche;
    }



    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }
}
