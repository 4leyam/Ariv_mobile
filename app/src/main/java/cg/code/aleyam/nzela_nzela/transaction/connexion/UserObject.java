package cg.code.aleyam.nzela_nzela.transaction.connexion;


public class UserObject {

private String  nom ,
        prenom ,
        contact_proche ,
        contact ,
        adresse ,
        sexe ;


public UserObject () {

}

public UserObject(String  nom ,
                  String prenom ,
                  String contact_proche ,
                  String contact ,
                  String adresse ,
                  String sexe ) {
    this.contact_proche = contact_proche;
    this.nom = nom;
    this.prenom = prenom;
    this.contact = contact;
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

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
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
