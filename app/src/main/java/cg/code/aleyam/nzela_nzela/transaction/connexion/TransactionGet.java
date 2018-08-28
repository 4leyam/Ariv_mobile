package cg.code.aleyam.nzela_nzela.transaction.connexion;

public class TransactionGet extends TransactionPost {

    private long date;


    public TransactionGet() {

    }

    public TransactionGet(int id_depart ,  String code , String user_id , boolean reservation , long date) {

        super( id_depart ,   code  ,  reservation );
        this.date = date;

    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}