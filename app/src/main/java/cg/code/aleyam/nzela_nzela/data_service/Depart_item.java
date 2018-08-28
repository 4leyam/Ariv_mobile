package cg.code.aleyam.nzela_nzela.data_service;

public class Depart_item {

    private int id_depart;
    private String formalite;
    private String depart_h;
    private String lieu_amont;
    private String lieu_aval;
    private int Place_disponible;
    private int tarif_adult;
    private int tarif_enfant;
    private String date_depart;
    private String image_bus;
    private int id_agence;
    public  int valide;

    public Depart_item() {

    }

    public Depart_item (int id_depart,
             String formalite,
             String depart_h,
             String lieu_amont,
             String lieu_aval,
             int Place_disponible,
             int tarif_adult,
             int tarif_enfant,
             String date_depart,
             String image_bus,
             int id_agence) {

        this.date_depart = date_depart;
        this.depart_h = depart_h;
        this.formalite = formalite;
        this.id_agence = id_agence;
        this.image_bus = image_bus;
        this.lieu_amont = lieu_amont;
        this.lieu_aval = lieu_aval;
        this.Place_disponible = Place_disponible;
        this.tarif_adult = tarif_adult;
        this.tarif_enfant = tarif_enfant;

    }

    public int getId_depart() {
        return id_depart;
    }

    public void setId_depart(int id_depart) {
        this.id_depart = id_depart;
    }

    public String getFormalite() {
        return formalite;
    }

    public void setFormalite(String formalite) {
        this.formalite = formalite;
    }

    public String getDepart_h() {
        return depart_h;
    }

    public void setDepart_h(String depart_h) {
        this.depart_h = depart_h;
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

    public int getPlace_disponible() {
        return Place_disponible;
    }

    public void setPlace_disponible(int place_disponible) {
        Place_disponible = place_disponible;
    }

    public int getTarif_adult() {
        return tarif_adult;
    }

    public void setTarif_adult(int tarif_adult) {
        this.tarif_adult = tarif_adult;
    }

    public int getTarif_enfant() {
        return tarif_enfant;
    }

    public void setTarif_enfant(int tarif_enfant) {
        this.tarif_enfant = tarif_enfant;
    }

    public String getDate_depart() {
        return date_depart;
    }

    public void setDate_depart(String date_depart) {
        this.date_depart = date_depart;
    }

    public String getImage_bus() {
        return image_bus;
    }

    public void setImage_bus(String image_bus) {
        this.image_bus = image_bus;
    }

    public int getId_agence() {
        return id_agence;
    }

    public void setId_agence(int id_agence) {
        this.id_agence = id_agence;
    }
}