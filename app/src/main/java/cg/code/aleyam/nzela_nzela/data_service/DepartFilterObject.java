package cg.code.aleyam.nzela_nzela.data_service;

import android.util.Log;

public class DepartFilterObject {
    String origine , destination;
    int intervalle = 0 , min = 0 , max = 0;
    String houre = "";
    String date = "";

    public DepartFilterObject( String origine ,
                               String destination,
                               int intervalle,
                               int min ,
                               int max  , String  houre, String date) {

        this.origine = origine;
        this.destination = destination;
        this.intervalle = intervalle;
        this.min = min;
        this.max = max;
        this.date = date;
        this.houre = houre;

    }

    public DepartFilterObject(){

    }

    public String getDate() {
        return date;
    }

    public String getHoure() {
        return houre;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setHoure(String houre) {
        this.houre = houre;
    }

    public String getOrigine() {
        return origine;
    }

    public String getDestination() {
        return destination;
    }

    public int getIntervalle() {
        return intervalle;
    }

    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setOrigine(String origine) {
        this.origine = origine;
    }

    public void setIntervalle(int intervalle) {
        this.intervalle = intervalle;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public void setMin(int min) {
        this.min = min;
    }
}
