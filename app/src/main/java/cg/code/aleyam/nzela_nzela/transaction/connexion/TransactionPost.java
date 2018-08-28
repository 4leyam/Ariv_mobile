package cg.code.aleyam.nzela_nzela.transaction.connexion;

public class TransactionPost{

    private int id_depart;

    private String code;
    //utilisation de userId a revoir
    //private String user_id;
    private boolean reservation;



    public TransactionPost(){

    }

    public TransactionPost(int id_depart ,  String code ,  boolean reservation) {

        this.id_depart = id_depart;

        this.code = code;
        //this.user_id = user_id;
        this.reservation = reservation;


    }

    public boolean isReservation() {
        return reservation;
    }

    public void setReservation(boolean reservation) {
        this.reservation = reservation;
    }



    public int getId_depart() {
        return id_depart;
    }

    public void setId_depart(int id_depart) {
        this.id_depart = id_depart;
    }



    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }



}