package cg.code.aleyam.nzela_nzela.data_service;

import java.util.ArrayList;

public class Depart_data<T> {
    //cette classe permet de modeliser les informations des departs recuperer du ws


    private String status = new String();
    private String message = new String();

    private ArrayList<Agence_info> info = new ArrayList<>();//l'objet contenant toutes les informations de l'agence selectionnee (une collection pour la forme)
    private ArrayList<T> depart = new ArrayList<>();//objet contenant la liste des depart de l'agence selectionne.


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Depart_data () {


    }
    public Depart_data(ArrayList<Agence_info> info , ArrayList<T> liste_depart , String status , String message) {
        this.depart = liste_depart;
        this.info = info;
        this.status = status;
        this.message = message;
    }

    public ArrayList<Agence_info> getInfo() {
        return info;
    }

    public void setInfo(ArrayList<Agence_info> info) {
        this.info = info;
    }

    public void setDepart(ArrayList<T> depart) {
        this.depart = depart;
    }

    public ArrayList<T> getDepart() {
        return depart;
    }
}