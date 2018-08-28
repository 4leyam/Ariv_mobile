package cg.code.aleyam.nzela_nzela.transaction.connexion;

public class TicketObject {

    private String nom_agence
            , lieu_amont
            , lieu_aval
            , date
            , code_transaction
            , url_image_agence;
    public int valide = 0 ;
    public boolean reservation = true;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public TicketObject(String nom_agence
            , String lieu_amont
            , String lieu_aval
            , String date
            , String code_transaction
            , String url_image_agence) {

        this.nom_agence = nom_agence;
        this.code_transaction = code_transaction;
        this.lieu_amont = lieu_amont;
        this.lieu_aval = lieu_aval;
        this.date = date;

        this.url_image_agence = url_image_agence;

    }

    public String getNom_agence() {
        return nom_agence;
    }

    public void setNom_agence(String nom_agence) {
        this.nom_agence = nom_agence;
    }

    public String getLieu_amont() {
        return lieu_amont;
    }

    public void setLieu_amont(String lieu_amont) {
        this.lieu_amont = lieu_amont;
    }

    public String getLieu_aval() {
        return lieu_aval;
    }

    public void setLieu_aval(String lieu_aval) {
        this.lieu_aval = lieu_aval;
    }

    public String getCode_transaction() {
        return code_transaction;
    }

    public void setCode_transaction(String code_transaction) {
        this.code_transaction = code_transaction;
    }

    public String getUrl_image_agence() {
        return url_image_agence;
    }

    public void setUrl_image_agence(String url_image_agence) {
        this.url_image_agence = url_image_agence;
    }
}