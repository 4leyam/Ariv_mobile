package cg.code.aleyam.nzela_nzela.data_service;

import java.util.ArrayList;

public class Agence_data {
    //cette classe permet de modeliser les donnees recuperer du ws

    private String status ;
    private String message;
    private ArrayList<Agence_first_info> agence;

    public Agence_data() {

    }
    public Agence_data( String status ,
             String message,
             ArrayList<Agence_first_info> agences) {
        this.agence = agences;
        this.message = message;
        this.status = status;
    }

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

    public ArrayList<Agence_first_info> getAgences() {
        return agence;
    }

    public void setAgences(ArrayList<Agence_first_info> agences) {
        this.agence = agences;
    }
}