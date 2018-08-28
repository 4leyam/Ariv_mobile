package cg.code.aleyam.nzela_nzela.data_service;



public class Agence_first_info {

    private int id_agence;
    private String nom_agence;
    private int avis;
    private String logo_agence;


    public Agence_first_info(int id_agence, String nom_agence, int avis , String logo_agence) {
        this.id_agence = id_agence;
        this.nom_agence = nom_agence;
        this.avis = avis;
        this.logo_agence = logo_agence;

    }
    public Agence_first_info() {

    }

    public String getLogo_agence() {
        return logo_agence;
    }

    public void setLogo_agence(String logo_agence) {
        this.logo_agence = logo_agence;
    }
    public int getId_agence() {
        return id_agence;
    }

    public void setId_agence(int id_agence) {
        this.id_agence = id_agence;
    }

    public String getNom_agence() {
        return nom_agence;
    }

    public void setNom_agence(String nom_agence) {
        this.nom_agence = nom_agence;
    }

    public int getAvis() {
        return avis;
    }

    public void setAvis(int avis) {
        this.avis = avis;
    }

}
