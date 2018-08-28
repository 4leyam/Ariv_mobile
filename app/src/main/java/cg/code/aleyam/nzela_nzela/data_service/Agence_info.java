package cg.code.aleyam.nzela_nzela.data_service;

public class Agence_info {

    private String information_agence;
    private String tel_agence;
    private String email_agence;
    private  String adresse_agence = null;



    public Agence_info() {

    }
    public Agence_info(  String information_agence,
             String tel_agence ,
             String email_agence ,
             String adresse_agence) {

        this.information_agence = information_agence;
        this.tel_agence = tel_agence;
        this.email_agence = email_agence;
        this.adresse_agence = adresse_agence;

    }


    public String getInformation_agence() {
        return information_agence;
    }

    public void setInformation_agence(String information_agence) {
        this.information_agence = information_agence;
    }

    public String getTel_agence() {
        return tel_agence;
    }

    public void setTel_agence(String tel_agence) {
        this.tel_agence = tel_agence;
    }

    public String getEmail_agence() {
        return email_agence;
    }

    public void setEmail_agence(String email_agence) {
        this.email_agence = email_agence;
    }

    public String getAdresse_agence() {
        return adresse_agence;
    }

    public void setAdresse_agence(String adresse_agence) {
        this.adresse_agence = adresse_agence;
    }
}